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
package novelang.opus.function.builtin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.google.common.collect.ImmutableList;
import novelang.ResourceTools;
import novelang.ResourcesForTests;
import novelang.common.Location;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.tree.Treepath;
import novelang.designator.FragmentIdentifier;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.novella.Novella;
import novelang.opus.CommandExecutionContext;
import novelang.opus.function.CommandParameterException;
import novelang.opus.function.builtin.insert.LevelHead;
import novelang.outfit.DefaultCharset;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Test;
import org.junit.runner.RunWith;
import novelang.testing.junit.NameAwareTestClassRunner;

import static novelang.ResourcesForTests.initialize;
import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.assertEqualsNoSeparators;
import static novelang.parser.antlr.TreeFixture.tree;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link InsertCommand}.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
@RunWith( value = NameAwareTestClassRunner.class )
public class InsertCommandTest {

  @Test
  public void goodFileUrl() throws CommandParameterException, MalformedURLException {

    final File oneWordFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        oneWordFile.toURI().toURL().toExternalForm(),
        false,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final CommandExecutionContext initialContext =
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( new SimpleTree( OPUS ) )
    ;

    final CommandExecutionContext result = insertCommand.evaluate(
        initialContext
    ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    assertEqualsNoSeparators(
        tree( OPUS, tree( PARAGRAPH_REGULAR, tree( WORD_, "oneword" ) ) ),
        result.getDocumentTree()
    ) ;

  }
  @Test
  public void missingPartFile() throws CommandParameterException, MalformedURLException {

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        new File( resourceInstaller.getTargetDirectory(), "doesNotExist" ).
            toURI().toURL().toExternalForm(),
        false,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final CommandExecutionContext initialContext =
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( new SimpleTree( OPUS ) )
    ;

    final CommandExecutionContext result = insertCommand.evaluate(
        initialContext
    ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;


  }

  @Test
  public void createChapterForSinglePart() 
      throws CommandParameterException, MalformedURLException
  {
    final File noChapterFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_NO_CHAPTER ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        noChapterFile.toURI().toURL().toExternalForm(),
        false,
        null,
        LevelHead.CREATE_LEVEL,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( OPUS ) ;
    final CommandExecutionContext initialContext =
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree ) ;
    final CommandExecutionContext result = insertCommand.evaluate( initialContext ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;

    final SyntacticTree documentTree = result.getDocumentTree();
    assertNotNull( documentTree ) ;

    assertEqualsNoSeparators(
        tree( OPUS,
            tree( _LEVEL,
                tree( LEVEL_TITLE, tree( WORD_, "no-chapter" ) ),
                tree( _LEVEL,
                    tree( _IMPLICIT_IDENTIFIER, "\\\\Section" ),
                    tree( _IMPLICIT_TAG, "Section" ),
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

    final File oneWordFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        oneWordFile.toURI().toURL().toExternalForm(),
        false,
        null,
        null,
        0,
        "myStyle",
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( OPUS ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    assertEqualsNoSeparators(
        tree(
            OPUS,
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
        resourceInstaller.getTargetDirectory().toURI().toURL().toExternalForm(),
        true,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( OPUS ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }

  @Test
  public void recurseWithSomeBrokenPart() throws CommandParameterException, MalformedURLException {
    final File brokenContentDirectory =
        resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_BROKEN_CANNOTPARSE ).getParentFile() ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,        
        brokenContentDirectory.toURI().toURL().toExternalForm(),
        true,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( OPUS ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            brokenContentDirectory,
            ResourceTools.getExecutorService()
        ).update( initialTree ) ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }


  @Test
  public void levelAboveIs1() throws CommandParameterException, MalformedURLException {

    final File oneWordFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        oneWordFile.toURI().toURL().toExternalForm(),
        false,
        null,
        null,
        1,
        null,
        ImmutableList.<FragmentIdentifier>of()
    ) ;

    final SyntacticTree initialTree = tree(
        OPUS,
        tree(
            _LEVEL,
            tree( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS )
        )
    ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;

    assertEqualsNoSeparators(
        tree(
            OPUS,
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
        OPUS,
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
        OPUS,
        tree( PARAGRAPH_REGULAR ),
        tree( _LEVEL, level )
    ) ;
    final Treepath< SyntacticTree > bookTreepath = Treepath.create( book ) ;

    final Treepath< SyntacticTree > gotLevel = callFindLastLevel( bookTreepath, 2 ) ;
    assertEqualsNoSeparators( level, gotLevel.getTreeAtEnd() ) ;
  }


  @Test
  public void noLevelaboveCausesProblem() throws CommandParameterException, MalformedURLException {

    final File oneWordFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,        
        oneWordFile.toURI().toURL().toExternalForm(),
        true,
        null,
        null,
        1,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = new SimpleTree( OPUS ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree ) ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;
    assertNotNull( result.getDocumentTree() ) ;
  }

  @Test
  public void recurseWithLevelabove1() throws CommandParameterException, MalformedURLException {

    resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_ONE_WORD ) ;
    resourceInstaller.copyWithPath( ResourcesForTests.Parts.dir, ResourcesForTests.Parts.NOVELLA_NO_CHAPTER ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        resourceInstaller.getTargetDirectory().toURI().toURL().toExternalForm(),
        true,
        null,
        null,
        1,
        null,
        ImmutableList.< FragmentIdentifier >of()
    ) ;

    final SyntacticTree initialTree = tree(
        OPUS,
        tree( _LEVEL )
    ) ;
    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree ) ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;
    assertEqualsNoSeparators( 
        tree(
            OPUS,
            tree( 
                _LEVEL,
                tree(
                    PARAGRAPH_REGULAR,
                    tree( WORD_, "oneword" )
                ),
                tree( 
                    _LEVEL,
                    tree( _IMPLICIT_IDENTIFIER  , "\\\\Section" ),
                    tree( _IMPLICIT_TAG, "Section" ),
                    tree( LEVEL_TITLE, tree( WORD_, "Section" ) ),
                    tree( PARAGRAPH_REGULAR, tree( WORD_, "paragraph" ) )
                )
            )
        ),
        result.getDocumentTree()
    ) ;
  }

  @Test
  public void useSimpleFragmentIdentifier() throws IOException {

    final File partFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_SOME_IDENTIFIERS_2 ) ;
    LOGGER.info(
        "Loaded Novella \n",
        new Novella( partFile, DefaultCharset.SOURCE,DefaultCharset.RENDERING ).
            getDocumentTree().toStringTree() 
    ) ;
    
    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        partFile.toURI().toURL().toExternalForm(),
        true,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of( new FragmentIdentifier( "level-2-4" ) )
    ) ;

    final SyntacticTree initialTree = tree( OPUS ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree )
    ) ;


    assertEqualsNoSeparators(
        tree(
            OPUS,
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, tree( "\\\\level-2-4" ) ),
                tree( _IMPLICIT_TAG, "L2-4" ),
                tree( LEVEL_TITLE, tree( WORD_, "L2-4" ) ),
                tree( PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-2-4" ) )
            )
        ),
        result.getDocumentTree()
      ) ;

  }


  @Test
  public void identifierWithSingleFileTreatedAsMultiple() throws IOException {

    final File partFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_SOME_IDENTIFIERS_2 ) ;
    LOGGER.info(
        "Loaded Novella \n",
        new Novella( partFile, DefaultCharset.SOURCE,DefaultCharset.RENDERING ).
            getDocumentTree().toStringTree()
    ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        "file:.", 
        true,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of( new FragmentIdentifier( "level-2-4" ) )
    ) ;

    final SyntacticTree initialTree = tree( OPUS ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree )
    ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;

    assertEqualsNoSeparators(
        tree(
            OPUS,
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, tree( "\\\\level-2-4" ) ),
                tree( _IMPLICIT_TAG, "L2-4" ),
                tree( LEVEL_TITLE, tree( WORD_, "L2-4" ) ),
                tree( PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-2-4" ) )
            )
        ),
        result.getDocumentTree()
    ) ;

  }


  @Test
  public void insertIdentifierWithNoHead() throws IOException {

    final File partFile = resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_SOME_IDENTIFIERS_1 ) ;
    LOGGER.info(
        "Loaded Novella \n",
        new Novella( partFile, DefaultCharset.SOURCE,DefaultCharset.RENDERING ).
            getDocumentTree().toStringTree()
    ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        "file:.",
        true,
        null,
        LevelHead.NO_HEAD,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of( new FragmentIdentifier( "level-1-0" ) )
    ) ;

    final SyntacticTree initialTree = tree( OPUS ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree )
    ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;

    assertEqualsNoSeparators(
        tree(
            OPUS,
            tree( PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-1-0" ) )
        ),
        result.getDocumentTree()
    ) ;

  }


  @Test
  public void useIdentifiersAcrossMultipleParts() 
      throws MalformedURLException 
  {
    resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_SOME_IDENTIFIERS_1 ) ;
    resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_SOME_IDENTIFIERS_2 ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        "file:.", 
        true,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of(
            new FragmentIdentifier( "level-1-0" ),
            new FragmentIdentifier( "level-2-4" )
        )
    ) ;

    final SyntacticTree initialTree = tree( OPUS ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext( 
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree )
    ) ;

    assertFalse( result.getProblems().iterator().hasNext() ) ;

    assertEqualsNoSeparators(
        tree(
            OPUS,
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, tree( "\\\\level-1-0" ) ),
                tree( _IMPLICIT_TAG, "L1-0" ),
                tree( LEVEL_TITLE, tree( WORD_, "L1-0" ) ),
                tree( PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-1-0" ) )
            ),
            tree(
                _LEVEL,
                tree( _EXPLICIT_IDENTIFIER, tree( "\\\\level-2-4" ) ),
                tree( _IMPLICIT_TAG, "L2-4" ),
                tree( LEVEL_TITLE, tree( WORD_, "L2-4" ) ),
                tree( PARAGRAPH_REGULAR, tree( WORD_, "Paragraph-2-4" ) )
            )
        ),
        result.getDocumentTree()
    ) ;

  }

  @Test
  public void detectIdentifierCollisionThroughMultipleParts() 
      throws MalformedURLException 
  {
    resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_SOME_IDENTIFIERS_2 ) ;
    resourceInstaller.copy( ResourcesForTests.Parts.NOVELLA_MANY_IDENTIFIERS ) ;

    final InsertCommand insertCommand = new InsertCommand(
        NULL_LOCATION,
        "file:.", 
        true,
        null,
        null,
        0,
        null,
        ImmutableList.< FragmentIdentifier >of( new FragmentIdentifier( "level-2-4" ) )
    ) ;

    final SyntacticTree initialTree = tree( OPUS ) ;

    final CommandExecutionContext result = insertCommand.evaluate(
        new CommandExecutionContext(
            resourceInstaller.getTargetDirectory(),
            ResourceTools.getExecutorService()
        ).update( initialTree )
    ) ;

    assertTrue( result.getProblems().iterator().hasNext() ) ;
    
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( InsertCommandTest.class );
  
  private static final Location NULL_LOCATION = new Location( "", -1, -1 ) ;

  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;


  static {
    initialize() ;
  }


  private static Treepath< SyntacticTree > callFindLastLevel(
      final Treepath< SyntacticTree > bookTreepath,
      final int levelAbove
  ) {
    return Reflection.staticMethod( "findLastLevel" ).
        withReturnType( new TypeRef< Treepath< SyntacticTree > >() {} ).
        withParameterTypes( Treepath.class, Integer.TYPE ).
        in( InsertCommand.class ).
        invoke( bookTreepath, levelAbove );
  }

}
