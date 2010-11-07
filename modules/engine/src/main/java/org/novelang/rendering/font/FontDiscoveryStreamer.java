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
package org.novelang.rendering.font;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.fop.fonts.FontTriplet;
import org.dom4j.Namespace;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.novelang.common.metadata.DocumentMetadata;
import org.novelang.configuration.FontQuadruplet;
import org.novelang.configuration.FopFontStatus;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.loader.ResourceName;
import org.novelang.parser.GeneratedLexemes;
import org.novelang.rendering.PdfWriter;
import org.novelang.rendering.XslWriter;

/**
 * Generates a PDF in an {@code OutputStream}.
 * This class delegates to an {@link XslWriter}.
 * Showing all characters, including those not recognized in a source document, requires
 * direct XML communication with FOP. This is achieved through SAX events as the document
 * is not complex enough to require a new framework and dom4j fails to handle namespaces
 * correctly.
 *
 * @author Laurent Caillette
 */
public class FontDiscoveryStreamer {

  protected static final Namespace NAMESPACE = Namespace.get( 
      "nf",
      "http://novelang.org/font-list-xml/1.0"
  ) ;

  private static final String ELEMENT_ROOT = "fonts" ;
  private static final String ELEMENT_FAMILY = "family" ;
  private static final String ELEMENT_NAME = "name" ;
  private static final String ELEMENT_EMBEDFILE = "embed-file" ;
  private static final String ELEMENT_STYLE = "style" ;
  private static final String ELEMENT_BROKEN = "broken" ;
  private static final String ELEMENT_NOFONTFOUNT = "no-font-found" ;

  private static final String ELEMENT_WEIGHT = "weight" ;
  private static final String ELEMENT_CHARACTERS = "characters" ;
  private static final String ELEMENT_CHARACTER = "character" ;
  private static final String ELEMENT_SENTENCES = "sentences" ;

  private static final String ELEMENT_SENTENCE = "sentence" ;

  private final XslWriter xslWriter ;
  private final FopFontStatus fopFontStatus ;

  public FontDiscoveryStreamer(
      final RenderingConfiguration renderingConfiguration,
      final ResourceName resourceName
  ) {
    xslWriter = createXslWriter( renderingConfiguration, resourceName ) ;
    fopFontStatus = renderingConfiguration.getCurrentFopFontStatus() ;
  }



