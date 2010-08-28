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
package novelang.system;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import novelang.common.LanguageTools;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import org.apache.commons.lang.SystemUtils;

/**
 * Utility class for dumping system properties.
 *
 * @author Laurent Caillette
 */
public final class EnvironmentTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( EnvironmentTools.class ) ;

  private EnvironmentTools() { }

  public static String getEnvironmentInformation() {
    final StringBuffer stringBuffer = new StringBuffer() ;
    appendEnvironmentInformation( stringBuffer ) ;
    return stringBuffer.toString() ;
  }

  /**
   * Feeds a <code>StringBuffer</code> with all standard environment information
   * known to be useful.
   * @param stringBuffer a non-null <code>StringBuffer</code>.
   */
  public static void appendEnvironmentInformation(
      final StringBuffer stringBuffer
  ) {
    stringBuffer.append( "System properties: \n" ) ;
    appendSystemProperties( stringBuffer, System.getProperties(), "  " ) ;
  }

  /**
   * Feeds a <code>StringBuffer</code> with <code>Properties</code> content.
   * For some keys like paths formatting is enhanced a bit.
   * @param stringBuffer a non-null <code>StringBuffer</code>.
   * @param systemProperties a non-null object as returned by
   *     {@link System#getProperties()}.
   * @param indent a non-null <code>String</code> containing
   *     the prefix for indenting lines (four spaces characters are usually ok).
   */
  public static void appendSystemProperties(
      final StringBuffer stringBuffer,
      final Properties systemProperties,
      final String indent
  ) {
    final Enumeration propertyNames = systemProperties.propertyNames() ;
    final List< String > propertyNameList = Lists.newArrayList() ;
    while( propertyNames.hasMoreElements() ) {
      propertyNameList.add( ( String ) propertyNames.nextElement() ) ;
    }

    final String pathIndent = indent + "  " ;
    for( final String name : Ordering.natural().sortedCopy( propertyNameList ) ) {
      stringBuffer.append( indent ) ;
      final String propertyValue = systemProperties.getProperty( name ) ;
      if( ! appendIfPath( stringBuffer, name, propertyValue, pathIndent ) &&
          ! appendIfLineSeparator( stringBuffer, name, propertyValue )
      ) {
        stringBuffer.append( name ) ;
        stringBuffer.append( " = " ) ;
        stringBuffer.append( propertyValue ) ;
        stringBuffer.append( "\n" ) ;
      }
    }
  }

  private static final String LINE_SEPARATOR_PROPERTY_NAME = "line.separator" ;

  private static final Set< String > PATH_PROPERTY_NAMES = Sets.newHashSet(
      "java.class.path",
      "java.library.path",
      "sun.boot.class.path",
      "java.endorsed.dirs",
      "sun.boot.library.path",
      "java.ext.dirs"
  ) ;

  private static boolean appendIfPath(
      final StringBuffer stringBuffer,
      final String propertyName,
      final String classpath,
      final String indent
  ) {
    if( PATH_PROPERTY_NAMES.contains( propertyName ) ) {
      stringBuffer.append( propertyName ) ;
      stringBuffer.append( " = " ) ;
      appendPath( stringBuffer, classpath, indent ) ;
      return true ;
    } else {
      return false ;
    }
  }

  private static boolean appendIfLineSeparator(
      final StringBuffer stringBuffer,
      final String name,
      final String lineSeparator
  ) {
    if( LINE_SEPARATOR_PROPERTY_NAME.equals( name ) ) {
      final char[] separatorChars = lineSeparator.toCharArray() ;
      stringBuffer.append( LINE_SEPARATOR_PROPERTY_NAME ) ;
      stringBuffer.append( " =" ) ;
      for( final char c : separatorChars ) {
        stringBuffer.append( " 0x" );
        stringBuffer.append( LanguageTools.to8byteHex( c ).toUpperCase() );
      }
      stringBuffer.append( "\n" ) ;
      return true ;
    } else {
      return false ;
    }
  }

  /**
   * Feeds a <code>StringBuffer</code> with one classpath entry per line.
   * @param stringBuffer a non-null <code>StringBuffer</code>.
   * @param classpath a string containing classpath as returned by
   *     <code>System.getProperty( "java.class.path" )</code>.
   * @param indent a non-null <code>String</code> containing
   *     the prefix for indenting lines (four spaces characters are usually ok).
   */
  public static void appendPath(
      final StringBuffer stringBuffer,
      final String classpath,
      final String indent
  ) {
    final StringTokenizer tokenizer = new StringTokenizer(
        classpath, File.pathSeparator );
    final File[] classpathEntries = new File[ tokenizer.countTokens() ] ;
    int i = 0 ;
    while ( tokenizer.hasMoreTokens() ) {
      classpathEntries[ i ]  = new File( tokenizer.nextToken() ) ;
      i++;
    }
//    Arrays.sort( classpathEntries, new Comparator< File >() {
//      public int compare( File file1, File file2 ) {
//        return file1.getName().compareTo( file2.getName() ) ;
//      }
//    } ) ;

    switch( classpathEntries.length ) {
      case 0 : break ;
      case 1 :
        stringBuffer.append( classpathEntries[ 0 ] ) ;
        break ;
      default :
        for( final File classpathEntry : classpathEntries ) {
          stringBuffer.append( SystemUtils.LINE_SEPARATOR );
          stringBuffer.append( indent );
          stringBuffer.append( classpathEntry );
        }
    }

    stringBuffer.append( "\n" ) ;

  }

  public static void logSystemProperties() {
    LOGGER.info( getEnvironmentInformation() ) ;
  }


}
