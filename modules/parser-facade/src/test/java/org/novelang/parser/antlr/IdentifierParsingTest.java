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

import antlr.RecognitionException;
import org.junit.Test;
import static org.novelang.parser.NodeKind.ABSOLUTE_IDENTIFIER;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class IdentifierParsingTest {

  @Test
  public void parseAbsoluteIdentifier() throws RecognitionException {
    PARSERMETHOD_ABSOLUTEIDENTIFIER.checkTreeAfterSeparatorRemoval(
        "\\\\absolute",
        tree( ABSOLUTE_IDENTIFIER, "absolute" )

    ) ;
  }






// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_ABSOLUTEIDENTIFIER = 
      new ParserMethod( "absoluteIdentifier" ) ;

}