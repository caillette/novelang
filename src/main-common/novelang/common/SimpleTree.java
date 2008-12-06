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
import novelang.common.tree.ImmutableTree;
import com.google.common.collect.Lists;

/**
 * Specific immplementation of a {@link novelang.common.tree.Tree}.
 * 
 * @author Laurent Caillette
 */
public class SimpleTree extends ImmutableTree< SyntacticTree > implements SyntacticTree {

  private final String text;
  private final Location location ;

  public SimpleTree( String text, SyntacticTree... children ) {
    this( text, Lists.newArrayList( children ) ) ;
  }

  public SimpleTree( String text, Iterable< ? extends SyntacticTree> children ) {
    super( children ) ;
    this.text = text ;
    this.location = null ;
  }

  public SyntacticTree adopt( SyntacticTree... newChildren ) throws NullArgumentException {
    return new SimpleTree( getText(), newChildren ) ;
  }

  public Iterable<? extends SyntacticTree> getChildren() {
    return super.getChildren();
  }

  public String getText() {
    return text;
  }

  public String toStringTree() {
    final StringBuffer buffer = new StringBuffer() ;
    buffer.append( "(" ) ;
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
    buffer.append( ")" ) ;
    return buffer.toString() ;
  }

  public Location getLocation() {
    return location ;
  }

  public boolean isOneOf( NodeKind... kinds ) {
    if( NodeKind.rootHasNodeKindName( this ) ) {
      for( NodeKind kind : kinds ) {
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
}
