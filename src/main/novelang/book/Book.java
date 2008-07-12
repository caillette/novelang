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
package novelang.book;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import novelang.common.Problem;
import novelang.common.NodeKind;
import novelang.common.tree.Treepath;
import novelang.common.SyntacticTree;
import novelang.common.SimpleTree;
import novelang.common.AbstractSourceReader;
import novelang.common.StylesheetMap;
import novelang.book.function.FunctionCall;
import novelang.book.function.FunctionRegistry;
import novelang.book.function.FunctionDefinition;
import novelang.book.function.UnknownFunctionException;
import novelang.book.function.IllegalFunctionCallException;
import novelang.parser.antlr.DefaultBookParserFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;

/**
 * Reads a Book file, processes functions and builds a Tree with inclusions and so on.
 *
 * @author Laurent Caillette
 */
public class Book extends AbstractSourceReader {

  private final Environment environment ;
  private final SyntacticTree documentTree ;

  public Book( FunctionRegistry functionRegistry, File baseDirectory, String content ) {
    final SyntacticTree rawTree = parse( new DefaultBookParserFactory(), content ) ;
    if( null == rawTree ) {
      this.environment = new Environment( baseDirectory ) ;
      this.documentTree = null ;
    } else {
      Iterable< FunctionCall > functionCalls = createFunctionCalls( functionRegistry, rawTree ) ;
      final Results results = callFunctions(
          functionCalls,
          new Environment( baseDirectory ),
          new SimpleTree( NodeKind.BOOK.name() )
      ) ;
      this.environment = results.environment ;
      this.documentTree = results.book ;
    }

  }

  public Book( FunctionRegistry functionRegistry, File bookFile ) throws IOException {
    this(
        functionRegistry,
        bookFile.getParentFile(),
        IOUtils.toString( new FileInputStream( bookFile ) )
    ) ;    
  }

  public SyntacticTree getDocumentTree() {
    return documentTree;
  }

  public StylesheetMap getCustomStylesheetMap() {
    return environment.getCustomStylesheets() ;
  }

  private Iterable< FunctionCall > createFunctionCalls(
      FunctionRegistry functionRegistry,
      SyntacticTree rawTree
  ) {
    final List< FunctionCall > functionCalls = Lists.newArrayList() ;
    for( int i = 0 ; i < rawTree.getChildCount() ; i++ ) {
      final SyntacticTree functionCallTree = rawTree.getChildAt( i ) ;
      final SyntacticTree functionNameTree = functionCallTree.getChildAt( 0 ) ;
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
    return ImmutableList.copyOf( functionCalls ) ;
  }

  private Results callFunctions(
      final Iterable< FunctionCall > functionCalls,
      Environment environment,
      final SyntacticTree tree
  ) {
    Treepath<SyntacticTree> book = Treepath.create( tree ) ;
    for( FunctionCall functionCall : functionCalls ) {
      FunctionCall.Result result = functionCall.evaluate( environment, book ) ;
      environment = result.getEnvironment() ;
      collect( result.getProblems() ) ;
      final Treepath<SyntacticTree> newBook = result.getBook() ;
      if( null != newBook ) {
        book = newBook ;
      }
    }
    return new Results( environment, book.getTreeAtStart() ) ;
  }

  private static class Results {
    public final Environment environment ;
    public SyntacticTree book ;

    private Results( Environment environment, SyntacticTree book ) {
      this.environment = environment ;
      this.book = book ;
    }
  }

  


}
