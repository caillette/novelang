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
import org.antlr.runtime.RecognitionException;
import static novelang.model.common.NodeKind.*;
import novelang.model.common.Tree;
import static novelang.parser.antlr.TreeFixture.tree;
import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import static novelang.parser.antlr.AntlrTestHelper.functionCall;
import static novelang.parser.antlr.AntlrTestHelper.valuedArgument;

/**
 * @author Laurent Caillette
 */
public class BookParserTest {

  /**
   * This is used elsewhere as we must be sure to pass a tree of the same form as the
   * parser produces.
   */
  public static final Tree FUNCTIONCALLWITHURL_TREE = tree(
      FUNCTION_CALL,
      tree( FUNCTION_NAME, "function" ),
      tree(
          VALUED_ARGUMENT_PRIMARY,
          tree( URL, "file://my/file" )
      )
  );

  @Test
  public void functionCallBare() throws RecognitionException {
    functionCall(
        "function",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" )
        )
    ) ;
  }

  @Test
  public void functionCallWithParagraphBody() throws RecognitionException {
    functionCall(
        "function with paragraphbody",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree(
                VALUED_ARGUMENT_PRIMARY,
                tree( WORD, "with" ),
                tree( WORD, "paragraphbody" )
            )
        )
    ) ;
  }

  @Test
  public void functionCallWithUrl() throws RecognitionException {
    functionCall(
        "function file://my/file",
        FUNCTIONCALLWITHURL_TREE
    ) ;
  }

  @Test
  public void functionCallWithFlag() throws RecognitionException {
    functionCall(
        "function $flag",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree( VALUED_ARGUMENT_FLAG, "flag" )
        )
    ) ;
  }

  @Test
  public void functionCallWithMoreFlags() throws RecognitionException {
    functionCall(
        "function $flag1 " + BREAK +
        " $flag2",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree( VALUED_ARGUMENT_FLAG, "flag1" ),
            tree( VALUED_ARGUMENT_FLAG, "flag2" )
        )
    ) ;
  }

  @Test
  public void functionCallWithAncillaries() throws RecognitionException {
    functionCall(
        "function \\identifier1 " + BREAK +
        " \\identifier2",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree( VALUED_ARGUMENT_ANCILLARY, tree( IDENTIFIER, "identifier1" ) ),
            tree( VALUED_ARGUMENT_ANCILLARY, tree( IDENTIFIER, "identifier2" ) )
        )
    ) ;
  }



  @Test
  public void valuedArgumentAncillaryIsBlockIdentifier() throws RecognitionException {
    valuedArgument(
        "\\identifier",
        tree( VALUED_ARGUMENT_ANCILLARY,
            tree( IDENTIFIER, "identifier" )
        )
    ) ;
  }

  @Test
  public void valuedArgumentAncillaryIsBlockIdentifierAndModifier() throws RecognitionException {
    valuedArgument(
        "+\\identifier",
        tree( VALUED_ARGUMENT_ANCILLARY,
            tree( VALUED_ARGUMENT_MODIFIER, "+" ),
            tree( IDENTIFIER, "identifier" )
        )
    ) ;
  }

}
