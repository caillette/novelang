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
package novelang.treemangling;

import org.junit.Test;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.part.FragmentIdentifier;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * @author Laurent Caillette
 */
public class FragmentExtractorTest {


  @Test
  public void simpleAbsoluteAtRoot() {
    verifyExtractIdentifiers(
        tree(
            _LEVEL,
            tree( ABSOLUTE_IDENTIFIER, tree( "wx" ), tree( "yz" ) ),
            tree( PARAGRAPH_REGULAR )
        ),
        tree(
            _LEVEL,
            tree( ABSOLUTE_IDENTIFIER, tree( "wx" ), tree( "yz" ) ),
            tree( PARAGRAPH_REGULAR )
        ),
        new FragmentIdentifier( "wx", "yz" )
    ) ;
  }


  @Test
  public void simpleAbsoluteAtLevel2() {
    verifyExtractIdentifiers(
        tree(
            _LEVEL,
            tree( ABSOLUTE_IDENTIFIER, tree( "wx" ), tree( "yz" ) ),
            tree( PARAGRAPH_REGULAR )
        ),
        tree(
            _LEVEL,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),                 // noise
            tree( _LEVEL, tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ) ), // noise
            tree(
                _LEVEL,
                tree( ABSOLUTE_IDENTIFIER, tree( "wx" ), tree( "yz" ) ),
                tree( PARAGRAPH_REGULAR )
            ),
            tree( _LEVEL, tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ) )  // noise
        ),
        new FragmentIdentifier( "wx", "yz" )
    ) ;
  }



  @Test
  public void simpleRelative() {
    verifyExtractIdentifiers(
        tree(
            _LEVEL,
            tree( ABSOLUTE_IDENTIFIER, tree( "wx" ), tree( "yz" ) ), // Resolved identifier
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        ),
        tree(
            _LEVEL,
            tree( ABSOLUTE_IDENTIFIER, tree( "wx" ) ),
            tree( PARAGRAPH_REGULAR ),
            tree(
                _LEVEL,
                tree( RELATIVE_IDENTIFIER, tree( "yz" ) ),
                tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
            )
        ),
        new FragmentIdentifier( "wx", "yz" )
    ) ;
  }


// =======
// Fixture
// =======

  private static Log LOG = LogFactory.getLog( FragmentExtractorTest.class ) ;

  private static void verifyExtractIdentifiers(
      SyntacticTree expectedTree,
      SyntacticTree initialTree,
      FragmentIdentifier fragmentIdentifier
  ) {
    LOG.info( "Extracted tree: %s", TreeFixture.asString( initialTree ) ) ;
    LOG.info( "Expected tree: %s", TreeFixture.asString( expectedTree ) ) ;
    final Treepath< SyntacticTree > expectedTreepath = Treepath.create( expectedTree ) ;
    final Treepath< SyntacticTree > initialTreepath = Treepath.create( initialTree ) ;

    final Treepath< SyntacticTree > extracted =
        FragmentExtractor.extractFragment( initialTreepath, fragmentIdentifier ) ;

    TreeFixture.assertEqualsNoSeparators(
        expectedTreepath.getTreeAtEnd(),
        extracted.getTreeAtEnd()
    ) ;
  }



}
