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
 * This enum represents blocks (subset of a paragraph-like piece of source document) which
 * have a start and end delimiter and which must be paired.
 * <p>
 * Definition : <em>twin delimiters</em> are delimiters where the tokens marking the
 * opening of the block have no other purpose and where the tokens marking the closing of the
 * block have no other purpose, so blocks delimited by twin delimiters can be directly nested.
 * <p>
 * Definition : <em>only delimiters</em> are delimiters which are the same for the beginning
 * and the end of the block.
 *
 * @see novelang.parser.antlr.BlockDelimiterVerifier
 *
 * @author Laurent Caillette
 */
public enum BlockDelimiter {

  PARENTHESIS( true, "(", ")" ),
  SQUARE_BRACKETS( true, "[", "]" ),
  DOUBLE_QUOTES( false, "\"", "\"" ),
  SOLIDUS_PAIRS( false, "//", "//" ),
  TWO_HYPHENS( false, "--", "--", "-_" ),
  ;

  private final boolean twin ;
  private final String start ;
  private final String[] end ;

  private BlockDelimiter( boolean twin, String start, String... end ) {
    this.twin = twin ;
    this.start = start ;
    this.end = end.clone() ;
  }

  public String getStart() {
    return start ;
  }

  public String[] getEnd() {
    return end.clone() ;
  }

  public boolean isTwin() {
    return twin ;
  }
}
