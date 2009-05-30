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
package novelang.treemangling;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;

/**
 * @author Laurent Caillette
 */
public final class SeparatorsMangler {
  /**
   * Removes {@link novelang.parser.NodeKind#WHITESPACE_} and {@link novelang.parser.NodeKind#LINE_BREAK_}
   * tokens in order to ease comparison.
   */
  public static SyntacticTree removeSeparators( SyntacticTree tree ) {
    return removeSeparators( Treepath.create( tree ) ).getTreeAtEnd() ;
  }

  /**
   * Transforms {@link NodeKind#WHITESPACE_} and {@link NodeKind#LINE_BREAK_} nodes between
   * two blocks of literal into a {@link NodeKind#_ZERO_WIDTH_SPACE}.
   */
  public static Treepath< SyntacticTree > addMandatoryWhitespace(
      Treepath< SyntacticTree > treepath
  ) {
    throw new UnsupportedOperationException( "addMandatoryWhitespace" ) ;
  }


  public static Treepath< SyntacticTree > removeSeparators( Treepath< SyntacticTree > treepath ) {
    int index = 0 ;
    while( index < treepath.getTreeAtEnd().getChildCount() ) {
      final SyntacticTree child = treepath.getTreeAtEnd().getChildAt( index ) ;
      final Treepath< SyntacticTree > childTreepath = Treepath.create( treepath, index ) ;
      if( child.isOneOf( NodeKind.WHITESPACE_, NodeKind.LINE_BREAK_ ) ) {
        treepath = TreepathTools.removeEnd( childTreepath ) ;
      } else {
        treepath = removeSeparators( childTreepath ).getPrevious() ;
        index++ ;
      }
    }
    return treepath ;
  }


  private enum LITERAL_STATE {
    GRAVE_ACCENTS_1,
    GRAVE_ACCENT_PAIRS_1,
    SEPARATOR,
    GRAVE_ACCENTS_2,
    GRAVE_ACCENT_PAIRS_2
  }
}
