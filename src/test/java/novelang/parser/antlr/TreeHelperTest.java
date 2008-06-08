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

package novelang.parser.antlr;

import org.junit.Test;
import static novelang.model.common.NodeKind.TITLE;
import static novelang.model.common.NodeKind.WORD;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class TreeHelperTest {

  @Test
  public void testEqualityOk() {
    TreeFixture.assertEquals(
        tree( TITLE, tree( WORD, "w0" ) ),
        tree( TITLE, tree( WORD, "w0" ) )
    ) ;
  }

  @Test( expected = AssertionError.class )
  public void testEqualityFail() {
    TreeFixture.assertEquals(
        tree( TITLE, tree( WORD, "xx" ) ),
        tree( TITLE, tree( WORD, "w0" ) )
    ) ;
  }

}
