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

package novelang.model.common;

import java.util.Iterator;

/**
 * @author Laurent Caillette
 */
public class IdentifierHelper {

  private IdentifierHelper() { }

  /**
   * Concatenates subtokens of a {@link novelang.model.common.NodeKind#IDENTIFIER}
   * (skipping the intermediary level defining what subtokens are made of,
   * like {@link novelang.model.common.NodeKind#WORD}s).
   *
   * @param tokenOwner a {@code Tree} containing all subtokens to create the identifier from.
   */
  public static String createIdentifier( SyntacticTree tokenOwner ) {
    if( NodeKind.IDENTIFIER.name() != tokenOwner.getText() ) {
      throw new IllegalArgumentException(
          "Token not of expected kind: " + tokenOwner.toStringTree() ) ;
    }
    final StringBuffer buffer = new StringBuffer() ;
    final Iterator<SyntacticTree> children = ( Iterator<SyntacticTree> )
        tokenOwner.getChildren().iterator();
    while( children.hasNext() ) {
      final SyntacticTree child = children.next() ;
      buffer.append( extractFirstChildText( child ) ) ;
      if( children.hasNext() ) {
        buffer.append( " " ) ;
       }
    }
    return buffer.toString() ;
  }

  public static String extractFirstChildText( SyntacticTree tree ) {
    return tree.getChildAt( 0 ).getText() ;
  }
}
