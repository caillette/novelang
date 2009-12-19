package novelang.marker;

import com.google.common.base.Preconditions;

/**
 * Strong-types a Tag which is just a non-null String.
 * 
 * @author Laurent Caillette
 */
public final class Tag {
  
  private final String name ;

  public Tag( String name ) {
    this.name = Preconditions.checkNotNull( name ) ;
  }

  public String getName() {
    return name ;
  }

  @Override
  public boolean equals( final Object other ) {
    if ( this == other ) {
      return true ;
    }
    if ( other == null || getClass() != other.getClass() ) {
      return false ;
    }
    final Tag tag = ( Tag ) other;
    if ( !name.equals( tag.name ) ) {
      return false ;
    }
    return true ;
  }

  @Override
  public int hashCode() {
    return name.hashCode() ;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + name + "]" ;
  }
}
