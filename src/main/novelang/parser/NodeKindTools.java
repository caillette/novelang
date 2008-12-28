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
package novelang.parser;

import java.util.Set;

import novelang.common.SyntacticTree;
import com.google.common.base.Preconditions;
import com.google.common.base.Function;
import com.google.common.base.Nullable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * As {@link NodeKind} is generated, all its utility methods should go there.
 *
 * @author Laurent Caillette
 */
public class NodeKindTools {
  private NodeKindTools() {
    throw new Error() ;
  }

  public static NodeKind ofRoot( SyntacticTree tree ) {
    return Enum.valueOf( NodeKind.class, tree.getText() ) ;
  }

  public static boolean rootHasNodeKindName( SyntacticTree tree ) {
    if( null == tree ) {
      return false ;
    }
    final String text = tree.getText() ;
    return null != tree && NodeKind.getNames().contains( text ) ;
  }

  public static void ensure( SyntacticTree tree, NodeKind nodeKind ) {
    Preconditions.checkNotNull( tree ) ;
    Preconditions.checkNotNull( nodeKind ) ;
    final String nodeText = Preconditions.checkNotNull( tree.getText() ) ;
    if( ! NodeKind.getNames().contains( nodeText ) ) {
      throw new RuntimeException( "Not a known node kind: '" + nodeText + "'" ) ;
    }
    if( nodeKind != ofRoot( tree ) ) {
      throw new RuntimeException( "Expected: " + nodeKind + ", got: " + nodeText ) ;
    }
  }

  public static String tokenNameAsXmlElementName( String tokenName ) {
    String result = tokenName.toLowerCase().replace( "_", "-" ) ;
    if( result.startsWith( "-" ) ) {
      result = result.substring( 1 ) ;
    }
    return result ;
  }

  private static final Function< String,String > TOKEN_NAME_AS_XML_ELEMENT_NAME =
      new Function< String, String >() {
        public String apply( String nodeName ) {
          return tokenNameAsXmlElementName( nodeName ) ;
        }
      }
  ;

  public static Set< String > getNamesAsXmlElementNames() {
    return Sets.newHashSet( Lists.transform(
        Lists.newArrayList( NodeKind.getNames() ),
        TOKEN_NAME_AS_XML_ELEMENT_NAME
    ) ) ;

  }
}
