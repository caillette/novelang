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
package org.novelang.treemangling;

import static org.novelang.parser.NodeKind.EMBEDDED_LIST_ITEM_WITH_HYPHEN_;
import static org.novelang.parser.NodeKind._EMBEDDED_LIST_WITH_HYPHEN;

import org.novelang.parser.NodeKind;

/**
 * Tests for {@link EmbeddedListMangler}.
 * 
 * @author Laurent Caillette
 */
public class EmbeddedListManglerWithHyphenTest extends AbstractEmbeddedListManglerTest {


// =======  
// Fixture
// =======  


  
  @Override
  protected NodeKind getParsedToken() {
    return EMBEDDED_LIST_ITEM_WITH_HYPHEN_ ;
  }

  @Override
  protected NodeKind getSyntheticToken() {
    return _EMBEDDED_LIST_WITH_HYPHEN ;
  }
}
