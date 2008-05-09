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

  public Tree buildTree( Map< String, Tree > identifiers ) {
    final MutableTree chapterTree = new DefaultMutableTree( NodeKind.CHAPTER ) ;
    if( null != getTitle() ) {
      chapterTree.addChild( getTitle() ) ;
    }
    final Tree styleTree = getStyle() ;
    if( null != styleTree ) {
      chapterTree.addChild( styleTree ) ;
    }
    for( final Section section : sections ) {
      chapterTree.addChild( section.buildTree( identifiers ) ) ;
    }
    return chapterTree ;
  }

  


}
