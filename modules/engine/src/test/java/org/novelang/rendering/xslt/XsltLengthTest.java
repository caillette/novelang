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
package org.novelang.rendering.xslt;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;

/**
 * Tests for {@link Numbering}.
 * This test relies on Xerces rather than dom4j because dom4j-1.6.1's {@code DOMElement}
 * seems to provide an incorrect implementation of Node class whith {@code getTextContent()}
 * causing an {@code AbstractMethodError} under Java 5.0.
 *
 *
 * @author Laurent Caillette
 */
public class XsltLengthTest {

  @Test
  public void findOkWithXerces() {
    final Element chapter = createElement( "chapter" ) ;
    addChild( chapter, "paragraph", "1" ) ;
    addChild( chapter, "paragraph", "22" ) ;
    addChild( chapter, "paragraph", "333" ) ;
    addChild( chapter, "paragraph", "4444" ) ;

    Assert.assertEquals(
        3,
        Length.positionUnderCharacterRemainderThreshold( chapter.getChildNodes(), 8 )
    ) ;
  }

// =======
// Fixture
// =======

  private Element createElement( final String name ) {
    final CoreDocumentImpl document = new CoreDocumentImpl() ;
    return document.createElement( name );
  }

  private void addChild( final Element parent, final String name, final String text ) {
    final Element child = parent.getOwnerDocument().createElement( name ) ;
    child.setTextContent( text ) ;
    parent.appendChild( child ) ;
  }

}
