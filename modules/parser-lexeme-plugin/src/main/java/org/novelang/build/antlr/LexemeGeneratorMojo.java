/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.build.antlr;

import java.io.File;
import java.io.IOException;

import org.novelang.build.CodeGenerationConstants;
import org.novelang.logger.ConcreteLoggerFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.novelang.logger.LoggerFactory;

/**
 * Generates Java source containing lexemes extracted from an ANTLR grammar.
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
   * Grammar file to parse.
   *
   * @parameter expression="${generate.grammarFile}"
   * @required
   */
  @SuppressWarnings( { "InstanceVariableMayNotBeInitialized" } )
  private File grammarFile;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    ConcreteLoggerFactory.setMojoLog( getLog() ) ;
    LoggerFactory.configurationComplete() ;
    getLog().info( "Generating into: '" + packageRootDirectory + "'" ) ;


    try {
      new LexemeGenerator(
          grammarFile,
          CodeGenerationConstants.GENERIC_PARSER_PACKAGENAME,
          CodeGenerationConstants.LEXEMES_CLASSNAME,
          packageRootDirectory
      ).generate() ;
    } catch( IOException e ) {
      throw new MojoExecutionException( e.getMessage() ) ;
    }

  }
}