package novelang.rendering.javascript;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;

import novelang.TestResourceTree;
import novelang.ScratchDirectoryFixture;
import novelang.TestResources;
import static novelang.TestResourceTree.TaggedPart;
import novelang.rendering.RenditionMimeType;
import novelang.configuration.ConfigurationTools;
import novelang.loader.ResourceLoader;
import novelang.loader.ClasspathResourceLoader;
import novelang.daemon.HttpDaemon;
import novelang.common.filefixture.Filer;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.UnexpectedPage;

/**
 * Tests for Javascript-based interactive behavior. 
 * 
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class TagInteractionTest {

  @Test
  public void justLetTheDaemonStartThenStop() throws IOException { }

  @Test
  public void justLoadPage() throws IOException {
    final WebClient webClient = new WebClient() ;
    webClient.setThrowExceptionOnScriptError( true ) ;
    webClient.setAjaxController( new NicelyResynchronizingAjaxController() ) ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    final Page page = webClient.getPage(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" +
        FilenameUtils.getBaseName( TaggedPart.TAGGED.getName() ) +
        "." + RenditionMimeType.HTML.getFileExtension()
    ) ;
    if( page instanceof UnexpectedPage ) {
      final UnexpectedPage unexpectedPage = ( UnexpectedPage ) page ;
      LOGGER.error( "Unexpected page!" ) ;
      LOGGER.error( "  Staus message:" + unexpectedPage.getWebResponse().getStatusMessage() ) ;
      LOGGER.error( "  Response headers:" + unexpectedPage.getWebResponse().getResponseHeaders() ) ;
      LOGGER.error( "Page content:\n" + unexpectedPage.getWebResponse().getContentAsString() ) ;
      Assert.fail( "Could not load the page." ) ;
    }
    LOGGER.debug( "Got page of type " + page.getClass().getName() );
    final HtmlPage htmlPage = ( HtmlPage ) page ;

//    htmlPage.getByXPath( "/html/body/div/h1" ) ;


    final HtmlForm tagList = htmlPage.getFormByName( TaggedPart.TAGS_FORM_NAME ) ;
    HtmlCheckBoxInput tag1Checkbox = tagList.getInputByName( TaggedPart.TAG1 ) ;
    tag1Checkbox.setChecked( true ) ;
  }


// =======  
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( TagInteractionTest.class ) ;

  static {
    TestResourceTree.initialize() ;
  }

  private static final int HTTP_DAEMON_PORT = 8081 ;
  private static final int AJAX_TIMEOUT_MILLISECONDS = 10000;


  private File testDirectory ;
  private HttpDaemon httpDaemon ;
  
  @Before
  public void before() throws Exception {
    final String testName = NameAwareTestClassRunner.getTestName() ;
    testDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory() ;

    final Filer filer = new Filer( testDirectory ) ;
    filer.copyContent( TaggedPart.dir ) ;

    final ResourceLoader resourceLoader =
        new ClasspathResourceLoader( "/" + ConfigurationTools.BUNDLED_STYLE_DIR ) ;

    httpDaemon = new HttpDaemon( TestResources.createDaemonConfiguration( 
        HTTP_DAEMON_PORT,
        testDirectory,
        resourceLoader
    ) ) ;
    httpDaemon.start() ;
  }

  @After
  public void tearDown() throws Exception {
//    TimeUnit.SECONDS.sleep( 3600 ) ;
    httpDaemon.stop() ;
  }
    
  
}
