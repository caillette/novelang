package novelang.novella;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import novelang.common.Problem;
import org.junit.Assert;

/**
 * Useful factory methods for tests using {@link Novella}.
 * 
 * @author Laurent Caillette
 */
public class NovellaFixture {

  private NovellaFixture() { }

  public static Novella createStandaloneNovella( final String content ) {
    return new Novella( content ).makeStandalone() ;
  }
  
  public static List< Problem > extractProblems( final Novella novella )
  {
    final List< Problem > problems = Lists.newArrayList() ;
    Assert.assertTrue( novella.hasProblem() ) ;
    Iterables.addAll( problems, novella.getProblems() ) ;
    return problems ;
  }

  public static Novella create( final String content ) {
    return new Novella( content ) ;
  }
}
