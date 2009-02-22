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
package novelang.produce;

import org.apache.commons.lang.ClassUtils;

/**
 * Contains everything needed to build a specific requested Document
 * but does <em>not</em> support errors and Raw Resources.
 *
 * @author Laurent Caillette
 */
public class DocumentRequest extends AbstractRequest {

  @Override
  public String toString() {
    return
        ClassUtils.getShortClassName( getClass() ) + "[" +
        "documentSourceName" + "=" + getDocumentSourceName() +
        ";originalTarget" + "=" + getOriginalTarget() +
        ";rendered" + "=" + isRendered() +
        ";stylesheet" + "=" + getAlternateStylesheet() +
        "]"
    ;
  }

  

}

