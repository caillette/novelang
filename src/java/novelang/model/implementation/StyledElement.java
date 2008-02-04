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
import com.google.common.base.Objects;
import novelang.model.common.LocationFactory;
import novelang.model.common.Location;

/**
 * @author Laurent Caillette
 */
/*package*/ class StyledElement extends Element implements LocationFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger( StyledElement.class ) ;

  private String title ;
  private String style ;

  public StyledElement( BookContext context, Location location ) {
    super( context, location ) ;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = Objects.nonNull( title ) ;
    LOGGER.debug( "Title set to '{}' for {}", title, this ) ;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle( String style ) {
    this.style = Objects.nonNull( style ) ;
    LOGGER.debug( "Style set to '{}' for {}", title, this ) ;
  }

}
