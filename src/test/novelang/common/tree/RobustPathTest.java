/*
 * Copyright (C) 2009 Laurent Caillette
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.common.tree;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.google.common.base.Predicate;

import novelang.common.SyntacticTree;
import novelang.parser.antlr.TreeFixture;

/**
 * Tests for {@link RobustPath}.
 *
 * @author Laurent Caillette
 */
public class RobustPathTest {

  @Test
  public void singleton() {
    final SyntacticTree tree = TreeFixture.tree( "foo" ) ;
    final Treepath< SyntacticTree > treepath = Treepath.create( tree ) ;
    final RobustPath< SyntacticTree > robustPath = RobustPath.create( treepath ) ;
    assertEquals(
        tree,
        robustPath.apply( treepath.getStart() ).getTreeAtEnd()
    ) ;
  }


  @Test
  public void parentChild() {
    final SyntacticTree child = TreeFixture.tree( "child" ) ;
    final SyntacticTree parent = TreeFixture.tree( "parent", child ) ;
    final Treepath< SyntacticTree > treepath = Treepath.create( parent, 0 ) ;
    final RobustPath< SyntacticTree > robustPath = RobustPath.create( treepath ) ;
    final Treepath< SyntacticTree > treepathRebuilt = robustPath.apply( treepath.getStart() ) ;
    assertEquals(
        parent,
        treepathRebuilt.getTreeAtStart()  
    ) ;
    assertEquals(
        child,
        treepathRebuilt.getTreeAtEnd()
    ) ;
  }


  @Test
  public void parentWithOneIgnoredChild() {
    final SyntacticTree child = TreeFixture.tree( "child" ) ;
    final SyntacticTree ignored = TreeFixture.tree( "-1" ) ;
    final SyntacticTree parent = TreeFixture.tree( "parent", ignored, child ) ;
    final Treepath< SyntacticTree > treepath = Treepath.create( parent, 1 ) ;
    final RobustPath< SyntacticTree > robustPath = RobustPath.create(
        treepath,
        new Predicate< SyntacticTree >() {
          public boolean apply( final SyntacticTree syntacticTree ) {
            return ! syntacticTree.getText().startsWith("-" ) ;
          }
        }
    ) ;
    final Treepath< SyntacticTree > treepathMinusIgnored =
        TreepathTools.removePreviousSibling( treepath ) ;
    final Treepath< SyntacticTree > treepathRebuilt =
        robustPath.apply( treepathMinusIgnored.getStart() ) ;
    assertEquals(
        child,
        treepathRebuilt.getTreeAtEnd()
    ) ;
  }


}
