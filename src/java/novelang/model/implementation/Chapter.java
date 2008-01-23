/*
 * Copyright (C) 2006 Laurent Caillette
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

import novelang.model.structural.StructuralChapter;
import novelang.model.common.Locator;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class Chapter extends Container implements StructuralChapter {

  private final List< Section > sections = Lists.newArrayList() ;

  public Chapter( BookContext context, int position ) {
    super( context.derive( "chapter[" + position + "]" ) );
  }

  public Section createSection( Locator locator ) {
    final int position = sections.size() ;
    final Section section = new Section( getContext(), position ) ;
    sections.add( position, section ) ;
    getContext().getLogger().debug( 
        "Created and added section: {}", section ) ;
    return section ;
  }

  public Locator createLocator( int line, int column ) {
    return getContext().createStructureLocator( line, column ) ;
  }

//  @Override
//  public String toString() {
//    return getContext().asString() + "@" + System.identityHashCode( this ) ;
//  }
}
