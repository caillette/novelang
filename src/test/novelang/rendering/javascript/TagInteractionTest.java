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
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import novelang.DirectoryFixture;
import novelang.TestResourceTree;
import static novelang.TestResourceTree.TaggedPart;
import novelang.TestResourceTools;
import novelang.common.filefixture.ResourceInstaller;
import novelang.configuration.ConfigurationTools;
import novelang.daemon.HttpDaemon;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.rendering.RenditionMimeType;

/**
 * Tests for Javascript-based interactive behavior.
 * <p>
 * Don't get impressed by this warning:
 * <pre>
  WARN  com.gargoylesoftware.htmlunit.html.HtmlPage - Obsolete content type encountered: 'text/javascript'.
 </pre>
 * The <code>text/javascript</code> type <em>is</em> to be
 * <a href="http://en.wikipedia.org/wiki/Client-side_JavaScript#Environment" >preferred</a>.
 * 
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class TagInteractionTest {
  @Test
  public void justLoadPage() throws IOException, InterruptedException {
    final List< HtmlElement > headers = getCurrentHeaders() ;
    assertEquals( 24, headers.size() )  ;
    verifyHidden( headers, ImmutableSet.< String >of() ) ;
  }

  @Test
  public void restrictToTag2() throws IOException, InterruptedException {
    tag2Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( getCurrentHeaders(), ImmutableSet.< String >of(
        H0_0,
        H0_1,
        H1_0,
        H1_1,
        H4,
        H4_0,
        H5,
        H5_1
    ) ) ;
  }

  @Test
  public void restrictToTag1() throws IOException, InterruptedException {
    tag1Checkbox.click() ;
    LOG.info( "Just clicked on the checkbox. Let's see what happens" ) ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( getCurrentHeaders(), ImmutableSet.< String >of(
        H0_0,
        H0_2,
        H2_0,
        H2_2,
        H4,
        H4_0
    ) ) ;
  }

  @Test
  public void revert1() throws IOException, InterruptedException {
    tag1Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    tag1Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( getCurrentHeaders(), ImmutableSet.< String >of() ) ;
  }

  @Test
  public void revert2() throws IOException, InterruptedException {
    tag2Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    tag2Checkbox.click() ;
    webClient.waitForBackgroundJavaScript( AJAX_TIMEOUT_MILLISECONDS ) ;
    verifyHidden( getCurrentHeaders(), ImmutableSet.< String >of() ) ;
  }



// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( TagInteractionTest.class ) ;

  static {
    TestResourceTree.initialize() ;
  }

  private static final int HTTP_DAEMON_PORT = 8081 ;
  private static final int AJAX_TIMEOUT_MILLISECONDS = 10000 ;


  private HttpDaemon httpDaemon ;
  private WebClient webClient ;
  private HtmlCheckBoxInput tag1Checkbox;
  private HtmlCheckBoxInput tag2Checkbox;


  @Before
  public void before() throws Exception {
    assertEquals( 24, ALL_HEADERS.size() ) ; 

    final String testName = NameAwareTestClassRunner.getTestName() ;
    final File testDirectory = new DirectoryFixture( testName ).getDirectory();

    final ResourceInstaller resourceInstaller = new ResourceInstaller( testDirectory ) ;
    resourceInstaller.copyContent( TaggedPart.dir ) ;

    final ResourceLoader resourceLoader =
        new ClasspathResourceLoader( "/" + ConfigurationTools.BUNDLED_STYLE_DIR ) ;

    httpDaemon = new HttpDaemon( TestResourceTools.createDaemonConfiguration(
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
      public void statusMessageChanged( final Page page, final String s ) {
        collectedStatusMessages.add( s ) ;
      }
    } ) ;
    webClient.setAjaxController( new NicelyResynchronizingAjaxController() ) ;
    webClient.setRedirectEnabled( true ) ;
    final Page page = webClient.getPage(
        "http://localhost:" + HTTP_DAEMON_PORT + "/" +
        FilenameUtils.getBaseName( TaggedPart.TAGGED.getName() ) +
        "." + RenditionMimeType.HTML.getFileExtension()
    ) ;
    if( ! ( page instanceof HtmlPage ) ) {
      LOG.error( "Got page of type %s", page.getClass().getName() ) ;
      final UnexpectedPage unexpectedPage = ( UnexpectedPage ) page ;
      LOG.error( "Unexpected page!" ) ;
      LOG.error( "  Staus message: %s", unexpectedPage.getWebResponse().getStatusMessage() ) ;
      LOG.error( "  Response headers: %s", unexpectedPage.getWebResponse().getResponseHeaders() ) ;
      LOG.error( "Page content:\n%s", unexpectedPage.getWebResponse().getContentAsString() ) ;
      fail( "Could not load the page." ) ;
    }
    final HtmlPage htmlPage = HtmlPage.class.cast( page ) ;

    LOG.info( "Now the whole page should have finished loading and initializing." ) ;
    
    LOG.debug( "This is the HTML we got:\n\n%s\n", htmlPage.asXml() ) ;

    final HtmlForm tagList = htmlPage.getFormByName( TaggedPart.TAGS_FORM_NAME ) ;
    tag1Checkbox = tagList.getInputByName( TaggedPart.TAG1 );
    tag2Checkbox = tagList.getInputByName( TaggedPart.TAG2 );
  }

  @After
  public void tearDown() throws Exception {
//    TimeUnit.SECONDS.sleep( 3600 ) ;
    httpDaemon.stop() ;
  }
    
  private static List< HtmlElement > extractAllHeaders( final HtmlPage htmlPage ) {
    final List< HtmlElement > allHeaders = Lists.newArrayList() ;
    allHeaders.addAll( ( Collection< ? extends HtmlElement > )
        htmlPage.getByXPath( "/html/body/h1" ) ) ;
    allHeaders.addAll( ( Collection< ? extends HtmlElement > )
        htmlPage.getByXPath( "/html/body/h2" ) ) ;
    return allHeaders ;
  }

  private static void verifyHidden(
      final Iterable< HtmlElement > documentElements,
      final Set< String > hiddenHeaders
  ) {
    final Set< String > errors = Sets.newTreeSet() ;

    final StringBuffer messageBuffer = new StringBuffer( "\n  | header | present | should be |" ) ;

    for( final String header : ALL_HEADERS ) {
      final boolean shouldBePresent = ! hiddenHeaders.contains( header ) ;
      final boolean isPresent = findByTextStart( documentElements, header ) != null ;

      messageBuffer.append( "\n  | " ) ;
      messageBuffer.append( String.format( "%-6s", header ) ) ;
      messageBuffer.append( " | " ) ;
      messageBuffer.append( String.format( "%-7b", isPresent ) ) ;
      messageBuffer.append( " | " ) ;
      messageBuffer.append( String.format( "%-9b", shouldBePresent ) ) ;
      messageBuffer.append( " |" ) ;

      if( isPresent ) {
        if( ! shouldBePresent ) {
          errors.add( String.format( "Header %s should be present", header ) ) ;
        }
      } else {
        if( shouldBePresent ) {
          errors.add( String.format( "Header %s should not be present", header ) ) ;
        }
      }
    }

    LOG.info( messageBuffer.toString() ) ;

    if( errors.size() > 0 ) {
      fail( errors.toString() ) ;
    }

  }

  private static HtmlElement findByTextStart(
      final Iterable< HtmlElement > htmlElements,
      final String textStart
  ) {
    for( final HtmlElement htmlElement : htmlElements ) {
      if( cleanTextContent( htmlElement ).startsWith( textStart ) ) {
        return htmlElement ;
      }
    }
    return null ;
  }

  private static final Pattern MEANINGFUL_HEADER_TEXT = Pattern.compile( "(H\\d\\.(?:\\d\\.)?)" ) ;

  private static String cleanTextContent( final HtmlElement htmlElement ) {
    final String textContent = htmlElement.getTextContent() ;
    final Matcher matcher = MEANINGFUL_HEADER_TEXT.matcher( textContent ) ;
    if( matcher.find() ) {
      return matcher.group( 1 ) ;
    } else {
      throw new IllegalArgumentException(
          "Could not find meaningful text in '" + textContent + "'" ) ;
    }
  }

  private List< HtmlElement > getCurrentHeaders() {
    final WebWindow webWindow = webClient.getCurrentWindow() ;
    final HtmlPage htmlPage = ( HtmlPage ) webWindow.getEnclosedPage() ;
    return Ordering.from( HTMLELEMENT_COMPARATOR ).sortedCopy( extractAllHeaders( htmlPage ) ) ;
  }

  private static final Comparator< HtmlElement > HTMLELEMENT_COMPARATOR =
      new Comparator< HtmlElement >() {
        public int compare( final HtmlElement e1, final HtmlElement e2 ) {
          return cleanTextContent( e1 ).compareTo( cleanTextContent( e2 ) ) ;
        }
      }
  ;

  private static final String H0   = "H0." ;
  private static final String H0_0 = "H0.0." ;
  private static final String H0_1 = "H0.1." ;
  private static final String H0_2 = "H0.2." ;
  private static final String H0_3 = "H0.3." ;
  
  private static final String H1   = "H1." ;
  private static final String H1_0 = "H1.0." ;
  private static final String H1_1 = "H1.1." ;
  private static final String H1_2 = "H1.2." ;
  private static final String H1_3 = "H1.3." ;
  
  private static final String H2   = "H2." ;
  private static final String H2_0 = "H2.0." ;
  private static final String H2_1 = "H2.1." ;
  private static final String H2_2 = "H2.2." ;
  private static final String H2_3 = "H2.3." ;
  
  private static final String H3   = "H3." ;
  private static final String H3_0 = "H3.0." ;
  private static final String H3_1 = "H3.1." ;
  private static final String H3_2 = "H3.2." ;
  private static final String H3_3 = "H3.3." ;
  
  private static final String H4 = "H4." ;
  private static final String H4_0 = "H4.0." ;
  
  private static final String H5 = "H5." ;
  private static final String H5_1 = "H5.1." ;

  private static final Set< String > ALL_HEADERS = ImmutableSet.of(
      H0, H0_0, H0_1, H0_2, H0_3,
      H1, H1_0, H1_1, H1_2, H1_3,
      H2, H2_0, H2_1, H2_2, H2_3,
      H3, H3_0, H3_1, H3_2, H3_3,
      H4, H4_0,
      H5, H5_1
  ) ;  

}
