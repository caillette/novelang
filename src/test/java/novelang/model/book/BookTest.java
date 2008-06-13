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
package novelang.model.book;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import org.antlr.runtime.RecognitionException;
import novelang.parser.antlr.AntlrTestHelper;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;
import static novelang.model.common.NodeKind.BOOK;
import static novelang.model.common.NodeKind.WORD;
import static novelang.model.common.NodeKind.PARAGRAPH_PLAIN;
import static novelang.model.common.NodeKind.SECTION;
import static novelang.model.common.NodeKind.TITLE;
import novelang.model.common.Tree;
import novelang.model.common.NodeKind;
import novelang.model.function.FunctionRegistry;
import novelang.TestResources;

/**
 * Test for {@link Book} and also built-in functions.
 * 
 * @author Laurent Caillette
 */
public class BookTest {

  /**
   * Tests the {@link novelang.model.function.builtin.FunctionSection}.
   */
  @Test
  public void justCreateSection() {
    final Book book = new Book( FunctionRegistry.getStandardRegistry(), "section My Section") ;
    final Tree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        tree( BOOK,
            tree(
                SECTION,
                tree( TITLE, tree( WORD, "My" ), tree( WORD, "Section" ) )
            )
        ),
        bookTree
    ) ;
    Assert.assertFalse( book.hasProblem() ) ;
  }

  /**
   * Test {@link novelang.model.function.builtin.FunctionInsert}.
   */
  @Test @Ignore
  public void justInsert() {

    final Book book = new Book(
        FunctionRegistry.getStandardRegistry(),
        "insert file://" + TestResources.ONE_WORD
    ) ;
    final Tree bookTree = book.getDocumentTree() ;
    TreeFixture.assertEquals(
        tree( BOOK,
            tree(
                NodeKind.PARAGRAPH_PLAIN,
                tree( WORD, "oneword" )
            )
        ),
        bookTree
    ) ;
    Assert.assertFalse( book.hasProblem() ) ;

  }

}
