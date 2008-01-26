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
import novelang.model.structural.StructuralPart;
import novelang.model.common.Location;
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class Part implements StructuralPart {

  private static final Logger LOGGER = LoggerFactory.getLogger( StyledElement.class ) ;

  private final BookContext context ;
  private final String fileName ;
  private final Location location;

  public Part( BookContext context, String fileName, Location location ) {
    this.context = Objects.nonNull( context ).derive( "part[" + fileName + "]" ) ;
    this.fileName = fileName ;
    this.location = Objects.nonNull( location ) ;
  }

  public Location getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return context.asString() + "@" + System.identityHashCode( this ) ;
  }
}
