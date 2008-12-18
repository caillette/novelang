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

package novelang.parser.antlr;

import novelang.parser.BookParserFactory;
import novelang.parser.BookParser;
import novelang.common.LocationFactory;

/**
 * @author Laurent Caillette
 */
public class DefaultBookParserFactory implements BookParserFactory {

  public BookParser createParser( final LocationFactory locationFactory, final String text ) {
    return new DelegatingBookParser( text, locationFactory ) ;
  }
}
