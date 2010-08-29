package org.novelang.common.tree;

import com.google.common.base.Preconditions;

/**
 * (Hopefully) useful implementations of {@link org.novelang.common.tree.Tree.Evolver}.
 *
 * @author Laurent Caillette
 */
public class EvolverTools {

  public static< T > Tree.Evolver< T > firstElementRemover() {
    return new RemoveFirst< T >() ;
  }

  public static< T > Tree.Evolver< T > lastElementRemover() {
    return new RemoveLast< T >() ;
  }

  public static< T > Tree.Evolver< T > elementAtIndexRemover( final int index ) {
    return new RemoveAtIndex< T >( index ) ;
  }

  public static class RemoveFirst< T > implements Tree.Evolver< T > {
    public T apply( final int index, final T original, final int listSize ) {
      return index <= 0 ? null : original ;
    }
  }

  public static class RemoveLast< T > implements Tree.Evolver< T > {
    public T apply( final int index, final T original, final int listSize ) {
      return index == listSize - 1 ? null : original ;
    }
  }

  public static class RemoveAtIndex< T > implements Tree.Evolver< T > {
    private final int indexForRemoval ;

    public RemoveAtIndex( final int indexForRemoval ) {
      Preconditions.checkArgument( indexForRemoval >= 0 ) ;
      this.indexForRemoval = indexForRemoval ;
    }

    public T apply( final int index, final T original, final int listSize ) {
      if( listSize < indexForRemoval + 1 ) {
        throw new IllegalArgumentException(
            "Expected to remove at index " + indexForRemoval + " but list size is " + listSize ) ;
      }
      return index == indexForRemoval ? null : original ;
    }
  }


}