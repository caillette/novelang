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

package novelang.parser.antlr;

import java.util.List;

import org.antlr.runtime.ClassicToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.parser.NodeKind;

/**
 * A {@code CommonTree} created by {@link novelang.parser.antlr.NovelangParser} implementing
 * {@link SyntacticTree}.
 *
 * @author Laurent Caillette
 */
public class CustomTree
    extends CommonTree
    implements SyntacticTree
{
  private static final Logger LOGGER = LoggerFactory.getLogger( CustomTree.class ) ;

  private final LocationFactory locationFactory ;

  public CustomTree( LocationFactory locationFactory, Token token ) {
		super( token ) ;
    this.locationFactory = locationFactory ;
  }

  public Location getLocation() {
    if( null == token ) {
      return locationFactory.createLocation( -1, -1 ) ;

    } else {
      return locationFactory.createLocation( 
          token.getLine(), token.getCharPositionInLine()) ;
    }
  }

  public boolean isOneOf( NodeKind... kinds ) {
    for( NodeKind kind : kinds ) {
      if( kind.isRoot( this ) ) {
        return true ;
      }
    }
    return false ;
  }

  public List< SyntacticTree > getChildren() {
    final List children = super.getChildren();
    if( null == children ) {
      return ImmutableList.of() ;
    } else {
      return children ;
    }
  }

  public CustomTree adopt( SyntacticTree... newChildren ) throws NullArgumentException {
    final CustomTree newCustomTree = new CustomTree( locationFactory, token ) ;
    for( SyntacticTree child : newChildren ) {
      newCustomTree.addChild( child ) ;
    }
    return newCustomTree ; 
  }


  public void addChild( SyntacticTree child ) {
    addChild( convert( child ) ) ;
  }

  /**
   * Ugly method to convert {@link SimpleTree} into {@code CustomTree}, useful in some rare
   * cases when a {@code CustomTree} must adopt a {@code SimpleTree}, like when a paragraph
   * is created directly from a Book file (as a {@code SimpleTree}. 
   */
  private CommonTree convert( SyntacticTree tree ) {
    if( tree instanceof SimpleTree ) {
      final CommonTree customTree = new CustomTree(
          locationFactory,
          new ClassicToken( // TODO don't swallow line + column information.
              ClassicToken.MIN_TOKEN_TYPE, tree.getText() ) 
      ) ;
      for( SyntacticTree child : tree.getChildren() ) {
        customTree.addChild( convert( child ) ) ;
      }
      return customTree ;
    } else {
      return ( CommonTree ) tree ;
    }
  }

  public SyntacticTree getChildAt( int i ) {
    return ( SyntacticTree ) getChild( i );
  }

  public Class< ? extends SyntacticTree > getStorageType() {
    return SyntacticTree.class ;
  }
  

}
