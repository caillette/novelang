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
package org.novelang.outfit.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Base class for implementing a SAX {@code ContentHandler} wrapping another one.
 *
 * @author Laurent Caillette
 */
public abstract class DelegatingContentHandler extends ContentHandlerAdapter {

  /**
   * @return a non-null object.
   */
  protected abstract ContentHandler getDelegate() ;

  @Override
  public void setDocumentLocator( final Locator locator ) {
    super.setDocumentLocator( locator ) ;
    getDelegate().setDocumentLocator( getDocumentLocator() ) ;
  }

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException {
    getDelegate().startPrefixMapping( prefix, uri ) ;
  }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException {
    getDelegate().endPrefixMapping( prefix ) ;
  }

  @Override
  public void startDocument() throws SAXException {
    getDelegate().startDocument() ;
  }

  @Override
  public void endDocument() throws SAXException {
    getDelegate().endDocument() ;
  }

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException {
    getDelegate().startElement( uri, localName, qName, attributes ) ;
  }

  @Override
  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) throws SAXException {
    getDelegate().endElement( uri, localName, qName ) ;
  }

  @Override
  public void characters(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException {
    getDelegate().characters( ch, start, length ) ;
  }

  @Override
  public void ignorableWhitespace(
      final char[] ch,
      final int start,
      final int length
  ) throws SAXException {
    getDelegate().ignorableWhitespace( ch, start, length ) ;
  }

  @Override
  public void processingInstruction(
      final String target,
      final String data
  ) throws SAXException {
    getDelegate().processingInstruction( target, data ) ;
  }

  @Override
  public void skippedEntity( final String name ) throws SAXException {
    getDelegate().skippedEntity( name ) ;
  }


}
