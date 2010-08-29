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

package org.novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import com.google.common.base.Preconditions;
import org.novelang.common.Nodepath;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.parser.NodeKindTools;
import org.novelang.outfit.DefaultCharset;

/**
 * @author Laurent Caillette
 */
public class XmlWriter implements FragmentWriter {

  private ContentHandler contentHandler ;
  private final String namespaceUri ;
  private final String nameQualifier ;
  private final Charset charset ;
  private final RenditionMimeType mimeType ;

  public XmlWriter( final RenditionMimeType mimeType ) {
    this( NAMESPACE_URI, NAME_QUALIFIER, DefaultCharset.RENDERING, mimeType ) ;
  }

  public XmlWriter(
      final String namespaceUri,
      final String nameQualifier,
      final Charset charset,
      final RenditionMimeType mimeType
  ) {
    this.namespaceUri = Preconditions.checkNotNull( namespaceUri ) ;
    this.nameQualifier = Preconditions.checkNotNull( nameQualifier ) ;
    this.charset = Preconditions.checkNotNull( charset ) ;
    this.mimeType = mimeType;
  }

  public XmlWriter() {
    this( RenditionMimeType.XML ) ;
  }

  
  public void startWriting(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata
  ) throws Exception {
    contentHandler = createContentHandler( outputStream, documentMetadata, charset ) ;
    contentHandler.startDocument() ;
  }

  public void finishWriting() throws Exception {
    contentHandler.endDocument() ;
    if( contentHandler instanceof XMLWriter ) {
      ( ( XMLWriter ) contentHandler ).flush() ;
    }
  }

  public void start( final Nodepath kinship, final boolean wholeDocument ) throws Exception {
    start( NodeKindTools.tokenNameAsXmlElementName( kinship.getCurrent().name() ), wholeDocument ) ;
  }
  
  public void start( final String elementName ) throws Exception {
    start( elementName, false ) ;
  }
  
  public void start( final String elementName, final boolean wholeDocument ) throws Exception {
    final Attributes attributes ;
    if( wholeDocument ) { // Declare the namespace.
      final AttributesImpl mutableAttributes = new AttributesImpl() ;
      mutableAttributes.addAttribute(
          namespaceUri,
          nameQualifier,
          "xmlns:" + nameQualifier,
          "CDATA",
          namespaceUri
      ) ;
      attributes = mutableAttributes ;
    } else {
      attributes = EMPTY_ATTRIBUTES ;
    }
    contentHandler.startElement(
        namespaceUri,
        elementName,
        nameQualifier + ":" + elementName,
        attributes
    ) ;
  }

  public void end( final Nodepath kinship ) throws Exception {
    end( NodeKindTools.tokenNameAsXmlElementName( kinship.getCurrent().name() ) ) ;
  }
  public void end( final String elementName ) throws Exception {
    contentHandler.endElement( namespaceUri, elementName, nameQualifier + ":" + elementName ) ;
  }

  public void write( final String word ) throws Exception {
    write( null, word ) ;
  }

  public void write( final Nodepath kinship, final String word ) throws Exception {
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
  }

  public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
    ( ( LexicalHandler ) contentHandler ).startCDATA() ;
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
    ( ( LexicalHandler ) contentHandler ).endCDATA() ;
  }

  public RenditionMimeType getMimeType() {
    return mimeType ;
  }

  protected ContentHandler createContentHandler(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata,
      final Charset charset
  )
      throws Exception
  {
    final OutputFormat outputFormat = new OutputFormat( "  ", true, charset.name() ) ;
    outputFormat.setExpandEmptyElements( false ) ;
    outputFormat.setXHTML( true ) ;
    return new XMLWriter(
        outputStream,
        outputFormat
    ) ;
  }

  protected static final String NAMESPACE_URI = "http://novelang.org/book-xml/1.0" ;
  protected static final String NAME_QUALIFIER = "n" ;
  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl() ;


}
