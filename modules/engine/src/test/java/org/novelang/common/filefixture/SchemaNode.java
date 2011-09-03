/*
 * Copyright (C) 2011 Laurent Caillette
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

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.novelang.outfit.loader.ResourceName;

/**
 * @author Laurent Caillette
 */
/*package*/ abstract class SchemaNode {

  private Directory parent ;
  private final String name ;
  private Class declaringClass ;
  private String underlyingResourcePath = null ;

  protected SchemaNode( final String name ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( name ) ) ;
    this.name = name;
  }

  public final Directory getParent() {
    return parent ;
  }


  /*package*/ final void setParent( final Directory parent ) {
    this.parent = parent ;
  }

  public abstract Directory getRoot() ;

  public final String getName() {
    return name ;
  }

  public final ResourceName getResourceName() {
    return new ResourceName( name ) ;
  }

  public boolean isInitialized() {
    return null != declaringClass ;
  }

  /*package*/ void setDeclaringClass( final Class declaringClass ) {
    if( null != this.declaringClass ) {
      throw new IllegalStateException( "Already set: declaringClass" ) ;
    }
    this.declaringClass = declaringClass ;
  }

  public String getAbsoluteResourceName() {
    if( null == underlyingResourcePath ) {
      throw new IllegalStateException( "not set: underlyingResource (did you initialize the resources?)" ) ;
    }
    return underlyingResourcePath;
  }

  public void setAbsoluteResourceName( final String absoluteResourceName ) {
    if( null != this.underlyingResourcePath ) {
      throw new IllegalStateException( "Already set: underlyingResourcePath" ) ;
    }
    this.underlyingResourcePath = absoluteResourceName ;
  }
}
