package novelang.rendering.javascript;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.IOException;
import java.io.File;
import java.util.concurrent.TimeUnit;

import novelang.TestResourceTree;
import novelang.ScratchDirectoryFixture;
import novelang.TestResources;
import novelang.daemon.HttpDaemon;
import novelang.common.filefixture.Filer;

/**
 * Tests for Javascript-based interactive behavior. 
 * 
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class TagInteractionTest {

  @Test
  public void nothing() {
    
  }


// =======  
// Fixture
// =======
  
  static {
    TestResourceTree.initialize() ;
  }

  private static final int HTTP_DAEMON_PORT = 8081 ;
  
  private File testDirectory ;
  private HttpDaemon httpDaemon ;
  
  @Before
  public void before() throws Exception {
    final String testName = NameAwareTestClassRunner.getTestName() ;
    testDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory() ;

    final Filer filer = new Filer( testDirectory ) ;
    filer.copyContent( TestResourceTree.TaggedPart.dir ) ;

    httpDaemon = new HttpDaemon( TestResources.createDaemonConfiguration( 
        HTTP_DAEMON_PORT, 
        testDirectory
    ) ) ;
    httpDaemon.start() ;
  }

  @After
  public void tearDown() throws Exception {
    TimeUnit.SECONDS.sleep( 60 ) ;
//    httpDaemon.stop() ;
  }
    
  
}
