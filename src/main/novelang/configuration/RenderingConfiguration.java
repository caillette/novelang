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

package novelang.configuration;

import java.io.File;

import org.apache.fop.apps.FopFactory;
import novelang.loader.ResourceLoader;

/**
 * @author Laurent Caillette
 */
public interface RenderingConfiguration {

  /**
   * Returns the {@code ResourceLoader} for stylesheets and companion files.
   * @return a non-null object.
   */
  ResourceLoader getResourceLoader() ;

  /**
   * Returns the directory where some renderers should find fonts.
   * @return a possibly null object.
   */
  FopFactory getFopFactory() ;

  /**
   * Returns all user-defined fonts.
   * @return a non-null object iterating over non-null objects.
   */
  Iterable< FontDescriptor > getFontDescriptors() ;

}
