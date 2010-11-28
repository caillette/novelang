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
package org.novelang.rendering.xslt.validate;

import java.util.Set;

import org.novelang.outfit.xml.XmlNamespaces;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.novelang.common.Location;

/**
 * Connects the {@link ExpandedNameVerifier} to a {@code ContentHanler}.
 *
 * @author Laurent Caillette
 */
public class SaxConnectorForVerifier implements ContentHandler {

/*
  private static final Logger LOGGER = LoggerFactory.getLogger( SaxConnectorForVerifier.class ) ;
*/

  private final String namespaceUri ;
  private final ExpandedNameVerifier verifier ;

  public SaxConnectorForVerifier( final String namespaceUri, final Set< String > nodeNames ) {
    this.namespaceUri = namespaceUri ;
    verifier = new ExpandedNameVerifier( nodeNames ) ;
  }

  private void verifyXpath( final String xpath ) {
    verifier.verify( getLocation(), xpath ) ;
  }

// =========
// Utilities
// =========

  private static final Location UNKNOWN_LOCATION = new Location( "unknown", -1, -1 ) ;

  private Locator locator = null ;

  private Location getLocation() {
    if( null == locator ) {
      return UNKNOWN_LOCATION ;
    } else {
      return new Location(
          locator.getSystemId(),
          locator.getLineNumber(),
          locator.getColumnNumber()
      ) ;
    }
  }

/*
  private String attributesToString( Attributes attributes ) {
    final StringBuffer buffer = new StringBuffer() ;
    buffer.append( "[ " ) ;
    for( int i = 0 ; i < attributes.getLength() ; i++ ) {
      buffer.append( attributes.getLocalName( i ) ) ;
      buffer.append( "=`" ) ;
      buffer.append( attributes.getValue( i ) ) ;
      buffer.append( "`" ) ;
      if( i < attributes.getLength() - 1 ) {
        buffer.append( ", " ) ;
      }
    }
    buffer.append( " ]" ) ;
    return buffer.toString() ;
  }
*/

  private static boolean isXslUri( final String uri ) {
    return XmlNamespaces.XSL_NAMESPACE_URI.equals( uri ) ;
  }


// ======================
// ContentHandler methods
// ======================

  @Override
  public void setDocumentLocator( final Locator locator ) {
    this.locator = locator ;
  }

  @Override
  public void startPrefixMapping( final String prefix, final String uri ) throws SAXException {
    if( namespaceUri.equals( uri ) ) {
      if( null == verifier.getXmlPrefix() ) {
        verifier.setXmlPrefix( prefix );
/*
        LOGGER.debug(
            "startPrefixMapping( " +
            "prefix='" + prefix + "' " +
            "uri='" + uri + "'" +
            " )" +
            " at " + getLocation()
        ) ;
*/
      } else {
        throw new IllegalStateException(
            getLocation() +
            "Prefix already mapped: '" + prefix + "'" +
            "to URI " + namespaceUri
        ) ;
      }
    }
  }

  @Override
  public void endPrefixMapping( final String prefix ) throws SAXException {
    if( prefix.equals( verifier.getXmlPrefix() ) ) {
      verifier.unsetXmlPrefix() ;
/*
      LOGGER.debug( "endPrefixMapping( '" + prefix + "' )" ) ;
*/
    }
  }

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) throws SAXException {
/*
    LOGGER.debug(
        "startElement( " +
        "uri=" + uri + " " +
        "localName=" + localName + " " +
        "qName=" + qName + " " +
        attributesToString( attributes ) +
        ")"
    ) ;
*/
    if( isXslUri( uri ) ) {
      for( int i = 0 ; i < attributes.getLength() ; i++ ) {
        final XpathAwareAttribute xpathAwareAttribute =
            new XpathAwareAttribute( localName, attributes.getLocalName( i ) ) ;
        if( XpathAwareAttribute.isXpathCombination( xpathAwareAttribute ) ) {
          verifyXpath( attributes.getValue( i ) ) ;
        }
      }
    }

  }

  @Override
  public void endDocument() throws SAXException {
    verifier.checkNoBadExpandedNames() ;
  }

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

  @Override
  public void startDocument() throws SAXException { }

}