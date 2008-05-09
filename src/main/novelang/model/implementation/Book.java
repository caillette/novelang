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

package novelang.model.implementation;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Collections;
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
import com.google.common.collect.Maps;
import novelang.model.structural.StructuralBook;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.common.MutableTree;
import novelang.model.common.NodeKind;
import novelang.model.common.Problem;
import novelang.model.common.TreeMetadata;
import novelang.model.common.MetadataHelper;
import novelang.model.weaved.IdentifierNotUniqueException;
import novelang.model.weaved.WeavedPart;
import novelang.model.weaved.WeavedBook;
import novelang.model.weaved.WeavedChapter;
import novelang.model.renderable.Renderable;
import novelang.parser.BookParser;
import novelang.parser.antlr.DefaultBookParserFactory;

/**
 * @author Laurent Caillette
 */
public class Book extends StyledElement implements StructuralBook, WeavedBook, Renderable {

  private static final Logger LOGGER = LoggerFactory.getLogger( Book.class ) ;

  /**
   * This should be scoped to the method resolving generic Parts and loading Trees.
   * We don't need to keep Parts -- even using automatic chapter generation?
   */
  private final List< Part > parts = Lists.newArrayList() ;
  private final Multimap< String, Tree > multipleTreesFromPartsByIdentifier = Multimaps.newHashMultimap() ;
  private final List< Chapter > chapters = Lists.newArrayList() ;

  private Tree tree ;
  private TreeMetadata treeMetadata ;

  private final File bookFile ;
  public static final String DEBUG = "Only_for_debugging_AntlrStructureParser_with_ANTLRWorks" ;

  public static final Charset DEFAULT_ENCODING;
  static {
    DEFAULT_ENCODING = Charset.forName( Element.CHARSET_NAME ) ;
  }

  public Book( String identifier, File bookFile ) {
    super(
        new DefaultBookContext( "book[" + identifier + "]", bookFile, DEFAULT_ENCODING ),
        new Location( bookFile.getAbsolutePath(), -1, -1 )
    ) ;
    this.bookFile = Objects.nonNull( bookFile ) ;
    identifier = Objects.nonNull( identifier ) ;
    LOGGER.info( "Created {} referencing file {}", this, bookFile.getAbsolutePath() ) ;
  }


  public Charset getEncoding() {
    return getContext().getEncoding() ;
  }

  public void loadStructure() {

    final File localFile = context.relativizeFile( bookFile.getName() ) ;
    LOGGER.info( "Attempting to load file '{}' from {}", localFile.getAbsolutePath(), this ) ;

    try {
      final FileReader reader = new FileReader( localFile ) ;
      final String content = new String(
          IOUtils.toByteArray( reader, context.getEncoding().name() ) ) ;
      loadStructure( content ) ;
    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
    }

  }

  public void load() {
    loadStructure() ;
    loadParts() ;
    gatherIdentifiers() ;
  }

  /**
   * For tests only.
   */
  protected void loadStructure( String text ) {
    final BookParser parser = new DefaultBookParserFactory()
        .createParser( this, text ) ;

    try {
      // Yeah we do it here!
      parser.parse() ;
    } catch( RecognitionException e ) {
      LOGGER.warn( "Could not parse file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
    }
  }

  protected Tree getTree( String identifier ) {
    final Collection< Tree > trees = multipleTreesFromPartsByIdentifier.get( identifier ) ;
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
    multipleTreesFromPartsByIdentifier.clear() ;
    for( final Part part : parts ) {
      multipleTreesFromPartsByIdentifier.putAll( part.getIdentifiers() ) ;
    }
    for( final String identifier : multipleTreesFromPartsByIdentifier.keySet() ) {
      final Collection< Tree > trees = multipleTreesFromPartsByIdentifier.get( identifier ) ;
      if( trees.size() > 1 ) {
        final IdentifierNotUniqueException notUniqueException =
            new IdentifierNotUniqueException( identifier, trees ) ;
        collect( Problem.createProblem( this, notUniqueException ) ) ;
        LOGGER.warn( "Same identifer found several times from {}\n{}",
            notUniqueException.getMessage() ) ;
      }
    }
    LOGGER.info(
        "Found {} identifiers in {}",
        multipleTreesFromPartsByIdentifier.keySet().size(),
        this
    ) ;
  }


  
// ==================
// Book tree creation
// ==================


  public Tree getTree() {
    if( null == tree ) {
      buildTree() ;
    }
    return tree ;
  }

  /**
   * {@code Chapter}s and their subelements feed a {@code MutableTree} using a map of {@code Tree}s
   * that was loaded by the {@code Part}s. Then this raw tree becomes a synthetic one after
   * all global enhancements like on speeches.
   */
  private void buildTree() {

    final Map< String, Tree > mutableIdentifiers = Maps.newHashMap() ;
    for( String identifier : multipleTreesFromPartsByIdentifier.keySet() ) {
      mutableIdentifiers.put(
          identifier, multipleTreesFromPartsByIdentifier.get( identifier ).iterator().next() ) ;
    }
    final Map< String, Tree > treesFromPartsByIdentifier =
        Collections.unmodifiableMap( mutableIdentifiers ) ;

    final MutableTree bookTree = new DefaultMutableTree( NodeKind._BOOK ) ;
    final Tree styleTree = getStyle() ;
    if( null != styleTree ) {
      bookTree.addChild( styleTree ) ;
    }
    for( final WeavedChapter chapter : chapters ) {
      bookTree.addChild( chapter.buildTree( treesFromPartsByIdentifier ) ) ;
    }

    treeMetadata = MetadataHelper.createMetadata( bookTree, getEncoding() ) ;

    tree = bookTree ;
  }

  public TreeMetadata getTreeMetadata() {
    if( null == treeMetadata ) {
      buildTree() ;
    }
    return treeMetadata ;
  }

// ===============
// Other utilities
// ===============

  @Override
  public String toString() {
    return context.asString() + "@" + System.identityHashCode( this ) ;
  }

  public Location createLocation( int line, int column ) {
    return context.createStructureLocator( line, column ) ;
  }

  private static class DefaultBookContext implements BookContext {

    private final File structureFile;
    private final String name;
    private final Charset encoding ;


    public DefaultBookContext( String bookName, File structureFile, Charset encoding ) {
      this.structureFile = structureFile;
      this.name = bookName;
      this.encoding = encoding ;
    }

    public Location createStructureLocator( int line, int column ) {
      return new Location( structureFile.getName(), line, column ) ;
    }

    public String asString() {
      return name;
    }

    public BookContext derive( String extension ) {
      return new DefaultBookContext( name + ":" + extension, structureFile, encoding ) ;
    }

    public File relativizeFile( String fileName ) {
      return new File( structureFile.getParentFile(), fileName ) ;
    }

    public Charset getEncoding() {
      return encoding ;
    }

  }
}
