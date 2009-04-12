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

import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.tree;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

/**
 * Tests various features that aim to attach a block inside double quotes to a URL as its name.
 * 
 * @author Laurent Caillette
 */
public class NamedUrlTest {
  
  public static final String BREAK = "\n" ;

  private final ParserMethod PARSERMETHOD_PART =
      new ParserMethod( "part" ) ;
  private final ParserMethod PARSERMETHOD_SMALL_DASHED_LIST_ITEM =
      new ParserMethod( "smallDashedListItem" ) ;
  private final ParserMethod PARSERMETHOD_PARAGRAPH =
      new ParserMethod( "paragraph" ) ;

  @Test
  public void namedUrlWithIndentOutsideParagraph() throws RecognitionException {
    PARSERMETHOD_PART.checkTree(
        "  \"name\"" + BREAK +
        "http://foo.com"
        ,
        tree( 
            PART,
            tree( WHITESPACE_, "  " ),
            tree( 
                PARAGRAPH_REGULAR,
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL, "http://foo.com" )
            
            )
        )
    ) ;
  }

  @Test
  public void indentInsideParagraph() throws RecognitionException {
    PARSERMETHOD_PART.checkTree(
        "nothing" + BREAK +
        "  \"name\"" + BREAK +
        "http://foo.com"
        ,
        tree( 
            PART,
            tree(
                PARAGRAPH_REGULAR,
                tree( WORD_, "nothing" ),
                tree( LINE_BREAK_ ),
                tree( WHITESPACE_, "  " ),
                tree( BLOCK_INSIDE_DOUBLE_QUOTES, tree( WORD_, "name" ) ),
                tree( LINE_BREAK_ ),
                tree( URL, "http://foo.com" )
            
            )
        )
    ) ;
  }





}