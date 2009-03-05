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

import org.apache.commons.lang.StringUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Preconditions;

/**
 * @author Laurent Caillette
 */
public final class Directory implements Comparable< Directory > {
  
  private final String name ;
  private Class declaringClass ;
  private List< Resource > resources = null ;
  private List< Directory > directories = null ;
  private String underlyingResourcePath = null ;

  protected Directory( String name ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( name ) ) ;
    this.name = name;
  }

  public boolean isInitialized() {
    return null != declaringClass ;
  }

  public List< Directory > getSubdirectories() {
    if( null == directories ) {
      throw new IllegalStateException( "Not set: directories" ) ;
    }
    return directories ;
  }

  public List< Resource > getResources() {
    if( null == resources ) {
      throw new IllegalStateException( "No set: resources" ) ;
    }
    return resources ;
  }

  public String getName() {
    return name ;
  }

  public int compareTo( Directory other ) {
    return name.compareTo( other.getName() ) ;
  }
  
// ===============================================
// Fields set when interpreting class declarations
// ===============================================

  /*package*/ void setDirectories( List< Directory > directories ) {
    if( null != this.directories ) {
      throw new IllegalStateException( "Already set: directories" ) ;
    }
    this.directories = ImmutableList.copyOf( directories ) ;
  }

  /*package*/ void setResources( List< Resource > resources ) {
    if( null != this.resources ) {
      throw new IllegalStateException( "Already set: resources" ) ;
    }
    this.resources = ImmutableList.copyOf( resources )  ;
  }

  /*package*/ void setDeclaringClass( Class declaringClass ) {
    if( null != this.declaringClass ) {
      throw new IllegalStateException( "Already set: declaringClass" ) ;
    }
    this.declaringClass = declaringClass ;
  }

  /*package*/ void setUnderlyingResourcePath( String underlyingResourcePath ) {
    if( null != this.underlyingResourcePath ) {
      throw new IllegalStateException( "Already set: underlyingResourcePath" ) ;
    }
    this.underlyingResourcePath = underlyingResourcePath;
  }
}
