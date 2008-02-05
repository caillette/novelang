/*
 * Copyright (C) 2006 Laurent Caillette
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
package novelang.renderer;

import java.io.File;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

/**
 * @author Laurent Caillette
 */
public class PdfRenderer {

  private static final String NOVELANG_STYLES_DIR = "novelang.styles.dir" ;

  private static final Logger LOGGER = LoggerFactory.getLogger( PdfRenderer.class ) ;

  private static final File stylesDir ;
  static {
    final String stylesDirName = System.getProperty( NOVELANG_STYLES_DIR ) ;
    if( StringUtils.isBlank( stylesDirName ) ) {
      stylesDir = null ;
      LOGGER.debug( "No directory set for styles" ) ;
    } else {
      final File dir = new File( stylesDirName ) ;
      if( dir.exists() ) {
        stylesDir = dir ;
        LOGGER.info( "Styles directory set to '{}'", stylesDir.getAbsolutePath() ) ;
      } else {
        stylesDir = null ;
        LOGGER.warn( "Styles directory '{}' does not exist", stylesDir.getAbsolutePath() ) ;
      }
    }
  }

  public PdfRenderer( String stylesheetRelativeFileName ) {
    final StreamSource streamSource ;
    if( null == stylesDir ) {
      throw new RuntimeException(
          "No default stylesheet supported yet, set -D" + NOVELANG_STYLES_DIR + " instead") ;
    } else {
      streamSource = new StreamSource( new File( stylesDir, stylesheetRelativeFileName ) ) ;
    }


  }
}
