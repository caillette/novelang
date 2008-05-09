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
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.common.Problem;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
/*package*/ class Element /*implements LocationFactory*/ {

  private static final Logger LOGGER = LoggerFactory.getLogger( Element.class ) ;

  protected final Location location;
  private final List< Problem > problems = Lists.newArrayList() ;
  protected static final String CHARSET_NAME = "ISO-8859-1" ;
  protected static final Charset DEFAULT_CHARSET ;
  static {
    DEFAULT_CHARSET = Charset.forName( Element.CHARSET_NAME ) ;
  }

  public Element( Location location ) {
    this.location = Objects.nonNull( location ) ;
  }

  public Location getLocation() {
    return location;
  }

  public Iterable< Problem > getProblems() {
    return Lists.immutableList( problems ) ;
  }

  public boolean hasProblem() {
    return ! problems.isEmpty() ;
  }

  protected final void collect( Problem problem ) {
    LOGGER.debug( "Collecting Problem: " + problem ) ;
    problems.add( Objects.nonNull( problem ) ) ;
  }


}
