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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.novelang.nhovestone.Scenario;

/**
 * The general contract for something that performs a side-effect somewhere, causing an iteration
 * in a {@link Scenario} to use bigger stuff.
 *
 * @author Laurent Caillette
 */
public interface Upsizer< UPSIZING > {

  /**
   * Creates initial document(s) or augments it (them).
   */
  void upsize() throws IOException ;

  List< UPSIZING > getUpsizings() ;

  /**
   * Using a factory avoids setting the directory to an already-existing instance of the
   * {@link Upsizer}. The pattern of configuring everything through the constructor works well,
   * let's apply it.
   */
  public interface Factory< UPSISING > {

    Upsizer< UPSISING > create( File contentDirectory ) throws IOException;

    /**
     * Returns document request matching with generated document.
     *
     * @return A non-null, non-empty {@code String} starting with "/" (solidus).
     */
    String getDocumentRequest() ;
  }


}
