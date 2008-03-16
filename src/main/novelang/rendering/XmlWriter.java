/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Laurent Caillette
 */
public class XmlWriter implements FragmentWriter {

  private ContentHandler contentHandler ;

  public void startWriting( OutputStream outputStream, Charset encoding ) throws Exception {
    contentHandler = createContentHandler( outputStream, encoding ) ;
    contentHandler.startDocument() ;
  }

  public void finishWriting() throws Exception {
    contentHandler.endDocument() ;
    if( contentHandler instanceof XMLWriter ) {
      ( ( XMLWriter ) contentHandler ).flush() ;
    }
  }

  public void start( Path kinship, boolean wholeDocument ) throws Exception {
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

  public void end( Path kinship, boolean wholeDocument ) throws Exception {
    final String tokenName = tokenNameAsXmlElementName( kinship.getCurrent().name() ) ;
    contentHandler.endElement( NAMESPACE_URI, tokenName, NAME_QUALIFIER + ":" + tokenName ) ;
  }

  public void just( String word ) throws Exception {
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
  }

  public RenditionMimeType getMimeType() {
    return RenditionMimeType.XML ;
  }

  protected ContentHandler createContentHandler( OutputStream outputStream, Charset encoding )
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
