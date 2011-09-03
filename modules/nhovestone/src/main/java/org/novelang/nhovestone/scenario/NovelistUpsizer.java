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
package org.novelang.nhovestone.scenario;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.novelang.novelist.Novelist;

/**
 * @author Laurent Caillette
 */
public abstract class NovelistUpsizer implements Upsizer< Long > {

  protected final Novelist novelist ;
  private final List< Long > upsizings = Lists.newArrayList() ;

  protected NovelistUpsizer( final Novelist novelist ) {
    this.novelist = Preconditions.checkNotNull( novelist ) ;
  }

  @Override
  public final void upsize() throws IOException {
    upsizings.add( doUpsize() ) ;
  }

  protected abstract long doUpsize() throws IOException;

  @Override
  public final List< Long > getUpsizings() {
    return ImmutableList.copyOf( upsizings ) ;
  }

  /**
   * @author Laurent Caillette
   */
  public static class NovellaeLength extends NovelistUpsizer {

    public NovellaeLength( final Novelist novelist ) {
      super( novelist ) ;
    }

    @Override
    protected long doUpsize() throws IOException {
      return novelist.write( 1 ) ;
    }

  }

  /**
   * @author Laurent Caillette
   */
  public static class NovellaCount extends NovelistUpsizer {

    public NovellaCount( final Novelist novelist ) {
      super( novelist ) ;
    }

    @Override
    protected long doUpsize() throws IOException {
      return novelist.addGhostwriter( 1 ) ;
    }

  }
}
