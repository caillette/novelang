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
package novelang.model.book;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import novelang.model.common.Tree;
import novelang.model.common.Problem;
import novelang.model.common.NodeKind;
import novelang.model.common.Treepath;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionRegistry;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.UnknownFunctionException;
import novelang.model.function.IllegalFunctionCallException;
import novelang.model.implementation.DefaultMutableTree;
import novelang.reader.AbstractSourceReader;
import novelang.parser.antlr.DefaultBookParserFactory;
import com.google.common.collect.Lists;

/**
 * Reads a Book file, processes functions and builds a Tree with inclusions and so on.
 *
 * @author Laurent Caillette
 */
public class Book extends AbstractSourceReader {

  private final Environment environment ;
  private final Tree documentTree ;

  public Book( FunctionRegistry functionRegistry, File baseDirectory, String content ) {
    environment =  new Environment( baseDirectory ) ;
    final Tree rawTree = parse( new DefaultBookParserFactory(), content ) ;
    Iterable< FunctionCall > functionCalls = createFunctionCalls( functionRegistry, rawTree ) ;
    documentTree = callFunctions( functionCalls, new DefaultMutableTree( NodeKind.BOOK ) ) ;
  }

  public Book( FunctionRegistry functionRegistry, File bookFile ) throws IOException {
    this(
        functionRegistry,
        bookFile.getParentFile(),
        IOUtils.toString( new FileInputStream( bookFile ) )
    ) ;    
  }

  public Tree getDocumentTree() {
    return documentTree;
  }

  private Iterable< FunctionCall > createFunctionCalls(
      FunctionRegistry functionRegistry,
      Tree rawTree
  ) {
    final List< FunctionCall > functionCalls = Lists.newArrayList() ;
    for( int i = 0 ; i < rawTree.getChildCount() ; i++ ) {
      final Tree functionCallTree = rawTree.getChildAt( i ) ;
      final Tree functionNameTree = functionCallTree.getChildAt( 0 ) ;
      final String functionName = functionNameTree.getChildAt( 0 ).getText() ;
      try {
        final FunctionDefinition functionDefinition =
            functionRegistry.getFunctionDeclaration( functionName ) ;
        final FunctionCall functionCall =
            functionDefinition.instantiate( functionCallTree.getLocation(), functionCallTree ) ;
        functionCalls.add( functionCall ) ;
      } catch( UnknownFunctionException e ) {
        collect( Problem.createProblem( e ) ) ;
      } catch( IllegalFunctionCallException e ) {
        collect( Problem.createProblem( e ) ) ;
      }
    }
    return Lists.immutableList( functionCalls ) ;
  }

  private Tree callFunctions( Iterable< FunctionCall > functionCalls, Tree tree ) {
    Treepath book = Treepath.create( tree ) ;
    for( FunctionCall functionCall : functionCalls ) {
      FunctionCall.Result result = functionCall.evaluate( environment, book ) ;
      collect( result.getProblems() ) ;
      final Treepath newBook = result.getBook() ;
      if( null != newBook ) {
        book = newBook ;
      }
    }
    return book.getTop() ;
  }

  


}
