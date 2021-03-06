/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.treemangling.designator;

import java.util.List;

import org.novelang.common.Problem;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.designator.FragmentIdentifier;
import org.novelang.parser.NodeKind;
import org.novelang.parser.NodeKindTools;

/**
 * Given a {@link Treepath} to some identifier-bearing tree, extracts a single identifier
 * segment. The segment may be self-defining if the tree has a {@link NodeKind#ABSOLUTE_IDENTIFIER}
 * as immediate child, or may rely on other segments in the parents.
 * TODO: some cleanup as this seems to deal with defunct relative identifiers.
 *
 * @author Laurent Caillette
*/
public class SegmentExtractor
{
  private final IdentifierDefinition identifierDefinition ;
  private final String segment ;

  /**
   *
   * @param treepath Path to the node that may carry some (implicit or explicit) identifier.
   * @param problems a list of problems to add to one occurs.
   */
  public SegmentExtractor(
      final Treepath< SyntacticTree > treepath,
      final List< Problem > problems
  ) {

    final Treepath< SyntacticTree > pathToIdentifier =
        DesignatorTools.findPathToExplicitIdentifier( treepath ) ;
    final SyntacticTree tree = pathToIdentifier == null ?
            null :
            pathToIdentifier.getTreeAtEnd()
    ;

    if( NodeKindTools.is( NodeKind.ABSOLUTE_IDENTIFIER, tree ) ) {
      segment = extractSegment( tree ) ;
      identifierDefinition = IdentifierDefinition.ABSOLUTE ;
    } else {
      String markerText ;
      final SyntacticTree titleTree = DesignatorTools.findTitleTree( treepath.getTreeAtEnd() ) ;

      if( titleTree == null ) {
        markerText = null ;
      } else {
        try {
          markerText = DesignatorTools.getMarkerText( titleTree ) ;
        } catch( Exception e ) {
          // Exception type is a bit wide but it's due to the signature of getMarkerText
          // which delegates to a Renderer/Writer inside of which so many things can go wrong.
          // Not sure this is a Problem (user input), may be a technical exception at this stage.
          problems.add( Problem.createProblem( e ) ) ;
          markerText = null ;
        }
      }
      if( markerText == null ) {
        segment = null ;
        identifierDefinition = IdentifierDefinition.NONE ;
      } else {
        segment = markerText ;
        identifierDefinition = IdentifierDefinition.IMPLICIT ;
      }
    }
  }

  private static String extractSegment( final SyntacticTree identifierTree ) {
    return identifierTree.getChildAt( 0 ).getText() ;
  }

  public IdentifierDefinition getIdentifierDefinition() {
    return identifierDefinition ;
  }

  public String getSegment() {
    return segment ;
  }

  /*package*/ static FragmentIdentifier extract(
      final FragmentIdentifier parentIdentifier,
      final Treepath< SyntacticTree > pathToIdentifier
  ) {
    final SyntacticTree identifierTree = pathToIdentifier.getTreeAtEnd() ;
    if( NodeKind.ABSOLUTE_IDENTIFIER == identifierTree.getNodeKind() ) {
      return new FragmentIdentifier( extractSegment( identifierTree ) ) ;
    }
    return null ;
  }
}
