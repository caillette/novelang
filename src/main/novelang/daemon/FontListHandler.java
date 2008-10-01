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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mortbay.jetty.Request;
import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontTriplet;
import org.apache.fop.apps.FOPException;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.FopTools;
import novelang.configuration.FopFontStatus;
import novelang.configuration.RenderingConfiguration;
import novelang.common.Renderable;
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
import com.google.common.collect.Ordering;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.base.Predicate;
import com.google.common.base.Nullable;

/**
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
      final String nonWordCharacters = createNonWordCharactersString() ;
      final FopFontStatus fontsStatus;
      fontsStatus = renderingConfiguration.getCurrentFopFontStatus() ;
      generateSourceDocument( textBuffer, nonWordCharacters, fontsStatus );

      LOGGER.debug( "Rendering: \n{}", textBuffer.toString() ) ;
      final Renderable rendered = new Part( textBuffer.toString() ) ;


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

//  private void generateSourceDocument_tooMuchCrapAndIncorrectStylesApplied(
  private void generateSourceDocument(
      StringBuffer textBuffer,
      String nonWordCharacters,
      FopFontStatus fontsStatus
  ) {
    for( EmbedFontInfo fontInfo : fontsStatus.getFontInfos() ) {
      for( Object fontTripletAsObject : fontInfo.getFontTriplets() ) {
        final FontTriplet fontTriplet = ( FontTriplet ) fontTripletAsObject;
        textBuffer
            .append( "=== \"" ).append( fontTriplet.getName() ).append( "\"")
            .append( "[style:" ).append( fontTriplet.getStyle() ).append( "]" )
            .append( "[weight:" ).append( fontTriplet.getWeight() ).append( "]" )
            .append( "[priority:" ).append( fontTriplet.getPriority() ).append( "]" )
            .append( "``" ).append( fontInfo.getEmbedFile() ).append( "``" )
            .append( "\n\n" )
//            .append( "ABCDEFGHIJKLMNOPQRSTUVWXYZ" ).append( "\n\n" )
//            .append( "abcdefghijklmnopqrstuvwxyz" ).append( "\n\n" )
//            .append( "0123456789" ).append( "\n\n" )
//            .append( "`").append( nonWordCharacters ).append( "`").append( "\n\n" )
            ;
      }
    }
  }

  private static final Set< Character > ESCAPERS = Sets.newHashSet(
      '`',
      '\\',
      '\u0152',
      '\u0153',
      '\u2039',
      '\u203a',
      Escape.ESCAPE_START,
      Escape.ESCAPE_END
  ) ;

  private String createNonWordCharactersString() {
    final Set< Character > nonWordCharacterSet = SupportedCharacters.getNonWordCharacters() ;
    final StringBuffer buffer = new StringBuffer() ;
    for( Character character : nonWordCharacterSet ) {
      if( ! ESCAPERS.contains( character ) ) {
        buffer.append( character ) ;
      }
    }   
    return buffer.toString() ;    
  }



  private void generateSourceDocument_(
      StringBuffer textBuffer,
      String nonWordCharacters,
      FopFontStatus fontsStatus
  ) {
    final Multimap< String, FontTriplet > tripletsByEmbedFileName = Multimaps.newHashMultimap() ;

  }


  private static final Ordering< FontTriplet > PRIORITY_ORDERING = new Ordering<FontTriplet>() {
    public int compare( FontTriplet triplet1, FontTriplet triplet2 ) {
      return triplet2.getPriority() - triplet1.getPriority() ;
    }
  } ;

  private static Iterable< FontTriplet > sortByPriorityDescending(
      Iterable< FontTriplet > fontTriplets
  ) {
    return PRIORITY_ORDERING.sortedCopy( fontTriplets ) ;
  }

  private static final Predicate< FontTriplet > PRIORITY_ABOVE_ZERO = new Predicate<FontTriplet>() {
    public boolean apply( FontTriplet fontTriplet ) {
      return fontTriplet.getPriority() > 0 ;
    }
  } ;

  private static Iterable< FontTriplet > retainPriorityAboveZero(
      Iterable< FontTriplet > fontTriplets
  ) {
    return Iterables.filter( fontTriplets, PRIORITY_ABOVE_ZERO ) ;
  }

  private static Multimap< String, FontTriplet > mapTripletsByCleanNames(
      Iterable< FontTriplet > fontTriplets
  ) {
    final Multimap< String, FontTriplet > map = Multimaps.newHashMultimap() ;
    for( FontTriplet fontTriplet : fontTriplets ) {
      map.put( fontTriplet.getName(), fontTriplet ) ;
    }
    return ImmutableMultimap.copyOf( map ) ;
  }

}
