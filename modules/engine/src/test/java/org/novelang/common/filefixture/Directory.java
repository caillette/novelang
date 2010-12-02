/*
 * Copyright (C) 2010 Laurent Caillette
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

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Laurent Caillette
 */
public final class Directory extends SchemaNode implements Comparable< Directory > {

  private List< Resource > resources = null ;
  private List< Directory > directories = null ;

  /*package*/ Directory( final String name ) {
    super( name );
  }

  public List< Directory > getSubdirectories() {
    if( null == directories ) {
      throw new IllegalStateException(
              "Not set: directories (did you initialize your test resources properly?)" ) ;
    }
    return directories ;
  }

  @Override
  public Directory getRoot() {
    if( getParent() == null ) {
      return this ;
    } else {
      return getParent().getRoot() ;
    }
  }

  public List< Resource > getResources() {
    if( null == resources ) {
      throw new IllegalStateException( "No set: resources" ) ;
    }
    return resources ;
  }


  /**
   * Comparison occurs on {@link #getName()} as corresponding member is set at instantiation.
   */
  @Override
  public int compareTo( final Directory other ) {
    return getName().compareTo( other.getName() ) ;
  }
  
// ===============================================
// Fields set when interpreting class declarations
// ===============================================

  /*package*/ void setDirectories( final List< Directory > directories ) {
    if( null != this.directories ) {
      throw new IllegalStateException( "Already set: directories" ) ;
    }
    this.directories = ImmutableList.copyOf( directories ) ;
  }

  /*package*/ void setResources( final List< Resource > resources ) {
    if( null != this.resources ) {
      throw new IllegalStateException( "Already set: resources" ) ;
    }
    this.resources = ImmutableList.copyOf( resources )  ;
  }


}
