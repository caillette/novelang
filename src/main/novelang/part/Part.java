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

package novelang.part;

import java.io.File;
import java.nio.charset.Charset;
import java.net.URL;
import java.net.MalformedURLException;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.ImmutableMultimap;
import novelang.common.IdentifierHelper;
import novelang.parser.NodeKind;
import novelang.common.tree.Treepath;
import novelang.common.SyntacticTree;
import novelang.common.AbstractSourceReader;
import novelang.common.StylesheetMap;
import novelang.parser.Encoding;
import novelang.parser.antlr.DefaultPartParserFactory;
import novelang.hierarchy.Hierarchizer;

/**
 * A Part loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Part extends AbstractSourceReader {

  private final SyntacticTree tree ;
  private final Multimap< String, SyntacticTree> identifiers ;
  private final boolean standalone;


  public Part( String content ) {
    this( content, false ) ; // Would be logical to make it true but would pollute the tests.
  }

  public Part( String content, boolean standalone ) {
    this.standalone = standalone ; 
    tree = createTree( content ) ;
    identifiers = findIdentifiers() ;
  }

  private SyntacticTree createTree( String content ) {
    final SyntacticTree rawTree = parse( new DefaultPartParserFactory(), content ) ;
    if( null == rawTree || hasProblem() ) {
      return null ;
    } else {
      final Treepath< SyntacticTree > rehierarchized1 =
          Hierarchizer.rehierarchizeDelimiters2To3( Treepath.create( rawTree ) ) ;
      if( standalone ) {
        return addMetadata( Hierarchizer.rehierarchizeLists( rehierarchized1 ).getTreeAtEnd() ) ;
      } else {
        return rehierarchized1.getTreeAtEnd() ;
      }
    }
  }

  public Part( final File partFile ) throws MalformedURLException {
    this( partFile, false ) ;
  }

  public Part( final File partFile, boolean standalone ) throws MalformedURLException {
    this(
        partFile.toURI().toURL(),
        Encoding.DEFAULT,
        "part[" + partFile.getName() + "]",
        standalone
    ) ;

  }

  protected Part(
      URL partUrl,
      Charset encoding,
      String thisToString,
      boolean standalone
  ) {
    super( partUrl, encoding, thisToString ) ;
    this.standalone = standalone ;
    tree = createTree( readContent( partUrl, encoding ) ) ;
    identifiers = findIdentifiers() ;
  }

  public StylesheetMap getCustomStylesheetMap() {
    return StylesheetMap.EMPTY_MAP ;
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
      return ImmutableMultimap.empty() ;  
    }

    for( final SyntacticTree sectionCandidate : tree.getChildren() ) {
      if( NodeKind.DELIMITER_TWO_EQUAL_SIGNS_.name().equals( sectionCandidate.getText() ) ) {
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
