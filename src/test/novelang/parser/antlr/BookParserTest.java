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
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.TreeTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class BookParserTest {
  /*package*/ static final ParserMethod PARSERMETHOD_FUNCTION_CALL = 
      new ParserMethod( "functionCall" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_VALUED_ARGUMENT_ASSIGNMENT =
      new ParserMethod( "assignmentArgument" ) ;
  /*package*/ static final ParserMethod PARSERMETHOD_BOOK = new ParserMethod( "book" ) ;

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
        FUNCTION_CALL_.name(),
        new SimpleTree( FUNCTION_NAME_.name(), new SimpleTree( "function" ) ),
        new SimpleTree(
            VALUED_ARGUMENT_PRIMARY_.name(),
            new SimpleTree( URL_LITERAL.name(), new SimpleTree( "file:" + fileName ) )
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
          new SimpleTree( VALUED_ARGUMENT_FLAG_.name(), new SimpleTree( flagArgument ) )
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
        tree( FUNCTION_CALL_.name(), tree( FUNCTION_NAME_, functionName ) ) ) ;

    functionCall = addValuedArgumentAssignments( functionCall, map ) ;
    return functionCall.getTreeAtStart() ;
  }

  private static Treepath< SyntacticTree > addValuedArgumentAssignments(
      Treepath< SyntacticTree > functionCall,
      Map< String, String > map
  ) {
    for( String key : map.keySet() ) {
      SyntacticTree assignment = tree( NodeKind.VALUED_ARGUMENT_ASSIGNMENT_ ) ;
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
    PARSERMETHOD_BOOK.checkTreeAfterSeparatorRemoval(
        "insert file:one-word.nlp",
        tree( BOOK,
            tree( FUNCTION_CALL_,
                tree( FUNCTION_NAME_, "insert" ),
                tree( VALUED_ARGUMENT_PRIMARY_, tree( URL_LITERAL, "file:one-word.nlp" ) )
            )
        )
    ); 
  }

  @Test
  public void bookWithTwoBareCalls() throws RecognitionException {
    PARSERMETHOD_BOOK.checkTreeAfterSeparatorRemoval(
        " function1 file:my/file1 " + BREAK + BREAK +
        "function2 file:/my/file2 " + BREAK + "  "
        ,
        tree( BOOK,
            tree( FUNCTION_CALL_,
                tree( FUNCTION_NAME_, "function1" ),
                tree( VALUED_ARGUMENT_PRIMARY_, tree( URL_LITERAL, "file:my/file1" ) )
            ),
            tree( FUNCTION_CALL_,
                tree( FUNCTION_NAME_, "function2" ),
                tree( VALUED_ARGUMENT_PRIMARY_, tree( URL_LITERAL, "file:/my/file2" ) )
            )
        )
    ) ;
  }

  @Test
  public void functionCallBare() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTreeAfterSeparatorRemoval(
        "function",
        tree( FUNCTION_CALL_,
            tree( FUNCTION_NAME_, "function" )
        )
    ) ;
  }

  @Test
  public void functionCallWithParagraphBody() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTreeAfterSeparatorRemoval(
        "function \n" + " with paragraphbody",
        tree( FUNCTION_CALL_,
            tree( FUNCTION_NAME_, "function" ),
            tree(
                VALUED_ARGUMENT_PRIMARY_,
                tree( NodeKind.PARAGRAPH_REGULAR,
                    tree( WORD_, "with" ),
                    tree( WORD_, "paragraphbody" )
                )
            )
        )
    ) ;
  }

  @Test
  public void functionCallWithUrl() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTreeAfterSeparatorRemoval(
        "function file:my/file",
        createFunctionCallWithUrlTree( "my/file" )
    ) ;
  }

  @Test
  public void functionCallWithFlag() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTreeAfterSeparatorRemoval(
        "function $flag",
        tree( FUNCTION_CALL_,
            tree( FUNCTION_NAME_, "function" ),
            tree( VALUED_ARGUMENT_FLAG_, "flag" )
        )
    ) ;
  }

  @Test
  public void functionCallWithMoreFlags() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTreeAfterSeparatorRemoval(
        "function $flag1 " + BREAK +
        " $flag2",
        tree( FUNCTION_CALL_,
            tree( FUNCTION_NAME_, "function" ),
            tree( VALUED_ARGUMENT_FLAG_, "flag1" ),
            tree( VALUED_ARGUMENT_FLAG_, "flag2" )
        )
    ) ;
  }

  @Test
  public void functionCallWithTwoAssignments() throws RecognitionException {
    PARSERMETHOD_FUNCTION_CALL.checkTreeAfterSeparatorRemoval(
        "function $key1=value1 " + BREAK +
        " $key2=value2",
        createFunctionCallWithValuedAssignmentTree(
            "function",
            ImmutableMap.of( "key1", "value1", "key2", "value2" ) )
    ) ;
  }



  @Test
  public void valuedArgumentAssignment() throws RecognitionException {
    PARSERMETHOD_VALUED_ARGUMENT_ASSIGNMENT.checkTreeAfterSeparatorRemoval(
        "$key=value/with/solidus.and-other",
        tree( VALUED_ARGUMENT_ASSIGNMENT_,
            tree( "key" ),
            tree( "value/with/solidus.and-other" )
        )
    ) ;
  }

}
