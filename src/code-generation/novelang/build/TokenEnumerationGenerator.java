/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package novelang.build;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.StringTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;


/**
 * Generates a Java enum from tokens (in a {@code tokens} clause) declared in and ANTLR grammar.
 *
 * @author Laurent Caillette
 */
public class TokenEnumerationGenerator extends GrammarBasedJavaGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( TokenEnumerationGenerator.class ) ;


  public TokenEnumerationGenerator(
      final File grammarFile,
      final String packageName,
      final String className,
      final File targetDirectory
  ) throws IOException
  {
    super( grammarFile, packageName, className, targetDirectory ) ;
  }

  protected String generateCode() {
    final Iterable< Item > enumerationItems = findAntlrTokens( getGrammar() ) ;
    return generateJavaEnumeration( enumerationItems ) ;
  }

  private static final Map< TokenProperty, String > TAG_SCOPE =
      ImmutableMap.of( TokenProperty.TAG_BEHAVIOR, TagBehavior.SCOPE.name() ) ;

  private static final Map< TokenProperty, String > TAG_TRAVERSABLE =
      ImmutableMap.of( TokenProperty.TAG_BEHAVIOR, TagBehavior.TRAVERSABLE.name() ) ;

  private static final Collection< Item > SYNTHETIC_ITEMS = ImmutableList.of(
      new Item( "_STYLE" ),
      new Item( "_LEVEL", TAG_SCOPE ),
      new Item( "_LIST_WITH_TRIPLE_HYPHEN", TAG_TRAVERSABLE ),

      /*  Crafted out of PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ by GenericRenderer,
          no tag scope then. */
      new Item( "_PARAGRAPH_AS_LIST_ITEM" ),

      new Item( "_EMBEDDED_LIST_WITH_HYPHEN" ),
      new Item( "_EMBEDDED_LIST_ITEM" ),
      new Item( "_META_TIMESTAMP" ),
      new Item( "_META" ),
      new Item( "_LOCATION" ),
      new Item( "_WORD_COUNT" ),
      new Item( "_TAGS" ),
      new Item( "_IMAGE_WIDTH" ),
      new Item( "_IMAGE_HEIGHT" ),
      new Item( "_URL" ),
      new Item( "_PLACEHOLDER_" ),
      new Item( "_ZERO_WIDTH_SPACE" ),
      new Item( "_PRESERVED_WHITESPACE" ),
      new Item( "_IMPLICIT_IDENTIFIER" ), 
      new Item( "_EXPLICIT_IDENTIFIER" ),
      new Item( "_IMPLICIT_TAG" ),
      new Item( "_PROMOTED_TAG" ),
      new Item( "_EXPLICIT_TAG" ) 

  ) ;

  private static final Pattern ALL_TOKENS_PATTERN =
      Pattern.compile( "tokens(?:\\s*)\\{[^\\}]*\\}" ) ;

  private static final Pattern ONE_TOKEN_PATTERN ;
  static {
    final StringBuffer regexBuilder = new StringBuffer() ;
    regexBuilder.append( "(?: *([A-Z_]+) *;)(?: *//" ) ;
    for( final TokenProperty tokenProperty : TokenProperty.values() ) {
      regexBuilder
          .append( "(?:\\s+" )
          .append( tokenProperty.getPublicName() )
          .append( "=(\\w+))?" )
      ;
    }
    regexBuilder.append( ")? *" ) ;
    ONE_TOKEN_PATTERN = Pattern.compile( regexBuilder.toString() ) ;
    LOGGER.debug( "Crafted regex {}", ONE_TOKEN_PATTERN.pattern() ) ;
  }

  protected static Iterable< Item > findAntlrTokens( final String grammar ) {
    final Matcher allTokensMatcher = ALL_TOKENS_PATTERN.matcher( grammar ) ;
    if( ! allTokensMatcher.find() ) {
      LOGGER.warn( "No token found" ) ;
      return ImmutableList.of() ;
    }
    final String allTokens = allTokensMatcher.group( 0 ) ;

    final Matcher eachTokenMatcher = ONE_TOKEN_PATTERN.matcher( allTokens ) ;
    final List< Item > tokenList = Lists.newLinkedList() ;

    while( eachTokenMatcher.find() ) {
      final Map< TokenProperty, String > properties = Maps.newHashMap() ;
      for( final TokenProperty tokenProperty : TokenProperty.values() ) {
        final String propertyValue = eachTokenMatcher.group( tokenProperty.ordinal() + 2 ) ;
        if( null != propertyValue ) {
          properties.put( tokenProperty, propertyValue ) ;
        }
      }
      final Item item = new Item( eachTokenMatcher.group( 1 ), properties ) ;
      tokenList.add( item ) ;
    }
    if( tokenList.size() < 1 ) {
      LOGGER.warn( "No token found" ) ;
    }


    tokenList.addAll( SYNTHETIC_ITEMS ) ;

    return ImmutableList.copyOf( tokenList ) ;
  }

  protected String generateJavaEnumeration(
      final Iterable< Item > enumerationItems
  ) {
    final StringTemplate javaEnum = createStringTemplate( "enum" ) ;
    javaEnum.setAttribute( "items", enumerationItems ) ;
    return javaEnum.toString() ;

  }

  public static final class Item {
    public final String name ;
    public final boolean punctuationSign ;
    public final TagBehavior tagBehavior ;

    public Item( final String name ) {
      this( name, ImmutableMap.< TokenProperty, String >of() ) ;
    }

    public Item( final String name, final Map< TokenProperty, String > properties ) {
      this.name = name ;
      this.punctuationSign =
          "true".equalsIgnoreCase( properties.get( TokenProperty.PUNCTUATION_SIGN ) ) ;
      final String tagBehaviorAsString = properties.get( TokenProperty.TAG_BEHAVIOR ) ;
      if( null == tagBehaviorAsString ) {
        tagBehavior = TagBehavior.NON_TRAVERSABLE ;
      } else {
        tagBehavior = TagBehavior.valueOf( tagBehaviorAsString ) ;
      }      
    }

    @Override
    public boolean equals( final Object o ) {
      if( this == o ) {
        return true ;
      }
      if( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final Item item = ( Item ) o ;

      return !( name != null ? !name.equals( item.name ) : item.name != null );

    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0 ;
      result = 31 * result + ( punctuationSign ? 1 : 0 ) ;
      return result ;
    }

    @Override
    public String toString() {
      return "Item[ name: " + name + ( punctuationSign ? " ; punctuationsign" : "" ) + " ]" ;
    }
  }

  private static enum TokenProperty {
    PUNCTUATION_SIGN,
    TAG_BEHAVIOR ;

    private final String publicName ;
    
    private TokenProperty() {
      publicName = name().toLowerCase().replace( "_", "" ) ;
    }

    public String getPublicName() {
      return publicName ;
    }
  }

  /**
   * This class mirrors the one in {@code novelang.common} which cannot be referenced because
   * of source directories layout.
   */
  public enum TagBehavior {
    NON_TRAVERSABLE,
    TRAVERSABLE,
    SCOPE,
    TERMINAL
  }

}