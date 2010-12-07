package org.novelang.novella;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.novelang.common.Problem;

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
