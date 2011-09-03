/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.parser.antlr;

import java.util.List;

import com.google.common.collect.Lists;
import org.novelang.common.Location;
import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.parser.NodeKind;
import org.antlr.runtime.tree.Tree;

/**
 * Converts a {@link org.antlr.runtime.tree.Tree} into a {@link org.novelang.common.SyntacticTree}.
 * This helps using lighter objects than those created by ANTLR, which keep a backpointer 
 * to the parent tree.
 * 
 * @author Laurent Caillette
 */
public class TreeConverter {

  private TreeConverter() {}

  public static SyntacticTree convert( 
      final Tree antlrTree, 
      final TokenNameProvider tokenNameProvider 
  ) {

    final String originalText = antlrTree.getText() ;
    final String treeText ;
    final NodeKind treeKind ;
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
    } else if( childText == null ) {
      children = null ;
    } else {
      children = Lists.newArrayList() ;
      children.add( new SimpleTree( childText ) ) ;
    }


    if( antlrTree.getType() >= 0 && antlrTree instanceof CustomTree ) {
      treeText = null ;
      treeKind = NodeKind.valueOf( tokenNameProvider.getTokenName( antlrTree.getType() ) ) ;
    } else {
      treeText = originalText ; 
      treeKind = null ;
    }


    // TODO pool punctuation signs and whitespaces.
    if( children == null ) {
      return treeKind == null ? 
          new SimpleTree( treeText, location ) : 
          new SimpleTree( treeKind, location ) 
      ;
    } else {
      return treeKind == null ? 
          new SimpleTree( treeText, location, children ) : 
          new SimpleTree( treeKind, location, children ) 
      ;
    }
  }
  
}
