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
package novelang.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.nio.charset.Charset;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;

import novelang.book.function.CommandParameterException;
import novelang.book.function.Command;
import novelang.book.function.CommandFactory;
import novelang.common.AbstractSourceReader;
import novelang.common.Problem;
import novelang.common.SimpleTree;
import novelang.common.StylesheetMap;
import novelang.common.SyntacticTree;
import novelang.common.FileTools;
import novelang.common.metadata.MetadataHelper;
import novelang.common.tree.Treepath;
import novelang.designator.Tag;
import novelang.treemangling.LevelMangler;
import novelang.treemangling.SeparatorsMangler;
import novelang.treemangling.TagFilter;
import novelang.treemangling.ListMangler;
import novelang.parser.NodeKind;
import novelang.parser.GenericParser;
import novelang.parser.antlr.DelegatingBookParser;
import novelang.system.DefaultCharset;

/**
 * Reads a Book file, processes functions and builds a Tree with inclusions and so on.
 *
 * @author Laurent Caillette
 */
public class Book extends AbstractSourceReader {

  private final CommandExecutionContext environment ;


  public Book(
      final File baseDirectory,
      final File bookFile,
      final Charset suggestedSourceCharset,
      final Charset suggestedRenderingCharset,
      final Set< Tag > restrictingTags
  ) throws IOException {
    this(
        baseDirectory,
        bookFile.getParentFile(),
        IOUtils.toString( new FileInputStream( bookFile ) ),
        suggestedSourceCharset,
        suggestedRenderingCharset,
        restrictingTags
    ) ;
  }


  public Book(
      final File baseDirectory,
      final File bookDirectory,
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
        new CommandExecutionContext( baseDirectory, bookDirectory ) ;


    final SyntacticTree tree = parse( content ) ;
    if( tree != null ) {
      final SyntacticTree rawTree = SeparatorsMangler.removeSeparators( tree ) ;

      final Iterable< Command > commands = createCommands( new CommandFactory(), rawTree ) ;
      currentEnvironment = callCommands(
          currentEnvironment.update( new SimpleTree( NodeKind.BOOK ) ),
          commands
      ) ;
      Treepath< SyntacticTree > rehierarchized =
          Treepath.create( currentEnvironment.getDocumentTree() ) ;
      final Set< Tag > tagset = MetadataHelper.findTags( rehierarchized.getTreeAtEnd() ) ;
      rehierarchized = ListMangler.rehierarchizeLists( rehierarchized ) ;
      rehierarchized = LevelMangler.rehierarchizeLevels( rehierarchized ) ;
      rehierarchized = TagFilter.filter( rehierarchized, tagRestrictions ) ;

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

  protected GenericParser createParser( final String content ) {
    return new DelegatingBookParser( content, this ) ;
  }

  public SyntacticTree getDocumentTree() {
    return environment.getDocumentTree() ;
  }

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

  private CommandExecutionContext callCommands(
      CommandExecutionContext context,
      final Iterable< Command > commands
  ) {
    for( final Command command : commands ) {
      context = command.evaluate( context ) ;
    }
    return context ;
  }


}
