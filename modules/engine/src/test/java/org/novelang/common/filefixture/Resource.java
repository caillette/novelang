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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * Represents a node which has a content addressable as a stream of bytes.
 * <p>
 * Design note: all methods for manipulating path names copy names from {@link FilenameUtils}.
 *
 * @author Laurent Caillette
 */
public final class Resource extends SchemaNode implements Comparable< Resource > {

  /*package*/ Resource( final String name ) {
    super( name ) ;
  }

  @Override
  public final Directory getRoot() {
    if( getParent() == null ) {
      return null ;
    } else {
      return getParent().getRoot() ;
    }
  }



  /**
   * Comparison occurs on {@link #getName()} as corresponding member is set at instantiation.
   */
  @Override
  public int compareTo( final Resource other ) {
    return getName().compareTo( other.getName() ) ;
  }

// ===============================================
// Fields set when interpreting class declarations
// ===============================================

  public String getBaseName() {
    return FilenameUtils.getBaseName( getAbsoluteResourceName() ) ;
  }

  public String getExtension() {
    return FilenameUtils.getExtension( getAbsoluteResourceName() ) ;
  }

  public String getPathNoEndSeparator() {
    return FilenameUtils.getPathNoEndSeparator( getAbsoluteResourceName() ) ;
  }

  public String getFullPath() {
    return FilenameUtils.getFullPath( getAbsoluteResourceName() ) ;
  }

  public InputStream getInputStream() {
    return getClass().getResourceAsStream( getAbsoluteResourceName() );
  }
    
  public byte[] getAsByteArray() {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
    try {
      IOUtils.copy( getInputStream(), outputStream ) ;
    } catch( IOException e ) {
      throw new Error( "Could not read stream for resource " + this, e ) ;
    }
    return outputStream.toByteArray() ;

  }
  public String getAsString( final Charset charset ) {
    return new String( getAsByteArray(), charset ) ;
  }
}
