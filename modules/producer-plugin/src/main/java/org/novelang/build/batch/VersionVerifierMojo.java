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
package org.novelang.build.batch;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.novelang.Version;
import org.novelang.logger.ConcreteLoggerFactory;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;


/**
 * Verifies there is a file named {@code versions/${project.version}.novella}
 * under {@link #contentRootDirectory}.
 * This is useful to make the build fail clear and fast if there is a missing
 * release note.
 *
 * @goal check
 *
 * @requiresDependencyResolution runtime
 *
 * @threadSafe
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class VersionVerifierMojo extends AbstractProducerMojo {



  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    ConcreteLoggerFactory.setMojoLog( getLog() ) ;
    LoggerFactory.configurationComplete() ;
    
    
    final Logger log = LoggerFactory.getLogger( getClass() ) ;


    final File versionFile;
    final Version version = getVersion() ;
    try {
      versionFile = getReleaseNoteSourceFile( version ) ;
    } catch( IOException e ) {
      throw new MojoExecutionException( "Couldn't create File object for " + version, e ) ;
    }
    if( ! versionFile.exists() ) {
      throw new MojoExecutionException( "Missing '" + versionFile.getAbsolutePath() + "'" ) ;
    }
    log.info( "Found '", versionFile.getAbsolutePath(), "'." ) ;

  }

}
