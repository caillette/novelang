package novelang.common.list;

/**
 * @author Laurent Caillette
 */
public class Relinkers {

  public static< T extends Cell< T > > Relinker< T > firstElementRemover() {
    return new RemoveFirst< T >() ;
  }

  public static< T extends Cell< T > > Relinker< T > lastElementRemover() {
    return new RemoveLast< T >() ;
  }

  public static class RemoveFirst< T extends Cell< T > > implements Relinker< T > {

    public int startsAt( final int listSize ) {
      return -1 ;
    }

    public T apply(
        final int listSize,
        final int index,
        final T original,
        final T lastKept
    ) {
      if( index <= 0 ) {
        return null ;
      } else {
        return original == null ? null : original.relink( lastKept ) ;
      }
    }
  }

  public static class RemoveLast< T extends Cell > implements Relinker< T > {

    public int startsAt( final int listSize ) {
      return listSize - 1 ;
    }

    public T apply(
        final int listSize,
        final int index,
        final T original,
        final T lastKept
    ) {
      if( index == listSize -1 ) {
        return null ; 
      } else {
        return null ; // Should happen only for trailing insertion placeholder.
      }
    }
  }


}
