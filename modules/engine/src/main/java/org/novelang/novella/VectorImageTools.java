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
package org.novelang.novella;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.loader.ClasspathResourceLoader;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Laurent Caillette
 */
public class VectorImageTools {

  private VectorImageTools() { }

  /**
   * Loads a SVG document, using all official entities.
   */
  public static org.dom4j.Document loadSvgAsDom4jDocument( final InputSource inputSource )
      throws DocumentException
  {
    final SAXReader reader = new SAXReader() ;
    reader.setEntityResolver( ENTITY_RESOLVER ) ;
    final Document document = reader.read( inputSource ) ;
    return document ;
  }


  /**
   * This global variable is dirty.
   * TODO propagate the {@code ResourceLoader} up to here.
   */
  private static final ResourceLoader ENTITY_RESOURCE_LOADER =
      new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR ) ;

  private static final String SVG11_PUBLICID_PREFIX_0 = "-//W3C//DTD SVG 1.0//EN" ;
  private static final String SVG11_PUBLICID_PREFIX_1 = "-//W3C//ENTITIES SVG 1.1" ;
  private static final String SVG11_PUBLICID_PREFIX_2 = "-//W3C//DTD SVG 1.1" ;
  private static final String SVG11_PUBLICID_PREFIX_3 = "-//W3C//ELEMENTS SVG 1.1" ;

  /**
   * This is the path under default {@value ConfigurationTools#BUNDLED_STYLE_DIR}.
   */
  private static final String SVG_1_0_DTD_RESOURCE_PREFIX = "svg10-dtd";

  /**
   * This is the path under default {@value ConfigurationTools#BUNDLED_STYLE_DIR}.
   */
  private static final String SVG_1_1_DTD_RESOURCE_PREFIX = "svg11-dtd";

  private static final Logger LOGGER = LoggerFactory.getLogger( VectorImageTools.class );

  /**
   * Dirty implementation only supporting DTD for SVG 1.1 in bundled style directory.
   */
  private static final EntityResolver ENTITY_RESOLVER = new EntityResolver() {
    @Override
    public InputSource resolveEntity( final String publicId, final String systemId ) {
      if( publicId.startsWith( SVG11_PUBLICID_PREFIX_1 )
       || publicId.startsWith( SVG11_PUBLICID_PREFIX_2 )
       || publicId.startsWith( SVG11_PUBLICID_PREFIX_3 )
      ) {
        return createInputSourceForBundledDtd( publicId, systemId, SVG_1_1_DTD_RESOURCE_PREFIX ) ;
      } else if( publicId.startsWith( SVG11_PUBLICID_PREFIX_0 ) ) {
        return createInputSourceForBundledDtd( publicId, systemId, SVG_1_0_DTD_RESOURCE_PREFIX ) ;      
      } else {
        throw new IllegalArgumentException(
            "Unsupported yet: public identifier='" + publicId + "', systemId='" + systemId + "'" ) ;
      }
    }
  } ;

  private static InputSource createInputSourceForBundledDtd(
      final String publicId,
      final String systemId,
      final String dtdResourcePrefix
  ) {
    final String dtdResourceName = systemId.substring( systemId.lastIndexOf( "/" ) + 1 ) ;
    LOGGER.debug(
        "Attempting to load definition for publicIdentifier='",
        publicId,
        "', systemIdentifier='",
        systemId,
        "', resourceName='",
        dtdResourceName,
        "'"
    ) ;
    return new InputSource(
        ENTITY_RESOURCE_LOADER.getInputStream(
            new ResourceName( dtdResourcePrefix + "/" + dtdResourceName ) ) ) ;
  }


}
