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
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.fonts.apps.TTFReader;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import novelang.common.FileTools;

/**
 * Utility class for generating fonts metrics as FOP needs for custom fonts.
 * 
 * @author Laurent Caillette
 */
public class FopFontsTools {

  private static final Logger LOGGER = LoggerFactory.getLogger( FopFontsTools.class ) ;
  private static final String FONTNAMESUFFIX_BOLD_ITALIC = "-bold-italic";
  private static final String FONTNAMESUFFIX_ITALIC = "-italic";
  private static final String FONTNAMESUFFIX_BOLD = "-bold";

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
      final FontDescriptor.Format fontFormat = guessFontFormat( fontFile ) ;
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

  private static FontDescriptor.Format guessFontFormat( File fontFile ) {
    return guessFontFormat( FilenameUtils.getBaseName( fontFile.getName() ) ) ;
  }

  private static FontDescriptor.Format guessFontFormat( String fontFileName ) {
    fontFileName = fontFileName.toUpperCase() ;
    final boolean bold = fontFileName.contains( "BOLD" ) ;
    final boolean italic =
        fontFileName.contains( "ITALIC" )
     || fontFileName.contains( "OBLIQUE" )
     || fontFileName.contains( "INCLINED" )
    ;
    if( bold ) {
      if( italic ) {
        return FontDescriptor.Format.BOLD_ITALIC ;
      } else {
        return FontDescriptor.Format.BOLD ;
      }
    } else {
      if( italic ) {
        return FontDescriptor.Format.ITALIC ;
      } else {
        return FontDescriptor.Format.PLAIN ;
      }
    }
  }

  private static FontDescriptor.Format findFontFormat( String fontFileName ) {
    if( fontFileName.endsWith( FONTNAMESUFFIX_BOLD ) ) {
      return FontDescriptor.Format.BOLD ;
    } ;
    if( fontFileName.endsWith( FONTNAMESUFFIX_ITALIC ) ) {
      return FontDescriptor.Format.ITALIC ;
    }
    if( fontFileName.endsWith( FONTNAMESUFFIX_BOLD_ITALIC ) ) {
      return FontDescriptor.Format.BOLD_ITALIC ;
    }
    return FontDescriptor.Format.PLAIN ;
  }

  private static String extractFontName( String fontFileName ) {
    if( fontFileName.endsWith( FONTNAMESUFFIX_BOLD ) ) {
      return fontFileName.substring( 0, fontFileName.length() - FONTNAMESUFFIX_BOLD.length() ) ;
    } ;
    if( fontFileName.endsWith( FONTNAMESUFFIX_ITALIC ) ) {
      return fontFileName.substring( 0, fontFileName.length() - FONTNAMESUFFIX_ITALIC.length() ) ;
    }
    if( fontFileName.endsWith( FONTNAMESUFFIX_BOLD_ITALIC ) ) {
      return fontFileName.substring( 0, fontFileName.length() - FONTNAMESUFFIX_BOLD_ITALIC.length() ) ;
    }
    return fontFileName ;
  }

  private static String italicOrNormal( FontDescriptor.Format fontFormat ) {
    switch( fontFormat ) {
      case BOLD_ITALIC :
      case ITALIC : return "italic" ;
      default : return "normal" ;
    }
  }

  private static String boldOrNormal( FontDescriptor.Format fontFormat ) {
    switch( fontFormat ) {
      case BOLD_ITALIC :
      case BOLD : return "bold" ;
      default : return "normal" ;
    }
  }

  /**
   * Creates FOP font metrics from a given font directory, to a given directory.
   *
   * @param fontDirectory hoped to contain font files ending with {@link #FONT_FILE_EXTENSIONS}.
   * @param metricsDirectory directory to write to.
   * @return a description of font files for which metrics were created.
   * @throws IOException
   */
  public static Iterable<FontDescriptor> createFopMetrics(
      File fontDirectory,
      File metricsDirectory
  ) throws IOException {

    fontDirectory = fontDirectory.getCanonicalFile() ;
    metricsDirectory = metricsDirectory.getCanonicalFile() ;

    final List<FontDescriptor> fontDescriptors = Lists.newArrayList() ;
    final List< File > fontFiles = FileTools.scanFiles( fontDirectory, FONT_FILE_EXTENSIONS ) ;

    for( File fontFile : fontFiles ) {

      final String fontFileBaseName = FilenameUtils.getBaseName( fontFile.getName() );
      final FontDescriptor.Format fontFormat = findFontFormat( fontFileBaseName ) ;

      final File fontMetricsFile = new File( metricsDirectory, fontFileBaseName + ".xml" );
      final String[] arguments = {
          fontFile.getCanonicalPath(),
          fontMetricsFile.getCanonicalPath()
      } ;

      LOGGER.info( "Calling TTFReader( {} )", Arrays.toString( arguments ) ) ;
      TTFReader.main( arguments ) ;
      fontDescriptors.add( new FontDescriptor(
          extractFontName( fontFileBaseName ),
          fontFile,
          fontMetricsFile,
          fontFormat
      ) ) ;
    }
    return fontDescriptors;

  }

  public static Configuration createFopConfiguration( 
      Iterable<FontDescriptor> fontFileDescritors
  ) {
    final MutableConfiguration fop = new DefaultConfiguration( "fop" ) ;
    fop.setAttribute( "version", "1.0" ) ;
    final MutableConfiguration renderers = new DefaultConfiguration( "renderers" ) ;
    final MutableConfiguration renderer = new DefaultConfiguration( "renderer" ) ;
    renderer.setAttribute( "mime", "application/pdf" ) ;
    final MutableConfiguration fonts = new DefaultConfiguration( "fonts" ) ;

    for( FontDescriptor fontDescriptor : fontFileDescritors ) {
      final MutableConfiguration font = new DefaultConfiguration( "font" ) ;
      try {
        font.setAttribute(
            "metrics-url",
            fontDescriptor.getMetricsFile().toURI().toURL().toExternalForm()
        ) ;
        font.setAttribute( "kerning", "yes" ) ;
        font.setAttribute(
            "embed-url",
            fontDescriptor.getFontFile().toURI().toURL().toExternalForm()
        ) ;
        final MutableConfiguration fontTriplet = new DefaultConfiguration( "font-triplet" ) ;
        final FontDescriptor.Format fontFormat = fontDescriptor.getFontFormat() ;
        fontTriplet.setAttribute( "name", fontDescriptor.getFontName() ) ;
        fontTriplet.setAttribute( "style", italicOrNormal( fontFormat ) ) ;
        fontTriplet.setAttribute( "weight", boldOrNormal( fontFormat ) ) ;
        font.addChild( fontTriplet ) ;
        fonts.addChild( font ) ;
      } catch( MalformedURLException e ) {
        LOGGER.error( "Problem with font metrics", e ) ;
      }
    }

    renderer.addChild( fonts ) ;
    renderers.addChild( renderer ) ;
    fop.addChild( renderers ) ;

    try {
      final StringWriter stringWriter = new StringWriter() ;
      final OutputFormat format = OutputFormat.createPrettyPrint() ;
      final XMLWriter xmlWriter = new XMLWriter( stringWriter, format ) ;
      new DefaultConfigurationSerializer().serialize( xmlWriter, fop ) ;
      xmlWriter.close() ;

      LOGGER.debug( "Created configuration: \n" + stringWriter.toString() ) ;
    } catch( Exception e ) {
      LOGGER.error( "Could not serialize configuration to string", e ) ;
    }
    return fop ;
    
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
