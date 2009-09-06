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
package novelang.book;

import java.io.File;
import java.io.IOException;

import novelang.system.DefaultCharset;
import com.google.common.collect.ImmutableSet;

/**
 * Some methods for creating {@link Book} instances easily.
 *
 * @author Laurent Caillette
 */
public class BookTestTools {

  public static Book createBook(
      File baseDirectory,
      String content
  ) {
    return new Book(
        baseDirectory,
        baseDirectory,
        content,
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< String >of()
    ) ;
  }

  public static Book createBook(
      File bookFile
  ) throws IOException {
    return new Book(
        bookFile.getParentFile(),
        bookFile,
        DefaultCharset.SOURCE,
        DefaultCharset.RENDERING,
        ImmutableSet.< String >of()
    ) ;
  }


}