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

import static org.novelang.parser.NodeKind.EMBEDDED_LIST_ITEM_NUMBERED_;

import org.novelang.parser.NodeKind;

/**
 * Tests for embedded list parsing.
 *
 * @author Laurent Caillette
 */
public class EmbeddedListWithNumberSignParsingTest extends AbstractEmbeddedListParsingTest{

// =======
// Fixture
// =======

  @Override
  protected char getMarker() {
    return '#' ;
  }


  private final ParserMethod PARSERMETHOD = new ParserMethod( "smallNumberedListItem" ) ;

  @Override
  protected NodeKind getNodeKind() {
    return EMBEDDED_LIST_ITEM_NUMBERED_ ;
  }

  @Override
  protected ParserMethod getParserListMethod() {
    return PARSERMETHOD;
  }
}
