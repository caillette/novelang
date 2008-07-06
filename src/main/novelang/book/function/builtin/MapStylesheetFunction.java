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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.book.function.FunctionDefinition;
import novelang.book.function.FunctionCall;
import novelang.book.function.IllegalFunctionCallException;
import static novelang.book.function.FunctionTools.verify;
import novelang.book.Environment;
import novelang.common.Location;
import novelang.common.SyntacticTree;
import novelang.common.NodeKind;
import static novelang.common.NodeKind.VALUED_ARGUMENT_PRIMARY;
import novelang.common.tree.Treepath;
import novelang.rendering.RenditionMimeType;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Maps;

/**
 * @author Laurent Caillette
 */
public class MapStylesheetFunction implements FunctionDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger( MapStylesheetFunction.class ) ;

  public String getName() {
    return "mapstylesheet" ;
  }

  public FunctionCall instantiate( Location location, SyntacticTree functionCall )
      throws IllegalFunctionCallException
  {
    verify( "No valued argument assignment", true, functionCall.getChildCount() >= 2 ) ;
    final Map< RenditionMimeType, String > assignments = Maps.newHashMap() ;

    for( int i = 1 ; i < functionCall.getChildCount() ; i++ ) {
      final SyntacticTree assignmentTree = functionCall.getChildAt( i ) ;
      if( NodeKind.VALUED_ARGUMENT_ASSIGNMENT.name().equals( assignmentTree.getText() ) ) {
        verify( "No key / value pair", true, 2 == assignmentTree.getChildCount() ) ;
        final String keyAsString = assignmentTree.getChildAt( 0 ).getText() ;
        final String value = assignmentTree.getChildAt( 1 ).getText() ;
        verify( "Not a supported rendition MIME type: '" + keyAsString + "'", true,
            RenditionMimeType.contains( keyAsString ) ) ;
        RenditionMimeType key = RenditionMimeType.valueOf( keyAsString.toUpperCase() ) ;
        verify( "Duplicate assignment for key '" + key + "'",
            false, assignments.containsKey( key ) ) ;
        assignments.put( key, value ) ;
      }
    }

    if( LOGGER.isDebugEnabled() ) {
      final StringBuffer buffer = new StringBuffer() ;
      for( RenditionMimeType key : assignments.keySet() ) {
        buffer.append( "\n  $" ).append( key.getFileExtension() ).append( "=" ) ;
        buffer.append( assignments.get( key ) ) ;
      }
      LOGGER.debug( "Parsed function '{}'{}", getName(), buffer.toString() ) ;
    }

    return new FunctionCall( location ) {
      public Result evaluate( Environment environment, Treepath< SyntacticTree > book ) {
        return MapStylesheetFunction.evaluate( environment, book, assignments ) ;
      }
    } ;
  }

  private static FunctionCall.Result evaluate(
      Environment environment,
      Treepath< SyntacticTree > book,
      Map< RenditionMimeType, String > assignments
  ) {
    for( RenditionMimeType renditionMimeType : assignments.keySet() ) {
      environment = environment.map( renditionMimeType, assignments.get( renditionMimeType ) ) ;
    }
    return new FunctionCall.Result( environment, book, null ) ;
  }
}
