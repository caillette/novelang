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
package novelang.parser.antlr;

import org.junit.Test;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.treemangling.SeparatorsMangler;

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
            PART,
            tree( 
                PARAGRAPH_REGULAR,
                tree( WORD_, "w" )
            )
        ),
        SeparatorsMangler.removeSeparators( tree(
            PART,
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
