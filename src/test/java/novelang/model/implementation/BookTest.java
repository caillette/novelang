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
package novelang.model.implementation;

import static novelang.model.common.NodeKind.*;
import static novelang.parser.antlr.TreeHelper.tree;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.ClassUtils;
import novelang.ResourceTools;
import novelang.ScratchDirectoryFixture;
import novelang.parser.antlr.TreeHelper;
import novelang.parser.antlr.AntlrTestHelper;
import novelang.model.common.Tree;

/**
 * 
 *
 * @author Laurent Caillette
 */
public class BookTest {

  @Test
  public void loadSimpleBook() throws IOException {
    simpleBook.load() ;

    Assert.assertFalse( simpleBook.hasProblem() ) ;

    Assert.assertNotNull( simpleBook.getTree( "small1" ) ) ;
    Assert.assertNotNull( simpleBook.getTree( "small2" ) ) ;
    Assert.assertNotNull( simpleBook.getTree( "small3" ) ) ;
    Assert.assertNotNull( simpleBook.getTree( "small4" ) ) ;

    final Tree bookTree = simpleBook.getTree();
    TreeHelper.assertEquals(
      tree(
          _BOOK,
          tree( CHAPTER,
              tree( SECTION,
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w10" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w20" ) )
              ),
              tree( SECTION,
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w30" ) )
              )
          ),
          tree( CHAPTER,
              tree( SECTION,
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w40" ) )
              )
          )
      ),
      bookTree
    ) ;

  }

  @Test
  public void loadStylishBook() throws IOException {
    stylishBook.load() ;
    Assert.assertFalse(
        AntlrTestHelper.createProblemList( stylishBook.getProblems() ),
        stylishBook.hasProblem()
    ) ;

    final Tree bookTree = stylishBook.getTree();
    TreeHelper.assertEquals(
      tree(
          _BOOK,
          tree( CHAPTER,
              tree( TITLE, tree( WORD, "Chapter" ), tree( WORD, "one" ) ),
              tree( STYLE, tree( WORD, "chapter-one-style" ) ),
              tree( SECTION,
                  tree( TITLE, tree( WORD, "Section" ), tree( WORD, "one-one" ) ),
                  tree( STYLE, tree( WORD, "section-one-one-style" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w10" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w20" ) )
              ),
              tree( SECTION,
                  tree( TITLE, tree( WORD, "Section" ), tree( WORD, "one-two" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w30" ) )
              )
          ),
          tree( CHAPTER,
              tree( TITLE, tree( WORD, "Chapter" ), tree( WORD, "two" ) ),
              tree( STYLE, tree( WORD, "chapter-two-style" ) ),
              tree( SECTION,
                  tree( TITLE, tree( WORD, "Section" ), tree( WORD, "two-two" ) ),
                  tree( STYLE, tree( WORD, "section-two-two-style" ) ),
                  tree( PARAGRAPH_PLAIN, tree( WORD, "w40" ) )
              )
          )
      ),
      bookTree
    ) ;

  }

// =======
// Fixture
// =======

  private static final String SIMPLE_BOOK = TestResources.STRUCTURE_5 ;
  private static final String STYLISH_BOOK = TestResources.STRUCTURE_6 ;
  private static final String PART_1 = TestResources.SMALL_1 ;
  private static final String PART_2 = TestResources.SMALL_2 ;
  private static final String PART_3 = TestResources.SMALL_3 ;
  private static final String PART_4 = TestResources.SMALL_4 ;
  private Book simpleBook;
  private Book stylishBook;

  @Before
  public void setUp() throws IOException {
    final String testName = ClassUtils.getShortClassName( getClass() );
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    final File book1Directory = scratchDirectoryFixture.getBook4Directory();
    ResourceTools.copyResourceToFile( getClass(), SIMPLE_BOOK, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), STYLISH_BOOK, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), PART_1, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), PART_2, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), PART_3, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), PART_4, book1Directory ) ;
    simpleBook = new Book( testName, new File( book1Directory, SIMPLE_BOOK ) );
    stylishBook = new Book( testName, new File( book1Directory, STYLISH_BOOK ) );
  }

}
