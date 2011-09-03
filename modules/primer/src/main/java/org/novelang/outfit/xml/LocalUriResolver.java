/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.outfit.xml;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import static com.google.common.base.Preconditions.checkNotNull;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;

/**
 * Resolves an URI into an XML {@link Source}, basing on a
 * {@link org.novelang.outfit.loader.ResourceLoader}.
 * This class offers a {@link #decorate(org.xml.sax.ContentHandler)} hook} to plug multiple content
 * handlers, like for checking if XML element names for Novelang document tree are valid ones.
 *
 * @author Laurent Caillette
 */
public class LocalUriResolver implements URIResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger( LocalUriResolver.class ) ;

  private final ResourceLoader resourceLoader ;
  private final EntityResolver entityResolver ;

  public LocalUriResolver(
      final ResourceLoader resourceLoader,
      final EntityResolver entityResolver
  ) {
    this.resourceLoader = checkNotNull( resourceLoader ) ;
    this.entityResolver = checkNotNull( entityResolver ) ;
  }

  @Override
  public Source resolve( final String href, final String base ) throws TransformerException {
    LOGGER.debug( "Resolving URI href='", href, "' base='", base, "'" ) ;

    final SAXTransformerFactory saxTransformerFactory =
        ( SAXTransformerFactory ) TransformerFactory.newInstance() ;
    saxTransformerFactory.setURIResolver( this ) ;

    final XMLReader reader ;


    try {
      reader = new ForwardingXmlReader( XMLReaderFactory.createXMLReader() ) {
         @Override
         public void setContentHandler( final ContentHandler defaultContentHandler ) {
           super.setContentHandler( decorate( defaultContentHandler ) ) ;
         }
      } ;
    } catch( SAXException e ) {
      throw new RuntimeException( e ) ;
    }

    reader.setEntityResolver( entityResolver ) ;

    return new SAXSource(
        reader,
        new InputSource( resourceLoader.getInputStream( new ResourceName( href ) ) )
    ) ;

  }

  /**
   * Hook for decorating original {@code ContentHandler} to tweak or listen to SAX events.
   * No way to do that in the constructor, because each included document should have
   * its own fresh {@code ContentHandler}s.
   *
   * @return a possibly null object.
   */
  protected ContentHandler decorate( final ContentHandler original ) {
    return original ;
  }
}
