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
package org.novelang.configuration;

import org.novelang.produce.DocumentRequest;

/**
 * @author Laurent Caillette
 */
public interface DocumentGeneratorConfiguration extends BatchConfiguration {

  /**
   * Returns documents requests.
   * @return a non-null object iterating over no null, with at least one element.
   */
  Iterable<DocumentRequest> getDocumentRequests() ;


}
