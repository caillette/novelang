/*
 * Copyright (C) 2010 Laurent Caillette
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

import com.google.common.base.Preconditions;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.novelang.common.Nodepath;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.xml.XmlNamespaces;
import org.novelang.parser.NodeKindTools;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

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
    this(
        XmlNamespaces.TREE_NAMESPACE_URI,
        XmlNamespaces.TREE_NAME_QUALIFIER,
        DefaultCharset.RENDERING, mimeType
    ) ;
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

  
  @Override
  public void startWriting(
      final OutputStream outputStream,
      final DocumentMetadata documentMetadata
  ) throws Exception {
    contentHandler = createContentHandler( outputStream, documentMetadata, charset ) ;
    contentHandler.startDocument() ;
  }

  @Override
  public void finishWriting() throws Exception {
    contentHandler.endDocument() ;
    if( contentHandler instanceof XMLWriter ) {
      ( ( XMLWriter ) contentHandler ).flush() ;
    }
  }

  @Override
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

  @Override
  public void end( final Nodepath kinship ) throws Exception {
    end( NodeKindTools.tokenNameAsXmlElementName( kinship.getCurrent().name() ) ) ;
  }
  public void end( final String elementName ) throws Exception {
    contentHandler.endElement( namespaceUri, elementName, nameQualifier + ":" + elementName ) ;
  }

  public void write( final String word ) throws Exception {
    write( null, word ) ;
  }

  @Override
  public void write( final Nodepath kinship, final String word ) throws Exception {
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
  }

  @Override
  public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
    ( ( LexicalHandler ) contentHandler ).startCDATA() ;
    contentHandler.characters( word.toCharArray(), 0, word.length() ) ;
    ( ( LexicalHandler ) contentHandler ).endCDATA() ;
  }

  @Override
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

  private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl() ;


}
