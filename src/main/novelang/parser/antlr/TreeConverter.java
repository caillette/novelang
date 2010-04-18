/*
 * Copyright (C) 2010 Laurent Caillette
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

import com.google.common.collect.Lists;
import novelang.common.Location;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import org.antlr.runtime.tree.Tree;

/**
 * Converts a {@link org.antlr.runtime.tree.Tree} into a {@link novelang.common.SyntacticTree}.
 * This helps using lighter objects than those created by ANTLR, which keep a backpointer 
 * to the parent tree.
 * 
 * @author Laurent Caillette
 */
public class TreeConverter {
  
  public static SyntacticTree convert( 
      final Tree antlrTree, 
      final TokenNameProvider tokenNameProvider 
  ) {

    final String originalText = antlrTree.getText() ;
    final String treeText ;
    final Location location ;
    final String childText ;
    final List< SyntacticTree > children ;

    if( antlrTree instanceof CustomTree ) { // Need to check because of ANTLR error nodes.
      final CustomTree customTree = ( CustomTree ) antlrTree ;
      childText = customTree.getChildText() ;
      location = customTree.getLocation() ;
    } else {
      childText = null ;
      location = null ;
    }
    
    if( antlrTree.getChildCount() > 0 ) {
      children = Lists.newArrayList() ;
      if( childText != null ) {
        children.add( new SimpleTree( childText ) ) ;
      }
      for( int childIndex = 0 ; childIndex < antlrTree.getChildCount() ; childIndex ++ ) {
        children.add( convert( antlrTree.getChild( childIndex ), tokenNameProvider ) ) ;
      }
      treeText = originalText ;
    } else if( childText == null ) {
      children = null ;
      treeText = originalText ;
    } else {
      children = Lists.newArrayList() ;
      children.add( new SimpleTree( childText ) ) ;
      treeText = tokenNameProvider.getTokenName( antlrTree.getType() );
    }
    
    
    // TODO pool punctuation signs and whitespaces.
    if( children == null ) {
      return new SimpleTree( treeText, location ) ;
    } else {
      return new SimpleTree( treeText, location, children ) ;      
    }
  }
  
}
