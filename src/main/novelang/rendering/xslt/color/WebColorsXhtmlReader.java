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
package novelang.rendering.xslt.color;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import novelang.system.Log;
import novelang.system.LogFactory;

import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.BODY;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.DL;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.DOCUMENT;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.DONE;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.DT;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.EM;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.HTML;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.NONE;
import static novelang.rendering.xslt.color.WebColorsXhtmlReader.State.STRONG;

/**
 * @author Laurent Caillette
 */
public class WebColorsXhtmlReader {

  private static final Log LOG = LogFactory.getLog( WebColorsXhtmlReader.class );
  private static final Charset CHARSET = Charset.forName( "UTF-8" ) ;

  private final List< ColorPair > colorPairs ;

  public WebColorsXhtmlReader( final URL resourceUrl ) {
    colorPairs = readColorPairs( resourceUrl ) ;
  }

  public WebColorsXhtmlReader( final String xml ) throws XMLStreamException, IOException {
    colorPairs = readColorPairs( new ByteArrayInputStream( xml.getBytes( CHARSET ) ) ) ;
  }

  private List< ColorPair > readColorPairs( final URL resourceUrl ) {
    try {
      if( resourceUrl == null ) {
        LOG.error( "Color cycle disabled: could not read from " + resourceUrl ) ;
      } else {
        final InputStream inputStream = resourceUrl.openStream() ;
        try {
          return readColorPairs( inputStream ) ;
        } finally {
          if( inputStream != null ) {
            inputStream.close() ;
          }
        }
      }
    } catch( Exception e ) {
      LOG.error( "Color cycle disabled: could not read from " + resourceUrl.toExternalForm(), e ) ;
    }
    return ImmutableList.of() ;
  }

  /*package*/ List< ColorPair > readColorPairs( final InputStream inputStream )
      throws IOException, XMLStreamException
  {
    final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance() ;

    String backgroundColorName = null ;
    String foregroundColorName = null ;
    final ImmutableList.Builder< ColorPair > colorPairsBuilder =
        new ImmutableList.Builder< ColorPair >() ;

    final XMLStreamReader parser = xmlInputFactory.createXMLStreamReader( inputStream ) ;
    for (int event = parser.next() ;
        event != XMLStreamConstants.END_DOCUMENT ;
        event = parser.next()
    ) {
        final QName elementQualifiedName ;

        switch( event ) {

          case XMLStreamConstants.START_ELEMENT :
            elementQualifiedName = parser.getName() ;
            switch( state ) {
              case NONE :
                changeState( DOCUMENT, NONE ) ;
                break ;
              case DOCUMENT :
                if( changeIfNameMatches( elementQualifiedName, HTML ) ) break ;
              case HTML :
                if( changeIfNameMatches( elementQualifiedName, BODY ) ) break ;
              case BODY:
                if( changeIfNameMatches( elementQualifiedName, DL ) ) break ;
              case DL:
                if( changeIfNameMatches( elementQualifiedName, DT ) ) break ;
              case DT:
                if( changeIfNameMatches( elementQualifiedName, STRONG ) ) break ;
                if( changeIfNameMatches( elementQualifiedName, EM ) ) break ;
//              case STRONG:
//                throw new IllegalStateException( "State: " + state ) ;
//              case EM:
//                throw new IllegalStateException( "State: " + state ) ;
//              case DONE :
//                break ;
              default : break ;
            }
            break ;

          case XMLStreamConstants.END_ELEMENT :
            elementQualifiedName = parser.getName() ;
            switch( state ) {
//              case NONE :
//                throw new IllegalStateException( "State: ") ;
//              case DOCUMENT :
//                throw new IllegalStateException( "State: " + state ) ;
              case HTML :
                if( changeIfNameMatches( elementQualifiedName, DOCUMENT ) ) break ;
              case BODY :
                if( changeIfNameMatches( elementQualifiedName, HTML ) ) break ;
              case DL :
                if( changeIfNameMatches( elementQualifiedName, BODY ) ) break ;
              case DT :
                if( changeIfNameMatches( elementQualifiedName, DL ) ) {
                  colorPairsBuilder.add(
                      new ColorPair( backgroundColorName, foregroundColorName ) ) ;
                  backgroundColorName = null ;
                  foregroundColorName = null ;
                  break ;
                }
              case STRONG:
                if( changeIfNameMatches( elementQualifiedName, STRONG, DT ) ) break ;
              case EM :
                if( changeIfNameMatches( elementQualifiedName, EM, DT ) ) break ;
              case DONE :
                break ;
              default : break ;
            }
            break ;


          case XMLStreamConstants.CHARACTERS :
            switch( state ) {
              case STRONG:
                backgroundColorName = parser.getText() ;
                break ;
              case EM :
                foregroundColorName = parser.getText() ;
                break ;
            }
            break ;


          case XMLStreamConstants.END_DOCUMENT :
            changeState( DONE, DOCUMENT ) ;
            break ;

          default : break ;
        }
    }
    return colorPairsBuilder.build() ;
  }

  /**
   * Returns an {@code Iterable} returning {@code Iterator}s that cycle forever.
   * @return a non-null object returning a non-null {@code Iterator}.
   */
  public Iterable< ColorPair > getColorCycler() {
    return new Iterable< ColorPair >() {
      public Iterator< ColorPair > iterator() {
        return Iterators.cycle( colorPairs ) ;
      }
    } ;
  }

  public List< ColorPair > getColorPairs() {
    return colorPairs ;
  }

  /*package visibility for static import*/ enum State {
    NONE( null ),
    DOCUMENT( null ), 
    HTML( new QName( "html" ) ),
    BODY( new QName( "body" ) ),
    DL( new QName( "dl" ) ),
    DT( new QName( "dt" ) ),
    STRONG( new QName( "strong" ) ),
    EM( new QName( "em" ) ),
    DONE( null );

    private final QName elementName ;

    State( final QName elementName ) {
      this.elementName = elementName ;
    }

    public QName getElementName() {
      return elementName ;
    }
  }
  
  private State state = NONE ;

  private boolean changeIfNameMatches( final QName elementName, final State newStateIfMatch ) {
    return changeIfNameMatches( elementName, newStateIfMatch, newStateIfMatch ) ;
  }

  private boolean changeIfNameMatches(
      final QName elementName,
      final State stateToMatch,
      final State newStateIfMatch
  ) {
    if( stateToMatch.getElementName().equals( elementName ) ) {
      state = newStateIfMatch ;
      return true ;
    }
    return false ;
  }

  private void changeState( final State newState, final State expectedCurrentState ) {
    if( state != expectedCurrentState ) {
      throw new IllegalStateException( 
          "From state " + state + " tried to change to " + newState + 
          " while in " + expectedCurrentState + "." 
      ) ;
    }
    state = newState ;
  }

}
