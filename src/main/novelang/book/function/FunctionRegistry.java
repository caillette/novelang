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
package novelang.book.function;

import novelang.book.function.builtin.InsertFunction;
import novelang.book.function.builtin.MapStylesheetFunction;
import novelang.system.Log;
import novelang.system.LogFactory;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Registry of {@link FunctionDefinition}s.
 *
 * @deprecated
 * 
 * @author Laurent Caillette
 */
public class FunctionRegistry {

  private static final Log LOG = LogFactory.getLog( FunctionRegistry.class ) ;

  private final Map< String, FunctionDefinition > definitions = Maps.newHashMap() ;

  public FunctionRegistry( FunctionDefinition... definitions ) {
    for( FunctionDefinition definition : definitions ) {
      this.definitions.put( definition.getName(), definition ) ;
      LOG.debug( "Added function definition: '%s'", definition.getName() ) ;
    }
  }

  public FunctionDefinition getFunctionDeclaration( String name ) throws UnknownFunctionException {
    final FunctionDefinition definition = definitions.get( name ) ;
    if( null == definition ) {
      LOG.warn( "Could not find funtion '%s'", name ) ;
      throw new UnknownFunctionException( name ) ;
    }
    return definition ;
  }

  private static final FunctionRegistry BUILTIN_FUNCTIONS = new FunctionRegistry(
      new InsertFunction(),
      new MapStylesheetFunction()
  ) ;

  public static FunctionRegistry getStandardRegistry() {
    return BUILTIN_FUNCTIONS ;
  }

}
