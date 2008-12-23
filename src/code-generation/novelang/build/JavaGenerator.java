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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplateGroupLoader;
import org.antlr.stringtemplate.CommonGroupLoader;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplate;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 * Single entry point for the build: generates all needed Java files from ANTLR grammar.
 * 
 * @author Laurent Caillette
 */
public abstract class JavaGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger( JavaGenerator.class ) ;

  private static final String JAVA_ENUMERATION = "NodeKind";

  protected static final String JAVA_SUFFIX = ".java" ;

  private final String grammar ;
  private final String packageName ;
  private final String className ;
  private final File targetFile ;
  private final String generatorName ;
  private final String generationTimestamp ;

  public JavaGenerator (
      File grammarFile,
      String packageName, 
      String className, 
      File targetDirectory 
  ) throws IOException
  {
    this.grammar = readGrammar( grammarFile ) ;
    this.packageName = packageName ;
    this.className = className ;
    final String relativePath = packageName.replace( '.', '/' ) ;
    this.targetFile = new File( targetDirectory, relativePath + "/" + className + JAVA_SUFFIX ) ;
    this.generatorName = getClass().getName() ;
    this.generationTimestamp = new Date().toString() ;
  }

  public final String getGrammar() {
    return grammar;
  }

  public final void generate() throws IOException {
    final String code = generateCode() ;
    IOUtils.write(
        code,
        new FileOutputStream( targetFile )
    ) ;
  }

  protected abstract String generateCode() throws IOException ;


  public static String readGrammar( File grammarFile ) throws IOException {
    return IOUtils.toString( new FileInputStream( grammarFile ) ) ;
  }


  public static void main( String[] args ) throws IOException {
    
    if( args.length == 2 ) {
      final File grammar = new File( args[ 0 ] ) ;
      final File targetDirectory = new File( args[ 1 ] ) ;

      new TokenEnumerationGenerator(
          grammar,
          "novelang.parser",
          JAVA_ENUMERATION,
          targetDirectory
      ).generate() ;

    } else {
      throw new IllegalArgumentException( 
          "Usage: " + ClassUtils.getShortClassName( JavaGenerator.class ) + 
          " <grammar-file> <target-directory>"
      ) ;
    }
    
  }

  private static final StringTemplateGroup STRINGTEMPLATEGROUP = loadStringTemplateGroup() ;

  private static final StringTemplateErrorListener STRING_TEMPLATE_ERROR_LISTENER =
      new StringTemplateErrorListener() {
        public void error( String s, Throwable throwable ) {
          throw new RuntimeException( s, throwable ) ;
        }
        public void warning( String s ) {
          throw new RuntimeException( s ) ;
        }
      }
  ;

  private static StringTemplateGroup loadStringTemplateGroup() {
    final String templateDirectory =
        ClassUtils.getPackageName( TokenEnumerationGenerator.class ).replace( '.', '/' ) ;
    LOGGER.info( "Loading StringTemplates from classpath directory: '{}'", templateDirectory ) ;

    final StringTemplateGroupLoader loader =
        new CommonGroupLoader( templateDirectory, STRING_TEMPLATE_ERROR_LISTENER ) ;
    StringTemplateGroup.registerGroupLoader( loader ) ;
    return StringTemplateGroup.loadGroup( "java" ) ;
  }

  protected final StringTemplate createStringTemplate( String templateName ) {
    final StringTemplate template = STRINGTEMPLATEGROUP.getInstanceOf( templateName ) ;

    template.setAttribute( "package", packageName ) ;
    template.setAttribute( "name", className ) ;
    template.setAttribute( "generatorName", generatorName ) ;
    template.setAttribute( "generationTimestamp", generationTimestamp ) ;

    return template ;

  }
}
