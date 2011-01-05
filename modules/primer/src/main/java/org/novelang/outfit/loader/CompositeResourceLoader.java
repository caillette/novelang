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

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link ResourceLoader} made of multiple {@link AbstractResourceLoader}s.
 * It tries all of them in sequence and throws a {@link ResourceNotFoundException}
 * referencing every place it tried.
 * <p>
 * The {@link org.novelang.outfit.loader.ClasspathResourceLoader}s are always tried last.
 *
 * @author Laurent Caillette
 */
public class CompositeResourceLoader implements ResourceLoader {

  private final ImmutableList< AbstractResourceLoader > preferredResourceLoaders;
  private final ImmutableList< ClasspathResourceLoader > classpathResourceLoaders ;

  public CompositeResourceLoader( final AbstractResourceLoader... resourceLoaders ) {
    this( ImmutableList.< AbstractResourceLoader >builder().add( resourceLoaders ).build() ) ;
  }

  /**
   * Trying to load from {@link AbstractResourceLoader}s in the order of the list.
   * 
   * @param resourceLoaders a non-null, non-empty {@code List}.
   */
  public CompositeResourceLoader( final ImmutableList< AbstractResourceLoader > resourceLoaders ) {
    checkArgument( ! resourceLoaders.isEmpty() ) ;
    final ImmutableList.Builder< AbstractResourceLoader > preferredResourceLoaderBuilder =
        ImmutableList.builder() ;
    final ImmutableList.Builder< ClasspathResourceLoader > classpathResourceLoaderBuilder =
        ImmutableList.builder() ;
    for( final AbstractResourceLoader resourceLoader : resourceLoaders ) {
      if( resourceLoader instanceof ClasspathResourceLoader ) {
        classpathResourceLoaderBuilder.add( ( ClasspathResourceLoader ) resourceLoader ) ;
      } else {
        preferredResourceLoaderBuilder.add( resourceLoader ) ;
      }
    }
    preferredResourceLoaders = preferredResourceLoaderBuilder.build() ;
    classpathResourceLoaders = classpathResourceLoaderBuilder.build() ;
  }

  /*package*/ ImmutableList< AbstractResourceLoader > getAll() {
    return new ImmutableList.Builder< AbstractResourceLoader >()
        .addAll( preferredResourceLoaders )
        .addAll( classpathResourceLoaders )
        .build()
    ;
  }

  /**
   * First {@link AbstractResourceLoader}s tried first.
   */
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
      return ( ( CompositeResourceLoader ) resourceLoader ).getAll() ;
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
    InputStream inputStream ;

    inputStream = tryResourceLoaders( preferredResourceLoaders, missesBuilder, resourceName ) ;
    if( inputStream != null ) {
      return inputStream ;
    }
    inputStream = tryResourceLoaders( classpathResourceLoaders, missesBuilder, resourceName ) ;
    if( inputStream != null ) {
      return inputStream ;
    }

    final ImmutableList< String > misses = missesBuilder.build() ;
    throw new ResourceNotFoundException( resourceName, Joiner.on( "\n" ).join( misses ) ) ;
  }

  private static InputStream tryResourceLoaders(
      final ImmutableList< ? extends AbstractResourceLoader > resourceLoaders,
      final ImmutableList.Builder< String > missesBuilder,
      final ResourceName resourceName
  ) {
    for( final AbstractResourceLoader resourceLoader : resourceLoaders ) {
      final InputStream inputStream = resourceLoader.maybeGetInputStream( resourceName ) ;
      if( inputStream == null ) {
        missesBuilder.add( resourceLoader.toString() ) ;
      } else {
        return inputStream ;
      }
    }
    return null ;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() ;
  }

  public String getMultilineDescription() {
    final StringBuilder stringBuilder =
        new StringBuilder( this.toString() + ", searching (in order):" ) ;
    for( final AbstractResourceLoader resourceLoader : getAll() ) {
      stringBuilder.append( "\n  " ) ;
//      stringBuilder.append( resourceLoader.toString() ) ;
      stringBuilder.append( resourceLoader.getMultilineDescription() ) ;
    }
    return stringBuilder.toString() ;
  }


  public static CompositeResourceLoader create(
      final String resourcePath,
      final File... directories
  ) {
    final ImmutableList.Builder< UrlResourceLoader > urlResourceLoaders = ImmutableList.builder() ;
    for( final File directory : directories ) {
      try {
        urlResourceLoaders.add( new UrlResourceLoader( directory.toURI().toURL() ) ) ;
      } catch( MalformedURLException e ) {
        throw new RuntimeException( e ) ;
      }
    }
    return create( resourcePath, urlResourceLoaders.build() ) ;
  }

  public static CompositeResourceLoader create(
      final String resourcePath,
      final ImmutableList< UrlResourceLoader > urlResourceLoaders
  ) {
    final ImmutableList.Builder< AbstractResourceLoader > resourceLoaders =
        ImmutableList.builder() ;
    resourceLoaders.addAll( urlResourceLoaders ) ;
    resourceLoaders.add( new ClasspathResourceLoader( resourcePath ) ) ;
    return new CompositeResourceLoader( resourceLoaders.build() ) ;
  }

}
