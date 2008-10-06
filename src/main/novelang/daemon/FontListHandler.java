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
package novelang.daemon;

import java.io.IOException;
import java.util.Set;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mortbay.jetty.Request;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontTriplet;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.FopFontStatus;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.FontQuadruplet;
import novelang.common.Renderable;
import novelang.common.Problem;
import novelang.part.Part;
import novelang.produce.DocumentProducer;
import novelang.produce.RequestTools;
import novelang.produce.DocumentRequest;
import novelang.rendering.RenditionMimeType;
import novelang.loader.ResourceName;
import novelang.parser.SupportedCharacters;
import novelang.parser.Escape;
import com.google.common.collect.Sets;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.base.Predicate;

/**
 * Generates a PDF document showing available fonts.
 *
 * @author Laurent Caillette
 */
public class FontListHandler extends GenericHandler{

  private static final Logger LOGGER = LoggerFactory.getLogger( FontListHandler.class ) ;

  private final DocumentProducer documentProducer ;
  private final RenderingConfiguration renderingConfiguration ;
  private static final String DOCUMENT_NAME = "/~fonts.pdf" ;
  private static final ResourceName STYLESHEET = new ResourceName( "font-list.xsl" ) ;

  public FontListHandler( ProducerConfiguration producerConfiguration ) {
    documentProducer = new DocumentProducer( producerConfiguration ) ;
    renderingConfiguration = producerConfiguration.getRenderingConfiguration() ;
  }

