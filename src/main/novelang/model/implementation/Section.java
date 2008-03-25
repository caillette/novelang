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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
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


  public Tree buildTree( Map< String, Tree > identifiers ) {
    final MutableTree sectionTree = new DefaultMutableTree( NodeKind.SECTION ) ;
    final Tree styleTree = getStyle() ;
    if( null != styleTree ) {
      sectionTree.addChild( styleTree ) ;
    }

    for( final Inclusion inclusion : inclusions ) {
      // TODO don't take all paragraphs inconditionally.
      for( final Tree tree : inclusion.buildTrees( identifiers ) ) {
        sectionTree.addChild( tree ) ;
      }
    }
    return enhanceSectionTree( sectionTree ) ;
  }

  /**
   * Gathers contiguous speech items in a {@link NodeKind#_SPEECH_SEQUENCE} node.
   */
  private Tree enhanceSectionTree( Tree rawSectionTree ) {
    final MutableTree enhancedSectionTree = new DefaultMutableTree( NodeKind.SECTION ) ;

    if( getTitle() != null ) {
      enhancedSectionTree.addChild( getTitle() ) ;
    }

    MutableTree speechSequenceTree = null ;
    for( final Tree maybeParagraphTree : rawSectionTree.getChildren() ) {
      if( maybeParagraphTree.isOneOf(
          NodeKind.PARAGRAPH_SPEECH,
          NodeKind.PARAGRAPH_SPEECH_CONTINUED,
          NodeKind.PARAGRAPH_SPEECH_ESCAPED
      ) ) {
        if( null == speechSequenceTree ) {
          speechSequenceTree = new DefaultMutableTree( NodeKind._SPEECH_SEQUENCE ) ;
        }
        speechSequenceTree.addChild( maybeParagraphTree ) ;
      } else {
        if( null == speechSequenceTree ) {
          enhancedSectionTree.addChild( maybeParagraphTree ) ;
        } else {
          enhancedSectionTree.addChild( speechSequenceTree ) ;
          speechSequenceTree = null ;
          enhancedSectionTree.addChild( maybeParagraphTree ) ;
        }
      }
    }
    if( null != speechSequenceTree ) {
      enhancedSectionTree.addChild( speechSequenceTree ) ;
    }
    return enhancedSectionTree ;
  }


}
