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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.net.MalformedURLException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.fonts.apps.TTFReader;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import com.google.common.collect.Lists;
import com.google.common.base.Objects;
import novelang.common.FileTools;

/**
 * Utility class for generating fonts metrics as FOP needs for custom fonts.
 * 
 * @author Laurent Caillette
 */
public class FopFontsTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( FopFontsTools.class ) ;

  /**
   * Lists all system files to the console.
   * @deprecated because FOP doesn't rely on logical fonts.
   */
  public static void listLogicalFonts() {
    final GraphicsEnvironment graphicsEnvironment =
        GraphicsEnvironment.getLocalGraphicsEnvironment() ;
    final Font[] fonts = graphicsEnvironment.getAllFonts() ;

    for( int i = 0; i < fonts.length ; i++ ) {
      final Font font = fonts[ i ] ;
      System.out.println( font.toString() ) ;
    }
  }

  private static final String[] FONT_FILE_EXTENSIONS = { "ttf" } ;

  /**
   * Lists all font files of a known directory to the console, after loading the font.
   *
   * @deprecated because font loading doesn't provide format and family information as expected.
   */
  public static void listPhysicalFonts( File baseDirectory ) {
    LOGGER.info( "Listing '{}'", baseDirectory ) ;
    final List< File > fontFiles = FileTools.scanFiles( baseDirectory, FONT_FILE_EXTENSIONS ) ;
    for( File fontFile : fontFiles ) {
      final FontFormat fontFormat = guessFontFormat( fontFile ) ;
      try {
        final Font font = Font.createFont( fontFormat.ordinal(), fontFile ) ;
        System.out.println(
            "\n" + FilenameUtils.getName( fontFile.getName() )+ "\n"
          + "  Created font: " + font + " - " + font.getName() + " - " + fontFormat  
        ) ;
      } catch( Exception e ) {
        System.err.println(
            "\nFont creation failed for: " + fontFile
          + " with format " + fontFormat 
          + "\n  " + e.getMessage()
        ) ;
      }
    }
  }

  private static FontFormat guessFontFormat( File fontFile ) {
    return guessFontFormat( FilenameUtils.getBaseName( fontFile.getName() ) ) ;
  }

  private static FontFormat guessFontFormat( String fontFileName ) {
    fontFileName = fontFileName.toUpperCase() ;
    final boolean bold = fontFileName.contains( "BOLD" ) ;
    final boolean italic =
        fontFileName.contains( "ITALIC" )
     || fontFileName.contains( "OBLIQUE" )
     || fontFileName.contains( "INCLINED" )
    ;
    if( bold ) {
      if( italic ) {
        return FontFormat.BOLD_ITALIC ;
      } else {
        return FontFormat.BOLD ;
      }
    } else {
      if( italic ) {
        return FontFormat.ITALIC ;
      } else {
        return FontFormat.PLAIN ;
      }
    }
  }

  private static FontFormat findFontFormat( String fontFileName ) {
    if( fontFileName.endsWith( "-bold" ) ) {
      return FontFormat.BOLD ;
    } ;
    if( fontFileName.endsWith( "-italic" ) ) {
      return FontFormat.ITALIC ;
    }
    if( fontFileName.endsWith( "-bold-italic" ) ) {
      return FontFormat.BOLD_ITALIC ;
    }
    return FontFormat.PLAIN ;
  }

  public static enum FontFormat {
    PLAIN,
    BOLD,
    ITALIC,
    BOLD_ITALIC
  }

  /**
   * Creates FOP font metrics from a given font directory, to a given directory.
   *
   * @param fontDirectory hoped to contain font files ending with {@link #FONT_FILE_EXTENSIONS}.
   * @param metricsDirectory directory to write to.
   * @return a description of font files for which metrics were created.
   * @throws IOException
   */
  public static Iterable< FontFileDescriptor > createFopMetrics(
      File fontDirectory,
      File metricsDirectory
  ) throws IOException {

    fontDirectory = fontDirectory.getCanonicalFile() ;
    metricsDirectory = metricsDirectory.getCanonicalFile() ;

    final List< FontFileDescriptor > fontFileDescriptors = Lists.newArrayList() ;
    final List< File > fontFiles = FileTools.scanFiles( fontDirectory, FONT_FILE_EXTENSIONS ) ;

    for( File fontFile : fontFiles ) {

      final String fontFileBaseName = FilenameUtils.getBaseName( fontFile.getName() );
      final FontFormat fontFormat = findFontFormat( fontFileBaseName ) ;

      final File fontMetricsFile = new File( metricsDirectory, fontFileBaseName + ".xml" );
      final String[] arguments = {
          fontFile.getCanonicalPath(),
          fontMetricsFile.getCanonicalPath()
      } ;

      LOGGER.info( "Calling TTFReader( {} )", Arrays.toString( arguments ) ) ;
      TTFReader.main( arguments ) ;
      fontFileDescriptors.add( new FontFileDescriptor( fontFile, fontMetricsFile, fontFormat ) ) ;
    }
    return fontFileDescriptors ;

  }

  public static Configuration createFopConfiguration( 
      Iterable< FontFileDescriptor > fontFileDescritors
  ) {
    final MutableConfiguration fop = new DefaultConfiguration( "fop" ) ;
    fop.setAttribute( "version", "1.0" ) ;
    final MutableConfiguration renderers = new DefaultConfiguration( "renderers" ) ;
    final MutableConfiguration renderer = new DefaultConfiguration( "renderer" ) ;
    renderer.setAttribute( "mime", "application/pdf" ) ;
    final MutableConfiguration fonts = new DefaultConfiguration( "fonts" ) ;

    for( FontFileDescriptor fontFileDescriptor : fontFileDescritors ) {
      final MutableConfiguration font = new DefaultConfiguration( "font" ) ;
      try {
        font.setAttribute(
            "metrics-url",
            fontFileDescriptor.getMetricsFile().toURI().toURL().toExternalForm()
        ) ;
        fonts.addChild( font ) ;
      } catch( MalformedURLException e ) {
        LOGGER.error( "Problem with font metrics", e ) ;
      }
    }

    renderer.addChild( fonts ) ;
    renderers.addChild( renderer ) ;
    fop.addChild( renderers ) ;


    try {
      


      LOGGER.debug( "Created configuration: \n" + new DefaultConfigurationSerializer().serialize( fop ) ) ;
    } catch( Exception e ) {
      LOGGER.error( "Could not serialize configuration to string", e ) ;
    }
    return fop ;
    
  }

  public static final class FontFileDescriptor {
    private final File fontFile ;
    private final File metricsFile ;
    private final FontFormat fontFormat ;

    public FontFileDescriptor( File fontFile, File metricsFile, FontFormat fontFormat ) {
      this.fontFile = Objects.nonNull( fontFile ) ;
      this.metricsFile = Objects.nonNull( metricsFile ) ;
      this.fontFormat = Objects.nonNull( fontFormat ) ;
    }

    public File getFontFile() {
      return fontFile;
    }

    public File getMetricsFile() {
      return metricsFile;
    }

    public FontFormat getFontFormat() {
      return fontFormat;
    }
  }


  public static void main( String[] args ) throws IOException {
    final File metricsDirectory = new File( "fop-metrics" ) ;
    FileUtils.deleteDirectory( metricsDirectory ) ;
    metricsDirectory.mkdirs() ;
    final File fontsDirectory = new File( "../samples/fonts" ) ;

//    listLogicalFonts() ;

//    listPhysicalFonts( fontsDirectory ) ;
//    listPhysicalFonts( new File( "/Library/Fonts" ) ) ;

    createFopMetrics( fontsDirectory, metricsDirectory ) ;
  }
}
