/*
 * Copyright (C) 2010 Laurent Caillette
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

import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_;
import static org.novelang.parser.NodeKind._LIST_WITH_TRIPLE_HYPHEN;

/**
 * Tests for {@link org.novelang.treemangling.ListMangler}.
 *
 * @author Laurent Caillette
 */
public class ListManglerWithTripleHyphenTest extends AbstractListManglerTest {



// =======
// Fixture
// =======

  @Override
  protected NodeKind getSyntheticToken() {
    return _LIST_WITH_TRIPLE_HYPHEN;
  }


  @Override
  protected NodeKind getParsedToken() {
    return PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_;
  }
}