package novelang.parser.antlr;

import java.net.MalformedURLException;
import java.util.List;

import novelang.novella.Novella;
import novelang.novella.NovellaFixture;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import novelang.common.Problem;
import novelang.system.LogFactory;
import novelang.system.Log;

/**
 * @author Laurent Caillette
 */
public class ParsingProblemMessagesTest {

  @Test
  public void missingClosingDoubleQuote() throws MalformedURLException {
    verify( "[ foo \" bar ]", "missing DOUBLE_QUOTE" );
  }

  @Test @Ignore
  public void missingClosingAngledBracketPair() throws MalformedURLException {
    verify(
        "<<\n" +
        "whatever\n", 
        "*** TODO ***"
    );
  }



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( ParsingProblemMessagesTest.class ) ;

  private static void verify( final String sourceDocument, final String problemMessageFragment ) {
    LOG.info( "For document: \n" + sourceDocument ) ;
    final Novella novella = NovellaFixture.createStandaloneNovella( sourceDocument ) ;
    final List< Problem > problems = NovellaFixture.extractProblems( novella ) ;
    assertEquals( problems.toString(), 1, problems.size() ) ;
    final String message = problems.get( 0 ).getMessage();
    LOG.info( "Expecting message fragment: '" + problemMessageFragment + "'") ;
    LOG.info( "Got message: '" + message + "'") ;
    assertTrue( message.contains( problemMessageFragment ) ) ;

  }


}
