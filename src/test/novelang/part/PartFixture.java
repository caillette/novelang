package novelang.part;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

import novelang.common.Problem;

import org.junit.Assert;

/**
 * Useful factory methods for tests using {@link Part}.
 * 
 * @author Laurent Caillette
 */
public class PartFixture {

  private PartFixture() { }

  public static Part createPart( final String content ) {
    return new Part( content ) ;
  }
  
  public static Part createPart( final File content ) throws IOException {
    return new Part( content ) ;
  }
  
  public static Part createStandalonePart( final String content ) {
    return new Part( content ).makeStandalone() ;
  }
  
  public static Part createStandalonePart( final File content ) throws IOException {
    return new Part( content ).makeStandalone() ;
  }
  
  public static List< Problem > extractProblems( final Part part )
  {
    final List< Problem > problems = Lists.newArrayList() ;
    Assert.assertTrue( part.hasProblem() ) ;
    Iterables.addAll( problems, part.getProblems() ) ;
    return problems ;
  }

}
