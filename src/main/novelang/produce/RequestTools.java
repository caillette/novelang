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

import java.util.Map;

/**
 * Helper for creating concrete Requests.
 * This avoids accessing to package-protected {@link AbstractRequest}.
 * 
 * @author Laurent Caillette
 */
public class RequestTools {
  public static final String ERRORPAGE_SUFFIX = "/error.html";

  private RequestTools() { throw new Error( "Don't call this" ) ; }

  public static DocumentRequest createDocumentRequest( String rawRequest ) {
    return AbstractRequest.createDocumentRequest( rawRequest ) ;
  }

  public static PolymorphicRequest createPolymorphicRequest( String rawRequest ) {
    return AbstractRequest.createPolymorphicRequest( rawRequest ) ;
  }

}
