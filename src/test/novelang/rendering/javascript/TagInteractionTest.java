package novelang.rendering.javascript;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.CSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.HTMLElement;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTree;
import static novelang.TestResourceTree.TaggedPart;
import novelang.TestResources;
import novelang.common.filefixture.Filer;
import novelang.configuration.ConfigurationTools;
import novelang.daemon.HttpDaemon;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.rendering.RenditionMimeType;

/**
 * Tests for Javascript-based interactive behavior. 
 * 
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class TagInteractionTest {

  @Test @Ignore
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
      fail( "Could not load the page." ) ;
    }
    LOGGER.debug( "Got page of type " + page.getClass().getName() );
    final HtmlPage htmlPage = ( HtmlPage ) page ;

    final List< HtmlElement > allHeaders = extractAllHeaders( htmlPage ) ;
//    for( DomNode header : allHeaders ) {
//      LOGGER.debug( "Header: " + header.getTextContent() ) ;
//    }
    assertEquals( 24, allHeaders.size() ) ;

    verifyHidden( allHeaders, ImmutableSet.< String >of() ) ;


    final HtmlForm tagList = htmlPage.getFormByName( TaggedPart.TAGS_FORM_NAME ) ;
    HtmlCheckBoxInput tag1Checkbox = tagList.getInputByName( TaggedPart.TAG1 ) ;
    tag1Checkbox.setChecked( true ) ;

    verifyHidden( allHeaders, ImmutableSet.< String >of( "H4." ) ) ;
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
    
  private static List< HtmlElement > extractAllHeaders( HtmlPage htmlPage ) {
    final List< HtmlElement > allHeaders = Lists.newArrayList() ;
    allHeaders.addAll( ( Collection< ? extends HtmlElement > )
        htmlPage.getByXPath( "/html/body/div/h1" ) ) ;
    allHeaders.addAll( ( Collection< ? extends HtmlElement > )
        htmlPage.getByXPath( "/html/body/div/div/h2" ) ) ;
    return allHeaders ;
  }

  private static void verifyHidden(
      Iterable< HtmlElement > htmlElements,
      Set< String > headerTextStarts
  ) {
    final Set< HtmlElement > elementsToBeVerified = Sets.newHashSet( htmlElements ) ;
    for( String headerTextStart : headerTextStarts ) {
      final HtmlElement htmlElement = findByTextStart( htmlElements, headerTextStart ) ;
      LOGGER.debug( "Verifying hidden for header '{}'", headerTextStart ) ;
      if( htmlElement.isDisplayed() ) {
        fail( "Expected to be hidden: " + headerTextStart ) ;
      } else {
        elementsToBeVerified.remove( htmlElement ) ;
      }
    }
    for( HtmlElement htmlElement : elementsToBeVerified ) {
      LOGGER.debug( "Verifying visible for element '{}'", cleanTextContent( htmlElement ) ) ;
      assertTrue( htmlElement.isDisplayed() ) ;
    }

  }

  private static HtmlElement findByTextStart(
      Iterable< HtmlElement > htmlElements,
      String textStart
  ) {
    for( HtmlElement htmlElement : htmlElements ) {
      if( cleanTextContent( htmlElement ).startsWith( textStart ) ) {
        return htmlElement ;
      }
    }
    fail( "Could not find element with text content starting with '" + textStart + "'" ) ;
    throw new Error( "Should never happen" ) ;
  }

  private static String cleanTextContent( HtmlElement htmlElement ) {
    final String textContent = htmlElement.getTextContent() ;
    return textContent.replace( "\n", "" ) ;
  }


  /**
   * Returns if an element is displayed.
   * According to Firebug, here is how jQuery's $(...).hide() works: turns 'display: block;'
   * to 'display: none;'.
   * So there is the{@code DomNode}'s
   * <a href="http://htmlunit.svn.sourceforge.net/viewvc/htmlunit/tags/HtmlUnit-2.5/src/main/java/com/gargoylesoftware/htmlunit/html/DomNode.java?revision=4422&view=markup" >isDisplayed()</a>
   * which seems to do it right but it doesn't work.
   */
  private static boolean isDisplayed( HtmlElement element ) {

    throw new UnsupportedOperationException( "isDisplayed" ) ;
  }


}
