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
package novelang.model.implementation;

import java.util.Set;

import novelang.model.common.NodeKind;
import novelang.model.common.tree.Treepath;
import novelang.model.common.tree.TreeTools;
import novelang.model.common.SyntacticTree;
import static novelang.model.common.NodeKind.SECTION;
import static novelang.model.common.NodeKind.CHAPTER;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;

/**
 * Transforms the tree representing a Part for handling various stuff
 * that could not be left to the parser.
 *
 * <ol>
 * <li>All stuff like paragraph under a Section becomes Section's child.
 * <li>All stuff like Section under a Chapter becomes a Chapter's child.
 * <li>TODO All contiguous Speech stuff becomes a {@link NodeKind#_SPEECH_SEQUENCE} child.
 * <li>TODO Identifier above paragraph-like stuff becomes a paragraph's child.
 * </ol>
 *
 *
 * @author Laurent Caillette
 */
public class Hierarchizer {

  static Treepath< SyntacticTree > rehierarchize( final Treepath< SyntacticTree > part ) {
    final Treepath rehierarchizedSections = rehierarchizeFromLeftToRight(
        part, SECTION, new ExclusionFilter( CHAPTER ) ) ;

    return rehierarchizeFromLeftToRight(
        rehierarchizedSections, CHAPTER, new YesFilter() ) ;
  }

  /**
   * Upgrades a degenerated {@code Tree} by making nodes of a given {@code NodeKind} adopt
   * their rightmost siblings.
   *
   * @param part a {@code Treepath} with bottom {@code Tree} of {@code PART} kind.
   * @param accumulatorKind kind of node becoming the parent of their siblings on the right,
   *     unless they are of {@code accumulatorKind} or {@code ignored} kind.
   * @param filter kind of nodes to handle.
   * @return the result of the changes.
   */
  protected static Treepath< SyntacticTree > rehierarchizeFromLeftToRight(
      final Treepath< SyntacticTree > part,
      NodeKind accumulatorKind,
      Filter filter
  ) {
    Treepath< SyntacticTree > treepath = Treepath.create( part, part.getBottom().getChildAt( 0 ) ) ;

    while( true ) {
      final NodeKind childKind = getKind( treepath ) ;
      if( accumulatorKind == childKind ) {
        while( true ) {
          // Consume all siblings on the right to be reparented.
          if( TreeTools.hasNextSibling( treepath ) ) {
            final Treepath< SyntacticTree > next = TreeTools.getNextSibling( treepath ) ;
            final NodeKind kindOfNext = getKind( next );
            if( accumulatorKind == kindOfNext || ! filter.isMoveable( kindOfNext ) ) {
              treepath = next ;
              break ;
            } else {
              treepath = TreeTools.moveLeftDown( next ).getParent() ;
            }
          } else {
            return treepath.getParent() ;
          }
        }
      } else if( TreeTools.hasNextSibling( treepath ) ) {
        treepath = TreeTools.getNextSibling( treepath ) ;
      } else {
        return treepath.getParent() ;
      }
    }
  }

  private static NodeKind getKind( Treepath<SyntacticTree> treepath ) {
    return NodeKind.ofRoot( treepath.getBottom() ) ;
  }


  public interface Filter {
    boolean isMoveable( NodeKind nodeKind ) ;
  }


  public static class ExclusionFilter implements Filter {

    private final Set< NodeKind > excluded ;

    public ExclusionFilter( NodeKind... excluded ) {
      this.excluded = ImmutableSet.copyOf( Sets.newHashSet( excluded ) ) ;
    }

    public boolean isMoveable( NodeKind nodeKind ) {
      return ! excluded.contains( nodeKind ) ;
    }
  }

  public static class YesFilter implements Filter {
    public boolean isMoveable( NodeKind nodeKind ) {
      return true ;
    }
  }

}
