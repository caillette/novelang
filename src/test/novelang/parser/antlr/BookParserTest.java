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

import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.TreeTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import static novelang.parser.antlr.AntlrTestHelper.*;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class BookParserTest {
  /*package*/ static final ParserMethod PARSERMETHOD_FUNCTION_CALL = 
      new ParserMethod( "functionCall" ) ;/*package*/ static final ParserMethod PARSERMETHOD_ANCILLARY_ARGUMENT = 
          new ParserMethod( "ancillaryArgument" ) ;/*package*/ static final ParserMethod PARSERMETHOD_VALUED_ARGUMENT_ASSIGNMENT = 
              new ParserMethod( "assignmentArgument" ) ;/*package*/ static final ParserMethod PARSERMETHOD_BOOK = 
                  new ParserMethod( "book" ) ;

  /**
   * This is used elsewhere as we must be sure to pass a tree of the same form as the
   * parser produces.
   */
  public static SyntacticTree createFunctionCallWithUrlTree(
      String fileName,
      String... flagArguments
  ) {
    Treepath< SyntacticTree > functionCall =
        createFunctionTreeWithFilenameAsPrimaryArgument( fileName );
    functionCall = addValuedArgumentFlags( functionCall, flagArguments );
    return functionCall.getTreeAtStart() ;
  }

  public static SyntacticTree createFunctionCallWithUrlTree(
      String fileName,
      Map< String, String > assignments,
      String... flagArguments
  ) {
    Treepath< SyntacticTree > functionCall =
        Treepath.create( createFunctionTreeWithFilenameAsPrimaryArgument( fileName ) ) ;
    functionCall = addValuedArgumentFlags( functionCall, flagArguments );
    functionCall = addValuedArgumentAssignments( Treepath.create( functionCall ), assignments ) ;
    return functionCall.getTreeAtStart() ; 
  }

  private static Treepath< SyntacticTree >
  createFunctionTreeWithFilenameAsPrimaryArgument( String fileName )
  {
    final Treepath< SyntacticTree > functionCall = Treepath.< SyntacticTree >create( new SimpleTree(
        FUNCTION_CALL.name(),
        new SimpleTree( FUNCTION_NAME.name(), new SimpleTree( "function" ) ),
        new SimpleTree(
            VALUED_ARGUMENT_PRIMARY.name(),
            new SimpleTree( URL.name(), new SimpleTree( "file:" + fileName ) )
        )
    ) ) ;
    return functionCall;
  }

  private static Treepath< SyntacticTree > addValuedArgumentFlags(
      Treepath< SyntacticTree > functionCall,
      String... flagArguments
  ) {
    for( String flagArgument : flagArguments ) {
      functionCall = TreepathTools.addChildLast(
          functionCall,
          new SimpleTree( VALUED_ARGUMENT_FLAG.name(), new SimpleTree( flagArgument ) )
      ) ;
    }
    return functionCall ;
  }

  /**
   * This is used elsewhere as we must be sure to pass a tree of the same form as the
   * parser produces.
   */
  public static SyntacticTree createFunctionCallWithValuedAssignmentTree(
      String functionName,
      Map< String, String > map
  ) {
    Treepath< SyntacticTree > functionCall = Treepath.create(
        tree( FUNCTION_CALL.name(), tree( FUNCTION_NAME, functionName ) ) ) ;

    functionCall = addValuedArgumentAssignments( functionCall, map ) ;
    return functionCall.getTreeAtStart() ;
  }

  private static Treepath< SyntacticTree > addValuedArgumentAssignments(
      Treepath< SyntacticTree > functionCall,
      Map< String, String > map
  ) {
    for( String key : map.keySet() ) {
      SyntacticTree assignment = tree( NodeKind.VALUED_ARGUMENT_ASSIGNMENT ) ;
      assignment = TreeTools.addLast(
          assignment,
          tree( key )
      ) ;
      assignment = TreeTools.addLast(
          assignment,
          tree( map.get( key ) )
      ) ;
      functionCall = TreepathTools.addChildLast( functionCall, assignment ).getPrevious() ;
    }
    return functionCall;
  }

  @Test
  public void bookWithOneBareCall() throws RecognitionException {
    PARSERMETHOD_BOOK.checkTree(
        "insert file:one-word.nlp",
        tree( BOOK,
            tree( FUNCTION_CALL,
                tree( FUNCTION_NAME, "insert" ),
                tree( VALUED_ARGUMENT_PRIMARY, tree( URL, "file:one-word.nlp" ) )
            )
        )
    ); 
  }

  @Test
  public void bookWithTwoBareCalls() throws RecognitionException {
    PARSERMETHOD_BOOK.checkTree(
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
    PARSERMETHOD_FUNCTION_CALL.checkTree(
        "function",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" )
        )
    ) ;
  }

  @Test
  public void functionCallWithParagraphBody() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTree(
        "function \n" + " with paragraphbody",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree(
                VALUED_ARGUMENT_PRIMARY,
                tree( NodeKind.PARAGRAPH_REGULAR,
                    tree( WORD, "with" ),
                    tree( WORD, "paragraphbody" )
                )
            )
        )
    ) ;
  }

  @Test
  public void functionCallWithUrl() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTree(
        "function file:my/file",
        createFunctionCallWithUrlTree( "my/file" )
    ) ;
  }

  @Test
  public void functionCallWithFlag() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTree(
        "function $flag",
        tree( FUNCTION_CALL,
            tree( FUNCTION_NAME, "function" ),
            tree( VALUED_ARGUMENT_FLAG, "flag" )
        )
    ) ;
  }

  @Test
  public void functionCallWithMoreFlags() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTree(
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
  public void functionCallWithTwoAssignments() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTree(
        "function $key1=value1 " + BREAK +
        " $key2=value2",
        createFunctionCallWithValuedAssignmentTree(
            "function",
            ImmutableMap.of( "key1", "value1", "key2", "value2" ) )
    ) ;
  }

  @Test
  public void functionCallWithAncillaries() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTree(
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
    PARSERMETHOD_ANCILLARY_ARGUMENT.checkTree(
        "\\identifier",
        tree( VALUED_ARGUMENT_ANCILLARY,
            tree( IDENTIFIER, "identifier" )
        )
    ) ;
  }

  @Test
  public void valuedArgumentAncillaryIsBlockIdentifierAndModifier() throws RecognitionException {
    PARSERMETHOD_ANCILLARY_ARGUMENT.checkTree(
        "+\\identifier",
        tree( VALUED_ARGUMENT_ANCILLARY,
            tree( VALUED_ARGUMENT_MODIFIER, "+" ),
            tree( IDENTIFIER, "identifier" )
        )
    ) ;
  }

  @Test
  public void valuedArgumentAssignment() throws RecognitionException {
    PARSERMETHOD_VALUED_ARGUMENT_ASSIGNMENT.checkTree(
        "$key=value/with/solidus.and-other",
        tree( VALUED_ARGUMENT_ASSIGNMENT,
            tree( "key" ),
            tree( "value/with/solidus.and-other" )
        )
    ) ;
  }

}
