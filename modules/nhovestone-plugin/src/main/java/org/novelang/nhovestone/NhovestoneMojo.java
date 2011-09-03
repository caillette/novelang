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
package org.novelang.nhovestone;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.novelang.Version;
import org.novelang.logger.ConcreteLoggerFactory;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

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
   * @required
   */
  private String novelangVersions = null ;

  /**
   * Iteration count for warmump.
   *
   * @parameter
   */
  private int warmupIterationCount = 1000;


  /**
   * Maximum number of requests for each benchmarked Novelang server, iterations not included.
   *
   * @parameter
   */
  private int maximumIterations = 10000;

  /**
   * Size of JVM heap in megabytes.
   *
   * @parameter
   */
  private int jvmHeapSizeMegabytes = 32;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    ConcreteLoggerFactory.setMojoLog( getLog() ) ;
    LoggerFactory.configurationComplete() ;

    final Logger logger = LoggerFactory.getLogger( NhovestoneMojo.class ) ;
    logger.info( "Using working directory: '" + workingDirectory + "'." ) ;
    logger.info( "Using distributions directory: '" + distributionsDirectory + "'." ) ;
    final String benchmarkedVersions = novelangVersions == null ? "SNAPSHOT" : novelangVersions ;
    logger.info( "Using version(s): '" + benchmarkedVersions + "'." ) ;
    logger.info( "Using warmupIterationCount: " + warmupIterationCount + "." ) ;
    logger.info( "Using maximumIterations: " + maximumIterations + "." ) ;
    logger.info( "Using jvmHeapSizeMegabytes: " + jvmHeapSizeMegabytes + "." ) ;


    try {
      final Iterable< Version > versions = NhovestoneTools.parseVersions( benchmarkedVersions ) ;
      Nhovestone.run(
          logger,
          workingDirectory,
          distributionsDirectory,
          versions,
          warmupIterationCount,
          maximumIterations,
          jvmHeapSizeMegabytes
      ) ;
    } catch( Exception e ) {
      throw new MojoExecutionException( "Couldn't run Nhovestone", e );
    }
  }
}
