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

import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontCache;
import org.apache.fop.fonts.FontResolver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.render.DefaultFontResolver;
import org.apache.fop.render.PrintRendererConfigurator;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.common.LanguageTools;
import novelang.common.ReflectionTools;

/**
 * Utility class for generating FOP configuration with hyphenation files and custom fonts.
 * 
 * @author Laurent Caillette
 */
public class FopTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( FopTools.class ) ;

  private static final String CONFIGURATION_NOT_SERIALIZED =
      "Could not serialize configuration to string" ;

  /**
   * Creates a {@code Configuration} object with {@code <renderer>} as element root,
   * using font directories as set by system property.
   */
  public static Configuration createPdfRendererConfiguration(
      Iterable < File > fontDirectories
  ) {
    final MutableConfiguration renderer = new DefaultConfiguration( "renderer" ) ;
    renderer.setAttribute( "mime", "application/pdf" ) ;
    final MutableConfiguration fonts = new DefaultConfiguration( "fonts" ) ;

    for( File fontDirectory : fontDirectories ) {
      final MutableConfiguration directory = new DefaultConfiguration( "directory" ) ;
      directory.setAttribute( "recurse", "true" ) ;
      directory.setValue( fontDirectory.getAbsolutePath() ) ;
      fonts.addChild( directory ) ;
    }
    renderer.addChild( fonts ) ;
    return renderer ;
  }

  /**
   * Creates a {@code Configuration} object with {@code <renderers>} element as root.
   */
  public static Configuration createRenderersConfiguration(
      Iterable< File > fontDirectories
  ) {
    final MutableConfiguration renderers = new DefaultConfiguration( "renderers" ) ;
    renderers.addChild( createPdfRendererConfiguration( fontDirectories ) ) ;
    return renderers ;
  }

  public static Configuration createHyphenationConfiguration( File hyphenationDirectory ) {
    final URL hyphenationBaseUrl ;
    try {
      hyphenationBaseUrl = hyphenationDirectory.toURI().toURL() ;
    } catch( MalformedURLException e ) {
      throw new RuntimeException( e ) ;
    }
    final MutableConfiguration hyphenationBase = new DefaultConfiguration( "hyphenation-base" ) ;
    hyphenationBase.setValue( hyphenationBaseUrl.toExternalForm() ) ;
    return hyphenationBase ;
  }

  public static String configurationAsString( Configuration configuration ) {
    try {
      final StringWriter stringWriter = new StringWriter() ;
      final OutputFormat format = OutputFormat.createPrettyPrint() ;
      final XMLWriter xmlWriter = new XMLWriter( stringWriter, format ) ;
      new DefaultConfigurationSerializer().serialize( xmlWriter, configuration ) ;
      xmlWriter.close() ;

      return stringWriter.toString() ;
    } catch( Exception e ) {
      LOGGER.error( CONFIGURATION_NOT_SERIALIZED, e ) ;
      LanguageTools.rethrowUnchecked( e ) ;
      throw new Error( "Should never execute, just make compiler happy" ) ;
    }
  }

  private static Map< String, EmbedFontInfo > extractFailedFontMap( FontCache fontCache ) {
    return ( Map< String, EmbedFontInfo > ) ( Map )
        ReflectionTools.getFieldValue( fontCache, "failedFontMap" ) ;
  }

  public static void main( String[] args ) throws FOPException {
    final FontsStatus fontsStatus = createGlobalFontStatus() ;

    System.out.println( "Official font list:" ) ;
    for( EmbedFontInfo fontInfo : fontsStatus.getFontInfos() ) {
      System.out.println( "  " + fontInfo ) ;
    }

    System.out.println( "Failed font map:" ) ;
    if( fontsStatus.getFailedFonts().isEmpty() ) {
        System.out.println( "  Empty." ) ;
    } else {
      for( String fontUrl : fontsStatus.getFailedFonts().keySet() ) {
          System.out.println( "  " + fontUrl ) ;
      } ;
    }
  }

  /**
   * Returns a global status for all fonts defined through
   * {@link novelang.configuration.ConfigurationTools#FONTS_DIRS_PROPERTYNAME}.
   */
  public static FontsStatus createGlobalFontStatus() throws FOPException {
    final Configuration pdfRendererConfiguration = createPdfRendererConfiguration(
        ConfigurationTools.getFontsDirectories() ) ;
    final FopFactory fopFactory = ConfigurationTools.buildRenderingConfiguration().getFopFactory() ;
    final FOUserAgent foUserAgent = fopFactory.newFOUserAgent() ;
    final FontResolver fontResolver = new DefaultFontResolver( foUserAgent ) ;
    final FontCache fontCache = new FontCache() ;
    final List< EmbedFontInfo > fontList = ( List< EmbedFontInfo > )
        PrintRendererConfigurator.buildFontListFromConfiguration(
            pdfRendererConfiguration,
            null,
            fontResolver,
            false,
            fontCache
        )
    ;
    final Map< String, EmbedFontInfo > failedFontMap = extractFailedFontMap( fontCache ) ;
    return new FontsStatus( fontList, failedFontMap ) ;
  }


  public static final class FontsStatus {
    private final Iterable<EmbedFontInfo> fontInfos ;
    private final Map< String, EmbedFontInfo > failedFonts ;

    public FontsStatus(
        Iterable< EmbedFontInfo > fontInfos,
        Map< String, EmbedFontInfo > failedFonts
    ) {
      this.fontInfos = fontInfos;
      this.failedFonts = failedFonts;
    }

    public Iterable<EmbedFontInfo> getFontInfos() {
      return fontInfos;
    }

    public Map<String, EmbedFontInfo> getFailedFonts() {
      return failedFonts;
    }
  }
}
