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
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontCache;
import org.apache.fop.fonts.FontResolver;
import org.apache.fop.render.DefaultFontResolver;
import org.apache.fop.render.PrintRendererConfigurator;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.google.common.base.Function;
import novelang.common.LanguageTools;
import novelang.common.ReflectionTools;

/**
 * Utility class for generating FOP configuration with hyphenation files and custom fonts.
 * 
 * @author Laurent Caillette
 */
public class FopTools {

  private static final Log LOG = LogFactory.getLog( FopTools.class ) ;

  private static final String CONFIGURATION_NOT_SERIALIZED =
      "Could not serialize configuration to string" ;

  public static final Function< ? super EmbedFontInfo,? extends String >
      EXTRACT_EMBEDFONTINFO_FUNCTION = new Function< EmbedFontInfo, String >() {
        public String apply( final EmbedFontInfo embedFontInfo ) {
          return embedFontInfo.getEmbedFile() ;
        }
      }
  ;

  /**
   * Creates a {@code Configuration} object with {@code <renderer>} as element root,
   * using font directories as set by system property.
   */
  public static Configuration createPdfRendererConfiguration(
      final Iterable < File > fontDirectories
  ) {
    final MutableConfiguration renderer = new DefaultConfiguration( "renderer" ) ;
    renderer.setAttribute( "mime", "application/pdf" ) ;
    final MutableConfiguration fonts = new DefaultConfiguration( "fonts" ) ;

    for( final File fontDirectory : fontDirectories ) {
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
      final Iterable< File > fontDirectories
  ) {
    final MutableConfiguration renderers = new DefaultConfiguration( "renderers" ) ;
    renderers.addChild( createPdfRendererConfiguration( fontDirectories ) ) ;
    return renderers ;
  }

  public static Configuration createHyphenationConfiguration( final File hyphenationDirectory ) {
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

  public static String configurationAsString( final Configuration configuration ) {
    try {
      final StringWriter stringWriter = new StringWriter() ;
      final OutputFormat format = OutputFormat.createPrettyPrint() ;
      final XMLWriter xmlWriter = new XMLWriter( stringWriter, format ) ;
      new DefaultConfigurationSerializer().serialize( xmlWriter, configuration ) ;
      xmlWriter.close() ;

      return stringWriter.toString() ;
    } catch( Exception e ) {
      LOG.error( CONFIGURATION_NOT_SERIALIZED, e ) ;
      LanguageTools.rethrowUnchecked( e ) ;
      throw new Error( "Should never execute, just make compiler happy" ) ;
    }
  }

  private static Map< String, EmbedFontInfo > extractFailedFontMap( final FontCache fontCache ) {
    return ( Map< String, EmbedFontInfo > )
        ReflectionTools.getFieldValue( fontCache, "failedFontMap" );
  }

  public static FopFontStatus createGlobalFontStatus(
      final FopFactory fopFactory,
      final Iterable< File > fontDirectories
  ) throws FOPException {
    final Configuration pdfRendererConfiguration =
        createPdfRendererConfiguration( fontDirectories ) ;
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
    return new FopFontStatus( fontList, failedFontMap ) ;
  }


  /*package*/ static FopFactory createFopFactory(
      final Iterable< File > fontsDirectories,
      final File hyphenationDirectory
  )
      throws FOPException
  {
    final FopFactory fopFactory = FopFactory.newInstance() ;
    Configuration renderers = null ;
    Configuration hyphenationBase = null ;
    boolean configure = false ;

    if( fontsDirectories.iterator().hasNext() ) {
      renderers = createRenderersConfiguration( fontsDirectories ) ;
      configure = true ;
    }

    if( null != hyphenationDirectory ) {
      hyphenationBase = createHyphenationConfiguration( hyphenationDirectory ) ;
      configure = true ;
    }

    if( configure ) {

      final MutableConfiguration configuration = new DefaultConfiguration( "fop" ) ;
      configuration.setAttribute( "version", "1.0" ) ;

      if( null != renderers ) {
        configuration.addChild( renderers ) ;
      }

      if( null != hyphenationBase ) {
        configuration.addChild( hyphenationBase ) ;
      }

      LOG.debug( "Created configuration: \n%s",
          configurationAsString( configuration ) ) ;

      fopFactory.setUserConfig( configuration ) ;
    }

    return fopFactory ;
  }
}
