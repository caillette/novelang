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
package novelang.daemon;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.FontDescriptor;

/**
 * @author Laurent Caillette
 */
public class FontListHandler extends GenericHandler{

  private static final Logger LOGGER = LoggerFactory.getLogger( FontListHandler.class ) ;

  private final Iterable< FontDescriptor > fontDescriptors ;

  public FontListHandler( RenderingConfiguration renderingConfiguration ) {
    fontDescriptors = renderingConfiguration.getFontDescriptors() ;
  }

  protected void doHandle( 
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {
    if( "/~fonts.pdf".equals( target ) ) {
      LOGGER.info( "Font listing requested" ) ;
    }
  }
}
