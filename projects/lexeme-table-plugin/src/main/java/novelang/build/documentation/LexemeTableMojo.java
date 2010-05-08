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
package novelang.build.documentation;

import java.io.File;
import java.io.IOException;

import novelang.system.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generates Java source containing lexemes extracted from an ANTLR grammar.
 *
 * @goal generate
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class LexemeTableMojo extends AbstractMojo {

  /**
   * Target file for generation.
   *
   * @parameter expression="${generate.destinationFile}"
   * @required
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private File destinationFile;

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info( "Generating into: '" + destinationFile.getAbsolutePath() + "'" ) ;

    LogFactory.setMavenPluginLog( getLog() ) ;

    try {
      LexemeTable.writeSourceDocument( destinationFile ) ;
    } catch( IOException e ) {
      throw new MojoExecutionException( e.getMessage() ) ;
    }

  }
}