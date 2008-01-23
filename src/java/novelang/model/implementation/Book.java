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
import novelang.model.common.Locator;

/**
 * @author Laurent Caillette
 */
public class Book implements StructuralBook {

  private final List< Exception > structureCreationExceptions = Lists.newArrayList() ;
  private final List< String > parts = Lists.newArrayList() ;
  private final List< Chapter > chapters = Lists.newArrayList() ;

  private final String identifier;
  private final Logger logger ;
  private final BookContext context ;


  public Book( String identifier ) {
    this.identifier = Objects.nonNull( identifier ) ;
    logger = LoggerFactory.getLogger( Book.class.getName() + "#" + identifier ) ;

    context = new DefaultBookContext(
        "TODO:structureFileName",
        "book[" + this.identifier + "]"
    ) ;
  }


  public void addStructureParsingException( Exception ex ) {
    Objects.nonNull( ex ) ;
    structureCreationExceptions.add( ex ) ;
    logger.debug( "Added exception for structure parsing: {}", ex ) ;
  }

  public Iterable< Exception > getStructureParsingExceptions() {
    return Lists.immutableList( structureCreationExceptions ) ;
  }

  public void addPartReference( String partFileName ) {
    Objects.nonNull( partFileName ) ;
    parts.add( partFileName ) ;
    logger.debug( "Added part: {}", partFileName ) ;
  }

  public Chapter createChapter() {
    final int position = chapters.size();
    final Chapter chapter = new Chapter( context, position ) ;
    chapters.add( chapter ) ;
    logger.debug( "Created and added chapter: {}", chapter ) ;
    return chapter;
  }

  public Iterable< Chapter > getChapters() {
    return Lists.immutableList( chapters ) ;
  }

  private class DefaultBookContext implements BookContext {

    private final String structureFileName ;
    private final String name;


    public DefaultBookContext( String structureFileName, String bookName ) {
      this.structureFileName = structureFileName;
      this.name = bookName;
    }

    public Locator createStructureLocator( int line, int column ) {
      return new Locator( structureFileName, line, column ) ;
    }

    public Logger getLogger() {
      return logger ;
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
