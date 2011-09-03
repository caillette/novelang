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

import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN;
import static org.novelang.parser.NodeKind._LIST_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN;

/**
 * Tests for {@link ListMangler}.
 *
 * @author Laurent Caillette
 */
public class ListManglerWithDoubleHyphenAndPlusSignTest extends AbstractListManglerTest {



// =======
// Fixture
// =======

  @Override
  protected NodeKind getSyntheticToken() {
    return _LIST_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN;
  }


  @Override
  protected NodeKind getParsedToken() {
    return PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN;
  }
}