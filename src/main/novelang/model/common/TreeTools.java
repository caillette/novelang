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

import java.util.Arrays;

import novelang.model.implementation.DefaultMutableTree;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

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

  public static boolean hasPreviousSibling( Treepath treepath ) throws IllegalArgumentException {
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

  public static boolean hasNextSibling( Treepath treepath ) throws IllegalArgumentException {
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
   * Returns the sibling on the left of the bottom of given {@link Treepath}.
   * <pre>
   * *t0               *t0
   *  |  \              |  \
   * t1  *t2    -->    *t1  t2
   * </pre>
   * @param treepath non-null object with minimum height of 2.
   * @return non-null object.
   */
  public static Treepath getPreviousSibling( Treepath treepath ) throws IllegalArgumentException {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum height of 2" ) ;
    }
    final Tree treeToMove = treepath.getBottom() ;
    final Tree parent = treepath.getTreeAtHeight( 1 ) ;
    for( int i = parent.getChildCount() - 1 ; i > 0 ; i-- ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return Treepath.create( treepath.getParent(), parent.getChildAt( i - 1 ) ) ;
      }
    }
    throw new IllegalArgumentException( "No previous sibling" ) ;
  }


  /**
   * Returns the sibling on the left of the bottom of given {@link Treepath}.
   * <pre>
   *    *t0               *t0
   *   /  |              /  |
   * *t1  t2    -->    t1  *t2
   * </pre>
   * @param treepath non-null object with minimum height of 2.
   * @return non-null object.
   */
  public static Treepath getNextSibling( Treepath treepath ) {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath must have minimum height of 2" ) ;
    }
    final Tree treeToMove = treepath.getBottom() ;
    final Tree parent = treepath.getTreeAtHeight( 1 ) ;
    for( int i = 0 ; i < parent.getChildCount() - 1 ; i++ ) {
      final Tree child = parent.getChildAt( i ) ;
      if( child == treeToMove ) {
        return Treepath.create( treepath.getParent(), parent.getChildAt( i + 1 ) ) ;
      }
    }
    throw new IllegalArgumentException( "No next sibling" ) ;
  }

  /**
   * Adds a sibling on the right of bottom of given {@link Treepath}.
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
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Minimum height is 2, got " + treepath.getHeight() ) ;
    }
    final Tree oldParent = treepath.getTreeAtHeight( 1 ) ;
    final MutableTree newparent = new DefaultMutableTree( oldParent.getText() ) ;
    for( int i = 0 ; i < oldParent.getChildCount() ; i++ ) {
      newparent.addChild( oldParent.getChildAt( i ) ) ;
    }
    newparent.addChild( tree ) ;
    return Treepath.create(
        updateBottom( treepath.getParent(), newparent ),
        treepath.getBottom()
    ) ;
  }

  /**
   * Adds a child on the right of bottom of given {@link Treepath}.
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
  public static Treepath addChildAtRight( Treepath treepath, Tree tree ) {
    if( treepath.getHeight() < 1 ) {
      throw new IllegalArgumentException( "Minimum height is 1, got " + treepath.getHeight() ) ;
    }
    final Tree oldParent = treepath.getBottom() ;
    final MutableTree newparent = new DefaultMutableTree( oldParent.getText() ) ;
    for( int i = 0 ; i < oldParent.getChildCount() ; i++ ) {
      newparent.addChild( oldParent.getChildAt( i ) ) ;
    }
    newparent.addChild( tree ) ;
    return Treepath.create( updateBottom( treepath, newparent ), tree ) ;
  }

  /**
   * Returns a {@link Treepath} corresponding to a change of a childhood at the
   * bottom of the given {@link Treepath}.
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
  public static Treepath updateBottom( Treepath treepath, Tree newTree ) {
    if( null == treepath.getParent() ) {
      return Treepath.create( newTree ) ;
    } else {
      final Treepath parentTreepath = treepath.getParent() ;
      final Tree newParent = reparent(
          parentTreepath.getBottom(), treepath.getBottom(), newTree ) ;
      return Treepath.create( updateBottom( parentTreepath, newParent ), newTree ) ;
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



  public static Treepath removeBottom( Treepath treepath ) {
    if( treepath.getHeight() < 2 ) {
      throw new IllegalArgumentException( "Treepath height must be 2 or more" ) ;
    }
    final Tree removed = treepath.getBottom() ;
    final Tree parentOfRemoved = treepath.getTreeAtHeight( 1 ) ;
    final MutableTree newTree = new DefaultMutableTree( parentOfRemoved.getText() ) ;
    for( int i = 0 ; i < parentOfRemoved.getChildCount() ; i++ ) {
      final Tree child = parentOfRemoved.getChildAt( i ) ;
      if( child != removed ) {
        newTree.addChild( child ) ;
      }
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
  public static Treepath moveLeftDown( Treepath targetTreepath ) throws IllegalArgumentException {

    final Tree moving = targetTreepath.getBottom() ;
    final Tree futureParent = getPreviousSibling( targetTreepath ).getBottom() ;

    final Treepath afterRemoval = removeBottom( targetTreepath ) ;
    return addChildAtRight( Treepath.create( afterRemoval, futureParent ), moving ) ;
  }


// ===============
// Immutable Trees
// ===============

  public static Tree tree( String text ) {
    return new ImmutableTree( text ) ;
  }

  public static Tree tree( String text, Tree... children ) {
    return new ImmutableTree( text, children ) ;
  }

  private static final Tree[] EMPTY_TREE_ARRAY = new Tree[ 0 ] ;

  private static final class ImmutableTree implements Tree {
    private final String text;
    private final Location location ;
    private final Tree[] children ;

    public ImmutableTree( String text ) {
      this.text = text ;
      location = null ;
      this.children = EMPTY_TREE_ARRAY ;
    }

    public ImmutableTree( String text, Location location ) {
      this.text = text ;
      this.location = location ;
      this.children = EMPTY_TREE_ARRAY ;
    }

    public ImmutableTree( String text, Tree[] children ) {
      this.text = text ;
      this.location = null ;
      this.children = children;
    }

    public ImmutableTree( String text, Location location, Tree[] children ) {
      this.text = text;
      this.location = location ;
      this.children = children;
    }

    public Tree getChildAt( int i ) {
      return children[ i ] ;
    }

    public int getChildCount() {
      return children.length ;
    }

    public Iterable< Tree > getChildren() {
      return Lists.immutableList( Arrays.asList( children ) ) ;
    }

    public String getText() {
      return text;
    }

    public String toStringTree() {
      return null;
    }

    public Location getLocation() {
      return location ;
    }

    public boolean isOneOf( NodeKind... kinds ) {
      if( NodeKind.rootHasNodeKindName( this ) ) {
        for( NodeKind kind : kinds ) {
          if( getText().equals( kind.name() ) ) {
            return true ;
          }
        }
      }
      return false ;
    }
  }


}
