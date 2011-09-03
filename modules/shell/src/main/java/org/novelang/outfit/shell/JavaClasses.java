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
package org.novelang.outfit.shell;

import java.io.File;
import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents executable Java classes.
 *
 * @author Laurent Caillette
 */
public abstract class JavaClasses {

  private final ImmutableList< String > elements ;

  protected JavaClasses( final ImmutableList< String > elements ) {
    this.elements = checkNotNull( elements ) ;
    for( final String element : elements ) {
      checkArgument( ! StringUtils.isBlank( element ) , "Can't have blank element: %s", elements ) ;
    }
  }


  public final ImmutableList< String > asStringList() {
    return elements ;
  }


  public static class SingleJar extends JavaClasses {

    public SingleJar( final File jarFile ) {
      super( ImmutableList.of( "-jar", jarFile.getAbsolutePath() ) ) ;
      checkArgument( jarFile.isFile() ) ;
    }

  }

  public static class ClasspathAndMain extends JavaClasses {

    public ClasspathAndMain( final String mainClassName, final File... files ) {
      this( Arrays.asList( files ), mainClassName ) ;  
    }

    public ClasspathAndMain( final Iterable< File > files, final String mainClassName ) {
      super( ImmutableList.< String >builder()
          .add( "-cp" )
          .add( Joiner.on( File.pathSeparator )
              .join( Iterables.transform( files, FILES_TO_NAMES ) ) )
          .add( mainClassName )
          .build()
      ) ;
    }

    public ClasspathAndMain( final String mainClassName, final Iterable< String > fileNames ) {
      super( ImmutableList.< String >builder()
          .add( "-cp" )
          .add( Joiner.on( File.pathSeparator ).join( fileNames ) )
          .add( mainClassName )
          .build()
      ) ;
    }

  }

  private static final Function< File, String > FILES_TO_NAMES = new Function< File, String >() {
    @Override
    public String apply( final File from ) {
      return from.getAbsolutePath() ;
    }
  } ;


}
