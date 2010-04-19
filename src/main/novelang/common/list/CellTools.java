package novelang.common.list;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Does things with {@link Cell}.
 *
 * @author Laurent Caillette
 */
public class CellTools {

  private CellTools() { }

  public static< T extends Cell< ? > > int size( final T list ) {
    return calculateSize( Preconditions.checkNotNull( list ) ) ;
  }

  private static< T extends Cell< ? > > int calculateSize( final T list ) {
    if( list == null ) {
      return 0 ;
    } else {
      return 1 + calculateSize( list.getLinked() ) ;
    }
  }

  public static< T extends Cell< T > > T atDistance( final T list, final int distance ) {
    Preconditions.checkArgument( distance >= 0 ) ;
    int step = 0 ;
    T current = list ;
    while( current != null ) {
      if( step == distance ) {
        return current ;
      }
      current = current.getLinked() ;
      step ++ ;
    }
    throw new IllegalArgumentException(
        "Distance of " + distance + " while list size is " + step ) ;
  }

  public static< T extends Cell< T > > T atOppositeDistance(
      final T list,
      final int oppositeDistance
  ) {
    Preconditions.checkArgument( oppositeDistance >= 0 ) ;
    final int size = size( list ) ;
    final int distance = size - oppositeDistance ;
    if( distance < 0 ) {
      throw new IllegalArgumentException(
          "Opposite distance of " + oppositeDistance + " while list size is " + size ) ;
    }
    return atDistance( list, distance ) ;
  }


  private static< T extends Cell< T > > List< T > asJavaList( final T list ) {
    final List< T > javaList = Lists.newArrayList() ;
    T current = Preconditions.checkNotNull( list ) ;
    while( current != null ) {
      javaList.add( current ) ;
      current = current.getLinked() ;
    }
    return javaList ;
  }

  public static< T extends Cell< T > > T addLast( ) {
    throw new UnsupportedOperationException( "TODO" ) ;
  }

  public static< T extends Cell< T > > T removeFirst( final T list ) {
    final Relinker< T > relinker = Relinkers.firstElementRemover() ;

    throw new UnsupportedOperationException( "removeFirst" ) ;
  }

  public static< T extends Cell< T > > T apply(
      final T list,
      final Relinker< T > relinker
  ) {
    T lastResult ;
    T backupResult ;
    final int size = size( list ) ;

    lastResult = relinker.apply( size, -1, null, null ) ;

    for( int oppositeDistance = relinker.startsAt( size ) ;
         oppositeDistance < size ;
         oppositeDistance ++
    ) {
      final T original = atDistance( list, oppositeDistance ) ;
      backupResult = relinker.apply( size, oppositeDistance, original, lastResult ) ;
      lastResult = backupResult == null ? lastResult : backupResult ;
    }

    backupResult = lastResult ;
    lastResult = relinker.apply( size, size, null, lastResult ) ;
    
    return lastResult == null ? backupResult : lastResult ;
  }

}
