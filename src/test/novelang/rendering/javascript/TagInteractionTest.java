package novelang.rendering.javascript;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.StatusHandler;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
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

  @Test
  public void justLoadPage() throws IOException, InterruptedException {
    assertEquals( 24, allHeaders.size() )  ;
    verifyHidden( allHeaders, ImmutableSet.< String >of() ) ;
  }

  @Test
  public void restrictToTag2() throws IOException, InterruptedException {
    tag2Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( allHeaders, ImmutableSet.< String >of(
        "H0.0.",
        "H0.1.",
        "H1.0.",
        "H1.1.",
        "H4.",
        "H4.0",
        "H5.",
        "H5.1"
    ) ) ;
  }

  @Test
  public void restrictToTag1() throws IOException, InterruptedException {
    tag1Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( allHeaders, ImmutableSet.< String >of(
        "H0.0.",
        "H0.2.",
        "H2.0.",
        "H2.2.",
        "H4.",
        "H4.0"
    ) ) ;
  }

  @Test
  public void revert1() throws IOException, InterruptedException {
    tag1Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    tag1Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( allHeaders, ImmutableSet.< String >of() ) ;
  }

  @Test
  public void revert2() throws IOException, InterruptedException {
    tag2Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    tag2Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( allHeaders, ImmutableSet.< String >of() ) ;
  }



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( TagInteractionTest.class ) ;

  static {
    TestResourceTree.initialize() ;
  }

  private static final int HTTP_DAEMON_PORT = 8081 ;
  private static final int AJAX_TIMEOUT_MILLISECONDS = 10000;


  private HttpDaemon httpDaemon ;
  private WebClient webClient ;
  private List< HtmlElement > allHeaders;
  private HtmlCheckBoxInput tag1Checkbox;
  private HtmlCheckBoxInput tag2Checkbox;


  @Before
  public void before() throws Exception {
    final String testName = NameAwareTestClassRunner.getTestName() ;
    File testDirectory = new ScratchDirectoryFixture( testName ).getTestScratchDirectory();

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

    setupWebClient() ;
  }

  private void setupWebClient() throws IOException {
    final List< String > collectedStatusMessages =
        Collections.synchronizedList( Lists.< String >newArrayList() ) ;
    webClient = new WebClient();
    webClient.setThrowExceptionOnScriptError( true ) ;
    webClient.setStatusHandler( new StatusHandler() {
      public void statusMessageChanged( Page page, String s ) {
        collectedStatusMessages.add( s ) ;
      }
    } );
    webClient.setAjaxController( new NicelyResynchronizingAjaxController() ) ;
    final Page page = webClient.getPage(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" +
        FilenameUtils.getBaseName( TaggedPart.TAGGED.getName() ) +
        "." + RenditionMimeType.HTML.getFileExtension()
    ) ;
    if( page instanceof UnexpectedPage ) {
      final UnexpectedPage unexpectedPage = ( UnexpectedPage ) page ;
      LOG.error( "Unexpected page!" ) ;
      LOG.error( "  Staus message: %s", unexpectedPage.getWebResponse().getStatusMessage() ) ;
      LOG.error( "  Response headers: %s", unexpectedPage.getWebResponse().getResponseHeaders() ) ;
      LOG.error( "Page content:\n%s", unexpectedPage.getWebResponse().getContentAsString() ) ;
      fail( "Could not load the page." ) ;
    }
    LOG.debug( "Got page of type %s", page.getClass().getName() );
    final HtmlPage htmlPage = ( HtmlPage ) page ;

    LOG.info( "Now the whole page should have finished loading and initializing." ) ;

    allHeaders = Ordering.from( HTMLELEMENT_COMPARATOR ).
        sortedCopy( extractAllHeaders( htmlPage ) );
//    logHeaderVisibility( allHeaders ) ;

    final HtmlForm tagList = htmlPage.getFormByName( TaggedPart.TAGS_FORM_NAME ) ;
    tag1Checkbox = tagList.getInputByName( TaggedPart.TAG1 );
    tag2Checkbox = tagList.getInputByName( TaggedPart.TAG2 );
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
    final Set< String > errors = Sets.newTreeSet() ;
    for( String headerTextStart : headerTextStarts ) {
      final HtmlElement htmlElement = findByTextStart( htmlElements, headerTextStart ) ;
      LOG.debug( "Verifying hidden for header '%s'", headerTextStart ) ;
      if( htmlElement.isDisplayed() ) {
        errors.add( headerTextStart + " should be hidden") ;
      } else {
        elementsToBeVerified.remove( htmlElement ) ;
      }
    }
    for( HtmlElement htmlElement : elementsToBeVerified ) {
      LOG.debug( "Verifying visible for element '%s'", cleanTextContent( htmlElement ) ) ;
      if( ! htmlElement.isDisplayed() ) {
        errors.add( cleanTextContent( htmlElement ) + " should be displayed" ) ;
      }
    }
    if( errors.size() > 0 ) {
      fail( errors.toString() ) ;
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

  private static final Pattern MEANINGFUL_HEADER_TEXT = Pattern.compile( "(H\\d\\.(?:\\d\\.)?)" ) ;

  private static String cleanTextContent( HtmlElement htmlElement ) {
    final String textContent = htmlElement.getTextContent() ;
    final Matcher matcher = MEANINGFUL_HEADER_TEXT.matcher( textContent ) ;
    if( matcher.find() ) {
      return matcher.group( 1 ) ;
    } else {
      throw new IllegalArgumentException(
          "Could not find meaningful text in '" + textContent + "'" ) ;
    }
  }


  private void logHeaderVisibility( List<HtmlElement> allHeaders ) {
    for( HtmlElement header : allHeaders ) {
      LOG.debug( "Header: %s displayed: %s", cleanTextContent( header ), header.isDisplayed() ) ;
    }
  }

  private static final Comparator< HtmlElement > HTMLELEMENT_COMPARATOR =
      new Comparator<HtmlElement>() {
        public int compare( HtmlElement e1, HtmlElement e2 ) {
          return cleanTextContent( e1 ).compareTo( cleanTextContent( e2 ) ) ;
        }
      }
  ;



}
