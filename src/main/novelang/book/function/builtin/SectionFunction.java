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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.book.Environment;
import novelang.common.Location;
import static novelang.common.NodeKind.VALUED_ARGUMENT_PRIMARY;
import static novelang.common.NodeKind.TITLE;
import static novelang.common.NodeKind.SECTION;
import static novelang.common.NodeKind.PARAGRAPH_PLAIN;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.common.SyntacticTree;
import novelang.common.SimpleTree;
import novelang.common.tree.TreeTools;
import novelang.book.function.FunctionCall;
import novelang.book.function.FunctionDefinition;
import novelang.book.function.IllegalFunctionCallException;
import static novelang.book.function.FunctionTools.verify;

/**
 * Creates a Section in a Book.
 *
 * @author Laurent Caillette
 */
public class SectionFunction implements FunctionDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger( SectionFunction.class ) ;

  public String getName() {
    return "section" ;
  }

  public FunctionCall instantiate(
      Location location,
      SyntacticTree functionCall
  ) throws IllegalFunctionCallException {

    verify( "No primary argument", 2, functionCall.getChildCount() ) ;
    final SyntacticTree primaryArgument = functionCall.getChildAt( 1 ) ;
    final String primaryArgumentText = primaryArgument.getText();
    verify( "Incorrect declaration for primary argument: '" + primaryArgumentText + "'",
        VALUED_ARGUMENT_PRIMARY.name(), primaryArgumentText ) ;
    verify( "Primary argument is empty", true, primaryArgument.getChildCount() > 0 ) ;
    final SyntacticTree paragraph = primaryArgument.getChildAt( 0 ) ;
    verify( "Primary argument should hold a paragraph, instead of: '" + paragraph.toStringTree() + "'",
        PARAGRAPH_PLAIN.name(), paragraph.getText() ) ;

    LOGGER.debug( "Parsed function '{}' title='{}'", getName(), primaryArgument.toStringTree() ) ;

    final SyntacticTree titleTree = new SimpleTree(
        TITLE.name(),
        paragraph.getChildren() 
    ) ;

    final SyntacticTree sectionTree = TreeTools.addLast(
        new SimpleTree( SECTION.name() ),
        titleTree
    ) ;

    return new FunctionCall( location ) {
      public Result evaluate( Environment environment, Treepath<SyntacticTree> book ) {
        final Treepath< SyntacticTree > newBook = TreepathTools.addChildLast( book, sectionTree ) ;
        return new Result( environment, newBook, null ) ;
      }
    } ;
  }


}