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
package novelang.model.common;

import java.util.Iterator;

/**
 * @author Laurent Caillette
 */
public class IdentifierHelper {

  private IdentifierHelper() { }

  /**
   * Concatenates subtokens of a {@link novelang.model.common.PartTokens#SECTION_TITLE} (skipping the intermediary
   * level defining what subtokens are made of, like {@link novelang.model.common.PartTokens#WORD}s).
   *
   * @param tokenOwner a {@code Tree} containing all subtokens to create the identifier from.
   */
  public static String createIdentifier( Tree tokenOwner ) {
    final StringBuffer buffer = new StringBuffer() ;
    final Iterator< Tree > children = tokenOwner.getChildren().iterator() ;
    while( children.hasNext() ) {
      final Tree child = children.next() ;
      buffer.append( extractFirstChildText( child ) ) ;
      if( children.hasNext() ) {
        buffer.append( " " ) ;
       }
    }
    return buffer.toString() ;
  }

  public static String extractFirstChildText( Tree tree ) {
    return tree.getChildAt( 0 ).getText() ;
  }
}
