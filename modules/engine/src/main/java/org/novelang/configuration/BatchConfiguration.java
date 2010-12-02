/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.configuration;

import java.io.File;

/**
 * @author Laurent Caillette
 */
public interface BatchConfiguration {

  /**
   * Returns a Configuration.
   * @return a non-null object.
   */
  ProducerConfiguration getProducerConfiguration() ;

  /**
   * Return the directory where to write generated documents to.
   * @return a non-null object referencing an existing directory.
   */
  File getOutputDirectory() ;

}
