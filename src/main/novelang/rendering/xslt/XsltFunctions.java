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
package novelang.rendering.xslt;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Laurent Caillette
 */
public class XsltFunctions {

  private static final Logger LOGGER = LoggerFactory.getLogger( XsltFunctions.class ) ;

  /**
   * Just to get an easy case.
   * @return The constant String "{@code Hello!}".
   */
  public static String hello() {
    return "Hello!" ;
  }

  public static String asString( Object treeResultFragment ) {
    return asString( "", treeResultFragment ) ;
  }


  public static String numberAsText(
      Object numberObject,
      Object localeNameObject,
      Object caseObject
  ) {
    final String numberAsString = asString( "number", numberObject ) ;
    final String localeAsString = asString( "locale", localeNameObject ) ;
    final String caseAsString = asString( "case", caseObject ) ;

    final String numberAsText ;

    if( numberObject instanceof Number ) {
      final Number number = ( Number ) numberObject ;
      if( "FR".equals( localeNameObject ) ) {
        numberAsText = numberAsFrenchText( number.intValue(), ( String ) caseObject ) ;
      } else {
        LOGGER.warn( "Locale not supported: " + localeAsString ) ;
        numberAsText = "" + number.intValue() ;
      }
      return numberAsText ;

    } else {
      final String message = "!NAN! Cannot convert to number: " + numberAsString ;
      LOGGER.error( message ) ;
      return message ;
    }
  }

  private static String numberAsFrenchText( int number, String caseType ) {

    final String frenchText = toFrenchText( number ) ;

    if( "upper".equalsIgnoreCase( caseType ) ) {
      return frenchText.toUpperCase( Locale.FRENCH ) ;
    } else if( "lower".equalsIgnoreCase( caseType ) ) {
      return frenchText.toLowerCase( Locale.FRENCH ) ;
    } else if( "capital".equalsIgnoreCase( caseType ) ) {
      return frenchText.substring( 0, 1 ).toUpperCase()
          + frenchText.substring( 1, frenchText.length() ) ;
    } else {
      LOGGER.warn( "Unsupported case: " + caseType ) ;
      return frenchText ;
    }
  }

  private static String toFrenchText( Number number ) {
    switch( number.intValue() ) {
      case 0  : return "zéro" ;
      case 1  : return "un" ;
      case 2  : return "deux" ;
      case 3  : return "trois" ;
      case 4  : return "quatre" ;
      case 5  : return "cinq" ;
      case 6  : return "six" ;
      case 7  : return "sept" ;
      case 8  : return "huit" ;
      case 9  : return "neuf" ;
      case 10 : return "dix" ;
      case 11 : return "onze" ;
      case 12 : return "douze" ;
      case 13  : return "treize" ;
      case 14  : return "quatorze" ;
      case 15  : return "quinze" ;
      case 16  : return "seize" ;
      case 17  : return "dix-sept" ;
      case 18  : return "dix-huit" ;
      case 19  : return "dix-neuf" ;
      case 20  : return "vingt" ;
      case 21  : return "vingt-et-un" ;
      case 22  : return "vingt-deux" ;
      case 23  : return "vingt-trois" ;
      case 24  : return "vingt-quatre" ;
      case 25  : return "vingt-cinq" ;
      case 26  : return "vingt-six" ;
      case 27  : return "vingt-sept" ;
      case 28  : return "vingt-huit" ;
      case 29  : return "vingt-neuf" ;

      default : throw new UnsupportedOperationException( "Not supported: " + number.intValue() ) ;
    }
  }

  private static String asString( String name, Object object ) {
    return
        name + ": '" + object + "' "
      + ( null == object ? "" : object.getClass().getName() )
    ;

  }
}
