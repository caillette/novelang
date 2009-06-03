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
import com.google.common.base.Function;

/**
 * A function that goes forward or backward across siblings of a {@code SyntacticTree}.
 *
 * @author Laurent Caillette
*/
/*package*/ interface SiblingTraverser
    extends Function< Treepath< SyntacticTree >, Treepath< SyntacticTree > >
{

  int getOffset() ;

  SiblingTraverser FORWARD = new SiblingTraverser() {
    public Treepath< SyntacticTree > apply( Treepath< SyntacticTree > treepath ) {
      if( TreepathTools.hasNextSibling( treepath ) ) {
        return TreepathTools.getNextSibling( treepath ) ;
      } else {
        return null ;
      }
    }

    public int getOffset() {
      return 1 ;
    }

  } ;

  SiblingTraverser BACKWARD = new SiblingTraverser() {
    public Treepath< SyntacticTree > apply( Treepath< SyntacticTree > treepath ) {
      if( TreepathTools.hasPreviousSibling( treepath ) ) {
        return TreepathTools.getPreviousSibling( treepath ) ;
      } else {
        return null ;
      }
    }

    public int getOffset() {
      return 0 ;
    }

  } ;
}
