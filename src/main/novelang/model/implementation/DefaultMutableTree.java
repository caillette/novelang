/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.implementation;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import novelang.model.common.Location;
import novelang.model.common.MutableTree;
import novelang.model.common.NodeKind;
import novelang.model.common.Tree;
import novelang.rendering.PlainTextRenderer;

/**
 * @author Laurent Caillette
 */
public class DefaultMutableTree implements MutableTree {

  private final NodeKind nodeKind ;
  private final String text ;
  private final List< Tree > children = Lists.newArrayList() ;


  public DefaultMutableTree( NodeKind nodeKind ) {
    this.nodeKind = Objects.nonNull( nodeKind ) ;
    this.text = nodeKind.name() ;
  }

  public DefaultMutableTree( String text ) {
    this.nodeKind = null ; // TODO support UNKNOWN or whatever.
    this.text = Objects.nonNull( text ) ;
  }

  public MutableTree createChild( NodeKind nodeKind ) {
    final MutableTree child = new DefaultMutableTree( nodeKind ) ;
    addChild( child ) ;
    return child ;
  }

  public void addChild( Tree child ) {
    children.add( Objects.nonNull( child ) ) ;
  }

  public Tree getChildAt( int i ) {
    return children.get( i ) ;
  }

  public int getChildCount() {
    return children.size() ;
  }

  public Iterable< Tree > getChildren() {
    return Lists.immutableList( children ) ;
  }

  public String getText() {
    return text ;
  }

  public String toStringTree() {
    return PlainTextRenderer.renderAsString( this ) ;
  }

  public Location getLocation() {
    return children.get( 0 ).getLocation() ;
  }

  public boolean isOneOf( NodeKind... kinds ) {
    for( NodeKind kind : kinds ) {
      if( nodeKind == kind ) {
        return true ;
      }
    }
    return false ;
  }
}
