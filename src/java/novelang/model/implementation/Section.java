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
import novelang.model.structural.StructuralSection;
import novelang.model.structural.StructuralInclusion;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.common.MutableTree;
import novelang.model.common.NodeKind;
import novelang.model.weaved.WeavedSection;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class Section extends StyledElement implements StructuralSection, WeavedSection {

  private static final Logger LOGGER = LoggerFactory.getLogger( Section.class ) ;

  private final List< Inclusion > inclusions = Lists.newArrayList() ;

  public Section( BookContext context, Location location, int position ) {
    super( context.derive( "section[" + position + "]" ), location ) ;
  }

  public StructuralInclusion createInclusion( Location location, String identifier ) {
    final int position = inclusions.size() ;
    final Inclusion inclusion = new Inclusion( getContext(), location, position, identifier ) ;
    inclusions.add( position, inclusion ) ;
    LOGGER.debug(
        "Created and added inclusion: '{}' {} from {}",
        new Object[] { identifier, inclusion, location }
    ) ;
    return inclusion ;
  }


  public Tree buildRawTree( Map< String, Tree > identifiers ) {
    final MutableTree rawSectionTree = new DefaultMutableTree( NodeKind.SECTION ) ;
    for( final Inclusion inclusion : inclusions ) {
      rawSectionTree.addChild( inclusion.buildRawTree( identifiers ) ) ;
    }
    return rawSectionTree ;
  }
}
