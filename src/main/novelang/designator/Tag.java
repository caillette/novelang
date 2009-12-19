package novelang.designator;

import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;

import com.google.common.base.Preconditions;

import java.util.Set;

import static novelang.parser.NodeKind.TAG;

/**
 * Strong-types a Tag which is just a non-null String.
 * 
 * @author Laurent Caillette
 */
public final class Tag {
  
  private final String name ;

  public Tag( String name ) {
    Preconditions.checkNotNull( name ) ;
    Preconditions.checkArgument( ! "".equals( name ) ) ;
    this.name = name ;
  }

  public String getName() {
    return name ;
  }
  
  public SyntacticTree asSyntacticTree() {
    return new SimpleTree( TAG, new SimpleTree( name ) ) ;
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
  
  public static boolean contains( final Set< Tag > tagset, final String tagAsString )
  {
    for( final Tag tag : tagset ) {
      if( tag.getName().equals( tagAsString ) ) {
        return true ;
      }
    }
    return false ;
  }
}
