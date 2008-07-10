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
package novelang.common.tree;

/**
 * Manipulation of immutable {@link Tree}s through {@link Treepath}s.
 * <p>
 * Convention for diagrams: the star (*) marks the {@link Tree}
 * objects referenced by the {@code Treepath} object.
 * The apostrophe (') marks new {@link Tree} objects
 * created for reflecting the new state of a logically "modified" Tree.
 *
 * @author Laurent Caillette
 */
public class TreepathTools {

  private TreepathTools() {
    throw new Error( "TreeTools" ) ;
  }

  /**
   * Returns true if given {@code Treepath} has a previous sibling, false otherwise.
   * @param treepath a non-null {@code Treepath} with a minimum length of 2.
   * @throws IllegalArgumentException
   * @see #getPreviousSibling(Treepath)
   */
  public static< T extends Tree > boolean hasPreviousSibling( Treepath< T > treepath )
      throws IllegalArgumentException
  {
    if( treepath.getLength() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum length of 2" ) ;
    }
    final Tree treeToMove = treepath.getTreeAtEnd() ;
    final Tree parent = treepath.getTreeAtDistance( 1 ) ;
    for( int i = parent.getChildCount() - 1 ; i > 0 ; i-- ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return true ;
      }
    }
    return false ;
  }

  /**
   * Returns true if given {@code Treepath} has a next sibling, false otherwise.
   * @param treepath a non-null {@code Treepath} with a minimum length of 2.
   * @throws IllegalArgumentException
   * @see #getNextSibling(Treepath)
   */
  public static< T extends Tree > boolean hasNextSibling( Treepath< T > treepath )
      throws IllegalArgumentException
  {
    if( treepath.getLength() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum length of 2" ) ;
    }
    final Tree treeToMove = treepath.getTreeAtEnd() ;
    final Tree parent = treepath.getTreeAtDistance( 1 ) ;
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
   * {@link Treepath}.
   * <pre>
   * *t0               *t0
   *  |  \              |  \
   * t1  *t2    -->    *t1  t2
   * </pre>
   * @param treepath non-null object with minimum length of 2.
   * @return non-null object.
   * @see #hasPreviousSibling(Treepath)
   */
  public static< T extends Tree > Treepath< T > getPreviousSibling(
      Treepath< T > treepath
  ) throws IllegalArgumentException
  {
    if( treepath.getLength() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum length of 2" ) ;
    }
    final T treeToMove = treepath.getTreeAtEnd() ;
    final T parent = treepath.getTreeAtDistance( 1 ) ;
    for( int i = parent.getChildCount() - 1 ; i > 0 ; i-- ) {
      final T child = ( T ) parent.getChildAt( i );
      if( child == treeToMove ) {
        return Treepath.create( treepath.getPrevious(), i - 1 ) ;
      }
    }
    throw new IllegalArgumentException( "No previous sibling" ) ;
  }


  /**
   * Returns the sibling on the left of the end of given {@link Treepath}.
   * <pre>
   *    *t0               *t0
   *   /  |              /  |
   * *t1  t2    -->    t1  *t2
   * </pre>
   * @param treepath non-null object with minimum length of 2.
   * @return non-null object.
   * @see #hasNextSibling(Treepath)
   */
  public static< T extends Tree > Treepath< T > getNextSibling( Treepath< T > treepath ) {
    if( treepath.getLength() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum length of 2" ) ;
    }
    final Tree treeToMove = treepath.getTreeAtEnd() ;
    final Tree parent = treepath.getTreeAtDistance( 1 ) ;
    for( int i = 0 ; i < parent.getChildCount() - 1 ; i++ ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return Treepath.create( treepath.getPrevious(), i + 1 ) ;
      }
    }
    throw new IllegalArgumentException( "No next sibling" ) ;
  }

  /**
   * Adds a sibling on the right of end of given {@link Treepath}.
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
  public static< T extends Tree > Treepath< T > addSiblingLast(
      Treepath< T > treepath,
      T tree
  ) {
    if( treepath.getLength() < 2 ) {
      throw new IllegalArgumentException( "Minimum length is 2, got " + treepath.getLength() ) ;
    }
    final T oldParent = treepath.getTreeAtDistance( 1 ) ;
    final T newParent = TreeTools.addLast( oldParent, tree ) ;

    return Treepath.create(
        replaceEnd( treepath.getPrevious(), newParent ),
        newParent.getChildCount() - 1
    ) ;
  }

  /**
   * Adds a first child to the end of given {@link Treepath}.
   * <pre>
   * *t0               *t0'
   *  |                 |
   * *t1    -->        *t1'
   *  |               / |
   *  t2         *new   t2 
   * </pre>
   *
   * @param treepath non-null object.
   * @param tree non-null object.
   * @return non-null {@code Treepath} referencing updated trees.
   *
   */
  public static < T extends Tree > Treepath< T > addChildFirst(
      Treepath< T > treepath,
      T tree
  ) {
    if( treepath.getLength() < 1 ) {
      throw new IllegalArgumentException( "Minimum length is 1, got " + treepath.getLength() ) ;
    }
    final T newParent = TreeTools.addFirst( treepath.getTreeAtEnd(), tree ) ;
    return Treepath.create( replaceEnd( treepath, newParent ), newParent.getChildCount() - 1 ) ;
  }

  /**
   * Adds a last child to the end of given {@link Treepath}.
   * <pre>
   * *t0            *t0'
   *  |              |
   * *t1    -->     *t1'
   *  |              |  \
   *  t2             t2  *new
   * </pre>
   *
   * @param treepath non-null object.
   * @param tree non-null object.
   * @return non-null {@code Treepath} referencing updated trees.
   *
   */
  public static < T extends Tree > Treepath< T > addChildLast(
      Treepath< T > treepath,
      T tree
  ) {
    if( treepath.getLength() < 1 ) {
      throw new IllegalArgumentException( "Minimum length is 1, got " + treepath.getLength() ) ;
    }
    final T newParent = TreeTools.addLast( treepath.getTreeAtEnd(), tree ) ;
    return Treepath.create( replaceEnd( treepath, newParent ), newParent.getChildCount() - 1 ) ;
  }

  /**
   * Returns a {@link Treepath} corresponding to a replacement of the end of the
   * given {@link Treepath}.
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
   * @return non-null {@code Treepath} with the same end referencing updated trees.
   *
   */
  public static< T extends Tree > Treepath< T > replaceEnd( Treepath< T > treepath, T newTree ) {
    if( null == treepath.getPrevious() ) {
      return Treepath.create( newTree ) ;
    } else {
      final Treepath< T > parentTreepath = treepath.getPrevious() ;
      final T newParent = TreeTools.replace(
          parentTreepath.getTreeAtEnd(),
          treepath.getIndexInPrevious(),
          newTree
      ) ;

      return Treepath.create(
          replaceEnd( parentTreepath, newParent ),
          treepath.getIndexInPrevious()
      ) ;
    }
  }

  /**
   * Removes the end of a given {@code Treepath}.
   *
   * @param treepath a non-null object with a minimum height of 2.
   * @return a {@code Treepath} referencing updated trees.
   */
  public static< T extends Tree > Treepath< T > removeEnd( Treepath< T > treepath ) {
    if( treepath.getLength() < 2 ) {
      throw new IllegalArgumentException( "Treepath length must be 2 or more" ) ;
    }

    final T removed = treepath.getTreeAtEnd() ;
    final T parentOfRemoved = treepath.getTreeAtDistance( 1 ) ;

    T newTree = null ;

    for( int i = 0 ; i < parentOfRemoved.getChildCount() ; i++ ) {
      final Tree child = parentOfRemoved.getChildAt( i ) ;
      if( child == removed ) {
        newTree = TreeTools.remove( parentOfRemoved, i ) ;
        break ;
      }
    }
    if( null == newTree ) {
      throw new Error( "Internal error: found no end" ) ;
    }
    return replaceEnd( treepath.getPrevious(), newTree ) ;
  }


  /**
   * Removes a {@code Tree} from its direct parent, and adds it as child of its former
   * previous sibling.
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
  public static < T extends Tree > Treepath< T > becomeLastChildOfPreviousSibling(
      Treepath< T > targetTreepath
  )
      throws IllegalArgumentException
  {
    final T moving = targetTreepath.getTreeAtEnd() ;
    final Treepath< T > previousSibling = getPreviousSibling( targetTreepath ) ;

    final Treepath< T > afterRemoval = removeEnd( targetTreepath ) ;
    return addChildLast(
        Treepath.create( afterRemoval, previousSibling.getIndexInPrevious() ),
        moving
    ) ;
  }



}