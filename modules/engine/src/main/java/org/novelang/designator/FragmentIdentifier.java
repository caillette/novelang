package org.novelang.designator;

import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Identifier for a Novella's fragment.
 * 
 * @author Laurent Caillette
 */
public class FragmentIdentifier {
  
  private final String stringRepresentation ;


  public FragmentIdentifier( final String stringRepresentation ) {
    this.stringRepresentation = "\\\\" + checkNotNull( stringRepresentation ) ;
    checkArgument( ! StringUtils.isBlank( stringRepresentation ) ) ;
  }

  @Override
  public int hashCode() {
    return stringRepresentation.hashCode() ;
  }

  @Override
  public boolean equals( final Object o ) {
    if( o instanceof FragmentIdentifier ) {
      return stringRepresentation.equals( ( ( FragmentIdentifier ) o ).stringRepresentation ) ;  
    } else {
      return false ;
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + stringRepresentation + "]" ;
  }

  public String getAbsoluteRepresentation() {
    return stringRepresentation ;
  }
}
