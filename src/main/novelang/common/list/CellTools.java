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

  public static< T extends Cell > int size( final Cell< T > list ) {
    return calculateSize( Preconditions.checkNotNull( list ) ) ;
  }

  private static< T extends Cell > int calculateSize( final Cell< T > list ) {
    if( list == null ) {
      return 0 ;
    } else {
      return 1 + calculateSize( list.getLinked() ) ;
    }
  }

  public static< T extends Cell > Cell< T > atDistance( final Cell< T > list, final int distance ) {
    Preconditions.checkArgument( distance >= 0 ) ;
    int step = 0 ;
    Cell< T > current = list ;
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

  public static< T extends Cell > Cell< T > atOppositeDistance(
      final Cell< T > list,
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


  private static< T extends Cell > List< Cell< T > > asJavaList( final Cell< T > list ) {
    final List< Cell< T > > javaList = Lists.newArrayList() ;
    Cell< T > current = Preconditions.checkNotNull( list ) ;
    while( current != null ) {
      javaList.add( current ) ;
      current = current.getLinked() ;
    }
    return javaList ;
  }

  public static< T extends Cell > Cell< T > addLast( ) {
    throw new UnsupportedOperationException( "TODO" ) ;
  }

  public static< T extends Cell > Cell< T > removeFirst( final Cell< T > list ) {
    final Relinker< T > relinker = Relinkers.firstElementRemover() ;

    throw new UnsupportedOperationException( "removeFirst" ) ;
  }

  public static< T extends Cell > Cell< T > apply(
      final Cell< T > list,
      final Relinker< T > relinker
  ) {
    Cell< T > lastResult ;
    Cell< T > backupResult ;
    final int size = size( list ) ;

    lastResult = relinker.apply( size, -1, null, null ) ;

    for( int oppositeDistance = relinker.startsAt( size ) ;
         oppositeDistance < size ;
         oppositeDistance ++
    ) {
      final Cell< T > original = atDistance( list, oppositeDistance ) ;
      backupResult = relinker.apply( size, oppositeDistance, original, lastResult ) ;
      lastResult = backupResult == null ? lastResult : backupResult ;
    }

    backupResult = lastResult ;
    lastResult = relinker.apply( size, size, null, lastResult ) ;
    
    return lastResult == null ? backupResult : lastResult ;
  }

}
