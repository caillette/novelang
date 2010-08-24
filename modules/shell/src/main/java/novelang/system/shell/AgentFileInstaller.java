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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.io.FileUtils;

/**
 * Installs the jar of the {@link novelang.system.shell.insider.InsiderAgent} somewhere
 * on the local filesystem.
 * The JVM installs agents from plain jar files.
 * <p>
 * This class applies the
 * <a href="http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom">Initialization on demand idiom</a>.
 *
 * @author Laurent Caillette
 */
public class AgentFileInstaller {

  private static final Log LOG = LogFactory.getLog( AgentFileInstaller.class ) ;

  /**
   * Name of the system property to set the agent jar file externally.
   * This is useful when running tests from an IDE.
   */
  public static final String AGENTJARFILE_SYSTEMPROPERTYNAME =
      "novelang.system.shell.agentjarfile" ;

  /**
   * Version of the embedded jar. Needed to find its resource name.
   */
  private final String version ;

  private final File jarFile ;


  private AgentFileInstaller() throws IOException {

    version = Resources.toString(
        AgentFileInstaller.class.getResource( VERSION_RESOURCE_NAME ),
        Charsets.UTF_8
    ) ;

    final String jarFileNameFromSystemProperty =
        System.getProperty( AGENTJARFILE_SYSTEMPROPERTYNAME ) ;

    if( jarFileNameFromSystemProperty == null ) {
      try {
        jarFile = File.createTempFile( "Novelang-insider-agent", ".jar" ) ;
        copyResourceToFile( JAR_RESOURCE_NAME_RADIX + version + ".jar", jarFile ) ;
      } catch( IOException e ) {
        throw new RuntimeException( e ) ;
      }
    } else {
      jarFile = new File( jarFileNameFromSystemProperty ) ;
      if( ! jarFile.isFile() ) {
        throw new IllegalArgumentException(
            "Jar file '" + jarFile.getAbsolutePath() + "' doesn't exist as a file" ) ;
      }
      LOG.info( "Using jar file '" + jarFile.getAbsolutePath() + "' " +
          "set from system property " + AGENTJARFILE_SYSTEMPROPERTYNAME + "." ) ;
    }

  }

  private final Object lock = new Object() ;


  /**
   * Returns a {@code File} object referencing the jar containing the
   * {@link novelang.system.shell.insider.InsiderAgent}.
   *
   * @return a non-null object.
   */
  public File getJarFile() {
    return jarFile ;
  }



  public void copyVersionedJarToFile( final String jarResourceRadix, final File file )
      throws IOException
  {
    copyResourceToFile( jarResourceRadix + version + ".jar", file ) ;
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
   */
  @SuppressWarnings( { "HardcodedFileSeparator" } )
  private static final String JAR_RESOURCE_NAME_RADIX =
      "/novelang/system/shell/Novelang-insider-" ;

  /**
   * Defined by the assembly of the Insider project.
   */
  @SuppressWarnings( { "HardcodedFileSeparator" } )
  private static final String VERSION_RESOURCE_NAME=
      "/novelang/system/shell/version.txt" ;


// ========================
// Initialization on demand
// ========================

  @SuppressWarnings( { "UtilityClassWithoutPrivateConstructor" } )
  private static class LazyHolder {
    private static final AgentFileInstaller INSTANCE ;
    static {
      try {
        INSTANCE = new AgentFileInstaller() ;
      } catch( IOException e ) {
        throw new RuntimeException( e ) ;
      }
    }
  }

  public static AgentFileInstaller getInstance() {
    return LazyHolder.INSTANCE ;
  }

}
