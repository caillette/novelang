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

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import static novelang.model.common.NodeKind.*;
import novelang.model.common.SimpleTree;
import novelang.model.common.SyntacticTree;
import novelang.model.common.tree.TreeTools;
import static novelang.parser.antlr.AntlrTestHelper.*;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class BookParserTest {

  private static final Function< String, SyntacticTree> CREATE_VALUED_ARGUMENT_FLAG_FUNCTION =
      new Function< String, SyntacticTree>() {
        public SyntacticTree apply( String s ) {
          return new SimpleTree( VALUED_ARGUMENT_FLAG.name(), new SimpleTree( s ) ) ;
        }
      }
  ;

  /**
   * This is used elsewhere as we must be sure to pass a tree of the same form as the
   * parser produces.
   */
  public static final SyntacticTree createFunctionCallWithUrlTree(
      String fileName,
      String... flagArguments
  ) {
    SyntacticTree functionCall = new SimpleTree(
        FUNCTION_CALL.name(),
        new SimpleTree( FUNCTION_NAME.name(), new SimpleTree( "function" ) ),
        new SimpleTree(
            VALUED_ARGUMENT_PRIMARY.name(),
            new SimpleTree( URL.name(), new SimpleTree( "file:" + fileName ) )
        )
    ) ;

    functionCall = TreeTools.addLast(
        functionCall,
        Iterables.transform(
            Lists.newArrayList( flagArguments ),
            CREATE_VALUED_ARGUMENT_FLAG_FUNCTION
        )
    ) ;

    return functionCall ;
  }

  @Test
  public void bookWithOneBareCall() throws RecognitionException {
    book(
        "insert file:one-word.nlp",
        tree( BOOK,
            tree( FUNCTION_CALL,
                tree( FUNCTION_NAME, "insert" ),
                tree( VALUED_ARGUMENT_PRIMARY, tree( URL, "file:one-word.nlp" ) )
            )
        )
    ) ;
  }

  @Test
  public void bookWithTwoBareCalls() throws RecognitionException {
    book(
        " function1 file:my/file1 " + BREAK + BREAK +
        "function2 file:/my/file2 " + BREAK + "  "
        ,
        tree( BOOK,
            tree( FUNCTION_CALL,
                tree( FUNCTION_NAME, "function1" ),
                tree( VALUED_ARGUMENT_PRIMARY, tree( URL, "file:my/file1" ) )
            ),
            tree( FUNCTION_CALL,
                tree( FUNCTION_NAME, "function2" ),
                tree( VALUED_ARGUMENT_PRIMARY, tree( URL, "file:/my/file2" ) )
            )
        )
    ) ;
  }

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
        "function \n" + " with paragraphbody",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree(
                VALUED_ARGUMENT_PRIMARY,
                tree( PARAGRAPH_PLAIN,
                    tree( WORD, "with" ),
                    tree( WORD, "paragraphbody" )
                )
            )
        )
    ) ;
  }

  @Test
  public void functionCallWithUrl() throws RecognitionException {
    functionCall(
        "function file:my/file",
        createFunctionCallWithUrlTree( "my/file" )
    ); ;
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
    ancillaryArgument(
        "\\identifier",
        tree( VALUED_ARGUMENT_ANCILLARY,
            tree( IDENTIFIER, "identifier" )
        )
    ) ;
  }

  @Test
  public void valuedArgumentAncillaryIsBlockIdentifierAndModifier() throws RecognitionException {
    ancillaryArgument(
        "+\\identifier",
        tree( VALUED_ARGUMENT_ANCILLARY,
            tree( VALUED_ARGUMENT_MODIFIER, "+" ),
            tree( IDENTIFIER, "identifier" )
        )
    ) ;
  }

}
