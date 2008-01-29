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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.antlr.runtime.RecognitionException;
import novelang.model.structural.StructuralPart;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.common.PartTokens;
import novelang.model.common.IdentifierHelper;
import novelang.model.weaved.WeavedPart;
import novelang.parser.PartParser;
import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @author Laurent Caillette
 */
public class Part extends Element implements StructuralPart, WeavedPart {

  private static final Logger LOGGER = LoggerFactory.getLogger( StyledElement.class ) ;
  private final String fileName ;
  private Tree tree ;

  public Part( BookContext context, String fileName, Location location ) {
    super( Objects.nonNull( context ).derive( "part[" + fileName + "]" ), location ) ;
    this.fileName = fileName ;
  }

  public Location getLocation() {
    return location;
  }

  private boolean loaded = false ;

  public void load() {
    if( loaded ) {
      throw new IllegalStateException( "Part already loaded" ) ;
    }
    loaded = true ;

    final File localFile = getContext().relativizeFile( fileName ) ;
    LOGGER.info( "Attempting to load file '{}' from {}", localFile.getAbsolutePath(), this ) ;

    try {
      final FileReader reader = new FileReader( localFile ) ;
      final String content = new String(
          IOUtils.toByteArray( reader, getContext().getEncoding().name() ) ) ;
      final PartParser parser = getContext().createParser( content ) ;
      try {

        // Yeah we get it here!
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
    return tree ;
  }


// ===========
// Identifiers
// ===========

  /**
   * Finds Section identifiers from inside the {@link #getTree() tree}.
   * At the first glance it seems better to do it from the grammar but
   * I'm not sure on how to concatenate tokens.
   * First I thought it would imply tons of ugly Java code embedded in the grammar,
   * implying a "lightweight"
   * version for having the {@code Part} member correctly initialized when using ANTLWorks
   * debugger (see what's done with the {@code Book} in {@code AntlrStructure.g}).
   * But the grammar could depend on a lightweight interface and create an instance
   * doing nothing by default, bypassing all dependencies to the {@code BookContext}.
   */
  public Multimap< String, Tree > getIdentifiers() {

    final Multimap< String, Tree > identifiedSectionTrees = Multimaps.newHashMultimap() ;

    for( final Tree sectionCandidate : tree.getChildren() ) {
      if( PartTokens.SECTION.name().equals( sectionCandidate.getText() ) ) {
        for( final Tree identifierCandidate : sectionCandidate.getChildren() ) {
          if( PartTokens.SECTION_IDENTIFIER.name().equals( identifierCandidate.getText() ) ) {
            final String identifier = IdentifierHelper.createIdentifier( identifierCandidate ) ;
            identifiedSectionTrees.put( identifier, sectionCandidate ) ;
            LOGGER.debug( "Recognized section identifier '{}' inside {}", identifier, this ) ;
          }
        }
      }
    }

    return Multimaps.newArrayListMultimap( identifiedSectionTrees ) ;
  }


}
