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

package org.novelang.build;

/**
 * Generates code.
 *
 * @author Laurent Caillette
 */
public class CodeGenerator {

  private CodeGenerator() { }


// ====
// Main
// ====

  public static void main( final String[] args )  {
/*

    if( args.length == 2 ) {
      final File grammar = new File( args[ 0 ] ) ;
      final File targetDirectory = new File( args[ 1 ] ).getCanonicalFile() ;

      if ( ! targetDirectory.exists() && ! targetDirectory.mkdirs() ) {
        throw new IOException( "Could not create: '" + targetDirectory.getPath() + "'" ) ;
      }

      new TokenEnumerationGenerator(
          grammar,
          CodeGenerationConstants.GENERIC_PARSER_PACKAGENAME,
          CodeGenerationConstants.NODE_ENUMERATION_CLASSNAME,
          targetDirectory
      ).generate() ;

      new LexemeGenerator(
          grammar,
          CodeGenerationConstants.GENERIC_PARSER_PACKAGENAME,
          CodeGenerationConstants.LEXEMES_CLASSNAME,
          targetDirectory
      ).generate() ;

      new AntlrGenerator(
          grammar,
          CodeGenerationConstants.ANTLR_PARSER_PACKAGENAME,
          CodeGenerationConstants.GRAMMAR_CLASSNAME,
          targetDirectory
      ).generate() ;

    } else {
      throw new IllegalArgumentException(
          "Usage: " + ClassUtils.getShortClassName( GrammarBasedJavaGenerator.class ) +
          " <grammar-file> <target-directory>"
      ) ;
    }
*/

  }
}
