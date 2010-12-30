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
package org.novelang.outfit.loader;

import java.io.InputStream;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link ResourceLoader} made of a chain of {@link AbstractResourceLoader}s.
 * It tries all of them in sequence and throws a {@link ResourceNotFoundException}
 * referencing every place it tried.
 *
 * @author Laurent Caillette
 */
public class CompositeResourceLoader implements ResourceLoader {

  private final ImmutableList< AbstractResourceLoader > resourceLoaders ;

  public CompositeResourceLoader( final AbstractResourceLoader... resourceLoaders ) {
    this( ImmutableList.< AbstractResourceLoader >builder().add( resourceLoaders ).build() ) ;
  }

  public CompositeResourceLoader( final ImmutableList< AbstractResourceLoader > resourceLoaders ) {
    checkArgument( ! resourceLoaders.isEmpty() ) ;
    this.resourceLoaders = resourceLoaders ;
  }

  public CompositeResourceLoader(
      final ResourceLoader first,
      final ResourceLoader second
  ) {
    this( ImmutableList.< AbstractResourceLoader >builder()
        .addAll( explode( first ) )
        .addAll( explode( second ) )
        .build()
    ) ;
  }

  private static ImmutableList< AbstractResourceLoader > explode(
      final ResourceLoader resourceLoader
  ) {
    checkNotNull( resourceLoader ) ;
    if( resourceLoader instanceof CompositeResourceLoader ) {
      return ( ( CompositeResourceLoader ) resourceLoader ).resourceLoaders ;
    } else if( resourceLoader instanceof AbstractResourceLoader ) {
      return ImmutableList.of( ( AbstractResourceLoader ) resourceLoader ) ;
    } else {
      throw new IllegalArgumentException( "Unsupported: " + resourceLoader.getClass().getName() ) ;
    }
  }

  @Override
  public InputStream getInputStream( final ResourceName resourceName )
      throws ResourceNotFoundException
  {
    final ImmutableList.Builder< String > missesBuilder = ImmutableList.builder() ;
    for( final AbstractResourceLoader resourceLoader : resourceLoaders ) {
      final InputStream inputStream = resourceLoader.maybeGetInputStream( resourceName ) ;
      if( inputStream == null ) {
        missesBuilder.add( resourceLoader.toString() ) ;
      } else {
        return inputStream ;
      }
    }
    final ImmutableList< String > misses = missesBuilder.build() ;
    throw new ResourceNotFoundException( resourceName, Joiner.on( "\n" ).join( misses ) ) ;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() ;
  }

  public String getMultilineDescription() {
    final StringBuilder stringBuilder = new StringBuilder( this.toString() + ", made of:") ;
    for( final AbstractResourceLoader resourceLoader : resourceLoaders ) {
      stringBuilder.append( "\n  " ) ;
//      stringBuilder.append( resourceLoader.toString() ) ;
      stringBuilder.append( resourceLoader.getMultilineDescription() ) ;
    }
    return stringBuilder.toString() ;
  }
}
