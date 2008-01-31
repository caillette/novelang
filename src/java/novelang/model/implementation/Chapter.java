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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.model.structural.StructuralChapter;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.common.MutableTree;
import novelang.model.common.NodeKind;
import novelang.model.weaved.WeavedChapter;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class Chapter extends StyledElement implements StructuralChapter, WeavedChapter {

  private static final Logger LOGGER = LoggerFactory.getLogger( Chapter.class ) ;

  private final List< Section > sections = Lists.newArrayList() ;

  public Chapter( BookContext context, Location location, int position ) {
    super( context.derive( "chapter[" + position + "]" ), location );
  }

  public Section createSection( Location location ) {
    final int position = sections.size() ;
    final Section section = new Section( getContext(), location, position ) ;
    sections.add( position, section ) ;
    LOGGER.debug(
        "Created and added section: {} from {}", section, location ) ;
    return section ;
  }

  public Location createLocation( int line, int column ) {
    return getContext().createStructureLocator( line, column ) ;
  }

  public Tree buildRawTree( Map< String, Tree > identifiers ) {
    final MutableTree rawChapterTree = new DefaultMutableTree( NodeKind.CHAPTER ) ;
    for( final Section section : sections ) {
      rawChapterTree.addChild( section.buildRawTree( identifiers ) ) ;
    }
    return rawChapterTree ;
  }
}
