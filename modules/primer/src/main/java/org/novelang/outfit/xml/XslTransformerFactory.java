/*
 * Copyright (C) 2010 Laurent Caillette
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

import java.io.IOException;

import com.google.common.collect.ImmutableList;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import org.dom4j.Document;
import org.dom4j.io.SAXWriter;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Applies an XSL stylesheet.
 *
 * @author Laurent Caillette
 */
public abstract class XslTransformerFactory {

  private final EntityResolver entityResolver ;
  private final URIResolver uriResolver ;
  private final ImmutableList< ContentHandler > additionalContentHandlers ;


  public XslTransformerFactory(
      final EntityResolver entityResolver,
      final URIResolver uriResolver,
      final ImmutableList< ContentHandler > additionalContentHandlers
  ) {
    this.entityResolver = checkNotNull( entityResolver ) ;
    this.uriResolver = checkNotNull( uriResolver ) ;
    this.additionalContentHandlers = checkNotNull( additionalContentHandlers ) ;
  }


  
  public final TransformerHandler newTransformerHandler()
      throws TransformerConfigurationException, SAXException, IOException
  {
    final SAXTransformerFactory saxTransformerFactory =
        ( SAXTransformerFactory ) TransformerFactory.newInstance() ;

    saxTransformerFactory.setURIResolver( uriResolver ) ;

    final TemplatesHandler templatesHandler = saxTransformerFactory.newTemplatesHandler() ;

    final XMLReader reader = XMLReaderFactory.createXMLReader() ;

    reader.setContentHandler( new SaxMulticaster( templatesHandler, additionalContentHandlers ) ) ;

    reader.setEntityResolver( entityResolver ) ;

    parse( reader ) ;

    final Templates templates = templatesHandler.getTemplates() ;
    return saxTransformerFactory.newTransformerHandler( templates ) ;

  }

  protected abstract void parse( final XMLReader reader ) throws IOException, SAXException ;


  public static class FromInputSource extends XslTransformerFactory {

    private final InputSource inputSource ;

    public FromInputSource(
        final InputSource inputSource,
        final EntityResolver entityResolver,
        final URIResolver uriResolver,
        final ImmutableList< ContentHandler > additionalContentHandlers
    ) {
      super( entityResolver, uriResolver, additionalContentHandlers ) ;
      this.inputSource = checkNotNull( inputSource ) ;
    }

    @Override
    protected void parse( final XMLReader reader ) throws IOException, SAXException {
      reader.parse( inputSource ) ;
    }
  }

  public static class FromResource extends FromInputSource {

    public FromResource(
        final ResourceLoader resourceLoader,
        final ResourceName xslFileName,
        final EntityResolver entityResolver,
        final URIResolver uriResolver,
        final ImmutableList< ContentHandler > additionalContentHandlers ) {
      super(
          new InputSource( resourceLoader.getInputStream( xslFileName ) ),
          entityResolver,
          uriResolver,
          additionalContentHandlers
      ) ;
    }
  }

  public static class FromDom4jDocument extends XslTransformerFactory {

    private final Document document ;

    public FromDom4jDocument(
        final Document dom4jDocument,
        final EntityResolver entityResolver,
        final URIResolver uriResolver,
        final ImmutableList< ContentHandler > additionalContentHandlers
    ) {
      super( entityResolver, uriResolver, additionalContentHandlers ) ;
      this.document = checkNotNull( dom4jDocument ) ;
    }

    @Override
    protected void parse( final XMLReader reader ) throws IOException, SAXException {
      final SAXWriter saxWriter = new SAXWriter( ) ;
      saxWriter.setContentHandler( reader.getContentHandler() ) ;
      saxWriter.write( document ) ; 
    }
  }

}