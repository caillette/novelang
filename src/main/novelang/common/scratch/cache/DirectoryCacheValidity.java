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
package novelang.common.scratch.cache;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import novelang.common.scratch.Tuple2;

/**
 * @author Laurent Caillette
 */
public class DirectoryCacheValidity implements CacheValidity< DirectoryCacheValidity >
{

  private final List<Tuple2< String, Long >> files ;
  private final List< DirectoryCacheValidity > directories ;

  public DirectoryCacheValidity( final File root ) {
    files = Lists.newArrayList() ;
    directories = Lists.newArrayList() ;
  }

  public boolean isValid( final DirectoryCacheValidity other ) {
    return false;
  }
}
