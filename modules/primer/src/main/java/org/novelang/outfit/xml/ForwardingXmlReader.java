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
package org.novelang.outfit.xml;

import java.io.IOException;

import com.google.common.base.Preconditions;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Forwards every method call to another {@link org.xml.sax.XMLReader},
 * implementing the Adapter pattern.
 *
 * @author Laurent Caillette
 */
public class ForwardingXmlReader implements XMLReader {

  private final XMLReader delegate ;

  protected ForwardingXmlReader( final XMLReader delegate ) {
    this.delegate = Preconditions.checkNotNull( delegate ) ;
  }

  @Override
  public boolean getFeature( final String name )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return delegate.getFeature( name );
  }

  @Override
  public void setFeature( final String name, final boolean value )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    delegate.setFeature( name, value );
  }

  @Override
  public Object getProperty( final String name )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return delegate.getProperty( name );
  }

  @Override
  public void setProperty( final String name, final Object value )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    delegate.setProperty( name, value );
  }

  @Override
  public void setEntityResolver( final EntityResolver resolver ) {
    delegate.setEntityResolver( resolver );
  }

  @Override
  public EntityResolver getEntityResolver() {
    return delegate.getEntityResolver();
  }

  @Override
  public void setDTDHandler( final DTDHandler handler ) {
    delegate.setDTDHandler( handler );
  }

  @Override
  public DTDHandler getDTDHandler() {
    return delegate.getDTDHandler();
  }

  @Override
  public void setContentHandler( final ContentHandler handler ) {
    delegate.setContentHandler( handler );
  }

  @Override
  public ContentHandler getContentHandler() {
    return delegate.getContentHandler();
  }

  @Override
  public void setErrorHandler( final ErrorHandler handler ) {
    delegate.setErrorHandler( handler );
  }

  @Override
  public ErrorHandler getErrorHandler() {
    return delegate.getErrorHandler() ;
  }

  @Override
  public void parse( final InputSource input ) throws IOException, SAXException {
    delegate.parse( input );
  }

  @Override
  public void parse( final String systemId ) throws IOException, SAXException {
    delegate.parse( systemId );
  }
}
