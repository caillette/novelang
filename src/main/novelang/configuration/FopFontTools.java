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
package novelang.configuration;

import java.util.Map;
import java.util.Iterator;
import java.net.URL;
import java.io.File;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.fonts.CachedFontInfo;
import org.apache.fop.fonts.FontTriplet;
import org.apache.fop.fonts.EmbedFontInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.ReflectionTools;
import novelang.system.EnvironmentTools;

/**
 * This class supercedes {@link FopTools}.
 *
 * @author Laurent Caillette
 */
public class FopFontTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( FopFontTools.class ) ;

  public static Map< String, EmbedFontInfo> extractFontMap( FopFactory fopFactory ) {
    final Map< String, EmbedFontInfo > fontMap = ( Map )
        ReflectionTools.getFieldValue( fopFactory.getFontCache(), "fontMap" ) ;
    return fontMap ;
  }

  public static void logFontStatus( FopFactory fopFactory ) {
    final Map< String, EmbedFontInfo > fontMap = extractFontMap( fopFactory ) ;
    LOGGER.debug( "Font files:" );
    for( EmbedFontInfo fontInfo : fontMap.values() ) {
      LOGGER.debug( "  " + fontInfo.getEmbedFile() ) ;
    }

  }


  public static void main( String[] args ) {
//    System.out.println( EnvironmentTools.getEnvironmentInformation() ) ;
    System.out.println( "user.dir=" + System.getProperty( "user.dir" ) ) ;
    final String fontCacheFilename = System.getProperty( "user.home" ) + "/.fop/fop-fonts.cache";
    final File fopFontCache = new File( fontCacheFilename ) ;
    fopFontCache.delete() ;

    final RenderingConfiguration renderingConfiguration =
        ConfigurationTools.buildRenderingConfiguration() ;
    final FopFactory fopFactory = renderingConfiguration.getFopFactory() ;
    fopFactory.getFontCache().getFont( "xxx" ) ;
    final Map< String, EmbedFontInfo > fontMap = extractFontMap( fopFactory ) ;
    System.out.println( "Font files:" );
    for( EmbedFontInfo fontInfo : fontMap.values() ) {
      System.out.println( "  " + fontInfo.getEmbedFile() ) ;
    }


  }

}
