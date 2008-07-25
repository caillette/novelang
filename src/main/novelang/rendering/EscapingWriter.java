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

import novelang.configuration.RenderingConfiguration;
import novelang.common.Nodepath;
import novelang.parser.Escape;
import novelang.loader.ResourceName;

/**
 * An {@code XslWriter} escaping characters known as {@link novelang.parser.Escape}.
 *
 * @author Laurent Caillette
 */
public class EscapingWriter extends XslWriter {

  public EscapingWriter(
      RenderingConfiguration configuration,
      ResourceName xslFileName,
      RenditionMimeType mimeType
  ) {
    super( configuration, xslFileName, mimeType );
  }

  public void write( Nodepath kinship, String word ) throws Exception {
    final String escaped = Escape.escapeText( word );
    super.write( kinship, escaped ) ;
  }


}