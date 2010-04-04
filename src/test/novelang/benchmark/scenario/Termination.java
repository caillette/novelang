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
package novelang.benchmark.scenario;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.Set;

/**
 * Kind of enum supporting inheritance.
 *
 * @author Laurent Caillette
 */
public class Termination {

  private static final Set< String > names = Sets.newHashSet() ;

  private final String name ;

  public Termination( final String name ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( name ) ) ;
    synchronized( names ) {
      Preconditions.checkArgument( ! names.contains( name ) ) ;
      names.add( name ) ;
    }
    this.name = name ;
  }

  public String getName() {
    return name ;
  }
}