  public void generate( final OutputStream outputStream, final Charset charset ) throws Exception {

    final Iterable< Character > supportedCharacters ;
    {
      final Set< Character > characterSet = Sets.newHashSet( GeneratedLexemes.getCharacters() ) ;
      characterSet.addAll( CHARACTERS_SUPPLEMENT ) ;
      characterSet.removeAll( CHARACTERS_TO_REMOVE ) ;
      supportedCharacters = CHARACTER_ORDERING.sortedCopy( characterSet ) ;
    }


    final Multimap< String, FontQuadruplet > quadruplets =
        SyntheticFontMap.createSyntheticFontMap( fopFontStatus ) ;

    final DocumentMetadata documentMetadata = new DocumentMetadata() {
      public ReadableDateTime getCreationTimestamp() {
        return new DateTime() ; // No null allowed!
      }
      public Charset getCharset() {
        return charset ;
      }
    } ;

    xslWriter.startWriting( outputStream, documentMetadata ) ;

    xslWriter.start( ELEMENT_ROOT, true ) ;

    if( fopFontStatus.getFailedFontFileNames().isEmpty() && quadruplets.keySet().isEmpty() ) {
      xslWriter.start( ELEMENT_NOFONTFOUNT ) ;
      xslWriter.end( ELEMENT_NOFONTFOUNT ) ; 
    }

    if( ! fopFontStatus.getFailedFontFileNames().isEmpty() ) {
      xslWriter.start( ELEMENT_BROKEN ) ;
      for( final String embedFileName : fopFontStatus.getFailedFontFileNames() ) {
        xslWriter.start( ELEMENT_EMBEDFILE ) ;
        xslWriter.write( embedFileName ) ;
        xslWriter.end( ELEMENT_EMBEDFILE ) ;
      }
      xslWriter.end( ELEMENT_BROKEN ) ;
    }

    for( final String fontName : quadruplets.keySet() ) {
      for( final FontQuadruplet quadruplet : quadruplets.get( fontName ) ) {
        final FontTriplet fontTriplet = quadruplet.getFontTriplet() ;
        xslWriter.start( ELEMENT_FAMILY ) ; {

          xslWriter.start( ELEMENT_NAME ) ;
          xslWriter.write( fontTriplet.getName() ) ;
          xslWriter.end( ELEMENT_NAME ) ;

          xslWriter.start( ELEMENT_STYLE ) ;
          xslWriter.write( fontTriplet.getStyle() ) ;
          xslWriter.end( ELEMENT_STYLE ) ;

          xslWriter.start( ELEMENT_WEIGHT ) ;
          xslWriter.write( "" + fontTriplet.getWeight() ) ;
          xslWriter.end( ELEMENT_WEIGHT ) ;

          xslWriter.start( ELEMENT_EMBEDFILE ) ;
          xslWriter.write( quadruplet.getEmbedFileName() ) ;
          xslWriter.end( ELEMENT_EMBEDFILE ) ;

        } xslWriter.end( ELEMENT_FAMILY ) ;
      }
    }

    xslWriter.start( ELEMENT_CHARACTERS ) ;
    for( final Character character : supportedCharacters ) {
      xslWriter.start( ELEMENT_CHARACTER ) ;
      xslWriter.write( "" + character ) ;
      xslWriter.end( ELEMENT_CHARACTER ) ;
    }
    xslWriter.end( ELEMENT_CHARACTERS ) ;

    xslWriter.start( ELEMENT_SENTENCES ) ; {

      xslWriter.start( ELEMENT_SENTENCE ) ;
      xslWriter.write( "AaBbCcDdEeFf GgHhIiJjKkLl MmNnOoPpQqRr SsTtUuVvWwXx YyZz" ) ;
      xslWriter.end( ELEMENT_SENTENCE ) ;

      xslWriter.start( ELEMENT_SENTENCE ) ;
      xslWriter.write( "The quick brown fox jumps over the lazy dog." ) ;
      xslWriter.end( ELEMENT_SENTENCE ) ;

      xslWriter.start( ELEMENT_SENTENCE ) ;
      xslWriter.write( "Voix ambig\u00fce d'un c\u0153ur qui au z\u00e9phyr " +
          "pr\u00e9f\u00e8re les jattes de kiwis." ) ;
      xslWriter.end( ELEMENT_SENTENCE ) ;

  } xslWriter.end( ELEMENT_SENTENCES ) ;


    xslWriter.end( ELEMENT_ROOT ) ;

    xslWriter.finishWriting() ;


  }

  protected XslWriter createXslWriter(
      final RenderingConfiguration renderingConfiguration,
      final ResourceName resourceName
  ) {
    return new PdfWriter(
        renderingConfiguration,
        resourceName,
        FontDiscoveryStreamer.NAMESPACE.getURI(),
        FontDiscoveryStreamer.NAMESPACE.getPrefix()        
    ) ;
//      return new XslWriter(
//          FontDiscoveryStreamer.NAMESPACE.getURI(),
//          FontDiscoveryStreamer.NAMESPACE.getPrefix(),
//          renderingConfiguration,
//          new ResourceName( "identity.xsl" )
//      )  ;
  }

  private static final Ordering< Character > CHARACTER_ORDERING = new Ordering< Character >() {
    public int compare( final Character character0, final Character character1 ) {
      return character0.compareTo( character1 ) ;
    }
  } ;

  /**
   * There are some characters to not include in generated source document as they would
   * mess escaping or whatever.
   */
  private static final Set< Character > CHARACTERS_TO_REMOVE = Sets.newHashSet(
      '\n',
      '\r',
      ' '
  ) ;

  public static final Set< Character > CHARACTERS_SUPPLEMENT = Sets.newHashSet(
      '\u2014',
      '\u2013',
      '\u2026'
  ) ;

}
