/*
 * Copyright (C) 2008 Laurent Caillette
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
package novelang.common;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.ClassUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.common.tree.ImmutableTree;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;

import java.util.Set;

/**
 * Specific immplementation of a {@link novelang.common.tree.Tree}.
 * 
 * @author Laurent Caillette
 */
public class SimpleTree extends ImmutableTree< SyntacticTree > implements SyntacticTree {

  private final String text;
  private final Location location ;

  public SimpleTree( final String text, final SyntacticTree... children ) {
    this( text, null, Lists.newArrayList( children ) ) ;
  }

  public SimpleTree( final String text, final Location location, final SyntacticTree... children ) {
    this( text, location, Lists.newArrayList( children ) ) ;
  }

  public SimpleTree( final NodeKind nodeKind, final SyntacticTree... children ) {
    this( nodeKind.name(), Lists.newArrayList( children ) ) ;
  }

  public SimpleTree( final String text, final Iterable< ? extends SyntacticTree> children ) {
    this( text, null, children ) ;
  }

  public SimpleTree( final NodeKind nodeKind, final Iterable< ? extends SyntacticTree> children ) {
    this( nodeKind.name(), children ) ;
  }

  public SimpleTree(
      final String text,
      final Location location,
      final Iterable< ? extends SyntacticTree > children
  ) {
    super( children ) ;
    this.location = location ;
    this.text = text ;
  }

  public SyntacticTree adopt( final SyntacticTree... newChildren ) throws NullArgumentException {
    return new SimpleTree( getText(), getLocation(), newChildren ) ;
  }

  public Iterable< ? extends SyntacticTree > getChildren() {

    final Iterable< ? extends SyntacticTree > children = super.getChildren() ;
    if( null == children ) {
      return ImmutableList.of() ;
    } else {
      return children ;
    }
  }

  public String getText() {
    return text;
  }

  public String toStringTree() {
    final StringBuffer buffer = new StringBuffer() ;
    
    final boolean shouldWrap = getChildCount() > 0 ;

    if( shouldWrap ) {
      buffer.append( "(" ) ;
    }
    buffer.append( getText() ) ;

    if( getChildCount() > 0 ) {
      buffer.append( " " ) ;
      boolean first = true ;
      for( final SyntacticTree tree : getChildren() ) {
        if( ! first ) {
          buffer.append( " " ) ;
        }
        buffer.append( tree.toStringTree() ) ;
        first = false ;
      }
    }
    if( shouldWrap ) {
      buffer.append( ")" ) ;
    }
    return buffer.toString() ;
  }

  public Location getLocation() {
    return location ;
  }

  public boolean isOneOf( final NodeKind... kinds ) {
    if( NodeKindTools.rootHasNodeKindName( this ) ) {
      for( final NodeKind kind : kinds ) {
        if( getText().equals( kind.name() ) ) {
          return true ;
        }
      }
    }
    return false ;
  }

  /**
   * Is is possible to remove this kind of duplicate code?
   * 
   * @see #isOneOf(novelang.parser.NodeKind...)  
   */
  public boolean isOneOf( final Set< NodeKind > kinds ) {
    if( NodeKindTools.rootHasNodeKindName( this ) ) {
      for( final NodeKind kind : kinds ) {
        if( getText().equals( kind.name() ) ) {
          return true ;
        }
      }
    }
    return false ;
  }

  public Class< ? extends SyntacticTree > getStorageType() {
    return SyntacticTree.class ;
  }

  @Override
  public String toString() {
    return ClassUtils.getShortClassName( getClass() ) + "[" + text + "]" ;
  }
}
