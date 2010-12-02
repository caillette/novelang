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
package org.novelang.build;

import java.io.File;
import java.io.IOException;

/**
 * Stuff that need to exist prior to the rest.
 *
 * @author Laurent Caillette
 */
public class CodeGenerationTools {

  private CodeGenerationTools() { }

  public static File resolveTargetFile(
      final File targetDirectory,
      final String packageName,
      final String simpleFileName
  ) throws IOException {
    return new File(
        targetDirectory,
        packageName.replace( '.', File.separatorChar ) + File.separatorChar + simpleFileName
    ).getCanonicalFile() ;
  }


  public static final String UNICODE_NAMES_PACKAGE = "org.novelang.parser.unicode" ;

  public static final String UNICODE_NAMES_BINARY = "names.bin" ;
}
