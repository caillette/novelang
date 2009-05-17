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
package novelang.common;

/**
 *
 * This enum is some kind of hack which duplicates some declarations in the grammar.
 * But it's quite uneasy to extract the delimiters from the grammar, as they are buried the code.
 * So we take the easy way here, hoping that a mismatch will be detected by unit tests.
 *
 * @author Laurent Caillette
 */
public enum BlockDelimiter {

  PARENTHESIS( "(", ")" ),
  SQUARE_BRACKETS( "[", "]" ),
  DOUBLE_QUOTES( "\"", "\"" ),
  SOLIDUS_PAIRS( "//", "//" ),
  TWO_HYPHENS( "--", "--", "-_" ),
  ;

  private final String start ;
  private final String[] end ;

  private BlockDelimiter( String start, String... end ) {
    this.start = start;
    this.end = end.clone() ;
  }

}
