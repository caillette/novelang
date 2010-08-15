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
package novelang.system.shell;

import java.lang.management.ManagementFactory;

/**
 * Removes clutter from {@link JavaShell}.
 * @author Laurent Caillette
 */
public class JavaShellTools {

  private JavaShellTools() {
  }

  /**
   * Tries to obtain system process identifier of running JVM.
   *
   * @return {@value #UNKNOWN_PROCESS_ID} if not found, process identifier otherwise.
   *
   * @author from <a href="http://samuelsjoberg.com/archive/2006/12/jvm-pid" >Samuel Sjöberg</a>'s
   *     blog.
   */
  public static int extractProcessId( final String jvmName ) {
    final StringBuilder pid = new StringBuilder() ;
    for( int i = 0, l = jvmName.length()  ; i < l  ; i++ ) {
      if( Character.isDigit( jvmName.charAt( i ) ) ) {
        pid.append( jvmName.charAt( i ) ) ;
      } else if( pid.length() > 0 ) {
        break ;
      }
    }
    try {
      return Integer.parseInt( pid.toString() ) ;
    } catch( NumberFormatException ignored ) {
      return UNKNOWN_PROCESS_ID;
    }
  }

  public static final int UNDEFINED_PROCESS_ID = -1 ;
  public static final int UNKNOWN_PROCESS_ID = -2 ;

}
