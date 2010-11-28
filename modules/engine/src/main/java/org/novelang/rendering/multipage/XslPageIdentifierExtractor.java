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
package org.novelang.rendering.multipage;

import java.io.IOException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.TransformerHandler;
import org.dom4j.Document;
import org.novelang.common.SyntacticTree;
import org.novelang.outfit.xml.XslTransformerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Computes the pages of a document tree, using
 * previously-{@link XslMultipageStylesheetCapture captured}
 * stylesheet.
 *
 * @author Laurent Caillette
 */
public class XslPageIdentifierExtractor implements PageIdentifierExtractor {

  private final Document stylesheetDocument ;
  private final EntityResolver entityResolver ;
  private final URIResolver uriResolver ;

  public XslPageIdentifierExtractor(
      final EntityResolver entityResolver,
      final URIResolver uriResolver,
      final XslMultipageStylesheetCapture multipageStylesheetCapture
  ) {
    this.entityResolver = checkNotNull( entityResolver ) ;
    this.uriResolver = checkNotNull( uriResolver ) ;
    this.stylesheetDocument = checkNotNull(
        multipageStylesheetCapture.getStylesheetDocument() ) ;
  }


  @Override
  public ImmutableMap< PageIdentifier, String > extractPageIdentifiers(
      final SyntacticTree documentTree
  )
      throws IOException, TransformerConfigurationException, SAXException
  {
    

    final XslTransformerFactory xslTransformerFactory = new XslTransformerFactory.FromDom4jDocument(
        stylesheetDocument,
        entityResolver,
        uriResolver,
        ImmutableList.< ContentHandler >of()
    ) ;
    final TransformerHandler transformerHandler =
        xslTransformerFactory.newTransformerHandler() ;


    throw new UnsupportedOperationException( "TODO" ) ;
  }
}
