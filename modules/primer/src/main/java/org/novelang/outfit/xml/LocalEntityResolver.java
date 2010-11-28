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
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches local files in the same directory as the stylesheet.
 * This is because the {@code systemId} as read by the stylesheet loader is prefixed
 * with current directory (bug?).
 */
public class LocalEntityResolver implements EntityResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger( LocalEntityResolver.class );

  private final ResourceLoader resourceLoader ;
  private final EntityEscapeSelector entityEscapeSelector ;

  public LocalEntityResolver(
      final ResourceLoader resourceLoader,
      final EntityEscapeSelector entityEscapeSelector
  ) {
    this.resourceLoader = checkNotNull( resourceLoader ) ;
    this.entityEscapeSelector = Preconditions.checkNotNull( entityEscapeSelector ) ;
  }

  @Override
  public InputSource resolveEntity(
      final String publicId,
      final String systemId
  ) throws SAXException, IOException {
    final String cleanSystemId = systemId.substring( systemId.lastIndexOf( '/' ) + 1 ) ;
    final boolean shouldEscapeEntity =
        entityEscapeSelector.shouldEscape( publicId, cleanSystemId ) ;
    LOGGER.debug( "Resolving entity publicId='", publicId, "' systemId='", cleanSystemId, "' " +
        "escape=", shouldEscapeEntity
    ) ;
    final InputSource dtdSource = new InputSource(
        resourceLoader.getInputStream( new ResourceName( cleanSystemId ) ) );
    if( shouldEscapeEntity ) {
      return DtdTools.escapeEntities( dtdSource ) ;
    } else {
      return dtdSource;
    }
  }
}
