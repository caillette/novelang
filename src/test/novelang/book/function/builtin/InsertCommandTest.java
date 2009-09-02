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
package novelang.book.function.builtin;

import novelang.ScratchDirectoryFixture;
import novelang.TestResourceTools;
import novelang.TestResources;
import novelang.book.CommandExecutionContext;
import novelang.book.function.CommandParameterException;
import novelang.common.Location;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

import org.apache.commons.lang.ClassUtils;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.NameAwareTestClassRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Tests for {@link InsertCommand}.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class InsertCommandTest {

  @Test
  public void goodFileUrl() throws CommandParameterException, MalformedURLException {

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        oneWordFile.toURL().toExternalForm(),
        false,
        false,
        0,
        null
    ) ;

    final CommandExecutionContext initialContext =
        new CommandExecutionContext( goodContentDirectory ).
        update( new SimpleTree( BOOK.name() ) )
    ;

    final CommandExecutionContext result = insertCommand.evaluate(
        initialContext
    ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK, tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "oneword" ) ) ),
        result.getDocumentTree()
    ) ;

  }

  @Test
  public void createChapterForSinglePart() 
      throws CommandParameterException, MalformedURLException
  {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        noChapterFile.toURL().toExternalForm(),
        false,
        true,
        0,
        null
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext initialContext =
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ;
    final CommandExecutionContext result = insertCommand.evaluate( initialContext ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;

    final SyntacticTree documentTree = result.getDocumentTree();
    assertNotNull( documentTree ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree( BOOK,
            tree( _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "no-chapter" ) ),
                tree( _LEVEL,
                    tree( LEVEL_TITLE, tree( WORD_, "Section" ) ),
                    tree( NodeKind.PARAGRAPH_REGULAR, tree( WORD_, "paragraph" ) )
                )
            )
        ),
        documentTree
    ) ;

  }

  @Test
  public void addStyle() throws CommandParameterException, MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        oneWordFile.toURL().toExternalForm(), 
        false,
        false,
        0,
        "myStyle"
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree(
            BOOK,
            tree(
                NodeKind.PARAGRAPH_REGULAR,
                tree( _STYLE, "myStyle" ),
                tree( WORD_, "oneword" )
            )
        ),
        result.getDocumentTree() 
    ) ;

  }

  @Test
  public void recurseWithAllValidParts() throws CommandParameterException, MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        goodContentDirectory.toURL().toExternalForm(),
        true,
        false,
        0,
        null
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }

  @Test
  public void recurseWithSomeBrokenPart() throws CommandParameterException, MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,        
        brokenContentDirectory.toURL().toExternalForm(),
        true,
        false,
        0,
        null
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( brokenContentDirectory ).update( initialTree ) ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }



// =======
// Fixture
// =======

  private static final Location NULL_LOCATION = new Location( "", -1, -1 ) ;

  private static final String ONE_WORD_FILENAME = TestResources.ONE_WORD_ABSOLUTEFILENAME;
  private static final String NOCHAPTER_FILENAME = TestResources.NO_CHAPTER ;
  private static final String BROKEN_FILENAME = TestResources.BROKEN_CANNOTPARSE;

  private static final String CONTENT_GOOD_DIRNAME = "good" ;
  private static final String CONTENT_BROKEN_DIRNAME = "broken" ;

  private File oneWordFile ;
  private File noChapterFile;
  private File goodContentDirectory;
  private File brokenContentDirectory;

  @Before
  public void setUp() throws Exception {

    final String testName = NameAwareTestClassRunner.getTestName();    
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    File scratchDirectory = scratchDirectoryFixture.getTestScratchDirectory();

    goodContentDirectory = new File( scratchDirectory, CONTENT_GOOD_DIRNAME ) ;

    oneWordFile = TestResourceTools.copyResourceToDirectory(
        getClass(),
        ONE_WORD_FILENAME,
        goodContentDirectory
    ) ;

    noChapterFile = TestResourceTools.copyResourceToDirectory(
        getClass(),
        NOCHAPTER_FILENAME,
        goodContentDirectory
    ) ;

    brokenContentDirectory = new File( scratchDirectory, CONTENT_BROKEN_DIRNAME ) ;
    TestResourceTools.copyResourceToDirectory(
        getClass(),
        BROKEN_FILENAME,
        brokenContentDirectory
    ) ;
  }

}
