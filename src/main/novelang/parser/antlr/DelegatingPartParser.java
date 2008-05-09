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

import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import novelang.parser.PartParser;
import novelang.model.common.LocationFactory;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import novelang.model.structural.StructuralBook;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

/**
 * @author Laurent Caillette
*/
class DelegatingPartParser
    extends AbstractDelegatingParser< PartGrammarDelegate >
    implements PartParser
{
  public DelegatingPartParser( String text, LocationFactory locationFactory ) {
    super( text, new PartGrammarDelegate( locationFactory ) ) ;
  }

  public Tree parse() throws RecognitionException {
    return ( Tree ) getAntlrParser().part().getTree() ;
  }
  
}
