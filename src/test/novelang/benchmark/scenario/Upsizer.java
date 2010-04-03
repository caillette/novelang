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
import novelang.novelist.Novelist;

import java.io.File;
import java.io.IOException;

/**
 *
* @author Laurent Caillette
*/
public interface Upsizer {

  /**
   * Creates initial document(s) or augments it (them).
   */
  void upsize() throws IOException;

  public interface Factory {

    Upsizer create( File contentDirectory ) throws IOException;

    /**
     * Returns document request matching with generated document.
     *
     * @return A non-null, non-empty {@code String} starting with "/" (solidus).
     */
    String getDocumentRequest() ;
  }



  
  /**
   * @author Laurent Caillette
   */
  static class ForNovelist implements Upsizer {

    private final Novelist novelist ;

    public ForNovelist( final Novelist novelist ) {
      this.novelist = Preconditions.checkNotNull( novelist ) ;
    }

    public void upsize() throws IOException {
      novelist.write( 1 ) ;
    }
  }

}
