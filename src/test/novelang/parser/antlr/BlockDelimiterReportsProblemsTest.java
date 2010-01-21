package novelang.parser.antlr;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import novelang.part.Part;
import novelang.part.PartFixture;
import novelang.common.Problem;
import novelang.system.LogFactory;
import novelang.system.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

/**
 * @author Laurent Caillette
 */
public class BlockDelimiterReportsProblemsTest {

  @Test
  public void missingClosingDoubleQuote() throws MalformedURLException {
    verify( "[ foo \" bar ]", "missing DOUBLE_QUOTE at ']'" );
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

  private static final Log LOG = LogFactory.getLog( BlockDelimiterReportsProblemsTest.class ) ;

  private static void verify( final String sourceDocument, final String problemMessageFragment ) {
    final Part part = PartFixture.createStandalonePart( sourceDocument ) ;
    final List< Problem > problems = PartFixture.extractProblems( part ) ;
    assertEquals( problems.toString(), 1, problems.size() ) ;
    final String message = problems.get( 0 ).getMessage();
    LOG.info( "For document: \n" + sourceDocument ) ;
    LOG.info( "Got message: '" + message + "'") ;
    assertTrue( message.contains( problemMessageFragment ) ) ;

  }


}
