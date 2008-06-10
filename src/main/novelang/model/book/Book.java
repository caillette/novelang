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

import java.nio.charset.Charset;

import novelang.model.common.Tree;
import novelang.model.common.Problem;
import novelang.model.renderable.Renderable;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionRegistry;

/**
 * @author Laurent Caillette
 */
public class Book implements Renderable {

  private final FunctionRegistry functionRegistry ;



  public Book( FunctionRegistry functionRegistry, String content ) {
    this.functionRegistry = functionRegistry;
  }

  public Tree getTree() {
    throw new UnsupportedOperationException( "getTree" ) ;
  }

  private static Iterable<FunctionCall> createFunctionCalls( Tree tree ) {
    throw new UnsupportedOperationException( "createFunctionCalls" ) ;
  }

  


// ==========
// Renderable
// ==========

  public Iterable< Problem > getProblems() {
    throw new UnsupportedOperationException( "getProblems" ) ;
  }

  public Charset getEncoding() {
    throw new UnsupportedOperationException( "getEncoding" ) ;
  }

  public boolean hasProblem() {
    throw new UnsupportedOperationException( "hasProblem" ) ;
  }

}
