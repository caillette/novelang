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
package novelang.nhovestone;

import java.io.File;

import novelang.Version;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.system.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Laurent Caillette
 *
 * @goal generate
 *
 */
public class NhovestoneMojo extends AbstractMojo {

  /**
   * Where to create temporary files into.
   *
   * @parameter
   * @required
   */
  private File workingDirectory = null ;

  /**
   * Where to find Novelang releases, unzipped.
   *
   * @parameter
   * @required
   */
  private File distributionsDirectory = null ;

  /**
   * Comma-separated list of Novelang versions.
   *
   * @parameter
   */
  private String novelangVersions = null ;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    LogFactory.setMavenPluginLog( getLog() ) ;
    final Logger logger = LoggerFactory.getLogger( NhovestoneMojo.class ) ;
    logger.info( "Using working directory: '" + workingDirectory + "'." ) ;
    logger.info( "Using distributions directory: '" + distributionsDirectory + "'." ) ;
    final String benchmarkedVersions = novelangVersions == null ? "SNAPSHOT" : novelangVersions ;
    logger.info( "Using version(s): '" + benchmarkedVersions + "'." ) ;


    try {
      final Iterable< Version > versions = NhovestoneTools.parseVersions( benchmarkedVersions ) ;
      Nhovestone.run( logger, workingDirectory, distributionsDirectory, versions ) ;
    } catch( Exception e ) {
      throw new MojoExecutionException( "Couldn't run Nhovestone", e );
    }
  }
}
