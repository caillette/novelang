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
package novelang.composium;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import novelang.configuration.ConfigurationTools;
import novelang.designator.Tag;
import novelang.system.DefaultCharset;
import com.google.common.collect.ImmutableSet;

/**
 * Some methods for creating {@link Composium} instances easily.
 *
 * @author Laurent Caillette
 */
public class ComposiumTestTools {

  public static Composium createBook(
      final File baseDirectory,
      final String content
  ) {
    return new Composium(
        baseDirectory,
        baseDirectory,
        Executors.newSingleThreadExecutor( ConfigurationTools.getExecutorThreadFactory() ),
        content,
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;
  }

  public static Composium createBook(
      final File bookFile
  ) throws IOException {
    return new Composium(
        bookFile.getParentFile(),
        bookFile,
        Executors.newSingleThreadExecutor( ConfigurationTools.getExecutorThreadFactory() ),
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< Tag >of()
    ) ;
  }


}
