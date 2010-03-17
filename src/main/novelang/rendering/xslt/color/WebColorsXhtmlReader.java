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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import novelang.system.Log;
import novelang.system.LogFactory;

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

  private static List< ColorPair > readColorPairs( final URL resourceUrl ) {
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

  /*package*/ static List< ColorPair > readColorPairs( final InputStream inputStream )
      throws IOException, XMLStreamException {
    final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance() ;
    final XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader( inputStream ) ;
    final XMLStreamReader filteredReader = xmlInputFactory.createFilteredReader(
        streamReader,
        new StreamFilter() {
          public boolean accept( final XMLStreamReader xmlStreamReader ) {
            return false ;
          }
        }
    ) ;
    while( filteredReader.hasNext() ) {
      filteredReader.next() ;
    }
    
    throw new UnsupportedOperationException( "readColorPairs" ) ;
  }


  public Iterable< ColorPair > getColorCycler() {
    return new Iterable< ColorPair >() {
      public Iterator< ColorPair > iterator() {
        return Iterators.cycle( colorPairs ) ;
      }
    } ;
  }

}
