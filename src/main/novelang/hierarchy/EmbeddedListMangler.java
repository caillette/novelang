/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang.hierarchy ;

import novelang.common.SyntacticTree ;
import novelang.common.tree.Treepath ;
import static novelang.parser.NodeKind.* ;
import com.google.common.base.Preconditions ;

/**
 * @author Laurent Caillette
 */
public class EmbeddedListMangler {
  
  /**
   * Rehierarchize embedded list items.
   */
  public static Treepath< SyntacticTree > rehierarchizeEmbeddedLists(
      Treepath< SyntacticTree > parent
  ) {


    throw new UnsupportedOperationException( "rehierarchizeLists" ) ;
  }
  
  

  /**
   * Returns the depth of a list item given its indentation.
   *  
   * @param tree a tree of {@link novelang.parser.NodeKind#LEVEL_INTRODUCER_} kind.
   * @return a number equal to or greater than 1
   */
  private static int getItemDepth( SyntacticTree tree ) {
    Preconditions.checkArgument( tree.isOneOf( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ ) ) ;
    Preconditions.checkArgument( tree.getChildCount() > 0 ) ;
    final SyntacticTree indentTree = tree.getChildAt( 0 ) ;
    Preconditions.checkArgument( indentTree.isOneOf( LEVEL_INTRODUCER_INDENT_ ) ) ;
    Preconditions.checkArgument( indentTree.getChildCount() == 1 ) ;
    final String indent = indentTree.getChildAt( 0 ).getText() ;
    Preconditions.checkArgument( indent.startsWith( "=" ) ) ;
    Preconditions.checkArgument( indent.length() > 1 ) ;
    return indent.length() - 1 ;
  }
  
}
