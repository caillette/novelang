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

import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
/*package*/ class Element implements LocationFactory {

  protected final BookContext context ;
  protected final Location location;
  private final List< Exception > problems = Lists.newArrayList() ;

  public Element( BookContext context, Location location ) {
    this.context = Objects.nonNull( context ) ;
    this.location = Objects.nonNull( location ) ;
  }

  protected BookContext getContext() {
    return context;
  }

  public Location createLocation( int line, int column ) {
    return getContext().createStructureLocator( line, column ) ;
  }

  public Location getLocation() {
    return location;
  }

  public Iterable< Exception > getProblems() {
    return Lists.immutableList( problems ) ;
  }

  @Override
  public String toString() {
    return getContext().asString() + "@" + System.identityHashCode( this ) ;
  }

  protected final void collect( Exception exception ) {
    problems.add( Objects.nonNull( exception ) ) ;
  }


}
