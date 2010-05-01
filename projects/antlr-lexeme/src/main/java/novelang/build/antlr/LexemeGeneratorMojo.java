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
package novelang.build.antlr;

import java.io.File;
import java.io.IOException;

import novelang.build.CodeGenerationTools;
import novelang.build.antlr.LexemeGenerator;
import novelang.system.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generates binary file containing lexemes extracted from an ANTLR grammar.
 *
 * @goal generate
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class LexemeGeneratorMojo extends AbstractMojo {

  /**
   * Target directory for generation (where to generate parent packages).
   *
   * @parameter expression="${generate.packageRootDirectory}"
   * @required
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private File packageRootDirectory;

  /**
   * Target directory for generation (where to generate parent packages).
   *
   * @parameter expression="${generate.grammarFile}"
   * @required
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private File grammarFile;

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info( "Generating into: '" + packageRootDirectory + "'" ) ;

    LogFactory.setMavenPluginLog( getLog() ) ;

    try {
      new LexemeGenerator(
          grammarFile,
          CodeGenerationTools.UNICODE_NAMES_PACKAGE,
          CodeGenerationTools.UNICODE_NAMES_BINARY,
          packageRootDirectory
      ).generate() ;
    } catch( IOException e ) {
      throw new MojoExecutionException( e.getMessage() ) ;
    }

  }
}