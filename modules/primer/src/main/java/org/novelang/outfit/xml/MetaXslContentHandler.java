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
package org.novelang.outfit.xml;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * SAX content handler for XSL stylehseet metadata.
 *
 * @author Laurent Caillette
 */
public abstract class MetaXslContentHandler implements ContentHandler {

  protected final String namespaceUri ;

  protected MetaXslContentHandler() {
    this( XmlNamespaces.XSL_META_NAMESPACE_URI ) ;
  }

  protected MetaXslContentHandler( final String namespaceUri ) {
    this.namespaceUri = Preconditions.checkNotNull( namespaceUri ) ;
  }


// =======
// Locator
// =======

  protected Locator locator = null ;

  @Override
  public void setDocumentLocator( final Locator locator ) {
    this.locator = locator ;
  }

  protected final String buildMessageWithLocation( final String message ) {
    return message + (
        locator == null ? "" :
        ( " @ line=" + locator.getLineNumber() + ", column=" + locator.getColumnNumber() )
    ) ;
  }

  protected void throwException( final String message ) throws SAXException {
    throw new SAXException( buildMessageWithLocation( message ) ) ;
  }


// ================
// Namespace prefix
// ================

  private String namespacePrefix = null ;

  public final boolean isMetaPrefix( final String uri ) {
    return namespaceUri.equals( uri ) ;
  }

  /**
   * Maybe null in the extreme case where the element declaring it isn't parsed yet.
   * @return a possibly null {@code String}. 
   */
  protected String getNamespacePrefix() {
    return namespacePrefix ;
  }

  public String getNamespaceUri() {
    return namespaceUri ;
  }

  private final Map< String, String > prefixMappings = Maps.newTreeMap() ;

  public final ImmutableMap< String, String> getPrefixMappings() {
    return ImmutableMap.copyOf( prefixMappings ) ;
  }

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException {
    if( namespaceUri.equals( uri ) ) {
      if( namespacePrefix == null ) {
        namespacePrefix = Preconditions.checkNotNull( prefix ) ;
      } else {
        throw new IllegalStateException(
            "Namespace URI '" + namespaceUri + "' already mapped to '" + namespacePrefix + "'" ) ;
      }
    }
    prefixMappings.put( prefix, uri ) ;
  }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException {
    if( prefix.equals( namespacePrefix ) ) {
      namespacePrefix = null ;
    }
    prefixMappings.remove( prefix ) ;
  }


// =====
// No-op
// =====

  @Override
  public void startDocument() throws SAXException { }

  @Override
  public void endDocument() throws SAXException { }

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException { }

  @Override
  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) throws SAXException { }

  @Override
  public void characters(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException { }

  @Override
  public void ignorableWhitespace(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException { }

  @Override
  public void processingInstruction(
      final String target,
      final String data
  ) throws SAXException { }

  @Override
  public void skippedEntity( final String name ) throws SAXException { }



}
