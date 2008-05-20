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
 * A request which also supports Raw Resources and errors.
 * 
 * @author Laurent Caillette
 */
public final class PolymorphicRequest extends AbstractRequest {

  protected PolymorphicRequest() { }

  @Override
  public String toString() {
    return
        ClassUtils.getShortClassName( getClass() ) + "[" +
        "isErrorRequest" + "=" + getDisplayProblems() +
        ";documentMimeType" + "=" +
            ( getRenditionMimeType() == null ? "<null>" : getRenditionMimeType().getMimeName() ) +
        ";documentSourceName" + "=" + getDocumentSourceName() +
        ";originalTarget" + "=" + getOriginalTarget() +
        "]"
    ;
  }

// ============
// errorRequest
// ============

  private boolean displayProblems = false ;

  public boolean getDisplayProblems() {
    return displayProblems;
  }

  protected void setDisplayProblems( boolean displayProblems ) {
    this.displayProblems = displayProblems;
  }


  
}
