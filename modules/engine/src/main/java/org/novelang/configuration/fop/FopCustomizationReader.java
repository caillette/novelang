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
package org.novelang.configuration.fop;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;
import org.novelang.outfit.CollectionTools;
import org.novelang.outfit.Husk;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.outfit.xml.IncorrectXmlException;
import org.novelang.outfit.xml.StackBasedElementReader;
import org.novelang.outfit.xml.XmlNamespaces;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import static com.google.common.collect.ImmutableList.of;
import static org.novelang.configuration.fop.XmlElement.*;

/**
 * Creates a {@link FopCustomization}.
 * We're implementing the rather unconvenient {@link ContentHandler} because
 * {@link org.novelang.rendering.XslWriter} loads XSL stylesheets from a SAX source.
 *
 * @author Laurent Caillette
 */
public class FopCustomizationReader
    extends StackBasedElementReader< XmlElement, XmlAttribute, Object >
{

  public FopCustomizationReader() {
    super(
        XmlNamespaces.XSL_META_NAMESPACE_URI,
        ELEMENT_PATHS,
        PATH_ELEMENT_TO_STRING,
        ATTRIBUTE_TO_NAME,
        FIND_FROM_LOCAL_NAME
    ) ;
  }

  private static final Function< XmlElement,String > PATH_ELEMENT_TO_STRING =
      new Function< XmlElement, String >() {
        @Override
        public String apply( final XmlElement input ) {
          return input.getLocalName() ;
        }
      }
  ;

  private static final Function< XmlAttribute,String > ATTRIBUTE_TO_NAME =
      new Function< XmlAttribute, String >() {
        @Override
        public String apply( final XmlAttribute attribute ) {
          return attribute.getAttributeName() ;
        }
      }
  ;

  private static final Function< String, XmlElement > FIND_FROM_LOCAL_NAME =
      new Function< String, XmlElement >() {
        @Override
        public XmlElement apply( final String localName ) {
          return XmlElement.fromLocalName( localName ) ;
        }
      }
  ;
  private static final ImmutableSet< ImmutableList< XmlElement > > ELEMENT_PATHS = ImmutableSet.of(
      of( FOP, TARGET_RESOLUTION ),
      of( FOP, RENDERER ),
      of( FOP, RENDERER, FONTS_DIRECTORY ),
      of( FOP, RENDERER, OUTPUT_PROFILE ),
      of( FOP, RENDERER, FILTER_LIST ),
      of( FOP, RENDERER, FILTER_LIST, VALUE ),
      of( FOP )
  ) ;


  private final ImmutableList.Builder< FopCustomization > configurations =
      ImmutableList.builder() ;

  /**
   * Returns all the parsed configurations.
   * @return a non-null, possibly empty list.
   */
  public ImmutableList< FopCustomization > getConfigurations() {
    return configurations.build() ;
  }



// ========
// Stacking
// ========

  @Override
  protected Object preparePush( final XmlElement element, final Attributes attributes )
      throws IncorrectXmlException
  {

    switch( element ) {
      case FOP :
        return Husk.create( FopCustomization.class )
            .withRenderers( ImmutableSet.< FopCustomization.Renderer >of() )
        ;
      case TARGET_RESOLUTION :
        return null ;
      case RENDERER :
        final FopCustomization.Renderer renderer =
            Husk.create( FopCustomization.Renderer.class ) ;
        return renderer
            .withMime( getStringAttributeValue( attributes, XmlAttribute.MIME ) )
            .withFontsDirectories(
                ImmutableList.< FopCustomization.Renderer.FontsDirectory >of() )
            .withFilters( ImmutableList.< FopCustomization.Renderer.Filters >of() )
        ;
      case FONTS_DIRECTORY :
        return Husk.create( FopCustomization.Renderer.FontsDirectory.class )
            .withRecursive( getBooleanAttributeValue( attributes, XmlAttribute.RECURSIVE, false ) )
        ;
      case OUTPUT_PROFILE :
        return null ;
      case FILTER_LIST :
        return ImmutableList.< FopCustomization.Renderer.Filters >of() ;
      case VALUE :
        return null ;
      default :
        throw new IllegalArgumentException( "Unsupported: " + element ) ;
    }
  }

  /**
   * Returns the new element at the top of the stack after popping.
   * This method typically read the current top of the stack, or the collected text.
   */
  @Override
  protected Object preparePop() throws IncorrectXmlException {

    switch( getTopSegment() ) {

      case FOP :
        configurations.add( ( FopCustomization ) getBuildupOnTop() ) ;
        return null ;

      case TARGET_RESOLUTION :
        return ( ( FopCustomization ) getBuildupUnderTop() )
            .withTargetResolution( getIntegerFromCollectedText() ) ;

      case RENDERER :
        final FopCustomization fopCustomization = ( FopCustomization ) getBuildupUnderTop() ;
        final FopCustomization.Renderer renderer =
            ( FopCustomization.Renderer ) getBuildupOnTop() ;
        return fopCustomization.withRenderers(
            CollectionTools.append( fopCustomization.getRenderers(), renderer ) ) ;

      case FONTS_DIRECTORY :
        final FopCustomization.Renderer renderer0 =
            ( FopCustomization.Renderer ) getBuildupUnderTop() ;
        final String directoryName = StringUtils.trim( getAndClearCollectedText() ) ;
        if( StringUtils.isBlank( directoryName ) ) {
          throwException( "Directory name cannot be empty" ) ;
        }
        final FopCustomization.Renderer.FontsDirectory fontsDirectory =
            ( ( FopCustomization.Renderer.FontsDirectory ) getBuildupOnTop() )
            .withPath( directoryName )
        ;
        return renderer0.withFontsDirectories(
            CollectionTools.append( renderer0.getFontsDirectories(), fontsDirectory ) ) ;

      case OUTPUT_PROFILE:
        final FopCustomization.Renderer renderer1 =
            ( FopCustomization.Renderer ) getBuildupUnderTop() ;
        final ResourceName profile = getResourceNameFromCollectedText() ;
        return renderer1.withOutputProfile( profile ) ;

      case FILTER_LIST:
        // TODO.
        break;
      case VALUE:
        break;
    }

    // Default: avoid to discard stacks' top.
    return getBuildupUnderTop() ;
  }


// =========
// Utilities
// =========


}
