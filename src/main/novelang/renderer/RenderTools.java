/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.renderer;

import novelang.model.common.Tree;
import novelang.model.common.NodeKind;

/**
 * @author Laurent Caillette
 */
public class RenderTools {

  static final String spaces( int size ) {
    final StringBuffer buffer = new StringBuffer() ;
    for( int i = 0 ; i < size ; i++ ) {
      buffer.append( "  " ) ;
    }
    return buffer.toString() ;
  }

  static NodeKind getNodeKind( Tree tree ) {
    return Enum.valueOf( NodeKind.class, tree.getText() ) ;
  }

  static String generatePunctuationSign( Tree tree, String nonBreakableSpace ) {
    
    final NodeKind treeNodeKind = getNodeKind( tree );
    if( NodeKind.PUNCTUATION_SIGN != treeNodeKind ) {
      throw new IllegalArgumentException(
          "Expected tree of " + NodeKind.PUNCTUATION_SIGN +
          ", got " + treeNodeKind + " instead"
      ) ;
    }

    final NodeKind signNodeKind = getNodeKind( tree.getChildAt( 0 ) ) ;

    switch( signNodeKind ) {
      case SIGN_COLON :
        return nonBreakableSpace + ": " ;
      case SIGN_COMMA :
        return ", " ;
      case SIGN_ELLIPSIS :
        return "\u8230 " ;
      case SIGN_EXCLAMATIONMARK :
        return nonBreakableSpace + "! " ;
      case SIGN_FULLSTOP :
        return ". " ;
      case SIGN_QUESTIONMARK :
        return nonBreakableSpace + "? " ;
      case SIGN_SEMICOLON :
        return nonBreakableSpace + "; " ;
      default :
        return "<unsupported: " + signNodeKind.name() + ">" ;
    }
  }
}
