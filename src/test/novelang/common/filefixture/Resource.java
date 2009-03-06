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

import java.io.InputStream;

/**
 * @author Laurent Caillette
 */
public final class Resource extends SchemaNode implements Comparable< Resource > {

  protected Resource( String name ) {
    super( name ) ;
  }

  public String getUnderlyingResourcePath() {
    if( null == underlyingResourcePath ) {
      throw new IllegalStateException( "not set: underlyingResource" ) ;
    }
    return underlyingResourcePath;
  }

  /**
   * Comparison occurs on {@link #getName()} as corresponding member is set at instantiation.
   */
  public int compareTo( Resource other ) {
    return getName().compareTo( other.getName() ) ;
  }

  
// ===============================================
// Fields set when interpreting class declarations
// ===============================================

  private String underlyingResourcePath = null ;

  public void setUnderlyingResourcePath( String resourcePath ) {
    if( null != this.underlyingResourcePath ) {
      throw new IllegalStateException( "Already set: underlyingResourcePath" ) ;
    }
    this.underlyingResourcePath = resourcePath ;
  }

  public InputStream getInputStream() {
    return getClass().getResourceAsStream( getUnderlyingResourcePath() );
  }
}