  protected void doHandle( 
      String target,
      HttpServletRequest request,
      HttpServletResponse response,
      int dispatch
  ) throws IOException, ServletException {
    if( DOCUMENT_NAME.equals( target ) ) {
      LOGGER.info( "Font listing requested" ) ;

      final StringBuffer textBuffer = new StringBuffer() ;
      final String charactersString = createSupportedCharactersString() ;
      LOGGER.debug( "Got those characters: " + charactersString ) ;
      final FopFontStatus fontsStatus;
      fontsStatus = renderingConfiguration.getCurrentFopFontStatus() ;
      generateSourceDocument( textBuffer, charactersString, fontsStatus );

//      LOGGER.debug( "Rendering: \n{}", textBuffer.toString() ) ;
      final Renderable rendered = new Part( textBuffer.toString() ) ;

      if( rendered.hasProblem() ) {
        response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ) ;
        for( final Problem problem : rendered.getProblems() ) {
          LOGGER.error( problem.getMessage() );
        }
        
      } else {


        final DocumentRequest documentRequest = RequestTools.forgeDocumentRequest(
            DOCUMENT_NAME,
            RenditionMimeType.PDF,
            STYLESHEET
        ) ;

        response.setStatus( HttpServletResponse.SC_OK ) ;
        documentProducer.produce( documentRequest, rendered, response.getOutputStream() ) ;
        response.setContentType( RenditionMimeType.PDF.getMimeName() ) ;

        ( ( Request ) request ).setHandled( true ) ;
        LOGGER.debug( "Handled request {}", request.getRequestURI() ) ;
      }

    }
  }


  /**
   * There are some characters to not include in generated source document as they would
   * mess escaping or whatever.
   */
  private static final Set< Character > CHARACTERS_TO_HIDE = Sets.newHashSet(
      '`',
      '\\',
      '\n',
      '\r',
      '\u2039', // SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK
      '\u203a', // SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK
      ' '
      ) ;

  private String createSupportedCharactersString() {
    final Iterable< Character > supportedCharacters = SupportedCharacters.getSupportedCharacters() ;
    final StringBuffer buffer = new StringBuffer() ;
    for( Character character : supportedCharacters ) {
      if( ! CHARACTERS_TO_HIDE.contains( character ) ) {
        buffer.append( character ) ;
      }
    }   
    return buffer.toString() ;    
  }



  private void generateSourceDocument(
      StringBuffer textBuffer,
      String supportedCharacters,
      FopFontStatus fontsStatus
  ) {
    final Multimap< String, FontQuadruplet > quadruplets = createSyntheticFontMap( fontsStatus ) ;

    textBuffer.append( "*** Fonts" ).append(  "\n\n" ) ;

    for( String fontName : quadruplets.keySet() ) {
      for( FontQuadruplet quadruplet : quadruplets.get( fontName ) ) {
        final FontTriplet fontTriplet = quadruplet.getFontTriplet() ;
        textBuffer
            .append( "=== \"" ).append( fontTriplet.getName() ).append( "\"")
            .append( "[" ).append( fontTriplet.getStyle() ).append( "]" )
            .append( "[" ).append( fontTriplet.getWeight() ).append( "]" )
            .append( "[" ).append( fontTriplet.getPriority() ).append( "]" )
            .append( "``" ).append( quadruplet.getEmbedFileName() ).append( "``" )
            .append( "\n\n" )
        ;
      }
    }
    textBuffer.append( "*** Characters" ).append(  "\n\n" ) ;
    writeCharacters( textBuffer, supportedCharacters.toCharArray() ) ;
    textBuffer.append( "\n\n" ) ;
    textBuffer.append( "*** Sentences" ).append(  "\n\n" ) ;
    textBuffer.append( "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz" ).append(  "\n\n" ) ;
    textBuffer.append( "--- The quick brown fox jumps over the lazy dog." ).append(  "\n\n" ) ;
    textBuffer.append( "--- Voix ambig\u00fce d'un c\u0153ur qui au z\u00e9phyr " ) ;
    textBuffer.append( "pr\u00e9f\u00e8re les jattes de kiwis." ).append( "\n\n" ) ;
  }

  private void writeCharacters( StringBuffer textBuffer, char[] characters ) {
    for( char character : characters ) {
      final String characterAsString ;
      final String escapedCharacter = Escape.escapeText( "" + character ) ;
      if( null == escapedCharacter ) {
        characterAsString = "" + character ;
      } else {
        characterAsString = escapedCharacter ;
      }
      textBuffer
          .append( '`' ).append( characterAsString ).append( '`' ).append( ' ' )
          .append( "\n\n" )
          ;
    }
  }


  private static Iterable< FontQuadruplet > retainPriorityAboveZero(
      Iterable< FontQuadruplet > quadruplets
  ) {
    return Iterables.filter( quadruplets, new Predicate< FontQuadruplet >() {
      public boolean apply( FontQuadruplet quadruplet ) {
        return quadruplet.getFontTriplet().getPriority() > 0 ;
      }
    } ) ;
  }

  private static Iterable< FontQuadruplet > retainPriorityZero(
      Iterable< FontQuadruplet > quadruplets
  ) {
    return Iterables.filter( quadruplets, new Predicate< FontQuadruplet >() {
      public boolean apply( FontQuadruplet quadruplet ) {
        return quadruplet.getFontTriplet().getPriority() == 0 ;
      }
    } ) ;
  }

  private static Multimap< String, FontQuadruplet > mapQuadrupletsByCleanNames(
      Iterable< FontQuadruplet > quadruplets
  ) {
    final Multimap< String, FontQuadruplet > map = Multimaps.newHashMultimap() ;
    for( FontQuadruplet quadruplet : quadruplets ) {
      map.put( quadruplet.getFontTriplet().getName(), quadruplet ) ;
    }
    return ImmutableMultimap.copyOf( map ) ;
  }

  private static Multimap< String, FontQuadruplet > extractTripletsWithKnownName(
      Set< String > names,
      Iterable< FontQuadruplet > quadruplets
  ) {
    final Multimap< String, FontQuadruplet > extracted = Multimaps.newHashMultimap() ;
    for( FontQuadruplet quadruplet : quadruplets ) {
      final String name = quadruplet.getFontTriplet().getName() ;
      if( names.contains( name ) ) {
        extracted.put( name, quadruplet ) ;
      }
    }
    return extracted ;
  }

  public static Multimap< String, FontQuadruplet > createSyntheticFontMap(
      FopFontStatus fontStatus
  ) {
    final List< FontQuadruplet > quadruplets = Lists.newArrayList() ;

    for( EmbedFontInfo fontInfo : fontStatus.getFontInfos() ) {
      for( Object fontTripletAsObject : fontInfo.getFontTriplets() ) {
        final FontTriplet fontTriplet = ( FontTriplet ) fontTripletAsObject ;
        final FontQuadruplet fontQuadruplet =
            new FontQuadruplet( fontInfo.getEmbedFile(), fontTriplet ) ;
        quadruplets.add( fontQuadruplet ) ;
      }
    }
    final Iterable< FontQuadruplet > quadrupletsPriorityAboveZero =
        retainPriorityAboveZero( quadruplets ) ;
    final Iterable< FontQuadruplet > quadrupletsPriorityZero =
        retainPriorityZero( quadruplets ) ;
    final Multimap< String, FontQuadruplet > quadrupletsByCleanNames =
        Multimaps.newArrayListMultimap(
            mapQuadrupletsByCleanNames( quadrupletsPriorityAboveZero ) ) ;
    quadrupletsByCleanNames.putAll(
        extractTripletsWithKnownName( quadrupletsByCleanNames.keySet(), quadrupletsPriorityZero )
    ) ;

    return quadrupletsByCleanNames ;
  }

}
