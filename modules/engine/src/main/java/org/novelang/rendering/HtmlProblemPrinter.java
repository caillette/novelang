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

package org.novelang.rendering;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.novelang.common.Location;
import org.novelang.common.Problem;

/**
 * Almost does what it claims.
 * 
 * @author Laurent Caillette
 */
public class HtmlProblemPrinter {
  
  public void printProblems(
      final OutputStream outputStream,
      final Iterable< Problem > problems,
      final String requestedUri
  ) throws IOException
  {
    final PrintWriter writer = new PrintWriter( outputStream ) ;
    writer.println( "<html>" ) ;
    writer.println( "<body>" ) ;
    writer.println(
        "<p>Requested: <a href=\"" + requestedUri + "\">" + requestedUri + "</a></p>" ) ;
    for( final Problem problem : problems ) {
      writer.println( "<p>" ) ;
      final Location location = problem.getLocation();
      writer.println( "<pre>" ) ;
      writer.println( location ) ;
      writer.println( "</pre>" ) ;
      writer.println( problem.getMessage() ) ;
      writer.println( "</p>" ) ;
    }
    writer.println( "</body>" ) ;
    writer.println( "</html>" ) ;
    writer.flush() ;
  }


}
