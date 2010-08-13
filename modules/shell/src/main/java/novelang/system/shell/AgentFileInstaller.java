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
package novelang.system.shell;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;

import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.io.FileUtils;

/**
 * Installs the jar of the {@link novelang.system.shell.insider.InsiderAgent} somewhere
 * on the local filesystem.
 * The JVM installs agents from plain jar files.
 *
 * @author Laurent Caillette
 */
public class AgentFileInstaller {

  private static final Log LOG = LogFactory.getLog( AgentFileInstaller.class ) ;

  private AgentFileInstaller() { }

  private static final Object LOCK = new Object() ;

  @SuppressWarnings( { "StaticNonFinalField" } )
  private static File jarFile = null ;

  /**
   * Returns a {@code File} object referencing the jar containing the
   * {@link novelang.system.shell.insider.InsiderAgent}.
   *
   * @return a non-null object.
   */
  public static File getJarFile() {
    synchronized( LOCK ) {
      if( jarFile == null ) {
        try {
          jarFile = File.createTempFile( "Novelang-insider-agent", ".jar" ) ;
          copyResourceToFile( JAR_RESOURCE_NAME, jarFile ) ;
        } catch( IOException e ) {
          throw new RuntimeException( e ) ;
        }
      }
      return jarFile ;
    }
  }

  public static void copyResourceToFile( final String resourceName, final File file )
      throws IOException {
    final URL resourceUrl = AgentFileInstaller.class.getResource( resourceName ) ;
    if( resourceUrl == null ) {
      throw new MissingResourceException(
          "The resource '" + resourceName +"' does not appear in the classpath " +
              "(Maven should handle this, IDEs probably won't)",
          AgentFileInstaller.class.getName(),
          resourceName
      ) ;
    }

    LOG.info( "Copying resource '" + resourceName + "' to " +
            "'" + file.getAbsolutePath() + "'" ) ;
    FileUtils.copyURLToFile( resourceUrl, file ) ;
  }

  /**
   * Defined by the assembly of the Insider project.
   * TODO: get correct version everytime.
   */
  @SuppressWarnings( { "HardcodedFileSeparator" } )
  private static final String JAR_RESOURCE_NAME = "/Novelang-insider-SNAPSHOT.jar" ;


}
