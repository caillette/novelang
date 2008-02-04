/*
 * Copyright (C) 2008 Laurent Caillette
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
package novelang.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.antlr.runtime.RecognitionException;

/**
 * @author Laurent Caillette
 */
public class StructureParserSupport {

  private final List< RecognitionException > exceptions = new ArrayList< RecognitionException >() ;
  private final List< Chapter > chapters = new ArrayList< Chapter >() ;
  private final java.util.ArrayList< String > parts = new ArrayList< String >() ;

	public Iterable< RecognitionException > getExceptions() {
	  return Collections.unmodifiableList( exceptions ) ;
	}

  public void addException( RecognitionException exception ) {
    exceptions.add( exception ) ;
  }

  public Iterable< String > getParts() {
    return Collections.unmodifiableList( parts ) ;
  }

  public void addPart( String part ) {
    parts.add( part ) ;
    System.out.println( "Added part: " + part ) ;
  }

  public void addChapter( Chapter chapter ) {
    chapters.add( chapter ) ;
    System.out.println( "Added chapter" ) ;
  }

  public Iterable< Chapter > getChapters() {
    return Collections.unmodifiableList( chapters ) ;
  }



  public static class Container {

    private String title = "" ;
    private String style = "" ;

    public String getTitle() {
      return title ;
    }

    public void setTitle( String title ) {
      System.out.println( "Title set for " + getClass().getName() ) ;
      this.title = title ;
    }

    public String getStyle() {
      return style ;
    }

    public void setStyle( String style ) {
      this.style = style ;
    }
  }

  public static class Chapter extends Container {

    private final List< Section > sections = new ArrayList< Section >() ;

    public void addSection( Section section ) {
      sections.add( section ) ;
      System.out.println( "Added section" ) ;
    }

    public Iterable< Section > getSections() {
      return Collections.unmodifiableList( sections ) ;
    }

  }

  public static class Section extends Container {

    private final List< Insertion > insertions = new ArrayList< Insertion >() ;

    public Iterable< Insertion > getInsertions() {
      return Collections.unmodifiableList( insertions ) ;
    }

  }

  public static class Insertion {

  }

}
