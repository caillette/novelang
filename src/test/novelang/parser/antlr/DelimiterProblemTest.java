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
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static novelang.parser.antlr.AntlrTestHelper.BREAK;

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
    logText( text );
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }

  @Test
  public void nonSymmetricalOk() throws RecognitionException {
    final String text = "-- z --" ;
    logText( text );
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }

  @Test
  public void nonSymmetricalUnclosedAlone() throws RecognitionException {
    final String text = "y -- z" ;
    logText( text );
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }

  @Test
  public void symmetricalUnclosedAlone() throws RecognitionException {
    final String text = "( z" ;
    logText( text );
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }

  @Test
  public void nonSymmetricalUnclosedInsideSymmetrical() throws RecognitionException {
    final String text =
        "( w " + BREAK +
        "x -- y" + BREAK +
        "z )"
    ;
    logText( text );
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }

  @Test
  public void symmetricalUnclosedInsideNonSymmetrical() throws RecognitionException {
    final String text =
        "-- w " + BREAK +
        "x ( y" + BREAK +
        "z --"
    ;
    logText( text ) ;
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }


  @Test
  public void twoSymmetricalUnclosedInsideNonSymmetrical() throws RecognitionException {
    final String text =
        "-- u " + BREAK +
        "v ( w" + BREAK +
        "x [ y" + BREAK +
        "z --"
    ;
    logText( text );
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    parser.parse() ;
    parser.getDelegate().dumpBlockDelimiterVerifier() ;
  }



// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( DelimiterProblemTest.class ) ;

  @Before
  public void before() {
    final String testName = NameAwareTestClassRunner.getTestName() ;
    LOGGER.info( "\n\nRunning {}", testName ) ;
  }

  private void logText( String text ) {
    LOGGER.info( BREAK + text ) ;
  }

}
