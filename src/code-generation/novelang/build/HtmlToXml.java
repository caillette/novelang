package novelang.build;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.tools.ant.util.DOMElementWriter;
import org.cyberneko.html.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converts HTML to XML using <a href="http://nekohtml.sourceforge.net/usage.html" >Neko HTML</a>.
 * Build script generates Unicode declarations basing on HTML document.
 * Usage: {@link HtmlToXml} sourceFile destinationFile
 *
 * @author Laurent Caillette
 */
public class HtmlToXml {

  private HtmlToXml() {
  }

  public static void main( final String[] args ) throws IOException, SAXException {

    final FileInputStream fileInputStream = new FileInputStream( args[ 0 ] ) ;
    final FileOutputStream fileOutputStream = new FileOutputStream( args[ 1 ] ) ;
    final DOMParser domParser = new DOMParser() ;
    final InputSource inputSource = new InputSource( fileInputStream ) ;
    domParser.parse( inputSource );

    final OutputStreamWriter streamWriter =
        new OutputStreamWriter( fileOutputStream, Charset.forName( "UTF-8" ) ) ;
    final DOMElementWriter domWriter = new DOMElementWriter( false ) ;

    streamWriter.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" ) ;
    domWriter.write( domParser.getDocument().getDocumentElement(), streamWriter, 0, "  " ) ;
    streamWriter.flush() ;
    streamWriter.close() ;
    fileOutputStream.flush() ;
    fileOutputStream.close() ;
  }
}
