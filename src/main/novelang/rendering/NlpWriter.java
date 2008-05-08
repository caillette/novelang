/*
 * Copyright (C) 2006 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.rendering;

import java.io.OutputStream;
import java.nio.charset.Charset;

import org.xml.sax.ContentHandler;
import novelang.model.common.TreeMetadata;
import novelang.configuration.RenderingConfiguration;

/**
 * @author Laurent Caillette
 */
public class NlpWriter extends EscapingWriter {

  protected static final String DEFAULT_NLP_STYLESHEET =  "nlp.xsl" ;

  public NlpWriter( RenderingConfiguration configuration ) {
    super( configuration, DEFAULT_NLP_STYLESHEET, RenditionMimeType.NLP ) ;
  }

  protected final ContentHandler createSinkContentHandler(
      final OutputStream outputStream,
      TreeMetadata treeMetadata,
      final Charset encoding
  )
      throws Exception
  {

    // dom4j's XML writer does some clever entity-escaping not good for us.
    final ContentHandler sink = new TextSink( outputStream );

    return sink ;
  }


}