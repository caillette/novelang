package novelang.common.scratch.list;

/**
 * Generic update operator.
 */
public interface Relinker< T extends Cell > {

  /**
   * Gives a hint to avoid full list traversal whenever possible.
   *
   * @param listSize a value inside [ 1, {@link Integer#MAX_VALUE} ] interval.
   * @return a value inside [ -1, {@code listSize} ] interval.
   */
  int startsAt( int listSize ) ;

  /**
   * Returns the updated
   *
   * @param listSize number of elements, a value inside [ 0, {@link Integer#MAX_VALUE} ] interval.
   * @param index a value inside [ -1, {@code listSize} ] interval.
   * @param original a possibly null object.
   * @param lastKept the last value returned by this method for one given list.
   * @return a possibly null object, meaning removal (when {@code original} wasn't null) or
   *         do-nothing (when {@code original} was null, at the extremi of the traversal).
   */
  T apply( int listSize, int index, T original, T lastKept ) ;

}
