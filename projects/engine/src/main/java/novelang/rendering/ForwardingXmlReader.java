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
package novelang.rendering;

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
* @author Laurent Caillette
*/
class ForwardingXmlReader implements XMLReader {

  private final XMLReader delegate ;

  ForwardingXmlReader( final XMLReader delegate ) {
    this.delegate = Preconditions.checkNotNull( delegate ) ;
  }

  public boolean getFeature( final String name )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return delegate.getFeature( name );
  }

  public void setFeature( final String name, final boolean value )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    delegate.setFeature( name, value );
  }

  public Object getProperty( final String name )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    return delegate.getProperty( name );
  }

  public void setProperty( final String name, final Object value )
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    delegate.setProperty( name, value );
  }

  public void setEntityResolver( final EntityResolver resolver ) {
    delegate.setEntityResolver( resolver );
  }

  public EntityResolver getEntityResolver() {
    return delegate.getEntityResolver();
  }

  public void setDTDHandler( final DTDHandler handler ) {
    delegate.setDTDHandler( handler );
  }

  public DTDHandler getDTDHandler() {
    return delegate.getDTDHandler();
  }

  public void setContentHandler( final ContentHandler handler ) {
    delegate.setContentHandler( handler );
  }

  public ContentHandler getContentHandler() {
    return delegate.getContentHandler();
  }

  public void setErrorHandler( final ErrorHandler handler ) {
    delegate.setErrorHandler( handler );
  }

  public ErrorHandler getErrorHandler() {
    return delegate.getErrorHandler() ;
  }

  public void parse( final InputSource input ) throws IOException, SAXException {
    delegate.parse( input );
  }

  public void parse( final String systemId ) throws IOException, SAXException {
    delegate.parse( systemId );
  }
}
