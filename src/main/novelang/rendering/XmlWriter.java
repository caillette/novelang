/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import novelang.model.common.NodePath;
import novelang.model.common.TreeMetadata;

/**
 * @author Laurent Caillette
 */
public class XmlWriter implements FragmentWriter {

  private ContentHandler contentHandler ;
  private final RenditionMimeType mimeType ;

  public XmlWriter( RenditionMimeType mimeType ) {
    this.mimeType = mimeType;
  }

  public XmlWriter() {
    this( RenditionMimeType.XML ) ;
  }

  public void startWriting(
      OutputStream outputStream,
      TreeMetadata treeMetadata,
      Charset encoding
  ) throws Exception {
    contentHandler = createContentHandler( outputStream, treeMetadata, encoding ) ;
    contentHandler.startDocument() ;
  }

  public void finishWriting() throws Exception {
    contentHandler.endDocument() ;
    if( contentHandler instanceof XMLWriter ) {
      ( ( XMLWriter ) contentHandler ).flush() ;
    }
  }

  public void start( NodePath kinship, boolean wholeDocument ) throws Exception {
    final String tokenName = tokenNameAsXmlElementName( kinship.getCurrent().name() ) ;
    final Attributes attributes ;
    if( wholeDocument ) { // Declare the namespace.
      final AttributesImpl mutableAttributes = new AttributesImpl() ;
      mutableAttributes.addAttribute(
          NAMESPACE_URI,
          NAME_QUALIFIER,
          "xmlns:" + NAME_QUALIFIER,
          "CDATA",
          NAMESPACE_URI
      ) ;
      attributes = mutableAttributes ;
    } else {
      attributes = EMPTY_ATTRIBUTES ;
    }
    contentHandler.startElement(
        NAMESPACE_URI,
        tokenName,
        NAME_QUALIFIER + ":" + tokenName,
        attributes
    ) ;

  }

  public void end( NodePath kinship ) throws Exception {
    final String tokenName = tokenNameAsXmlElementName( kinship.getCurrent().name() ) ;
    contentHandler.endElement( NAMESPACE_URI, tokenName, NAME_QUALIFIER + ":" + tokenName ) ;
  }

  public void write( NodePath kinship, String word ) throws Exception {
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
  }

  public void writeLitteral( NodePath kinship, String word ) throws Exception {
    ( ( LexicalHandler ) contentHandler ).startCDATA() ;
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
    ( ( LexicalHandler ) contentHandler ).endCDATA() ;
  }

  public RenditionMimeType getMimeType() {
    return mimeType ;
  }

  protected ContentHandler createContentHandler(
      OutputStream outputStream,
      TreeMetadata treeMetadata,
      Charset encoding
  )
      throws Exception
  {
    return new XMLWriter(
        outputStream,
        new OutputFormat( "  ", true, encoding.name() )
    ) ;
  }

  private String tokenNameAsXmlElementName( String tokenName ) {
    String result = tokenName.toLowerCase().replace( "_", "-" ) ;
    if( result.startsWith( "-" ) ) {
      result = result.substring( 1 ) ;
    }
    return result ;
  }

  private static final String NAMESPACE_URI = "http://novelang.org/book-xml/1.0" ;
  private static final String NAME_QUALIFIER = "n" ;
  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl() ;


}
