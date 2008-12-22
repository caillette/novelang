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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.CommonGroupLoader;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplateGroupLoader;
import org.apache.commons.lang.ClassUtils;
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
public class TokenEnumerationGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( TokenEnumerationGenerator.class ) ;

  private static final Pattern ALL_TOKENS_PATTERN =
      Pattern.compile( "tokens(?:\\s*)\\{[^\\}]*\\}" ) ;
  private static final Pattern ONE_TOKEN_PATTERN =
      Pattern.compile( "(?: *([A-Z_]+) *;)" ) ;


  private static final StringTemplateErrorListener STRING_TEMPLATE_ERROR_LISTENER =
      new StringTemplateErrorListener() {
        public void error( String s, Throwable throwable ) {
          throw new RuntimeException( s, throwable ) ;
        }
        public void warning( String s ) {
          throw new RuntimeException( s ) ;
        }
      }
  ;

  public static Iterable< Item > findAntlrTokens( String grammar ) {
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

    return ImmutableList.copyOf( tokenList ) ;
  }

  public static String generateJavaEnumeration(
      String packageName,
      String className,
      Iterable< Item > enumerationItems
  ) {
    final String templateDirectory = 
        ClassUtils.getPackageName( TokenEnumerationGenerator.class ).replace( '.', '/' ) ;
    LOGGER.info( "Loading StringTemplates from classpath directory: '{}'", templateDirectory ) ;

    final StringTemplateGroupLoader loader =
        new CommonGroupLoader( templateDirectory, STRING_TEMPLATE_ERROR_LISTENER ) ;
    StringTemplateGroup.registerGroupLoader( loader ) ;
    final StringTemplateGroup templates = StringTemplateGroup.loadGroup( "java" ) ;
    final StringTemplate javaEnum = templates.getInstanceOf( "enum" ) ;

    javaEnum.setAttribute( "package", packageName ) ;
    javaEnum.setAttribute( "name", className ) ;
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