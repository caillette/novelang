/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.nhovestone;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.novelang.Version;

/**
 * Represents the measurements gathered when running a {@link Scenario}.
 *
 * @author Laurent Caillette
 */
public class Telemetrics {

  ImmutableMap< Version, ImmutableList< Shot > > shotsByVersion ;

  public Telemetrics( final ImmutableMap<Version, ImmutableList< Shot > > shotsByVersion ) {
    this.shotsByVersion = checkNotNull( shotsByVersion ) ;
  }

  public ImmutableList< Shot > getShots( final Version version ) {
    return shotsByVersion.get( version ) ;
  }

  public static Builder builder() {
    return new Builder() ;
  }

  public static class Builder {
    private final Map< Version, ImmutableList.Builder< Shot > > builders = Maps.newLinkedHashMap() ;

    public ImmutableList.Builder< Shot > getShotListBuilder( final Version version ) {
      checkNotNull( version ) ;
      ImmutableList.Builder< Shot > builder = builders.get( version ) ;
      if( builder == null ) {
        builder = ImmutableList.builder() ;
        builders.put( version, builder ) ;
      }
      return builder ;
    }

    public Telemetrics build() {
      final ImmutableMap.Builder< Version, ImmutableList< Shot > > rebuilder =
          ImmutableMap.builder() ;
      for( final Map.Entry< Version, ImmutableList.Builder< Shot > > entry : builders.entrySet() ) {
        rebuilder.put( entry.getKey(), entry.getValue().build() ) ;
      }
      return new Telemetrics( rebuilder.build() ) ;
    }
  }


  public static final class Shot {
    public final long duration ;
    public final long upsizing ;

    public Shot( final long duration, final long upsizing ) {
      checkArgument( duration >= 0, "Illegal: ", duration ) ;
      checkArgument( upsizing >= 0, "Illegal: ", upsizing ) ;
      this.duration = duration ;
      this.upsizing = upsizing ;
    }
  }
}
