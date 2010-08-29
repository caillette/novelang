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
package org.novelang.outfit.shell;

/**
* Thrown by {@link JavaShell} when the process doesn't initialize properly,
 * preventing JMX connection.
*
* @author Laurent Caillette
*/
public class ProcessInitializationException extends Exception {

  public ProcessInitializationException( final String message, final Exception cause ) {
    super( message, cause ) ;
  }
  
}
