package org.novelang.common.tree;

import com.google.common.base.Predicate;

/**
 * Thrown when the filtering of a {@link RobustPath} gives nothing. 
 * 
 * @author Laurent Caillette
 */
public class FilterException extends RuntimeException {

  public FilterException( final Treepath treepath, final Predicate predicate ) {
    super( "Predicate " + predicate + " applied on " + treepath + " gives 0 child" ) ;
  }
}
