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
package novelang.model.implementation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.antlr.runtime.RecognitionException;
import novelang.model.structural.StructuralPart;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.common.NodeKind;
import novelang.model.common.IdentifierHelper;
import novelang.model.common.LocationFactory;
import novelang.model.weaved.WeavedPart;
import novelang.model.renderable.Renderable;
import novelang.parser.PartParser;
import novelang.parser.PartParserFactory;
import novelang.parser.implementation.DefaultPartParserFactory;
import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @author Laurent Caillette
 */
public class Part
    extends Element 
    implements LocationFactory, StructuralPart, WeavedPart, Renderable
{

  private static final Logger LOGGER = LoggerFactory.getLogger( Part.class ) ;
  private final File partFile;
  private Tree tree ;
  private final String nameOfThis ;
  private final Charset encoding ;
  private final PartParserFactory partParserFactory ;

  /**
   * A constructor for creating a {@code Part} with no owning {@code Book}.
   *
   * @param partFile a non-null File object referencing a (hopefully) parseable Part.
   */
  public Part( File partFile ) {
    this(
        new Location( partFile.getAbsolutePath(), 0, 0 ),
        new DefaultPartParserFactory(),
        Element.DEFAULT_CHARSET,
        "part[" + partFile.getName() + "]",
        partFile
    ) ;
    this.load() ;
  }

  /**
   * The constructor that does the real job.
   *
   * @param locationInBook may be null, a non-null value will be inferred then.
   * @param partParserFactory
   * @param encoding
   * @param nameOfThis something to return from {@code toString()}.
   * @param partFile the real Part file on the filesystem.
   */
  protected Part(
      Location locationInBook,
      PartParserFactory partParserFactory,
      Charset encoding,
      String nameOfThis,
      File partFile
  ) {
    super( locationInBook ) ;
    this.partParserFactory = Objects.nonNull( partParserFactory ) ;
    this.encoding = Objects.nonNull( encoding ) ;
    this.nameOfThis = Objects.nonNull( nameOfThis ) + "@" + System.identityHashCode( this ) ;
    this.partFile = Objects.nonNull( partFile ) ;
  }

  public Part( BookContext context, String fileName, Location location ) {
    this(
        location,
        new DefaultPartParserFactory(),
        Element.DEFAULT_CHARSET,
        context.derive( "part[" + fileName + "]" ).asString(),
        context.relativizeFile( fileName )
    ) ;

  }

  private boolean loaded = false ;

  public void load() {
    if( loaded ) {
      throw new IllegalStateException( "Part already loaded" ) ;
    }
    loaded = true ;

    LOGGER.info( "Attempting to load file '{}' from {}", partFile.getAbsolutePath(), this ) ;

    try {
      final FileReader reader = new FileReader( partFile ) ;
      final String content = new String(
          IOUtils.toByteArray( reader, encoding.name() ) ) ;
      final PartParser parser = partParserFactory.createParser( this, content ) ;
      try {

        // Yeah we do it here!
        tree = parser.parse() ;

      } catch( RecognitionException e ) {
        LOGGER.warn( "Could not parse file", e ) ;
        collect( e ) ;
      }
    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( e ) ;
    }

  }

  public Tree getTree() {
    if( ! loaded ) {
      throw new IllegalStateException( "Part not loaded yet" ) ;
    }
    return tree ;
  }

  public Location createLocation( int line, int column ) {
    return new Location( partFile.getAbsolutePath(), line, column ) ;
  }

  public Charset getEncoding() {
    return encoding ;
  }

// ===========
// Identifiers
// ===========

  /**
   * Finds Section identifiers from inside the {@link #getTree() tree}.
   * At the first glance it seems better to do it from the grammar but
   * I'm not sure on how to concatenate tokens.
   * If I find how to do I should just add a Multimap member to the grammar file and
   * get it after parsing.
   */
  public Multimap< String, Tree > getIdentifiers() {

    if( ! loaded ) {
      throw new IllegalStateException( "Part not loaded yet" ) ;
    }

    final Multimap< String, Tree > identifiedSectionTrees = Multimaps.newHashMultimap() ;

    for( final Tree sectionCandidate : tree.getChildren() ) {
      if( NodeKind.SECTION.name().equals( sectionCandidate.getText() ) ) {
        for( final Tree identifierCandidate : sectionCandidate.getChildren() ) {
          if( NodeKind.IDENTIFIER.name().equals( identifierCandidate.getText() ) ) {
            final String identifier = IdentifierHelper.createIdentifier( identifierCandidate ) ;
            identifiedSectionTrees.put( identifier, sectionCandidate ) ;
            LOGGER.debug( "Recognized Section identifier '{}' inside {}", identifier, this ) ;
          }
        }
      }
    }

    return Multimaps.newArrayListMultimap( identifiedSectionTrees ) ;
  }


  @Override
  public String toString() {
    return nameOfThis ;
  }
}
