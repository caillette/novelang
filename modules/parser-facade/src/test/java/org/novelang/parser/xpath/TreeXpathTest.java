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
package org.novelang.parser.xpath;

import com.google.common.collect.ImmutableList;
import org.jaxen.JaxenException;
import org.jaxen.UnresolvableException;
import org.junit.Test;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;

import static org.fest.assertions.Assertions.assertThat;
import static org.novelang.parser.NodeKind.OPUS;
import static org.novelang.parser.NodeKind._LEVEL;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 *
 * Tests for {@link SyntacticTreeXpath}
 *
 * @author Laurent Caillette
 */
public class TreeXpathTest {

  @Test
  public void emptyTree() throws JaxenException {
    final SyntacticTreeXpath xpath = new SyntacticTreeXpath( "/*" ) ;
    final ImmutableList< Treepath< SyntacticTree > > treepaths = xpath.selectNodes(
        tree( OPUS )
    ) ;

    assertThat( treepaths ).hasSize( 0 ) ;
  }

  @Test
  public void singleChild() throws JaxenException {
    final SyntacticTreeXpath xpath = new SyntacticTreeXpath( "/*" ) ;
    final ImmutableList< Treepath< SyntacticTree > > treepaths = xpath.selectNodes(
        tree( OPUS, tree( _LEVEL ) )
    ) ;

    assertThat( treepaths ).hasSize( 1 ) ;
    assertThat( treepaths.get( 0 ).getTreeAtEnd().getNodeKind() ).isEqualTo( _LEVEL ) ;
  }

  @Test
  public void singleNamedChild() throws JaxenException {
    final SyntacticTreeXpath xpath = new SyntacticTreeXpath( "n:level" ) ;
    final ImmutableList< Treepath< SyntacticTree > > treepaths =
        xpath.selectNodes( tree( OPUS, tree( _LEVEL ) ) ) ;

    assertThat( treepaths ).hasSize( 1 ) ;
    assertThat( treepaths.get( 0 ).getTreeAtEnd().getNodeKind() ).isEqualTo( _LEVEL ) ;
  }

  @Test( expected = UnresolvableException.class )
  public void noNamespaceOtherThanN() throws JaxenException {
    new SyntacticTreeXpath( "xn:level" ).selectNodes( tree( OPUS, tree( _LEVEL ) ) ) ;
  }


}
