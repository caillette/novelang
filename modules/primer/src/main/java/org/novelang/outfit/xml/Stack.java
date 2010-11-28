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
package org.novelang.outfit.xml;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.novelang.outfit.CollectionTools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A stack of "buildup" objects (representing objects being built during XML parsing),
 * associating a "segment" to each stacked buildup, enforcing that stacked segments respect
 * a known order known as a "path".
 *
 * @param< SEGMENT >
 * @param< BUILDUP > 
 *
 * @author Laurent Caillette
 */
public class Stack< SEGMENT, BUILDUP > {

  private final ImmutableSet< ImmutableList< SEGMENT > > legalPaths ;
  private final Function< SEGMENT, String > pathElementToString ;

  public Stack(
      final ImmutableSet< ImmutableList< SEGMENT > > legalPaths,
      final Function< SEGMENT, String > pathElementToString
  ) {
    this.legalPaths = checkNotNull( legalPaths ) ;
    this.pathElementToString = checkNotNull( pathElementToString ) ;
  }

  private ImmutableList< Cell< SEGMENT, BUILDUP> > cells = ImmutableList.of() ;

  public void push( final SEGMENT segment, final BUILDUP buildup ) throws IllegalPathException {
    final ImmutableList< Cell< SEGMENT, BUILDUP> > newCells =
        CollectionTools.append( cells, new Cell< SEGMENT, BUILDUP>( segment, buildup ) ) ;
    final ImmutableList< SEGMENT > newPath = CollectionTools.append( getPath(), segment ) ;
    if( ! newPath.isEmpty() && legalPaths.contains( newPath ) ) {
      cells = newCells ;
    } else {
      throw new IllegalPathException( newPath, pathElementToString ) ;
    }
  }

  public void pop() {
    checkNotEmpty() ;
    cells = CollectionTools.removeLast( cells ) ;
  }

  public SEGMENT topSegment() {
    checkNotEmpty() ;
    final int lastIndex = cells.size() - 1;
    return cells.get( lastIndex ).segment ;
  }

  public BUILDUP getBuildupOnTop() {
    return getBuildupAtDepth( 0 ) ;
  }

  public BUILDUP getBuildupUnderTop() {
    return getBuildupAtDepth( 1 ) ;
  }

  public BUILDUP getBuildupAtDepth( final int depth ) {
    checkNotEmpty() ;
    final int index = cells.size() - 1 - depth ;
    return cells.get( index ).buildup ;
  }

  public void setTopBuildup( final BUILDUP buildup ) {
    checkNotEmpty() ;
    final ImmutableList.Builder< Cell< SEGMENT, BUILDUP > > newStackBuilder =
        ImmutableList.builder() ;
    for( int i = 0 ; i < cells.size() - 1 ; i ++ ) {
      newStackBuilder.add( cells.get( i ) ) ;
    }
    newStackBuilder.add( new Cell< SEGMENT, BUILDUP >( topSegment(), buildup ) ) ;
    cells = newStackBuilder.build() ;
  }

  private void checkNotEmpty() {
    if( isEmpty() ) {
      throw new IllegalStateException( "Empty stack" ) ;
    }
  }

  public boolean isEmpty() {
    return cells.isEmpty() ;
  }

  public ImmutableList< SEGMENT > getPath() {
    final ImmutableList.Builder< SEGMENT > builder = ImmutableList.builder() ;
    for( final Cell< SEGMENT, ? > cell : cells ) {
      builder.add( cell.segment ) ;
    }
    return builder.build() ;
  }

  public String getPathAsString() {
    return pathElementsAsString( getPath(), pathElementToString ) ;
  }

  private static< T > String pathElementsAsString(
      final ImmutableList< T > path,
      final Function< T, String > pathElementToString
  ) {
    return Joiner.on( "/" ).join( Iterables.transform( path, pathElementToString ) ) ;
  }

  /**
   * @author Laurent Caillette
   */
  public static class IllegalPathException extends Exception  {

    public < T > IllegalPathException(
        final ImmutableList< T > path,
        final Function< T , String> pathElementToString
    ) {
      super( "Not a legal path: " +
              "'" + pathElementsAsString( path, pathElementToString ) + "'" ) ;
    }
  }


  private static final class Cell< SEGMENT, BUILDUP > {
    private final SEGMENT segment ;
    private final BUILDUP buildup ;

    private Cell( final SEGMENT segment, final BUILDUP buildup ) {
      this.segment = checkNotNull( segment ) ;
      this.buildup = buildup ;
    }
  }

}
