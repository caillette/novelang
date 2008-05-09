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

import org.antlr.runtime.TokenStream;
import novelang.model.common.Location;
import novelang.model.implementation.Part;
import novelang.model.structural.StructuralChapter;
import novelang.model.structural.StructuralBook;

/**
 * @author Laurent Caillette
 */
public class BookGrammarDelegate extends GrammarDelegate {

  private final StructuralBook book ;

  public BookGrammarDelegate( StructuralBook book ) {
    super( book ) ;
    this.book = book ;
  }

  public Part createPart( String partFileName, TokenStream input ) {
    final Location location = AntlrParserHelper.createLocation( book, input ) ;
    return book.createPart( partFileName, location ) ;
  }

  public StructuralChapter createChapter( TokenStream input ) {
    final Location location = AntlrParserHelper.createLocation( book, input ) ;
    return book.createChapter( location ) ;
  }

  private boolean scopesEnabled = true ;

  public boolean getScopesEnabled() {
    return scopesEnabled;
  }

  public void setScopesEnabled( boolean scopesEnabled ) {
    this.scopesEnabled = scopesEnabled;
  }
}
