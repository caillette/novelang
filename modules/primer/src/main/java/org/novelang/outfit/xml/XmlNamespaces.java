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

/**
 * XML namespaces.
 *
 * @author Laurent Caillette
 */
public class XmlNamespaces {

  private XmlNamespaces() { }

  /**
   * Namespace for elements in rendered document tree.
   */
  public static final String TREE_NAMESPACE_URI = "http://novelang.org/book-xml/1.0" ;

  /**
   * Use only for output in {@link #TREE_NAMESPACE_URI}; for input rely on
   * {@link org.xml.sax.ContentHandler#startPrefixMapping}.
   */
  public static final String TREE_NAME_QUALIFIER = "n" ;

  /**
   * Namespace for XSL stylesheet metadata.
   */
  public static final String XSL_META_NAMESPACE_URI = "http://novelang.org/xsl-meta/1.0" ;


  /**
   * Namespace for XSL declarations.
   */
  public static final String XSL_NAMESPACE_URI = "http://www.w3.org/1999/XSL/Transform" ;

  /**
   * Use only for output in {@link #XSL_NAMESPACE_URI}; for input rely on
   * {@link org.xml.sax.ContentHandler#startPrefixMapping}.
   */
  public static final String XSL_NAME_QUALIFIER = "xsl" ;

}
