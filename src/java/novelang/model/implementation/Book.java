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
import java.util.Collection;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.antlr.runtime.RecognitionException;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import novelang.model.structural.StructuralBook;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.weaved.IdentifierNotUniqueException;
import novelang.model.weaved.WeavedPart;
import novelang.model.weaved.WeavedBook;
import novelang.parser.PartParser;
import novelang.parser.StructureParser;
import novelang.parser.implementation.DefaultPartParserFactory;
import novelang.parser.implementation.DefaultStructureParserFactory;

/**
 * @author Laurent Caillette
 */
public class Book implements StructuralBook, WeavedBook {

  private static final Logger LOGGER = LoggerFactory.getLogger( Book.class ) ;

  private static final String CHARSET_NAME = "ISO-8859-1" ;
  private final Charset encoding;

  /**
   * TODO find a better name.
   */
  private final List< Exception > structureCreationExceptions = Lists.newArrayList() ;

  /**
   * This should be scoped to the method resolving generic Parts and loading Trees.
   * We don't need to keep Parts -- even using automatic chapter generation?
   */
  private final List< Part > parts = Lists.newArrayList() ;
  private final Multimap< String, Tree > allIdentifiers = Multimaps.newHashMultimap() ;
  private final List< Chapter > chapters = Lists.newArrayList() ;

  private final BookContext context ;
  private final File bookFile ;
  private static final String DEBUG = "Only_for_debugging_AntlrStructureParser_with_ANTLRWorks" ;


  /**
   * This constructor is supposed to be called only from
   * {@link novelang.parser.antlr.AntlrStructureParser} because it needs a non-null {@code Book}
   * instance for running inside ANTLRWorks debugger.
   * It should never be used otherwise.
   */
  public Book() {
    context = new DefaultBookContext(
        "book[" + DEBUG + "]", new File( DEBUG )
    ) ;
    bookFile = new File( DEBUG ) ;
    encoding = Charset.forName( CHARSET_NAME ) ;
  }

  public Book( String identifier, File bookFile ) {
    this.bookFile = Objects.nonNull( bookFile ) ;
    identifier = Objects.nonNull( identifier ) ;
    context = new DefaultBookContext( "book[" + identifier + "]", bookFile ) ;
    encoding = Charset.forName( CHARSET_NAME ) ;
    LOGGER.info( "Created {} referencing file {}", this, bookFile.getAbsolutePath() ) ;
  }

  public void collect( Exception ex ) {
    structureCreationExceptions.add( Objects.nonNull( ex ) ) ;
    LOGGER.debug( "Added exception for structure parsing: {} to {}", ex.getClass(), this ) ;
  }

  public Iterable< Exception > getStructureParsingExceptions() {
    return Lists.immutableList( structureCreationExceptions ) ;
  }

  public void loadStructure() throws IOException {

    final File localFile = context.relativizeFile( bookFile.getName() ) ;
    LOGGER.info( "Attempting to load file '{}' from {}", localFile.getAbsolutePath(), this ) ;

    try {
      final FileReader reader = new FileReader( localFile ) ;
      final String content = new String(
          IOUtils.toByteArray( reader, context.getEncoding().name() ) ) ;
      loadStructure( content ) ;
    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( e ) ;
    }

  }

  /**
   * For tests only.
   */
  protected void loadStructure( String text ) {
    final StructureParser parser = new DefaultStructureParserFactory()
        .createParser( this, text ) ;

    try {
      // Yeah we do it here!
      parser.parse() ;
    } catch( RecognitionException e ) {
      LOGGER.warn( "Could not parse file", e ) ;
      collect( e ) ;
    }
  }

  protected Tree getTree( String identifier ) {
    final Collection< Tree > trees = allIdentifiers.get( identifier ) ;
    if( 1 != trees.size() ) {
      throw new RuntimeException(
          "Internal inconsistency (forgot to call #gatherIdentifiers()?)" ) ;
    }
    return trees.iterator().next() ;
  }

  /**
   * TODO: Add a {@code void addGenericPart(...)} method for supporting wildcards.
   * It will be called at the end of Structure file parsing.
   * This {@code createPart} method should be called at Weaving time, after wildcard resolution.
   *
   * @param partFileName name of the Part file, relative to Structure file.
   * @param location location inside the Structure file.
   * @return a non-null Part object.
   */
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

  public void loadParts() {
    LOGGER.info( "Loading Parts for {}", this ) ;
    for( final WeavedPart part : parts ) {
      part.load() ;
      part.getIdentifiers() ;
    }
  }

  public void gatherIdentifiers() {
    LOGGER.info( "Gathering identifiers for {}", this ) ;
    allIdentifiers.clear() ;
    for( final Part part : parts ) {
      allIdentifiers.putAll( part.getIdentifiers() ) ;
    }
    for( final String identifier : allIdentifiers.keySet() ) {
      final Collection< Tree > trees = allIdentifiers.get( identifier ) ;
      if( trees.size() > 1 ) {
        final IdentifierNotUniqueException notUniqueException =
            new IdentifierNotUniqueException( identifier, trees ) ;
        collect( notUniqueException ) ;
        LOGGER.warn( "Same identifer found several times from {}\n{}",
            notUniqueException.getMessage() ) ;
      }
    }
    LOGGER.info( "Found {} identifiers in {}", allIdentifiers.keySet().size(), this ) ;
  }

  @Override
  public String toString() {
    return context.asString() + "@" + System.identityHashCode( this ) ;
  }

  public Location createLocation( int line, int column ) {
    return context.createStructureLocator( line, column ) ;
  }

  private class DefaultBookContext implements BookContext {

    private final File structureFile;
    private final String name;


    public DefaultBookContext( String bookName, File structureFile ) {
      this.structureFile = structureFile;
      this.name = bookName;
    }

    public Location createStructureLocator( int line, int column ) {
      return new Location( structureFile.getName(), line, column ) ;
    }

    public String asString() {
      return name;
    }

    public BookContext derive( String extension ) {
      return new DefaultBookContext( name + ":" + extension, structureFile ) ;
    }

    public File relativizeFile( String fileName ) {
      return new File( structureFile.getParentFile(), fileName ) ;
    }

    public Charset getEncoding() {
      return encoding ;
    }

  }
}
