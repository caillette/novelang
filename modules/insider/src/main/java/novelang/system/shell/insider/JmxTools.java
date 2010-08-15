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
package novelang.system.shell.insider;

import java.lang.management.ManagementFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author Laurent Caillette
 */
public class JmxTools {

  private JmxTools() { }

  public static ObjectName getObjectNameQuiet( final String name ) {
    try {
      return new ObjectName( name ) ;
    } catch( MalformedObjectNameException e ) {
      throw new RuntimeException( e ) ;
    }
  }


  /**
   * Tries to obtain system process identifier of running JVM.
   *
   * @return -1 if not found, process identifier otherwise.
   *
   * @author from <a href="http://samuelsjoberg.com/archive/2006/12/jvm-pid" >Samuel Sjöberg</a>'s
   *     blog.
   */
  public static int getProcessId() {
    final String name = ManagementFactory.getRuntimeMXBean().getName() ;
    final StringBuilder pid = new StringBuilder() ;
    for( int i = 0, l = name.length()  ; i < l  ; i++ ) {
      if( Character.isDigit( name.charAt( i ) ) ) {
        pid.append( name.charAt( i ) ) ;
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
