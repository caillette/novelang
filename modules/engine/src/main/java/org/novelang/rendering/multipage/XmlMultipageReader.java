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
package org.novelang.rendering.multipage;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.outfit.Husk;
import org.novelang.outfit.xml.IncorrectXmlException;
import org.novelang.outfit.xml.StackBasedElementReader;
import org.novelang.outfit.xml.XmlNamespaces;
import org.xml.sax.Attributes;

import static com.google.common.collect.ImmutableList.of;
import static org.novelang.rendering.multipage.MultipageElement.*;

/**
 * Reads XML representing a list of pages as internally rendered by
 * {@link org.novelang.rendering.multipage.XslPageIdentifierExtractor}.
 *
 * @author Laurent Caillette
 */
/*package*/ class XmlMultipageReader
    extends StackBasedElementReader< MultipageElement, Void, Object >
{

  public XmlMultipageReader() {
    super(
        XmlNamespaces.TREE_NAMESPACE_URI,
        ELEMENT_PATHS,
        MULTIPAGE_ELEMENT_TO_STRING,
        NULL_FUNCTION_FOR_ATTRIBUTE, 
        FIND_FROM_LOCAL_NAME
    ) ;
  }

  private final Map< PageIdentifier, String > pageIdentifiers = Maps.newLinkedHashMap() ;

  public ImmutableMap< PageIdentifier, String > getPageIdentifiers() {
    return ImmutableMap.copyOf( pageIdentifiers ) ;
  }

// ========
// Stacking
// ========

  @Override
  protected Object preparePush( final MultipageElement element, final Attributes attributes )
      throws IncorrectXmlException
  {
    switch( element ) {
      case PAGES :
        return null ;
      case PAGE :
        return Husk.create( PageBuildup.class ) ;
      case IDENTIFIER:
        return null ;
      case PATH :
        return null ;
      default : throw new IllegalArgumentException( "Unsupported: " + element ) ;
    }

  }

  @Override
  protected Object preparePop() throws IncorrectXmlException {
    final MultipageElement topSegment = getTopSegment();
    switch( topSegment ) {
      case PAGES :
        return null ;
      case PAGE:
        final PageBuildup pageBuildup = ( PageBuildup ) getBuildupOnTop() ;
        pageIdentifiers.put( pageBuildup.getPageIdentifier(), pageBuildup.getPath() ) ;
        return null ;
      case IDENTIFIER:
        final PageBuildup buildup0 = ( PageBuildup ) getBuildupUnderTop() ;
        final String text = StringUtils.trim( getAndClearCollectedText() ) ;
        return buildup0.withPageIdentifier( new PageIdentifier( text ) ) ;
      case PATH:
        final PageBuildup buildup1 = ( PageBuildup ) getBuildupUnderTop() ;
        return buildup1.withPath( StringUtils.trim( getAndClearCollectedText() ) ) ;
      default :
        throw new IllegalArgumentException( "Unsupported: " + topSegment ) ;
    }
  }


  private static interface PageBuildup {
    PageIdentifier getPageIdentifier() ;
    PageBuildup withPageIdentifier( PageIdentifier name ) ;
    String getPath() ;
    PageBuildup withPath( String path ) ;

  }

// ================
// Boring constants
// ================

  private static final ImmutableSet< ImmutableList< MultipageElement > > ELEMENT_PATHS =
      ImmutableSet.of(
          of( PAGES ),
          of( PAGES, PAGE ),
          of( PAGES, PAGE, IDENTIFIER ),
          of( PAGES, PAGE, PATH )
      )
  ;

  private static final Function< MultipageElement, String > MULTIPAGE_ELEMENT_TO_STRING =
      new Function< MultipageElement, String >() {
        @Override
        public String apply( final MultipageElement input ) {
          return input.getLocalName() ;
        }
      }
  ;

  private static final Function< String, MultipageElement > FIND_FROM_LOCAL_NAME =
      new Function< String, MultipageElement >() {
        @Override
        public MultipageElement apply( final String input ) {
          return MultipageElement.fromLocalName( input ) ;
        }
      }
  ;

  private static final Function< Void, String > NULL_FUNCTION_FOR_ATTRIBUTE =
      new Function< Void, String >() {
        @Override
        public String apply( final Void input ) {
          throw new UnsupportedOperationException( "Should never be called" ) ;
        }
      }
  ;


}
