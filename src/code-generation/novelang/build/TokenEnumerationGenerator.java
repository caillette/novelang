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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.StringTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


/**
 * Generates a Java enum from tokens (in a {@code tokens} clause) declared in and ANTLR grammar.
 *
 * @author Laurent Caillette
 */
public class TokenEnumerationGenerator extends JavaGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( TokenEnumerationGenerator.class ) ;



  public TokenEnumerationGenerator(
      File grammarFile,
      String packageName,
      String className,
      File targetDirectory
  ) throws IOException
  {
    super( grammarFile, packageName, className, targetDirectory ) ;
  }

  protected String generateCode() {
    final Iterable< Item > enumerationItems = findAntlrTokens( getGrammar() ) ;
    return generateJavaEnumeration( enumerationItems ) ;
  }
  
  private static final Collection< Item > SYNTHETIC_ITEMS = ImmutableList.of( 
      new Item( "_STYLE" ), 
      new Item( "_LEVEL" ),
      new Item( "_LIST_WITH_TRIPLE_HYPHEN" ),
      new Item( "_PARAGRAPH_AS_LIST_ITEM" ),
      new Item( "_EMBEDDED_LIST_WITH_HYPHEN" ),
      new Item( "_EMBEDDED_LIST_ITEM" ),
      new Item( "_META_TIMESTAMP" ),
      new Item( "_META" ),
      new Item( "_WORD_COUNT" ),
      new Item( "_IMAGE_WIDTH" ),
      new Item( "_IMAGE_HEIGHT" )
  ) ;

  private static final Pattern ALL_TOKENS_PATTERN =
      Pattern.compile( "tokens(?:\\s*)\\{[^\\}]*\\}" ) ;

  private static final Pattern ONE_TOKEN_PATTERN =
      Pattern.compile( "(?: *([A-Z_]+) *;)" ) ;

  protected static Iterable< Item > findAntlrTokens( String grammar ) {
    final Matcher allTokensMatcher = ALL_TOKENS_PATTERN.matcher( grammar ) ;
    if( ! allTokensMatcher.find() ) {
      LOGGER.warn( "No token found" ) ;
      return Iterables.emptyIterable() ;
    }
    final String allTokens = allTokensMatcher.group( 0 ) ;

    final Matcher eachTokenMatcher = ONE_TOKEN_PATTERN.matcher( allTokens ) ;
    final List< Item > tokenList = Lists.newLinkedList() ;

    while( eachTokenMatcher.find() ) {
      tokenList.add( new Item( eachTokenMatcher.group( 1 ) ) ) ;
    }
    
    tokenList.addAll( SYNTHETIC_ITEMS ) ;

    return ImmutableList.copyOf( tokenList ) ;
  }

  protected String generateJavaEnumeration(
      Iterable< Item > enumerationItems
  ) {
    final StringTemplate javaEnum = createStringTemplate( "enum" ) ;
    javaEnum.setAttribute( "items", enumerationItems ) ;
    return javaEnum.toString() ;

  }

  public static final class Item {
    public final String name ;
    public final boolean punctuationSign ;

    public Item( String name ) {
      this.name = name;
      this.punctuationSign = name.startsWith( "SIGN_" );
    }

    @Override
    public boolean equals( Object o ) {
      if( this == o ) {
        return true ;
      }
      if( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final Item item = ( Item ) o ;

      if( name != null ? ! name.equals( item.name ) : item.name != null ) {
        return false ;
      }

      return true ;
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0 ;
      result = 31 * result + ( punctuationSign ? 1 : 0 ) ;
      return result ;
    }
  }

}