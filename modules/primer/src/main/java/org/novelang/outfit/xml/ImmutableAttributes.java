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
package org.novelang.outfit.xml;

import java.util.Arrays;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import org.xml.sax.Attributes;

/**
 * An immutable implementation. Doesn't seem to exist elsewhere.
 * Strongly inspired by JDK's implementation (1.6.0_22).
 *
 * @author Laurent Caillette
 */
public class ImmutableAttributes implements Attributes {

  private final int length ;
  private final String[] data ;

  public ImmutableAttributes() {
    length = 0 ;
    data = null ;
  }

  public ImmutableAttributes( final Attributes others ) {
    if( others.getLength() > 0 ) {
      length = others.getLength() ;
      data = new String[ length * 5 ] ;
      for( int i = 0 ; i < length ; i++ ) {
        data[ i * 5 ] = others.getURI( i ) ;
        data[ i * 5 + 1 ] = others.getLocalName( i ) ;
        data[ i * 5 + 2 ] = others.getQName( i ) ;
        data[ i * 5 + 3 ] = others.getType( i ) ;
        data[ i * 5 + 4 ] = others.getValue( i ) ;
      }
    } else {
      length = 0 ;
      data = null ;
    }
  }

  private ImmutableAttributes( final ImmutableList< Attribute > attributes ) {
    if( attributes.isEmpty() ) {
      length = 0 ;
      data = null;
    } else {
      length = attributes.size();
      data = new String[ length * 5 ] ;
      final Iterator< Attribute > iterator = attributes.iterator() ;
      for( int i = 0 ; i < length ; i++ ) {
        final Attribute attribute = iterator.next() ;
        data[ i * 5 ] = attribute.uri ;
        data[ i * 5 + 1 ] = attribute.localName ;
        data[ i * 5 + 2 ] = attribute.qName ;
        data[ i * 5 + 3 ] = attribute.type ;
        data[ i * 5 + 4 ] = attribute.value ;
      }
    }
  }

  @Override
  public int getLength() {
    return length ;
  }

  @Override
  public String getURI( final int index ) {
    if( index >= 0 && index < length ) {
      return data[ index * 5 ] ;
    } else {
      return null ;
    }
  }

  @Override
  public String getLocalName( final int index ) {
    if( index >= 0 && index < length ) {
      return data[ index * 5 + 1 ];
    } else {
      return null;
    }
  }

  @Override
  public String getQName( final int index ) {
    if( index >= 0 && index < length ) {
      return data[ index * 5 + 2 ];
    } else {
      return null;
    }
  }

  @Override
  public String getType( final int index ) {
    if( index >= 0 && index < length ) {
      return data[ index * 5 + 3 ] ;
    } else {
      return null ;
    }
  }

  @Override
  public String getValue( final int index ) {
    if( index >= 0 && index < length ) {
      return data[ index * 5 + 4 ] ;
    } else {
      return null;
    }
  }

  @Override
  public int getIndex( final String uri, final String localName ) {
    final int max = length * 5 ;
    for( int i = 0 ; i < max ; i += 5 ) {
      if( data[ i ].equals( uri ) && data[ i + 1 ].equals( localName ) ) {
        return i / 5 ;
      }
    }
    return -1 ;
  }

  @Override
  public int getIndex( final String qName ) {
    final int max = length * 5;
    for( int i = 0 ; i < max ; i += 5 ) {
      if( data[ i + 2 ].equals( qName ) ) {
        return i / 5 ;
      }
    }
    return -1 ;
  }

  @Override
  public String getType( final String uri, final String localName ) {
    final int max = length * 5 ;
    for( int i = 0 ; i < max ; i += 5 ) {
      if( data[ i ].equals( uri ) && data[ i + 1 ].equals( localName ) ) {
        return data[ i + 3 ] ;
      }
    }
    return null ;
  }

  @Override
  public String getType( final String qName ) {
    final int max = length * 5 ;
    for( int i = 0 ; i < max ; i += 5 ) {
      if( data[ i + 2 ].equals( qName ) ) {
        return data[ i + 3 ] ;
      }
    }
    return null ;
  }

  @Override
  public String getValue( final String uri, final String localName ) {
    final int max = length * 5 ;
    for( int i = 0 ; i < max ; i += 5 ) {
      if( data[ i ].equals( uri ) && data[ i + 1 ].equals( localName ) ) {
        return data[ i + 4 ] ;
      }
    }
    return null ;
  }

  @Override
  public String getValue( final String qName ) {
    final int max = length * 5 ;
    for( int i = 0 ; i < max ; i += 5 ) {
      if( data[ i + 2 ].equals( qName ) ) {
        return data[ i + 4 ] ;
      }
    }
    return null ;
  }

  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
      return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final ImmutableAttributes that = ( ImmutableAttributes ) other;

    if( length != that.length ) {
      return false ;
    }
    if( ! Arrays.equals( data, that.data ) ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    int result = length ;
    result = 31 * result + ( data != null ? Arrays.hashCode( data ) : 0 ) ;
    return result ;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" + ( data == null ? "" : Arrays.asList( data ) ) + "}" ;
  }


  private static final class Attribute {
/*
    data[ i * 5 ] = others.getURI( i ) ;
    data[ i * 5 + 1 ] = others.getLocalName( i ) ;
    data[ i * 5 + 2 ] = others.getQName( i ) ;
    data[ i * 5 + 3 ] = others.getType( i ) ;
    data[ i * 5 + 4 ] = others.getValue( i ) ;
*/
    public final String uri ;
    public final String localName ;
    public final String qName ;
    public final String type ;
    public final String value ;

    private Attribute(
        final String uri,
        final String localName,
        final String qName,
        final String type,
        final String value
    ) {
      this.uri = uri ;
      this.localName = localName ;
      this.qName = qName ;
      this.type = type ;
      this.value = value ;
    }
  }

  public static class Builder {

    private final ImmutableList.Builder< Attribute > attributes = ImmutableList.builder() ;

    public Builder add(
        final String uri,
        final String localName,
        final String qName,
        final String type,
        final String value
    ) {
      attributes.add( new Attribute( uri, localName, qName, type, value ) ) ;
      return this ;
    }

    public ImmutableAttributes build() {
      return new ImmutableAttributes( attributes.build() ) ;
    }
  }

}
