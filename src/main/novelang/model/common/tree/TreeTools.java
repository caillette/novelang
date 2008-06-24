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
package novelang.model.common.tree;

import com.google.common.base.Objects;

/**
 * Manipulation of immutable {@link novelang.model.common.tree.Tree}s through
 * {@link novelang.model.common.tree.Treepath}s.
 * <p>
 * All algorithms here work if all {@link novelang.model.common.tree.Tree}s are unique
 * (the same {@link novelang.model.common.tree.Tree}
 * object is the child of at most one other {@link novelang.model.common.tree.Tree}.
 * Expect unwanted results otherwise.
 * <p>
 * Convention for diagrams: the star (*) marks the {@link novelang.model.common.tree.Tree}
 * objects referenced by the {@code Treepath} object.
 * The apostrophe (') marks new {@link novelang.model.common.tree.Tree} objects
 * created for reflecting the new state of a logically "modified" Tree.
 *
 * @author Laurent Caillette
 */
public class TreeTools {

  private TreeTools() {
    throw new Error( "TreeTools" ) ;
  }
  

  public static< T extends Tree > boolean hasPreviousSibling(
      Treepath< T > treepath )
  throws IllegalArgumentException
  {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum height of 2" ) ;
    }
    final Tree treeToMove = treepath.getBottom() ;
    final Tree parent = treepath.getTreeAtHeight( 1 ) ;
    for( int i = parent.getChildCount() - 1 ; i > 0 ; i-- ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return true ;
      }
    }
    return false ;
  }

  public static< T extends Tree > boolean hasNextSibling(
      Treepath< T > treepath
  ) throws IllegalArgumentException
  {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum height of 2" ) ;
    }
    final Tree treeToMove = treepath.getBottom() ;
    final Tree parent = treepath.getTreeAtHeight( 1 ) ;
    for( int i = 0 ; i < parent.getChildCount() - 1 ; i ++ ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return true ;
      }
    }
    return false ;
  }

  /**
   * Returns the sibling on the left of the bottom of given
   * {@link novelang.model.common.tree.Treepath}.
   * <pre>
   * *t0               *t0
   *  |  \              |  \
   * t1  *t2    -->    *t1  t2
   * </pre>
   * @param treepath non-null object with minimum height of 2.
   * @return non-null object.
   */
  public static< T extends Tree > Treepath< T > getPreviousSibling(
      Treepath< T > treepath
  ) throws IllegalArgumentException
  {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum height of 2" ) ;
    }
    final T treeToMove = treepath.getBottom() ;
    final T parent = treepath.getTreeAtHeight( 1 ) ;
    for( int i = parent.getChildCount() - 1 ; i > 0 ; i-- ) {
      final T child = ( T ) parent.getChildAt( i );
      if( child == treeToMove ) {
        return Treepath.create( treepath.getParent(), ( T ) parent.getChildAt( i - 1 ) ) ;
      }
    }
    throw new IllegalArgumentException( "No previous sibling" ) ;
  }


  /**
   * Returns the sibling on the left of the bottom of given
   * {@link novelang.model.common.tree.Treepath}.
   * <pre>
   *    *t0               *t0
   *   /  |              /  |
   * *t1  t2    -->    t1  *t2
   * </pre>
   * @param treepath non-null object with minimum height of 2.
   * @return non-null object.
   */
  public static< T extends Tree > Treepath< T > getNextSibling( Treepath< T > treepath ) {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum height of 2" ) ;
    }
    final Tree treeToMove = treepath.getBottom() ;
    final Tree parent = treepath.getTreeAtHeight( 1 ) ;
    for( int i = 0 ; i < parent.getChildCount() - 1 ; i++ ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return Treepath.create( treepath.getParent(), ( T ) parent.getChildAt( i + 1 ) ) ;
      }
    }
    throw new IllegalArgumentException( "No next sibling" ) ;
  }

  /**
   * Adds a sibling on the right of bottom of given {@link novelang.model.common.tree.Treepath}.
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
  public static< T extends Tree > Treepath< T > addSiblingAtRight(
      Treepath< T > treepath,
      T tree
  ) {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Minimum height is 2, got " + treepath.getHeight() ) ;
    }
    final T oldParent = treepath.getTreeAtHeight( 1 ) ;
    final T newParent = ImmutableTree.addLast( oldParent, tree ) ;

    return Treepath.create(
        updateBottom( treepath.getParent(), newParent ),
        treepath.getBottom()
    ) ;
  }

  /**
   * Adds a child on the right of bottom of given {@link novelang.model.common.tree.Treepath}.
   * <pre>
   * *t0            *t0'
   *  |              |
   * *t1    -->     *t1'
   *                 |
   *                *new
   * </pre>
   *
   * @param treepath non-null object.
   * @param tree non-null object.
   * @return non-null {@code Treepath} including added tree.
   *
   */
  public static < T extends Tree > Treepath< T > addChildAtRight(
      Treepath< T > treepath,
      T tree
  ) {
    if( treepath.getHeight() < 1 ) {
      throw new IllegalArgumentException( "Minimum height is 1, got " + treepath.getHeight() ) ;
    }
    final T newParent = ImmutableTree.addLast( treepath.getBottom(), tree ) ;
    return Treepath.create( updateBottom( treepath, newParent ), tree ) ;
  }

  /**
   * Returns a {@link novelang.model.common.tree.Treepath}
   * corresponding to a change of a childhood at the bottom of the given
   * {@link novelang.model.common.tree.Treepath}.
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
  public static< T extends Tree > Treepath< T > updateBottom( Treepath< T > treepath, T newTree ) {
    if( null == treepath.getParent() ) {
      return Treepath.create( newTree ) ;
    } else {
      final Treepath parentTreepath = treepath.getParent() ;
      final Tree newParent = reparent(
          parentTreepath.getBottom(), treepath.getBottom(), newTree ) ;
      return Treepath.create( updateBottom( parentTreepath, newParent ), newTree ) ;
    }
  }

  /**
   * TODO fix this as a Tree supports having multiple children referencing the same object.
   */
  private static< T extends Tree > T reparent( T oldParent, T formerChild, T newChild ) {
    Objects.nonNull( formerChild );
    Objects.nonNull( newChild );

    T newParent = null ;
    for( int i = 0; i < oldParent.getChildCount() ; i++ ) {
      final T child = ( T ) oldParent.getChildAt( i ) ;
      if( formerChild == child ) {
        final T newParentNoChild = ImmutableTree.remove( oldParent, i ) ;
        newParent = ImmutableTree.addLast( newParentNoChild, newChild ) ;
      }
    }
    if( null == newParent ) {
      throw new IllegalArgumentException(
          "Not found: '" + formerChild + "' as child of '" + oldParent + "'" );
    }
    return newParent;
  }



  public static< T extends Tree > Treepath< T > removeBottom( Treepath< T > treepath ) {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath height must be 2 or more" ) ;
    }

    final T removed = treepath.getBottom() ;
    final T parentOfRemoved = treepath.getTreeAtHeight( 1 ) ;

    T newTree = null ;

    for( int i = 0 ; i < parentOfRemoved.getChildCount() ; i++ ) {
      final Tree child = parentOfRemoved.getChildAt( i ) ;
      if( child == removed ) {
        newTree = ImmutableTree.remove( parentOfRemoved, i ) ;
        break ;
      }
    }
    if( null == newTree ) {
      throw new Error( "Internal error: found no bottom" ) ;
    }
    return updateBottom( treepath.getParent(), newTree ) ;
  }


  /**
   * Removes a {@code Tree} from its direct parent, and adds it as child of its
   * former previous sibling.
   * <pre>
   * *t0              *t0'
   *  |  \             |
   * t1  *t2    -->   *t1'
   *                   |
   *                  *t2
   * </pre>
   *
   * @param targetTreepath non-null, minimum depth of 2.
   * @return non-null object representing path to moved {@code Tree}.
   * @throws IllegalArgumentException if there was no previous sibling.
   */
  public static < T extends Tree > Treepath< T > moveLeftDown( Treepath< T > targetTreepath )
      throws IllegalArgumentException
  {
    final T moving = targetTreepath.getBottom() ;
    final T futureParent = getPreviousSibling( targetTreepath ).getBottom() ;

    final Treepath< T > afterRemoval = removeBottom( targetTreepath ) ;
    return addChildAtRight( Treepath.create( afterRemoval, futureParent ), moving ) ;
  }



// ===============
// Immutable Trees
// ===============

//  public static< T extends Tree > tree( String text ) {
//    return new SimpleTree( text ) ;
//  }

//  public static Tree tree( String text, Tree... children ) {
//    return new SimpleTree( text, children ) ;
//    return new SimpleTree( text, children ) ;
//  }

//  private static final Tree[] EMPTY_TREE_ARRAY = new Tree[ 0 ] ;


}