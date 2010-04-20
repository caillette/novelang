package novelang.common.scratch.list;

/**
 * Represents an entry of a simple-linked list.
 *
 * There is an inconsistency with the {@link novelang.common.tree.Tree} interface which has no
 * method for accessing the payload (instead, subclasses define the payload as fields).
 * This is because the {@link Relinker} interface performs filtering
 * on the payload objects and lets other tools take care of the list consistency.
 *
 * @author Laurent Caillette
 */
public interface Cell< T extends Cell > {

  /**
   * Returns linked element.
   *
   * @return a possibly null object.
   */
  T getLinked() ;

  /**
   * Returns a copy of this object referencing a new linked element.
   * @param newLinked a possibly null object.
   * @return a non-null object.
   */
  T relink( final T newLinked ) ;


}
