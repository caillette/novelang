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
package novelang.build.unicode;

import java.io.File;
import java.io.IOException;

import novelang.build.CodeGenerationTools;
import novelang.system.LogFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generates binary file containing names of Unicode characters that fit in a 16-bit representation.
 *
 * @goal generate
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class UnicodeNamesGeneratorMojo extends AbstractMojo {

  /**
   * Target directory for generation (where to generate parent packages).
   *
   * @parameter expression="${generate.packageRootDirectory}"
   * @required
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private File packageRootDirectory;

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info( "Generating into: '" + packageRootDirectory + "'" ) ;

    LogFactory.setMavenPluginLog( getLog() ) ;

    try {
      new UnicodeNamesGenerator(
          CodeGenerationTools.UNICODE_NAMES_PACKAGE,
          CodeGenerationTools.UNICODE_NAMES_BINARY,
          packageRootDirectory
      ).generate() ;
    } catch( IOException e ) {
      throw new MojoExecutionException( e.getMessage() ) ;
    }

  }
}
