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

import novelang.ScratchDirectory;
import novelang.TestResourceTree;
import static novelang.TestResourceTree.initialize;
import novelang.part.FragmentIdentifier;
import novelang.book.CommandExecutionContext;
import novelang.book.function.CommandParameterException;
import novelang.common.Location;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.Relocator;
import novelang.common.tree.Treepath;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.assertEqualsNoSeparators;
import static novelang.parser.antlr.TreeFixture.tree;

import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.File;
import java.net.MalformedURLException;

import com.google.common.collect.ImmutableList;

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
        oneWordFile.toURI().toURL().toExternalForm(),
        false,
        null,
        false,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
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

    assertEqualsNoSeparators(
        tree( BOOK, tree( PARAGRAPH_REGULAR, tree( WORD_, "oneword" ) ) ),
        result.getDocumentTree()
    ) ;

  }

  @Test
  public void createChapterForSinglePart() 
      throws CommandParameterException, MalformedURLException
  {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        noChapterFile.toURI().toURL().toExternalForm(),
        false,
        null,
        true,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext initialContext =
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ;
    final CommandExecutionContext result = insertCommand.evaluate( initialContext ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;

    final SyntacticTree documentTree = result.getDocumentTree();
    assertNotNull( documentTree ) ;

    assertEqualsNoSeparators(
        tree( BOOK,
            tree( _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "no-chapter" ) ),
                tree( _LEVEL,
                    tree( LEVEL_TITLE, tree( WORD_, "Section" ) ),
                    tree( PARAGRAPH_REGULAR, tree( WORD_, "paragraph" ) )
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
        oneWordFile.toURI().toURL().toExternalForm(),
        false,
        null,
        false,
        0,
        "myStyle",
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    assertEqualsNoSeparators(
        tree(
            BOOK,
            tree(
                PARAGRAPH_REGULAR,
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
        goodContentDirectory.toURI().toURL().toExternalForm(),
        true,
        null,
        false,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
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
        brokenContentDirectory.toURI().toURL().toExternalForm(),
        true,
        null,
        false,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( brokenContentDirectory ).update( initialTree ) ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }


  @Test
  public void levelAboveIs1() throws CommandParameterException, MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        oneWordFile.toURI().toURL().toExternalForm(),
        false,
        null,
        false,
        1,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = tree(
        BOOK,
        tree(
            _LEVEL,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        )
    ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    assertEqualsNoSeparators(
        tree(
            BOOK,
            tree(
                _LEVEL,
                tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ),
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "oneword" )
                )
            )
        ),
        result.getDocumentTree()
    ) ;

  }

  @Test
  public void findLastLevel1() {

    final SyntacticTree level = tree(
        _LEVEL,
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;
    final SyntacticTree book = tree(
        BOOK,
        tree( PARAGRAPH_REGULAR ),
        level
    ) ;
    final Treepath< SyntacticTree > bookTreepath = Treepath.create( book ) ;

    final Treepath< SyntacticTree > gotLevel = callFindLastLevel( bookTreepath, 1 ) ;
    assertEqualsNoSeparators( level, gotLevel.getTreeAtEnd() ) ;
  }


  @Test
  public void findLastLevel2() {

    final SyntacticTree level = tree(
        _LEVEL,
        tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
    ) ;
    final SyntacticTree book = tree(
        BOOK,
        tree( PARAGRAPH_REGULAR ),
        tree( _LEVEL, level )
    ) ;
    final Treepath< SyntacticTree > bookTreepath = Treepath.create( book ) ;

    final Treepath< SyntacticTree > gotLevel = callFindLastLevel( bookTreepath, 2 ) ;
    assertEqualsNoSeparators( level, gotLevel.getTreeAtEnd() ) ;
  }


  @Test
  public void noLevelaboveCausesProblem() throws CommandParameterException, MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,        
        oneWordFile.toURI().toURL().toExternalForm(),
        true,
        null,
        false,
        1,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( BOOK.name() ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }

  @Test
  public void recurseWithLevelabove1() throws CommandParameterException, MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        goodContentDirectory.toURI().toURL().toExternalForm(),
        true,
        null,
        false,
        1,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = tree(
        BOOK,
        tree( _LEVEL )
    ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertEqualsNoSeparators( 
        tree( 
            BOOK, 
            tree( 
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "oneword" )
                ),
                tree( 
                    _LEVEL,
                    tree( LEVEL_TITLE, tree( WORD_, "Section" ) ),
                    tree( PARAGRAPH_REGULAR, tree( WORD_, "paragraph" ) )
                )
            )
        ),
        result.getDocumentTree()
    ) ;
  }

  @Test  @Ignore // TODO create the part with the identifier.
  public void useSimpleFragmentIdentifier() throws MalformedURLException {
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        goodContentDirectory.toURI().toURL().toExternalForm(),
        true,
        null,
        false,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of( new FragmentIdentifier( "x" ) )
    ) ;

    final SyntacticTree initialTree = tree( BOOK ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( goodContentDirectory ).update( initialTree ) ) ;


    assertEqualsNoSeparators(
        tree(
            BOOK,
            tree(
                _LEVEL,
                tree( ABSOLUTE_IDENTIFIER, tree( "x" ) ),
                tree( PARAGRAPH_REGULAR, tree( WORD_, "paragraph" ) )
            )
        ),
        result.getDocumentTree()
      ) ;

  }


// =======
// Fixture
// =======

  private static final Location NULL_LOCATION = new Location( "", -1, -1 ) ;

  private File oneWordFile ;
  private File noChapterFile;
  private File goodContentDirectory;
  private File brokenContentDirectory;


  static {
    initialize() ;
  }


  @Before
  public void setUp() throws Exception {

    final String testName = NameAwareTestClassRunner.getTestName();    
    final ScratchDirectory scratch = new ScratchDirectory( testName ) ;

    goodContentDirectory = scratch.getDirectory( "good" ) ;
    final Relocator toGoodContent = new Relocator( goodContentDirectory ) ;

    oneWordFile = toGoodContent.copy( TestResourceTree.Parts.ONE_WORD ) ;

    noChapterFile = toGoodContent.copyWithPath(
            TestResourceTree.Parts.dir,
            TestResourceTree.Parts.NO_CHAPTER
    ) ;

    brokenContentDirectory = scratch.getDirectory( "broken" ) ;

    final Relocator toBrokenContent = new Relocator( brokenContentDirectory ) ;
    toBrokenContent.copy( TestResourceTree.Parts.BROKEN_CANNOTPARSE ) ;
  }


  private static Treepath< SyntacticTree > callFindLastLevel(
      final Treepath<SyntacticTree> bookTreepath,
      final int levelAbove
  ) {
    return Reflection.staticMethod( "findLastLevel" ).
        withReturnType( new TypeRef< Treepath< SyntacticTree > >() {} ).
        withParameterTypes( Treepath.class, Integer.TYPE ).
        in( InsertCommand.class ).
        invoke( bookTreepath, levelAbove );
  }

}
