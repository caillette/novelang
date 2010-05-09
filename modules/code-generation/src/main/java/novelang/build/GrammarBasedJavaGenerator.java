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
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


/**
 * Base class for generating Java files from ANTLR grammar.
 * 
 * @author Laurent Caillette
 */
public abstract class GrammarBasedJavaGenerator extends JavaGenerator {

  private final File grammarFile ;
  private final String grammar ;

  protected GrammarBasedJavaGenerator(
      final File grammarFile,
      final String packageName, 
      final String className, 
      final File targetDirectory 
  ) throws IOException
  {
    super( className, packageName, targetDirectory );
    this.grammarFile = grammarFile.getCanonicalFile() ;
    this.grammar = readGrammar( grammarFile ) ;

  }

  public final String getGrammar() {
    return grammar;
  }

  public File getGrammarFile() {
    return grammarFile ;
  }


  public static String readGrammar( final File grammarFile ) throws IOException {
    return FileUtils.readFileToString( grammarFile ) ;
  }



}
