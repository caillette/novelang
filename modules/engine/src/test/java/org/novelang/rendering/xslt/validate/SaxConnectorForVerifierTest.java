/*
 * Copyright (C) 2011 Laurent Caillette
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

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.ClassUtils;
import org.junit.Test;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.novelang.outfit.xml.XmlNamespaces;

/**
 * Tests for {@link SaxConnectorForVerifier}.
 *
 * @author Laurent Caillette
 */
public class SaxConnectorForVerifierTest {

  @Test
  public void testVerifyOk() throws SAXException {

    final SaxConnectorForVerifier connectorForVerifier = createConnector( "foo", "bar" ) ;
    prepare( connectorForVerifier, new CustomLocator() ) ;

    play( connectorForVerifier, "template", attributes( "match", "t:foo/t:bar" ) ) ;
    play( connectorForVerifier, "for-each", attributes( "no-xpath", "t:ignored" ) ) ;
    play( connectorForVerifier, "dosomething", attributes( "select", "t:foo/t:bar" ) ) ;

    finish( connectorForVerifier ) ;

    connectorForVerifier.endDocument() ;
  }

  @Test( expected = BadExpandedNamesException.class )
  public void testVerifyThrowsException() throws SAXException {

    final SaxConnectorForVerifier connectorForVerifier = createConnector( "foo", "bar" ) ;
    prepare( connectorForVerifier, new CustomLocator() ) ;

    play( connectorForVerifier, "template", attributes( "match", "t:unknown" ) ) ;
    play( connectorForVerifier, "if", attributes( "test", "t:foo/x:ignore" ) ) ;
    play( connectorForVerifier, "if", attributes( "test", "t:foo/t:baz" ) ) ;
    play( connectorForVerifier, "variable", attributes( "select", "t:foo/t:baz" ) ) ;

    finish( connectorForVerifier ) ;

    connectorForVerifier.endDocument() ;
  }


// =======
// Fixture
// =======

  private static final String NAMESPACE_URI_TEST = "http://novelang/test" ;

  private static SaxConnectorForVerifier createConnector( final String... xpathElements ) {
    return new SaxConnectorForVerifier(
        NAMESPACE_URI_TEST,
        ImmutableSet.copyOf( xpathElements )
    ) ;
  }

  private static void prepare(
      final SaxConnectorForVerifier connectorForVerifier,
      final Locator locator
  ) throws SAXException {
    connectorForVerifier.setDocumentLocator( locator ) ;
    connectorForVerifier.startDocument() ;
    connectorForVerifier.startPrefixMapping( "t", NAMESPACE_URI_TEST ) ;
    connectorForVerifier.startPrefixMapping( "xsl", XmlNamespaces.XSL_NAMESPACE_URI ) ;
  }

  private static void play(
      final SaxConnectorForVerifier connectorForVerifier,
      final String localName,
      final CustomAttributes attributes
  ) throws SAXException {
    connectorForVerifier.startElement(
        XmlNamespaces.XSL_NAMESPACE_URI,
        localName,
        "xsl:" + localName,
        attributes
    ) ;
    connectorForVerifier.endElement( XmlNamespaces.XSL_NAMESPACE_URI, localName, "xsl:" + localName ) ;
  }

  private static void finish( final SaxConnectorForVerifier connectorForVerifier ) throws SAXException {
    connectorForVerifier.endPrefixMapping( "xsl" ) ;
    connectorForVerifier.endPrefixMapping( "t" ) ;
    connectorForVerifier.endDocument() ;
  }

  private static CustomAttributes attributes( final String name, final String value ) {
    final CustomAttributes freshAttributes = new CustomAttributes() ;
    add( freshAttributes, name, value ) ;
    return freshAttributes ;
  }

  private static void add( 
      final CustomAttributes attributes, 
      final String name, 
      final String value 
  ) {
    attributes.addAttribute(
        XmlNamespaces.XSL_NAMESPACE_URI,
        name,
        XmlNamespaces.XSL_NAME_QUALIFIER + ":" + name,
        "<no type set>",
        value
    ) ;
  }


  private static class CustomAttributes extends AttributesImpl {

    public CustomAttributes attributes( final String name, final String value ) {
      add( this, name, value ) ;
      return this ;
    }
  }


  private static class CustomLocator implements Locator {

    private final String publicId = "http://novelang/" + ClassUtils.getShortClassName( getClass() ) ;
    private final String systemId = "test:" + ClassUtils.getShortClassName( getClass() ) ;

    private int lineNumber = -1 ;
    private int columnNumber = -1 ;

    @Override
    public String getPublicId() {
      return publicId ;
    }

    @Override
    public String getSystemId() {
      return systemId ;
    }

    @Override
    public int getLineNumber() {
      return lineNumber ;
    }

    public void setLineNumber( final int lineNumber ) {
      this.lineNumber = lineNumber ;
    }

    @Override
    public int getColumnNumber() {
      return columnNumber ;
    }

    public void setColumnNumber( final int columnNumber ) {
      this.columnNumber = columnNumber ;
    }
  }
}
