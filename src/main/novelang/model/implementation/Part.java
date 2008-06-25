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

import java.io.File;
import java.nio.charset.Charset;
import java.net.URL;
import java.net.MalformedURLException;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ListMultimap;
import novelang.model.common.IdentifierHelper;
import novelang.model.common.NodeKind;
import novelang.model.common.tree.Treepath;
import novelang.model.common.SyntacticTree;
import static novelang.model.common.NodeKind.SECTION;
import novelang.parser.Encoding;
import novelang.parser.antlr.DefaultPartParserFactory;
import novelang.reader.AbstractSourceReader;

/**
 * A Part loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Part extends AbstractSourceReader {

  private final SyntacticTree tree ;
  private final Multimap< String, SyntacticTree> identifiers ;


  public Part( String content ) {
    tree = createTree( content ) ;
    identifiers = findIdentifiers() ;
  }

  private SyntacticTree createTree( String content ) {
    final SyntacticTree rawTree = parse( new DefaultPartParserFactory(), content ) ;
    if( null == rawTree ) {
      return null ;
    } else {
      return Hierarchizer.rehierarchize( Treepath.create( rawTree ) ).getTreeAtEnd() ;
    }
  }

  public Part( final File partFile ) throws MalformedURLException {
    this(
        partFile.toURL(), 
        Encoding.DEFAULT,
        "part[" + partFile.getName() + "]"
    ) ;
  }


  protected Part(
      URL partUrl,
      Charset encoding,
      String thisToString
  ) {
    super( partUrl, encoding, thisToString ) ;
    tree = createTree( readContent( partUrl, encoding ) ) ;
    identifiers = findIdentifiers() ;
  }


// ==============
// Content access
// ==============

  /**
   * Returns result of parsing, may be null if it failed.
   * @return a possibly null object.
   */
  public SyntacticTree getDocumentTree() {
    return tree ;
  }


// ===========
// Identifiers
// ===========

  /**
   * Finds Section identifiers from inside the {@link #getDocumentTree() tree}.
   * At the first glance it seems better to do it from the grammar but
   * I'm not sure on how to concatenate tokens.
   * If I find how to do I should just add a Multimap member to the grammar file and
   * get it after parsing.
   */
  private Multimap< String, SyntacticTree> findIdentifiers() {

    final Multimap< String, SyntacticTree> identifiedSectionTrees = Multimaps.newHashMultimap() ;

    if( null == tree ) {
      return Multimaps.immutableMultimap() ;  
    }

    for( final SyntacticTree sectionCandidate : tree.getChildren() ) {
      if( SECTION.name().equals( sectionCandidate.getText() ) ) {
        for( final SyntacticTree identifierCandidate : sectionCandidate.getChildren() ) {
          if( NodeKind.IDENTIFIER.name().equals( identifierCandidate.getText() ) ) {
            final String identifier = IdentifierHelper.createIdentifier( identifierCandidate ) ;
            identifiedSectionTrees.put( identifier, sectionCandidate ) ;
            LOGGER.debug( "Recognized Section identifier '{}' inside {}", identifier, this ) ;
          }
        }
      }
    }

    final ListMultimap< String, SyntacticTree> identifiersFound =
        Multimaps.newArrayListMultimap( identifiedSectionTrees ) ;
    return Multimaps.unmodifiableListMultimap( identifiersFound ) ;
  }

  public Multimap< String, SyntacticTree> getIdentifiers() {
    return identifiers ;
  }


}
