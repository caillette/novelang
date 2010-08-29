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
package org.novelang.common.filefixture;

/**
 * @author Laurent Caillette
 */
public class DeclarationException extends Exception {

  public DeclarationException() {
  }

  public DeclarationException( final String message ) {
    super( message );
  }

  public DeclarationException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public DeclarationException( final Throwable cause ) {
    super( cause );
  }
}
