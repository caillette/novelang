/*
 * Copyright (C) 2011 Laurent Caillette
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
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;
import org.novelang.treemangling.SeparatorsMangler;

/**
 * Tests for {@link TreeFixtureTest}.
 * 
 * @author Laurent Caillette
 */
public class TreeFixtureTest {
  
  @Test
  public void testRemoveSeparators() {
    TreeFixture.assertEqualsNoSeparators(  
        tree(
            NOVELLA,
            tree( 
                PARAGRAPH_REGULAR,
                tree( WORD_, "w" )
            )
        ),
        SeparatorsMangler.removeSeparators( tree(
            NOVELLA,
            tree( WHITESPACE_ ),
            tree( LINE_BREAK_ ),
            tree( 
                PARAGRAPH_REGULAR,
                tree( WHITESPACE_ ),
                tree( WORD_, "w" )
            )
        ) )
    ) ;
  }
}
