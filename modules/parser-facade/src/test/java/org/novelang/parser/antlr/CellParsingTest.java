/*
 * Copyright (C) 2009 Laurent Caillette
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
package org.novelang.parser.antlr;

import org.junit.Test;
import org.antlr.runtime.RecognitionException;
import static org.novelang.parser.antlr.TreeFixture.tree;
import static org.novelang.parser.antlr.AntlrTestHelper.BREAK;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.NodeKind.CELL;
import static org.novelang.parser.NodeKind.WORD_;

/**
 * Tests for cell parsing, including cell row and row sequence.
 *
 * @author Laurent Caillette
 */
public class CellParsingTest {

  /*package*/ static final ParserMethod PARSERMETHOD_CELL_ROW_SEQUENCE =
      new ParserMethod( "cellRowSequence" ) ;



  @Test
  public void cellRowSequence1x1() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| x |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree(
                CELL_ROW,
                tree( CELL, tree( WORD_, "x" ) )
            )
        )
    ) ;
  }

  @Test
  public void asteriskPairInsideCell() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| **x** |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree(
                CELL_ROW,
                tree( CELL,  tree( BLOCK_INSIDE_ASTERISK_PAIRS, tree( WORD_, "x" ) ) )
            )
        )
    ) ;
  }

  @Test
  public void cellRowSequence1x1Tagged() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "@t" + BREAK +
        "| x |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree( TAG, "t" ),
            tree(
                CELL_ROW,
                tree( CELL, tree( WORD_, "x" ) )
            )
        )
    ) ;
  }

  @Test
  public void cellRowSequence1x1ContainsImage() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| /foo.jpg |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree(
                CELL_ROW,
                tree( CELL, tree( RASTER_IMAGE, tree( RESOURCE_LOCATION, "/foo.jpg" ) ) )
            )
        )
    ) ;
  }

  @Test
  public void cellRowSequence2x2() throws RecognitionException {
    PARSERMETHOD_CELL_ROW_SEQUENCE.checkTreeAfterSeparatorRemoval(
        "| a | b   |" + BREAK +
        "|c  | d e |",
        tree(
            CELL_ROWS_WITH_VERTICAL_LINE,
            tree(
                CELL_ROW,
                tree( CELL, tree( WORD_, "a" ) ),
                tree( CELL, tree( WORD_, "b" ) )
            ),
            tree(
                CELL_ROW,
                tree( CELL, tree( WORD_, "c" ) ),
                tree( CELL, tree( WORD_, "d" ), tree( WORD_, "e" ) )
            )
        )
    ) ;
  }



}
