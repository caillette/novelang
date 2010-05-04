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
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import novelang.system.LogFactory;
import novelang.system.Log;
import static novelang.parser.antlr.AntlrTestHelper.BREAK;
import novelang.common.Problem;
import com.google.common.base.Joiner;

/**
 * Get sure that delimiters problems are correctly reported.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class DelimiterProblemTest {

  @Test
  public void symmetricalOk() throws RecognitionException {
    final String text = "( z )" ;
    process( text );
  }

  @Test
  public void nonSymmetricalOk() throws RecognitionException {
    final String text = "-- z --" ;
    process( text ) ;
  }

  @Test
  public void nonSymmetricalUnclosedAlone() throws RecognitionException {
    final String text = "y -- z" ;
    process( text, problem( 1, 2, "'--'" ) ) ;
  }

  @Test
  public void symmetricalUnclosedAlone() throws RecognitionException {
    final String text = "( z" ;
    process( text, problem( 1, 0, "'('" ) ) ;
  }

  @Test
  public void nonSymmetricalUnclosedInsideSymmetrical() throws RecognitionException {
    final String text =
        "( w " + BREAK +
        "x -- y" + BREAK +
        "z )"
    ;
    process( text, problem( 2, 2, "'--'" ) ) ;
  }

  @Test
  public void symmetricalUnclosedInsideNonSymmetrical() throws RecognitionException {
    final String text =
        "-- w " + BREAK +
        "x ( y" + BREAK +
        "z --"
    ;
    process( text, problem( 2, 2, "'('" ) ) ;
  }


  @Test
  public void twoSymmetricalUnclosedInsideNonSymmetrical() throws RecognitionException {
    final String text =
        "-- u " + BREAK +
        "v ( w" + BREAK +
        "x [ y" + BREAK +
        "z --"
    ;
    process( text, problem( 2, 2, "'('" ), problem( 3, 2, "[" ) ) ;
  }

  @Test
  public void boundarySwitch() throws RecognitionException {
    final String text =
        "( s " + BREAK +
        "t -- u" + BREAK +
        "v )" + BREAK +
        BREAK +
        "// w " + BREAK +
        "x [ y" + BREAK +
        "z //" + BREAK
    ;
    process( text, problem( 2, 2, "'--'" ), problem( 6, 2, "'['" ) ) ;
  }



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( DelimiterProblemTest.class ) ;

  @Before
  public void before() {
    final String testName = NameAwareTestClassRunner.getTestName() ;
    LOG.info( "\n\nRunning %s", testName ) ;
  }


  private void process( 
      final String text, 
      final ProblemSignature... signatures 
  ) throws RecognitionException {
    LOG.info( BREAK + text ) ;
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    final Iterable< Problem > problems = parser.getProblems() ;
        // parser.getDelegate().getBlockDelimiterSupervisor().getProblems() ;
    LOG.debug( "Faulty blocks: %s",
        problems.iterator().hasNext() ?
        "\n    " + Joiner.on( "\n    " ).join( problems ) :
        "none."
    ) ;
    if( signatures.length == 0 ) {
      Assert.assertFalse( problems.iterator().hasNext() );
    } else {
      for( final ProblemSignature signature : signatures ) {
        Assert.assertTrue( signature.in( problems ) ) ;
      }
    }
  }

  private static ProblemSignature problem( 
      final int line, 
      final int column, 
      final String messageFragment 
  ) {
    return new ProblemSignature( line, column, messageFragment ) ;
  }

  private static class ProblemSignature {
    private final int line ;
    private final int column ;
    private final String messageElement ;

    private ProblemSignature( final int line, final int column, final String messageElement ) {
      this.line = line;
      this.column = column;
      this.messageElement = messageElement;
    }

    private boolean in( final Problem problem ) {
      return
          problem.getLocation().getLine() == line
       && problem.getLocation().getColumn() == column
       && problem.getMessage().contains( messageElement )
      ;
    }

    private boolean in( final Iterable< Problem > problems ) {
      for( final Problem problem : problems ) {
        if( in( problem ) ) {
          return true ;
        }
      }
      return false ;
    }
  }

}