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
package novelang.common.tree;

import com.google.common.base.Preconditions;
import novelang.common.SyntacticTree;

/**
 * This class is useful for side-effect oriented programming.
 * 
 * @author Laurent Caillette
 */
public class TreepathPreorderIterator< T extends Tree > {
  
  private Treepath< T > treepath ;

  public TreepathPreorderIterator( Treepath< T > treepath ) {
    Preconditions.checkNotNull( treepath ) ;
    this.treepath = treepath;
  }

  public T getNextTree() {
    throw new UnsupportedOperationException( "getNextTree" ) ;
  }
  
  public final boolean hasNextTree() {
    throw new UnsupportedOperationException( "hasNext" ) ;
  }
  
  public void tagForRemoval() {
    throw new UnsupportedOperationException( "tagForRemoval" ) ;
  }
  
  public void replace( T newTree ) {
    throw new UnsupportedOperationException( "replace" ) ;
  }

  public Treepath< T > getTreepath() {
    return treepath ;
  }
}
