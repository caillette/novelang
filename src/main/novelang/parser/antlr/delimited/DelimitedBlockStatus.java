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
package novelang.parser.antlr.delimited;

import com.google.common.base.Joiner;

/**
 * Keeps track of notifications received for a kind of {@link BlockDelimiter} inside a
 * {@link BlockDelimitersBoundary}.
 * 
 * @author Laurent Caillette
*/
/*package*/  final class DelimitedBlockStatus {
  private final BlockDelimiter blockDelimiter ;
  private int line = -1 ;
  private int column = -1 ;
  private int startCount = 0 ;
  private int reachEndCount = 0 ;
  private int endPassedCount = 0 ;
  private int missingDelimiterCount = 0 ;

  DelimitedBlockStatus( BlockDelimiter blockDelimiter ) {
    this.blockDelimiter = blockDelimiter ;
  }

  /*package*/ DelimitedBlockStatus updatePosition( int line, int column ) {
    if( line >= this.line && column > this.column ) {
      this.line = line ;
      this.column = column ;
    }
    return this ;
  }

  /*package*/  DelimitedBlockStatus increaseStartCount() {
    startCount++ ;
    return this ;
  }

  /*package*/  DelimitedBlockStatus increaseReachEndCount() {
    reachEndCount++ ;
    return this ;
  }

  /*package*/  DelimitedBlockStatus increaseEndPassedCount() {
    endPassedCount++ ;
    return this ;
  }

  /*package*/  DelimitedBlockStatus increaseMissingDelimiterCount() {
    missingDelimiterCount++ ;
    return this ;
  }

  public BlockDelimiter getBlockDelimiter() {
    return blockDelimiter ;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public boolean hasLocation() {
    return line >= 0 && column >= 0 ;
  }

  public String getInternalStatusAsString() {
    return "[ " + Joiner.on( " ; " ).join(
        "line=" + String.format( "%1$2d", line ),
        "column=" + String.format( "%1$2d", column ),
        "start=" + startCount,
        "reachEnd=" + reachEndCount,
        "endPassed=" + endPassedCount,
        "missingDelimiter=" + missingDelimiterCount,
        "isConsistent()=" + isConsistent()
    ) + " ]" ;
  }

  /**
   * Returns if counters reflect consistency.
   * Inconsistency may be caused by another problem elsewhere.
   */
  /*package*/  boolean isConsistent() {
    return
        missingDelimiterCount == 0
     && startCount == reachEndCount
     && reachEndCount == endPassedCount
    ;
  }

  @Override
  public String toString() {
    return
        "Status[ " + blockDelimiter + " ; " +
        "line=" + ( line >= 0 ? line : "x" ) + " ; " +
        "column=" + ( column >= 0 ? column : "x" ) + " ]"
    ;
  }
}
