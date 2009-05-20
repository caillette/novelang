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
        "displayProblems" + "=" + getDisplayProblems() +
        ";documentMimeType" + "=" +
            ( getRenditionMimeType() == null ? "<null>" : getRenditionMimeType().getMimeName() ) +
        ";documentSourceName" + "=" + getDocumentSourceName() +
        ";rendered" + "=" + isRendered() +
        ";originalTarget" + "=" + getOriginalTarget() +
        ";stylesheet" + "=" + getAlternateStylesheet() +
        ";tags" + "=" + getTags() +
        "]"
    ;
  }

  @Override
  public boolean equals( Object o ) {
    if( this == o ) {
      return true ;
    }
    if( o == null || getClass() != o.getClass() ) {
      return false;
    }
    if( !super.equals( o ) ) {
      return false;
    }

    final PolymorphicRequest that = ( PolymorphicRequest ) o ;

    if( displayProblems != that.displayProblems ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ( displayProblems ? 1 : 0 );
    return result;
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
