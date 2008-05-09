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

import org.antlr.runtime.RecognitionException;
import novelang.parser.BookParser;
import novelang.model.structural.StructuralBook;
import novelang.model.common.Tree;
import com.sun.java_cup.internal.parser;

/**
 * @author Laurent Caillette
*/
public class DelegatingBookParser
    extends AbstractDelegatingParser< BookGrammarDelegate >
    implements BookParser
{

  public DelegatingBookParser( String text, StructuralBook book ) {
    super( text, new BookGrammarDelegate( book ) ) ;
  }

  public boolean getScopesEnabled() {
    return getDelegate().getScopesEnabled() ;
  }

  public void setScopesEnabled( boolean scopesEnabled ) {
    getDelegate().setScopesEnabled( scopesEnabled ) ;
  }

  public Tree parse() throws RecognitionException {
    return ( Tree ) getAntlrParser().book().getTree() ;
  }

}
