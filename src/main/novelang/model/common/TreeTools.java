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

import novelang.model.implementation.DefaultMutableTree;
import com.google.common.base.Objects;

/**
 * Manipulation of immutable {@link Tree}s through {@link Treepath}s.
 * <p>
 * All algorithms here work if all {@link Tree}s are unique (the same {@link Tree}
 * object is the child of at most one other {@link Tree}.
 * Expect unwanted results otherwise.
 * <p>
 * Convention for diagrams: the star (*) marks the {@link Tree} objects referenced
 * by the {@code Treepath} object. The apostrophe (') marks new {@link Tree} objects
 * created for reflecting the new state of a logically "modified" Tree. 
 *
 * @author Laurent Caillette
 */
public class TreeTools {

  private TreeTools() {
    throw new Error( "TreeTools" ) ;
  }

  /**
   * Returns the sibling on the right of the end of given {@link Treepath}.
   * <pre>
   * *t0               *t0'
   *  |  \              |  \
   * *t1  t2    -->    t1' *t2
   * </pre>
   * @param treepath non-null object with minimum height of 2.
   * @return non-null if sibling was found, null otherwise.
   */
  public static Treepath getSiblingAtRight( Treepath treepath ) {
    throw new UnsupportedOperationException( "getSiblingAtRight" ) ;
  }

  /**
   * Returns the sibling on the right of the end of given {@link Treepath}.
   * <pre>
   * *t0          *t0'
   *  |            |  \
   * *t1    -->  *t1   t2
   * </pre>
   *
   * @param treepath non-null object.
   * @param tree non-null object.
   * @return non-null {@code Treepath} with the same end but with updated parents.
   *
   */
  public static Treepath addSiblingAtRight( Treepath treepath, Tree tree ) {
    throw new UnsupportedOperationException( "addSibling" ) ;
  }

  /**
   * Returns a {@link Treepath} corresponding to a change of a childhood at the
   * end of the given {@link Treepath}.
   * <pre>
   * *t0          *t0'
   *  |            |
   * *t1    -->   *t1'
   *  |            |
   * *old        *new
   * </pre>
   *
   * @param treepath non-null object.
   * @param newTree non-null object.
   * @return non-null {@code Treepath} with the same end but with updated parents.
   *
   */
  public static Treepath reparent( Treepath treepath, Tree newTree ) {
    if( null == treepath.getParent() ) {
      return Treepath.create( newTree ) ;
    } else {
      final Treepath parentTreepath = treepath.getParent() ;
      final Tree oldParent = parentTreepath.getEnd() ;
      final Tree newParent = reparent( oldParent, treepath.getEnd(), newTree ) ;
      return Treepath.create( reparent( parentTreepath, newParent ), newTree ) ;
    }
  }

  private static Tree reparent( Tree oldParent, Tree formerChild, Tree newChild ) {
    Objects.nonNull( formerChild ) ;
    Objects.nonNull( newChild ) ;
        
    final MutableTree newParent = new DefaultMutableTree( oldParent.getText() ) ;
    boolean found = false ;
    for( int i = 0 ; i < oldParent.getChildCount() ; i++ ) {
      final Tree child = oldParent.getChildAt( i ) ;
      if( formerChild == child ) {
        newParent.addChild( newChild ) ;
        found = true ;
      } else {
        newParent.addChild( child ) ;
      }
    }
    if( ! found ) {
      throw new IllegalArgumentException(
          "Not found: '" + formerChild + "' as child of '" + oldParent + "'" ) ;
    }
    return newParent ;
  }



  /**
   * Removes a {@code Tree} from its direct parent, and adds it as sibling of its
   * former parent.
   * <pre>
   * *t0           *t0'
   *  |             |  \
   * *t1    -->    t1' *t2
   *  |
   * *t2
   * </pre>
   *
   * @param treepath non-null, minimum depth of 2.
   * @return non-null object.
   */
  public static Nodepath liftUpRight( Treepath treepath ) {
    throw new UnsupportedOperationException( "liftUpRight" ) ;
  }

}
