package novelang.parser.antlr;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import novelang.part.Part;
import novelang.part.PartFixture;
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
    final Part part = PartFixture.createStandalonePart( sourceDocument ) ;
    final List< Problem > problems = PartFixture.extractProblems( part ) ;
    assertEquals( problems.toString(), 1, problems.size() ) ;
    final String message = problems.get( 0 ).getMessage();
    LOG.info( "Expecting message fragment: '" + problemMessageFragment + "'") ;
    LOG.info( "Got message: '" + message + "'") ;
    assertTrue( message.contains( problemMessageFragment ) ) ;

  }


}
