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
package novelang.book.function.builtin;

import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.book.Environment;
import novelang.book.function.FunctionCall;
import novelang.book.function.FunctionDefinition;
import static novelang.book.function.FunctionTools.verify;
import novelang.book.function.IllegalFunctionCallException;
import novelang.common.Location;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.LEVEL_TITLE;
import static novelang.parser.NodeKind.VALUED_ARGUMENT_PRIMARY_;

/**
 * Creates a title in a Book.
 * TODO check existing TITLE nodes for inserting several TITLEs in order.
 * By now titles are added in reverse order.
 *
 * @author Laurent Caillette
 */
public class TitleFunction implements FunctionDefinition {

  private static final Log LOG = LogFactory.getLog( TitleFunction.class ) ;

  public String getName() {
    return "title" ;
  }

  public FunctionCall instantiate(
      Location location,
      SyntacticTree functionCall
  ) throws IllegalFunctionCallException {

    verify( "No primary argument", 2, functionCall.getChildCount() ) ;
    final SyntacticTree primaryArgument = functionCall.getChildAt( 1 ) ;
    final String primaryArgumentText = primaryArgument.getText();
    verify( "Incorrect declaration for primary argument: '" + primaryArgumentText + "'",
        VALUED_ARGUMENT_PRIMARY_.name(), primaryArgumentText ) ;
    verify( "Primary argument is empty", true, primaryArgument.getChildCount() > 0 ) ;
    final SyntacticTree paragraph = primaryArgument.getChildAt( 0 ) ;
    verify( "Primary argument should hold a paragraph, instead of: '" + paragraph.toStringTree() + "'",
        NodeKind.PARAGRAPH_REGULAR.name(), paragraph.getText() ) ;

    LOG.debug( "Parsed function '%s' title='%s'", getName(), primaryArgument.toStringTree() ) ;

    final SyntacticTree titleTree = new SimpleTree(
        LEVEL_TITLE.name(),
        paragraph.getChildren()
    ) ;

    return new FunctionCall( location ) {
      public Result evaluate( Environment environment, Treepath< SyntacticTree > book ) {
        final SyntacticTree bookTree = book.getTreeAtStart() ;
        int first = -1 ;
        for( int i = bookTree.getChildCount() - 1 ; i >= 0 ; i-- ) {
          if( NodeKind.LEVEL_TITLE.isRoot( bookTree.getChildAt( i  ) ) ) {
            first = i ;
          }
        }
        final Treepath< SyntacticTree > newBook ;
        if( -1 == first ) {
          newBook = TreepathTools.addChildFirst( book.getStart(), titleTree ) ;
        } else {
          newBook = TreepathTools.addChildAt( book.getStart(), titleTree, first + 1 ) ;
        }
        return new Result( environment, newBook, null ) ;
      }
    } ;
  }


}