package org.junit.runners ;


import java.util.ConcurrentModificationException;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * Provides the current test name, available for the <code>@Test</code> method
 * and <code>@Before</code> and <code>@After</code> methods, <strong>just like
 * the default runner should!</strong><br/> (see <code>Test.getName()</code> in JUnit3)
 *
 * @author Joshua.Graham@thoughtworks.com
 * @author based on http://tech.groups.yahoo.com/group/junit/message/18728
 *         <p/>
 *         Another warm, sunny Autumn day in Sydney ends, and I'm making this testable...
 * @since JUnit 4
 */
public class NameAwareTestClassRunner extends JUnit4ClassRunner {

  private final static ThreadLocal< String > testClassName = new ThreadLocal< String >() ;

  private final static ThreadLocal< String > testMethodName = new ThreadLocal< String >() ;

  protected static void setName( final String name ) {
    testMethodName.set( extractTestMethodName( name ) ) ;
    testClassName.set( extractTestClassName( name ) ) ;
  }

  /**
   * Old-style JUnit 3 TestCase.getName() format
   *
   * @return package.class.method()
   */
  public static String _getName() {
    return String.format( "%s.%s()", getTestClassName(), getTestMethodName() ) ;
  }

  /**
   * JUnit 4 test name format
   *
   * @return method(package.class)
   */
  public static String getTestName() {
    return String.format( "%s(%s)", getTestMethodName(), getTestClassName() ) ;
  }

  public static String getTestMethodName() {
    return testMethodName.get() ;
  }

  public static String getTestClassName() {
    return testClassName.get() ;
  }

  public NameAwareTestClassRunner( final Class< ? > klass ) throws InitializationError {
    super( klass ) ;
  }

  @Override
  public void run( final RunNotifier notifier ) {
    notifier.addListener( new NameListener() ) ;
    super.run( notifier ) ;
  }

  private static String extractTestMethodName( final String name ) {
    if( name != null ) {
      final int last = name.lastIndexOf( '(' ) ;
      return last < 0 ? name : name.substring( 0, last ) ;
    }
    return null ;
  }

  private static String extractTestClassName( final String name ) {
    if( name != null ) {
      final int last = name.lastIndexOf( '(' ) ;
      return last < 0 ? null : name.substring( last + 1, name.length() - 1 ) ;
    }
    return null ;
  }

  private static class NameListener extends RunListener {
    @Override
    public void testStarted( final Description description ) {
      /* record start of tests, not suites */
      setName( description.isTest() ? description.getDisplayName() : null ) ;
    }

    @Override
    public void testFinished( final Description description ) {
      if( getTestName() != null ) {
        if( getTestName().equals( description.getDisplayName() ) ) {
          setName( null ) ;
        } else {
          throw new ConcurrentModificationException(
              "Test name mismatch. Was " + description.getDisplayName() +
              " expected " + getTestName()
          ) ;
        }
      }
    }
  }
}