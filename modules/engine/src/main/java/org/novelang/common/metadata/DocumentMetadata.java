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

package org.novelang.common.metadata;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.joda.time.ReadableDateTime;

/**
 * @author Laurent Caillette
 */
public interface DocumentMetadata {

  /**
   * @return a non-null object.
   */
  ReadableDateTime getCreationTimestamp() ;

  /**
   * @return a non-null object.
   */
  Charset getCharset() ;

  /**
   * @return a possibly null object.
   */
  Page getPage() ;

  /**
   * Returns the URL that corresponds to
   * {@link org.novelang.configuration.parse.GenericParametersConstants#OPTION_CONTENT_ROOT},
   * with absolute path and no trailing solidus.
   *
   * @return a non-null object.
   */
  URL getContentDirectory() ;


}
