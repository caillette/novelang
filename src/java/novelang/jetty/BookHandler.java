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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.Request;
import com.google.common.base.Objects;
import novelang.model.implementation.Book;
import novelang.weaver.Weaver;
import novelang.renderer.PlainTextRenderer;

/**
 * @author Laurent Caillette
 */
public class BookHandler extends AbstractHandler {

  private final String bookIdentifier ;
  private final File structureFile;

  public BookHandler( String bookIdentifier, File structureFile ) {
    this.bookIdentifier = Objects.nonNull( bookIdentifier ) ;
    this.structureFile = Objects.nonNull( structureFile ) ;
  }

  public void handle(
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  )
      throws IOException, ServletException
  {
    final Book book = new Book( bookIdentifier, structureFile ) ;
    book.loadStructure() ;
    final Weaver weaver = new Weaver( book ) ;
    weaver.weave() ;
    final PlainTextRenderer renderer = new PlainTextRenderer() ;

    response.setContentType( "text/html" ) ;
    response.setStatus( HttpServletResponse.SC_OK ) ;
    renderer.render( book.createBookTree(), response.getOutputStream() ) ;
    ( ( Request ) request ).setHandled( true ) ;
  }

}
