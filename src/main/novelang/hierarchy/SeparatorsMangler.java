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
package novelang.hierarchy;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import com.google.common.base.Function;

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
  
  public static Function< ? super SyntacticTree, ? extends SyntacticTree > FUNCTION = 
      new Function< SyntacticTree, SyntacticTree >() {
        public SyntacticTree apply( SyntacticTree syntacticTree ) {
          return removeSeparators( syntacticTree ) ;
        }
      }
  ;
}
