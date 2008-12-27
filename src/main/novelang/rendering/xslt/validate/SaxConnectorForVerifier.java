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
package novelang.rendering.xslt.validate;

import java.util.Set;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.common.collect.ImmutableSet;
import novelang.parser.NodeKind;
import novelang.common.Location;

/**
 * Connects the {@link ExpandedNameVerifier} to a {@code ContentHanler}.
 *
 * @author Laurent Caillette
 */
public class SaxConnectorForVerifier implements ContentHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger( SaxConnectorForVerifier.class ) ;

  private final String namespaceUri ;
  private final ExpandedNameVerifier verifier = new ExpandedNameVerifier( NodeKind.getNames() ) ;

  public SaxConnectorForVerifier( String namespaceUri ) {
    this.namespaceUri = namespaceUri ;
  }

  private void verifyXpath( String xpath ) {
    verifier.verify( getLocation(), xpath ) ;
  }

  public Iterable< BadExpandedName > getBadExpandedNames() {
    return verifier.getBadExpandedNames() ;
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

  private static boolean isXslUri( String uri ) {
    return "http://www.w3.org/1999/XSL/Transform".equals( uri ) ;
  }

  public static final Set< ElementAttributeCombination > XPATH_COMBINATIONS = ImmutableSet.of(
    new ElementAttributeCombination( "apply-templates", "select" ),
    new ElementAttributeCombination( "if", "test" ),
    new ElementAttributeCombination( "template", "match" ),
    new ElementAttributeCombination( "for-each", "select" ),
    new ElementAttributeCombination( "value-of", "select" )
  ) ;

  private static boolean isXpathCombination( ElementAttributeCombination elementAttributeCombination ) {
    return XPATH_COMBINATIONS.contains( elementAttributeCombination ) ;
  }


// ======================
// ContentHandler methods
// ======================

  public void setDocumentLocator( Locator locator ) {
    this.locator = locator ;
  }

  public void startPrefixMapping( String prefix, String uri ) throws SAXException {
    if( namespaceUri.equals( uri ) ) {
      if( null == verifier.getXmlPrefix() ) {
        verifier.setXmlPrefix( prefix );
        LOGGER.debug(
            "startPrefixMapping( " +
            "prefix='" + prefix + "' " +
            "uri='" + uri + "'" +
            " )" +
            " at " + getLocation()
        ) ;
      } else {
        throw new IllegalStateException(
            getLocation() +
            "Prefix already mapped: '" + prefix + "'" +
            "to URI " + namespaceUri
        ) ;
      }
    }
  }

  public void endPrefixMapping( String prefix ) throws SAXException {
    if( prefix.equals( verifier.getXmlPrefix() ) ) {
      verifier.unsetXmlPrefix() ;
      LOGGER.debug( "endPrefixMapping( '" + prefix + "' )" ) ;
    }
  }

  public void startElement(
      String uri,
      String localName,
      String qName,
      Attributes attributes
  ) throws SAXException {
    LOGGER.debug(
        "startElement( " +
        "uri=" + uri + " " +
        "localName=" + localName + " " +
        "qName=" + qName + " " +
        attributesToString( attributes ) +
        ")"
    ) ;
    if( isXslUri( uri ) ) {
      for( int i = 0 ; i < attributes.getLength() ; i++ ) {
        final ElementAttributeCombination elementAttributeCombination =
            new ElementAttributeCombination( localName, attributes.getLocalName( i ) ) ;
        if( isXpathCombination( elementAttributeCombination ) ) {
          verifyXpath( attributes.getValue( i ) ); ;
        }
      }
    }

  }

  public void endElement( String uri, String localName, String qName ) throws SAXException { }

  public void characters( char[] ch, int start, int length ) throws SAXException { }

  public void ignorableWhitespace( char[] ch, int start, int length ) throws SAXException { }

  public void processingInstruction( String target, String data ) throws SAXException { }

  public void skippedEntity( String name ) throws SAXException { }

  public void startDocument() throws SAXException { }

  public void endDocument() throws SAXException { }

}