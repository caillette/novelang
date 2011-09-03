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

package org.novelang.build.antlr;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;

import org.novelang.build.GrammarBasedJavaGenerator;
import org.novelang.parser.shared.Lexeme;


/**
 * Generates a Java class from lexer tokens declared in and ANTLR grammar.
 *
 * @author Laurent Caillette
 */
public class LexemeGenerator extends GrammarBasedJavaGenerator {

  public LexemeGenerator(
      final File grammarFile,
      final String packageName,
      final String className,
      final File targetDirectory
  ) throws IOException
  {
    super( grammarFile, packageName, className, targetDirectory ) ;
  }

  @Override
  protected String generateCode() {
    final Set< Lexeme > lexemeDeclarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( getGrammar() ) ;

    return generateJavaEnumeration( lexemeDeclarations ) ;
  } 

  protected String generateJavaEnumeration( final Set< Lexeme > lexemes ) {
    final StringTemplate javaEnum = createStringTemplate( "lexemesClass" ) ;
    javaEnum.setAttribute( "lexemes", lexemes ) ;
    return javaEnum.toString() ;

  }





}