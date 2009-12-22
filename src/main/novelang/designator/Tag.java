package novelang.designator;

import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.parser.NodeKind;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;


import org.apache.commons.lang.StringUtils;

import static novelang.parser.NodeKind.TAG;
import static novelang.parser.NodeKind._TAGS;

/**
 * Strong-types a Tag which is just a non-null, non-empty String.
 * 
 * @author Laurent Caillette
 */
public final class Tag implements Comparable< Tag > {
  
  private final String name ;

  public Tag( final String name ) {
    Preconditions.checkNotNull( name ) ;
    Preconditions.checkArgument( ! "".equals( name ) ) ;
    this.name = name ;
  }

  public SyntacticTree asSyntacticTree() {
    return new SimpleTree( TAG, new SimpleTree( name ) ) ;
  }

  public static boolean contains( final Set< Tag > tagset, final String tagAsString ) {
    for( final Tag tag : tagset ) {
      if( tag.name.equals( tagAsString ) ) {
        return true ;
      }
    }
    return false ;
  }

  public static Set< Tag > toTagSet( final String... tagStrings ) {
    final Set< Tag > tagSet = Sets.newHashSet() ;
    for( final String tagAsString : tagStrings ) {
      if( ! StringUtils.isBlank( tagAsString ) ) {
        tagSet.add( new Tag( tagAsString ) ) ;
      }
    }
    return ImmutableSet.copyOf( tagSet ) ;
  }
  
  public static Iterable< SyntacticTree > toSyntacticTrees( 
      final NodeKind tagNodeKind, 
      final Set< Tag > tagset 
  ) {
    final List< SyntacticTree > tagsAsTrees = Lists.newArrayListWithCapacity( tagset.size() ) ;
    for( final Tag tag : Ordering.natural().sortedCopy( tagset ) ) {
      tagsAsTrees.add( new SimpleTree( tagNodeKind.name(), new SimpleTree( tag.name ) ) ) ;
    }
    return ImmutableList.copyOf( tagsAsTrees ) ;    
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

  public int compareTo( final Tag tag ) {
    if( tag == null ) {
      return 1 ;
    }
    return this.name.compareTo( tag.name ) ;
  }
}
