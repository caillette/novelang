/*
 * Copyright (C) 2011 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.novelang.designator;

import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.TAG;

/**
 * Strong-types a Tag which is just a non-null, non-empty String.
 * 
 * @author Laurent Caillette
 */
public final class Tag implements Comparable< Tag > {
  
  private final String name ;

  public static final Function<Tag,String> EXTRACT_TAG_NAME = new Function< Tag, String >() {
    @Override
    public String apply( final Tag tag ) {
      return tag.name ;
    }
  };

  public Tag( final String name ) {
    Preconditions.checkNotNull( name ) ;
    Preconditions.checkArgument( ! "".equals( name ) ) ;
    this.name = name ;
  }

  @Deprecated
  public SyntacticTree asSyntacticTree() {
    return asSyntacticTree( TAG ) ;
  }

  public SyntacticTree asSyntacticTree( final NodeKind nodekind ) {
    return new SimpleTree( Preconditions.checkNotNull( nodekind ), new SimpleTree( name ) ) ;
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
      tagsAsTrees.add( new SimpleTree( tagNodeKind, new SimpleTree( tag.name ) ) ) ;
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

  @Override
  public int compareTo( final Tag tag ) {
    if( tag == null ) {
      return 1 ;
    }
    return this.name.compareTo( tag.name ) ;
  }


  public static final Function< Tag, String > FUNCTION_TOSOURCESTRING = 
      new Function< Tag, String >() {
        @Override
        public String apply( final Tag tag ) {
          return "@" + tag.name ;
        }
      }
  ;
}
