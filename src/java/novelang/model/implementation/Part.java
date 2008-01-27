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
  private final Multimap< String, Tree > identifiedSectionTree = Multimaps.newHashMultimap() ;

  private Tree tree ;

  public Part( BookContext context, String fileName, Location location ) {
    super( Objects.nonNull( context ).derive( "part[" + fileName + "]" ), location ) ;
    this.fileName = fileName ;
  }

  public Location getLocation() {
    return location;
  }

  private boolean loaded = false ;

  public boolean load() {
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
      return true ;
    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( e ) ;
      return false ;
    }


  }

  public Tree getTree() {
    return tree ;
  }


// ===========
// Identifiers
// ===========

  private boolean identifiersFound ;

  /**
   * Finds Section identifiers from inside the {@link #getTree()}.
   * Doing it from inside the grammar would be more elegant but I'm not sure I'll know how
   * to concatenate tokens well.
   */
  public void findIdentifiers() {
    if( identifiersFound ) {
      throw new IllegalStateException( "Identifiers already found" ) ;
    }
    identifiersFound = true ;

    for( final Tree sectionCandidate : tree.getChildren() ) {
      if( PartTokens.SECTION.name().equals( sectionCandidate.getText() ) ) {
        for( final Tree identifierCandidate : sectionCandidate.getChildren() ) {
          if( PartTokens.SECTION_IDENTIFIER.name().equals( identifierCandidate.getText() ) ) {
            addSectionIdentifier(
                sectionCandidate,
                IdentifierHelper.createIdentifier( identifierCandidate )
            ) ;
          }
        }
      }
    }
  }

  private void addSectionIdentifier( Tree sectionTree, String identifier ) {
    identifiedSectionTree.put( identifier, sectionTree ) ;
    LOGGER.debug( "Added section identifier '{}' to {}", identifier, this ) ;
  }


}
