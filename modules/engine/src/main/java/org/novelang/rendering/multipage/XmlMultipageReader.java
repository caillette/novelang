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
package org.novelang.rendering.multipage;

import org.novelang.outfit.xml.ForwardingContentHandler;
import org.novelang.outfit.xml.StackBasedElementReader;
import org.novelang.outfit.xml.XmlNamespaces;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reads XML representing a list of pages as internally rendered by
 * {@link org.novelang.rendering.multipage.XslPageIdentifierExtractor}.
 *
 * @author Laurent Caillette
 */
public class XmlMultipageReader /*extends StackBasedElementReader*/ {

  public XmlMultipageReader() {
//    super( XmlNamespaces.TREE_NAMESPACE_URI ) ;
  }



  private enum MultipageElement {
    PAGES, PAGE, NAME, PATH 
  }
}
