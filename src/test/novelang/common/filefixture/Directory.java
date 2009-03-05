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
package novelang.common.filefixture;

import java.util.List;

/**
 * @author Laurent Caillette
 */
public final class Directory {
  
  private final String name ;

  protected Directory( String name ) {
    this.name = name;
  }

  public List< Directory > getSubdirectories() {
    throw new UnsupportedOperationException( "getSubdirectories" ) ;
  }

  public List< Resource > getResources() {
    throw new UnsupportedOperationException( "getResources" ) ;
  }


  public String getName() {
    throw new UnsupportedOperationException( "getName" ) ;
  }
}
