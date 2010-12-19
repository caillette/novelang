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

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Base class for implementing a SAX {@code ContentHandler}, with a dedicated feature
 * for detecting if an element is in a given namespace (set as a constructor parameter).
 *
 * @author Laurent Caillette
 */
public final class NamespaceAwareness {

  private final String namespaceUri ;

  public NamespaceAwareness( final String namespaceUri ) {
    this.namespaceUri = Preconditions.checkNotNull( namespaceUri ) ;
  }


// =======
// Locator
// =======

  private Locator locator = null ;

  public void setDocumentLocator( final Locator locator ) {
    this.locator = locator ;
  }

  public String buildMessageWithLocation( final String message ) {
    return message + (
        locator == null ? "" :
        ( " @ line=" + locator.getLineNumber() + ", column=" + locator.getColumnNumber() )
    ) ;
  }

  public void throwException( final String message ) throws SAXException {
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
  public String getNamespacePrefix() {
    return namespacePrefix ;
  }

  /**
   * @return a non-null object.
   */
  public String getNamespaceUri() {
    return namespaceUri ;
  }

  private final BiMap< String, String > prefixMappings = HashBiMap.create() ;

  public final ImmutableBiMap< String, String> getPrefixMappings() {
    return ImmutableBiMap.copyOf( prefixMappings ) ;
  }

  public void startPrefixMapping( final String prefix, final String uri ) {
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

  public void endPrefixMapping( final String prefix ) {
    if( prefix.equals( namespacePrefix ) ) {
      namespacePrefix = null ;
    }
    prefixMappings.remove( prefix ) ;
  }




}
