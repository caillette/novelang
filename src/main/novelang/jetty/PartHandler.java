/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.jetty;

import java.io.IOException;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.mortbay.jetty.Request;
import com.google.common.base.Objects;
import novelang.model.implementation.Part;

/**
 * Handles requests with target prefixed by "parts/"
 * (like {@code http://localhost:8888/parts/mystuff/part-1.pdf}).
 *
 * @author Laurent Caillette
 */
public class PartHandler extends AbstractDocumentHandler {

  private final File basedir ;


  public PartHandler( File basedir ) {
    this.basedir = Objects.nonNull( basedir ) ;
  }

  private static final String PART_PREFIX = "/parts" ;

  private static Pattern PATTERN = Pattern.compile(
      PART_PREFIX + "((?:(?:\\/(?:(?:\\w|\\-|\\_)++))++)\\.\\w++)" ) ;

  public void handle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {
    if( target.startsWith( PART_PREFIX ) ) { //
      final String path = extractPath( target ) ;
      if( null != path ) {
        final File partFile = new File( basedir, path ) ;
        if( partFile.exists() ) {
          final Part part = new Part( partFile ) ;
          serve( request, response, part, target ) ;
        } else {
          response.setStatus( HttpServletResponse.SC_BAD_REQUEST ) ;
        }
        ( ( Request ) request ).setHandled( true ) ;
      }
    }
  }

  protected static String extractPath( String target ) {
    final Matcher matcher = PATTERN.matcher( target ) ;
    if( matcher.find() && matcher.groupCount() > 0 ) {
      return matcher.group( 1 ) ;
    }
    return null ;
  }

}
