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

package novelang.model.common;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.model.implementation.Part;

/**
 * @author Laurent Caillette
 */
public class FileLookupHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger( FileLookupHelper.class ) ;

  private FileLookupHelper() { }

  public static File load( 
      File basedir,
      String fileNameNoExtension,
      String... fileExtensions
  ) throws FileNotFoundException {
    final StringBuffer buffer = new StringBuffer( "Not found:" ) ;
    for( final String extension : fileExtensions ) {
      final File file = new File( basedir, fileNameNoExtension + "." + extension ) ;
      if( file.exists() ) {
        return file ;
      } else {
        buffer.append( "\n    '" ) ;
        buffer.append( file.getAbsolutePath() ) ;
        buffer.append( "'" ) ;
      }
    }
    throw new FileNotFoundException( buffer.toString() ) ;
  }

}
