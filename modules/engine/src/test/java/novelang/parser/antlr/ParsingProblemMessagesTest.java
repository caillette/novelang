package novelang.parser.antlr;

import java.net.MalformedURLException;
import java.util.List;

import novelang.common.Problem;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.novella.Novella;
import novelang.novella.NovellaFixture;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

  private static final Logger LOGGER = LoggerFactory.getLogger( ParsingProblemMessagesTest.class ) ;

  private static void verify( final String sourceDocument, final String problemMessageFragment ) {
    LOGGER.info( "For document: \n", sourceDocument ) ;
    final Novella novella = NovellaFixture.createStandaloneNovella( sourceDocument ) ;
    final List< Problem > problems = NovellaFixture.extractProblems( novella ) ;
    assertEquals( problems.toString(), 1, problems.size() ) ;
    final String message = problems.get( 0 ).getMessage();
    LOGGER.info( "Expecting message fragment: '", problemMessageFragment, "'") ;
    LOGGER.info( "Got message: '" + message + "'") ;
    assertTrue( message.contains( problemMessageFragment ) ) ;

  }


}
