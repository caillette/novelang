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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import novelang.model.common.LocatorFactory;
import novelang.model.common.Location;

/**
 * @author Laurent Caillette
 */
public class Container implements LocatorFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger( Container.class ) ;

  private final BookContext context ;
  private final Location location;
  private String title ;
  private String style ;

  public Container( BookContext context, Location location ) {
    this.context = Objects.nonNull( context ) ;
    this.location = Objects.nonNull( location ) ;
  }

  protected BookContext getContext() {
    return context;
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

  public Location createStructuralLocator( int line, int column ) {
    return getContext().createStructureLocator( line, column ) ;
  }

  public Location getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return getContext().asString() + "@" + System.identityHashCode( this ) ;
  }

}
