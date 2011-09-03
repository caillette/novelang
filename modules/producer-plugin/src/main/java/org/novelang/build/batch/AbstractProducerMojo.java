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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.novelang.Version;
import org.novelang.VersionFormatException;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractProducerMojo extends AbstractMojo {
  /**
   * Base directory for documents sources.
   *
   * @parameter expression="${produce.contentRootDirectory}"
   * @required
   */
  protected File contentRootDirectory = null ;

  /**
   * Directory where to write log file(s) to.
   *
   * @parameter expression="${produce.logDirectory}"
   */
  protected File logDirectory = null ;

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project ;

  protected Version getVersion() throws MojoExecutionException {
    final Version version ;
    try {
      version = Version.parse( project.getVersion() ) ;
    } catch( VersionFormatException e ) {
      throw new MojoExecutionException( "Couldn't parse version '" + project.getVersion() + "'" ) ;
    }
    return version ;
  }

  protected final File getReleaseNoteSourceFile( final Version version ) throws IOException {
    final File versionFile = new File(
        contentRootDirectory,
        "versions/" + version.getName() + ".novella"
    ).getCanonicalFile() ;
    return versionFile ;
  }
}
