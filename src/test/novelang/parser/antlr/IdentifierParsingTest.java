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

import static novelang.parser.NodeKind.ABSOLUTE_IDENTIFIER;
import static novelang.parser.NodeKind.RELATIVE_IDENTIFIER;
import static novelang.parser.NodeKind.COMPOSITE_IDENTIFIER;
import static novelang.parser.antlr.TreeFixture.tree;

import antlr.RecognitionException;
import org.junit.Test;

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


  @Test
  public void compositeIdentifier() throws RecognitionException {
    PARSERMETHOD_COMPOSITEIDENTIFIER.checkTreeAfterSeparatorRemoval(
        "\\\\ab\\solute",
        tree( COMPOSITE_IDENTIFIER, tree( "ab" ), tree( "solute" ) )

    ) ;
  }


  @Test
  public void parseRelativeIdentifier() throws RecognitionException {
    PARSERMETHOD_RELATIVEIDENTIFIER.checkTreeAfterSeparatorRemoval(
        "\\relative",
        tree( RELATIVE_IDENTIFIER, "relative" )

    ) ;
  }




// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_ABSOLUTEIDENTIFIER = 
      new ParserMethod( "absoluteIdentifier" ) ;
  private static final ParserMethod PARSERMETHOD_RELATIVEIDENTIFIER = 
      new ParserMethod( "relativeIdentifier" ) ;
  private static final ParserMethod PARSERMETHOD_COMPOSITEIDENTIFIER =
      new ParserMethod( "compositeIdentifier" ) ;

}