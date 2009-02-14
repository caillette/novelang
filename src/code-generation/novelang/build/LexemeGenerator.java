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

package novelang.build;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.lang.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import novelang.parser.Lexeme;


/**
 * Generates a Java class from lexer tokens declared in and ANTLR grammar.
 *
 * @author Laurent Caillette
 */
public class LexemeGenerator extends JavaGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( LexemeGenerator.class ) ;

  public LexemeGenerator(
      File grammarFile,
      String packageName,
      String className,
      File targetDirectory
  ) throws IOException
  {
    super( grammarFile, packageName, className, targetDirectory ) ;
  }

  protected String generateCode() {
    final Set< Lexeme > lexemeDeclarations =
        LexemeDeclarationExtractor.extractLexemeDeclarations( getGrammar() ) ;

    return generateJavaEnumeration( lexemeDeclarations ) ;
  } 

  protected String generateJavaEnumeration( Set< Lexeme > lexemes ) {
    final StringTemplate javaEnum = createStringTemplate( "lexemesClass" ) ;
    javaEnum.setAttribute( "lexemes", lexemes ) ;
    return javaEnum.toString() ;

  }

  public static final class Item {

    public final String declaration ;
    public final String javaComment ;

    public Item( String declaration, String javaComment ) {
      this.declaration = declaration;
      this.javaComment = javaComment;
    }
  }





}