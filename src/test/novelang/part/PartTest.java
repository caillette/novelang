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

package novelang.part;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import static novelang.parser.NodeKind.*;
import novelang.common.SyntacticTree;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * @author Laurent Caillette
 */
public class PartTest {


  @Test
  public void loadPartOk() throws IOException {
    final Part part = new Part( justSections ) ;
    final SyntacticTree partTree = part.getDocumentTree();
    Assert.assertNotNull( partTree ) ;
    final SyntacticTree expected = tree( PART,
        tree( SECTION,
            tree( TITLE, tree( WORD, "Section1nlp" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p00" ), tree( WORD, "w001" ) )
        ),
        tree( SECTION,
            tree( TITLE, tree( WORD, "section1" ), tree( WORD, "w11" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p10" ), tree( WORD, "w101" ), tree( WORD, "w102" ) )
        )
    ) ;

    TreeFixture.assertEquals( expected, partTree ) ;
    Assert.assertFalse( part.getProblems().iterator().hasNext() ) ;
  }

  @Test
  public void loadPartWithMetadata() throws IOException {
    final Part part = new Part( justSections, true ) ;
    final SyntacticTree partTree = part.getDocumentTree();
    Assert.assertNotNull( partTree ) ;
    final SyntacticTree expected = tree( PART,
        tree( _META,
            tree( _WORD_COUNT, "8" )
        ),        
        tree( SECTION,
            tree( TITLE, tree( WORD, "Section1nlp" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p00" ), tree( WORD, "w001" ) )
        ),
        tree( SECTION,
            tree( TITLE, tree( WORD, "section1" ), tree( WORD, "w11" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p10" ), tree( WORD, "w101" ), tree( WORD, "w102" ) )
        )
    ) ;

    TreeFixture.assertEquals( expected, partTree ) ;
    Assert.assertFalse( part.getProblems().iterator().hasNext() ) ;
  }

  @Test
  public void loadSimpleStructure() throws IOException {
    final Part part = new Part( simpleStructureFile ) ;
    final SyntacticTree partTree = part.getDocumentTree();
    Assert.assertNotNull( partTree ) ;
    final SyntacticTree expected = tree( PART,
        tree( CHAPTER,
            tree( TITLE, tree( WORD, "Chapter-0" ) ),
            tree( SECTION,
                tree( TITLE, tree( WORD, "Section-0-0" ) ),
                tree( PARAGRAPH_PLAIN, tree( WORD, "Paragraph-0-0-0" ) ),
                tree( PARAGRAPH_PLAIN, tree( WORD, "Paragraph-0-0-1" ) )
            ),
            tree( SECTION,
                tree( TITLE, tree( WORD, "Section-0-1" ) ),
                tree( PARAGRAPH_PLAIN, tree( WORD, "Paragraph-0-1-0" ) )
            )
        ),
        tree( CHAPTER,
            tree( TITLE, tree( WORD, "Chapter-1" ) ),
            tree( SECTION,
                tree( TITLE, tree( WORD, "Section-1-0" ) ),
                tree( PARAGRAPH_PLAIN, tree( WORD, "Paragraph-1-0-0" ) ),
                tree( PARAGRAPH_PLAIN, tree( WORD, "Paragraph-1-0-1" ) )
            ),
            tree( SECTION,
                tree( TITLE, tree( WORD, "Section-1-1" ) ),
                tree( PARAGRAPH_PLAIN, tree( WORD, "Paragraph-1-1-0" ) )
            )
        )
    ) ;

    TreeFixture.assertEquals( expected, partTree ) ;
    Assert.assertFalse( part.getProblems().iterator().hasNext() ) ;

  }

  @Test
  public void loadMessyIdentifiers() throws IOException {
    final Part part = new Part( messyIdentifiersFile ) ;
    part.getIdentifiers() ;    
  }

  @Test
  public void findIdentifiersOk() throws IOException {
    final Part part = new Part( justSections ) ;
    part.getIdentifiers() ;
  }



// =======
// Fixture
// =======

  private File justSections;
  private File messyIdentifiersFile ;
  private File simpleStructureFile ;

  @Before
  public void setUp() throws IOException {
    final String testName = ClassUtils.getShortClassName( getClass() );
    final File scratchDirectory = new ScratchDirectoryFixture( testName ).
        getTestScratchDirectory() ;

    justSections = TestResourceTools.copyResourceToDirectory(
        getClass(),
        TestResources.JUST_SECTIONS,
        scratchDirectory
    ) ;


    messyIdentifiersFile = TestResourceTools.copyResourceToDirectory(
        getClass(),
        TestResources.MESSY_IDENTIFIERS,
        scratchDirectory
    ) ;

    simpleStructureFile = TestResourceTools.copyResourceToDirectory(
        getClass(),
        TestResources.SIMPLE_STRUCTURE,
        scratchDirectory
    ) ;

  }

}
