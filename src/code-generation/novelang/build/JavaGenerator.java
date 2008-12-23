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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Single entry point for the build: generates all needed Java files from ANTLR grammar.
 * 
 * @author Laurent Caillette
 */
public class JavaGenerator {
  
  private static final String JAVA_ENUMERATION = "NodeKind";
  protected static final String JAVA_SUFFIX = ".java";

  private final String grammar ;
  private final String packageName ;
  private final String className ;
  private final File targetDirectory ;

  public JavaGenerator( 
      String grammar, 
      String packageName, 
      String className, 
      File targetDirectory 
  ) {
    this.grammar = grammar;
    this.packageName = packageName;
    this.className = className;
    this.targetDirectory = targetDirectory;
  }

  public static String readGrammar( File grammarFile ) throws IOException {
    return IOUtils.toString( new FileInputStream( grammarFile ) ) ;
  }

  public static void main( String[] args ) throws IOException {
    
    if( args.length == 2 ) {
      final String grammar = readGrammar( new File( args[ 0 ] ) ) ;
      final File targetDirectory = new File( args[ 1 ] ) ;
      
      final String javaEnumeration = TokenEnumerationGenerator.generateJavaEnumeration( 
          "novelang.parser",
          JAVA_ENUMERATION, 
          TokenEnumerationGenerator.findAntlrTokens( grammar ) 
      ) ;
      
      IOUtils.write( 
          javaEnumeration, 
          new FileOutputStream( new File( targetDirectory, JAVA_ENUMERATION + JAVA_SUFFIX ) ) 
      ) ;
      
    } else {
      throw new IllegalArgumentException( 
          "Usage: " + ClassUtils.getShortClassName( JavaGenerator.class ) + 
          " <grammar-file> <target-directory>"
      ) ;
    }
    
  }
  
}
