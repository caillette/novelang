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

package novelang.model.common;

/**
 * @author Laurent Caillette
 */
public class Location {

  private final String fileName ;
  private final int line ;
  private final int column ;


  public Location( String fileName, int line, int column ) {
    this.fileName = fileName ;
    this.line = line ;
    this.column = column ;
  }


  public String getFileName() {
    return fileName;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  @Override
  public String toString() {
    return
        ( -1 == line && -1 == column ? "-:-" : line + ":" + column ) +
        ":'" + fileName + "'"
    ;
  }
}
