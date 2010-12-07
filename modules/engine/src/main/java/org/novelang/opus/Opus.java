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
package org.novelang.opus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.novelang.common.AbstractSourceReader;
import org.novelang.common.FileTools;
import org.novelang.common.Problem;
import org.novelang.common.SimpleTree;
import org.novelang.common.StylesheetMap;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Statistics;
import org.novelang.common.tree.Treepath;
import org.novelang.designator.Tag;
import org.novelang.opus.function.Command;
import org.novelang.opus.function.CommandFactory;
import org.novelang.opus.function.CommandParameterException;
import org.novelang.parser.GenericParser;
import org.novelang.parser.NodeKind;
import org.novelang.parser.antlr.DelegatingBookParser;
import org.novelang.treemangling.LevelMangler;
import org.novelang.treemangling.ListMangler;
import org.novelang.treemangling.SeparatorsMangler;
import org.novelang.treemangling.TagFilter;
import org.novelang.treemangling.TagMangler;
import org.novelang.treemangling.designator.DesignatorTools;
import org.novelang.treemangling.designator.IdentifierCollisions;

/**
 * Reads a Opus file, processes functions and builds a Tree with inclusions and so on.
 *
 * @author Laurent Caillette
 */
public class Opus extends AbstractSourceReader {

  private final org.novelang.opus.CommandExecutionContext environment ;


  public Opus(
      final File baseDirectory,
      final File bookFile,
      final ExecutorService executorService,
      final Charset suggestedSourceCharset,
      final Charset suggestedRenderingCharset,
      final Set< Tag > restrictingTags
  ) throws IOException {
    this(
        baseDirectory,
        bookFile.getParentFile(),
        executorService,
        FileUtils.readFileToString( bookFile ),  // TODO take care of encoding, Unicode et al.
        suggestedSourceCharset,
        suggestedRenderingCharset,
        restrictingTags
    ) ;
  }


  public Opus(
      final File baseDirectory,
      final File bookDirectory,
      final ExecutorService executorService,
      final String content,
      final Charset suggestedSourceCharset,
      final Charset defaultRenderingCharset,
      final Set< Tag > tagRestrictions
  ) {
    super( suggestedSourceCharset, defaultRenderingCharset ) ;

    Preconditions.checkArgument(
        bookDirectory.isDirectory(),
        "Should be a directory: '%s'",
        bookDirectory
    ) ;
    Preconditions.checkArgument(
        FileTools.isParentOfOrSameAs( baseDirectory, bookDirectory ),
        "Base directory '%s' shoud be parent of book directory '%s'",
        baseDirectory,
        bookDirectory
    ) ;

    CommandExecutionContext currentEnvironment =
        new CommandExecutionContext( baseDirectory, bookDirectory, executorService ) ;


    final SyntacticTree tree = parse( content ) ;
    if( tree != null ) {
      final SyntacticTree rawTree = SeparatorsMangler.removeSeparators( tree ) ;

      final Iterable< Command > commands = createCommands( new CommandFactory(), rawTree ) ;
      currentEnvironment = callCommands(
          currentEnvironment.update( new SimpleTree( NodeKind.OPUS ) ),
          commands
      ) ;
      Treepath< SyntacticTree > rehierarchized =
          Treepath.create( currentEnvironment.getDocumentTree() ) ;

      Statistics.logStatistics( rehierarchized.getTreeAtStart() ) ;

      // TODO: output colliding explicit identifiers into resulting tree.
      final IdentifierCollisions collisions = DesignatorTools.findCollisions( rehierarchized ) ;
      rehierarchized = DesignatorTools.removeCollidingImplicitIdentifiers(
          collisions, rehierarchized ) ;
      rehierarchized = DesignatorTools.tagCollidingExplicitIdentifiers(
          collisions, rehierarchized ) ;

      final Set< Tag > tagset = TagMangler.findExplicitTags( rehierarchized.getTreeAtEnd() ) ;
      rehierarchized = ListMangler.rehierarchizeLists( rehierarchized ) ;
      rehierarchized = LevelMangler.rehierarchizeLevels( rehierarchized ) ;
      rehierarchized = TagFilter.filter( rehierarchized, tagRestrictions ) ;
      rehierarchized = TagMangler.promote( rehierarchized, tagset ) ;

      currentEnvironment = currentEnvironment.update( rehierarchized.getTreeAtStart() ) ;

      if( hasProblem() ) {
        currentEnvironment =
            currentEnvironment.update( rehierarchized.getTreeAtStart() ) ;
      } else {
        currentEnvironment =
            currentEnvironment.update( addMetadata( rehierarchized.getTreeAtEnd(), tagset ) ) ;
      }

    }
    this.environment = currentEnvironment ;
    collect( environment.getProblems() ) ;
  }

  @Override
  protected GenericParser createParser( final String content ) {
    return new DelegatingBookParser( content, this ) ;
  }

  @Override
  public SyntacticTree getDocumentTree() {
    return environment.getDocumentTree() ;
  }

  @Override
  public StylesheetMap getCustomStylesheetMap() {
    return environment.getCustomStylesheets() ;
  }

  private Iterable< Command > createCommands(
      final CommandFactory commandFactory,
      final SyntacticTree rawTree
  ) {
    final List< Command > commands = Lists.newArrayList() ;
    for( final SyntacticTree syntacticTree : rawTree.getChildren() ) {
      try {
        final Command command = commandFactory.createFunctionCall( syntacticTree ) ;
        commands.add( command ) ;
      } catch( CommandParameterException e ) {
        collect( Problem.createProblem( e ) ) ;
      }
    }
    return ImmutableList.copyOf( commands ) ;
  }

  private static CommandExecutionContext callCommands(
      CommandExecutionContext context,
      final Iterable< Command > commands
  ) {
    for( final Command command : commands ) {
      context = command.evaluate( context ) ;
    }
    return context ;
  }


}
