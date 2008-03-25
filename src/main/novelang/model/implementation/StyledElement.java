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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import com.google.common.base.Objects;
import novelang.model.common.LocationFactory;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.common.NodeKind;

/**
 * @author Laurent Caillette
 */
/*package*/ class StyledElement
    extends ContextualizedElement
    implements LocationFactory
{

  private static final Logger LOGGER = LoggerFactory.getLogger( StyledElement.class ) ;

  private Tree title ;
  private Tree style ;

  public StyledElement( BookContext context, Location location ) {
    super( context, location ) ;
  }

  public Tree getTitle() {
    return title;
  }

  public void setTitle( Tree title ) {
    this.title = Objects.nonNull( title ) ;
    LOGGER.debug( "Title set to '{}' for {}", title, this ) ;
  }

  public Tree getStyle() {
    return style;
  }

  public void setStyle( Tree style ) {
    this.style = Objects.nonNull( style ) ;
    LOGGER.debug( "Style set to '{}' for {}", title, this ) ;
  }

  public String getStyleName() {
    Tree tree = style ;
    if( null != tree ) {
      tree = tree.getChildAt( 0 ) ;
      return tree.getText() ;
    } else {
      return null ;
    }

  }

}
