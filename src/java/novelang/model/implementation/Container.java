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
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class Container {

  private final BookContext context ;
  private String title ;
  private String style ;


  public Container( BookContext context ) {
    this.context = Objects.nonNull( context ) ;
  }


  protected BookContext getContext() {
    return context;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = Objects.nonNull( title ) ;
    context.getLogger().debug( "Title set to '{}' for {}", title, this ) ;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle( String style ) {
    this.style = Objects.nonNull( style ) ;
    context.getLogger().debug( "Style set to '{}' for {}", title, this ) ;
  }

  @Override
  public String toString() {
    return getContext().asString() + "@" + System.identityHashCode( this ) ;
  }
  
}
