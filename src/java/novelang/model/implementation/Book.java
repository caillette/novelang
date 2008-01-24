/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.implementation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import novelang.model.structural.StructuralBook;
import novelang.model.common.Location;

/**
 * @author Laurent Caillette
 */
public class Book implements StructuralBook {

  private static final Logger LOGGER = LoggerFactory.getLogger( Book.class ) ;

  private final List< Exception > structureCreationExceptions = Lists.newArrayList() ;
  private final List< Part > parts = Lists.newArrayList() ;
  private final List< Chapter > chapters = Lists.newArrayList() ;

  private final String identifier;
  private final BookContext context ;


  public Book( String identifier ) {
    this.identifier = Objects.nonNull( identifier ) ;

    context = new DefaultBookContext(
        "todo structureFileName",
        "book[" + this.identifier + "]"
    ) ;

    LOGGER.info( "Created {}", context.asString() ) ;
  }


  public void addStructureParsingException( Exception ex ) {
    Objects.nonNull( ex ) ;
    structureCreationExceptions.add( ex ) ;
    LOGGER.debug( "Added exception for structure parsing: {} to {}", ex, this ) ;
  }

  public Iterable< Exception > getStructureParsingExceptions() {
    return Lists.immutableList( structureCreationExceptions ) ;
  }

  public Part createPart( String partFileName, Location location ) {
    Objects.nonNull( partFileName ) ;
    Objects.nonNull( location ) ;
    final Part part = new Part( context, partFileName, location ) ;
    parts.add( part ) ;
    LOGGER.debug( "Created and added {} from {}", part, part.getLocation() ) ;
    return part ;
  }

  public Chapter createChapter( Location location ) {
    final int position = chapters.size();
    final Chapter chapter = new Chapter( context, location, position ) ;
    chapters.add( chapter ) ;
    LOGGER.debug( "Created and added {} from {}", chapter, chapter.getLocation() ) ;
    return chapter;
  }

  public Iterable< Chapter > getChapters() {
    return Lists.immutableList( chapters ) ;
  }



  @Override
  public String toString() {
    return context.asString() + "@" + System.identityHashCode( this ) ;
  }

  public Location createStructuralLocator( int line, int column ) {
    return context.createStructureLocator( line, column ) ;
  }

  private class DefaultBookContext implements BookContext {

    private final String structureFileName ;
    private final String name;


    public DefaultBookContext( String structureFileName, String bookName ) {
      this.structureFileName = structureFileName;
      this.name = bookName;
    }

    public Location createStructureLocator( int line, int column ) {
      return new Location( structureFileName, line, column ) ;
    }

    public String asString() {
          return name;
        }

    public BookContext derive( String extension ) {
          return new DefaultBookContext(
              structureFileName,
              name + ":" + extension
          ) ;
        }
  }
}
