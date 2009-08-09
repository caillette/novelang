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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import novelang.system.LogFactory;
import novelang.system.Log;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import novelang.book.CommandExecutionContext;
import novelang.book.function.AbstractFunctionCall;
import novelang.book.function.FunctionDefinition;
import static novelang.book.function.FunctionTools.verify;
import novelang.book.function.CommandParameterException;
import novelang.common.FileTools;
import novelang.common.Location;
import novelang.common.Problem;
import novelang.common.SimpleTree;
import novelang.common.StructureKind;
import novelang.common.SyntacticTree;
import novelang.common.Renderable;
import novelang.common.tree.TreeTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.part.Part;

/**
 * Inserts one or more Part files.
 * <p>
 * Syntax 1: insert given file.
 * <pre>
 * insert file:&lt;path-to-partfile&gt;
 * </pre>
 * Syntax 2: insert all Part files contained by given directory.
 * The <code>${@value #OPTION_RECURSE}</code> option causes a scan of subdirectories, if any.
 * The <code>${@value #OPTION_CREATECHAPTER}</code> option creates chapters with the short name of
 * originating file.
 * <pre>
 * insert file:&lt;path-to-partfiles&gt; [${@value #OPTION_RECURSE}] [${@value #OPTION_CREATECHAPTER}]
 * </pre>
 *
 * @deprecated
 * @author Laurent Caillette
 */
public class InsertFunction implements FunctionDefinition {

  private static final Log LOG = LogFactory.getLog( InsertFunction.class ) ;

  private static final String OPTION_RECURSE = "recurse" ;
  private static final String OPTION_CREATECHAPTER = "createlevel" ;
  private static final Set< String > SUPPORTED_OPTIONS = ImmutableSet.of(
      OPTION_RECURSE,
      OPTION_CREATECHAPTER
  ) ;

  private static final String ASSIGNMENT_STYLE = "style" ;

  private static final Set< String > SUPPORTED_ASSIGNMENTS = ImmutableSet.of(
      ASSIGNMENT_STYLE
  ) ;

  public String getName() {
    return "insert" ;
  }

  public AbstractFunctionCall instantiate(
      Location location,
      SyntacticTree functionCall
  ) throws CommandParameterException {

    verify( "No primary argument", true, functionCall.getChildCount() >= 2 ) ;
    final SyntacticTree primaryArgument = functionCall.getChildAt( 1 ) ;
//    verify( "No value for primary argument",
//        VALUED_ARGUMENT_PRIMARY_.name(), primaryArgument.getText() ) ;
    verify( "No value for primary argument", 1, primaryArgument.getChildCount() ) ;
    final SyntacticTree url = primaryArgument.getChildAt( 0 ) ;
    verify( URL_LITERAL.name(), url.getText() ) ;
    final String urlAsString = url.getChildAt( 0 ).getText() ;

    final List< String > otherArguments = Lists.newArrayList() ;
    final Map< String, String > assignments = Maps.newHashMap() ;
    for( int i = 2 ; i < functionCall.getChildCount() ; i++ ) {
      final SyntacticTree otherArgumentTree = functionCall.getChildAt( i ) ;
//      if( NodeKind.VALUED_ARGUMENT_FLAG_.name().equals( otherArgumentTree.getText() ) ) {
//        final String option = otherArgumentTree.getChildAt( 0 ).getText();
//        verify( "Not a supported option: " + option, true, SUPPORTED_OPTIONS.contains( option ) ) ;
//        otherArguments.add( option ) ;
//      }

//      if( NodeKind.VALUED_ARGUMENT_ASSIGNMENT_.name().equals( otherArgumentTree.getText() ) ) {
//        final String assignmentKey = otherArgumentTree.getChildAt( 0 ).getText();
//        verify(
//            "Not a supported assignment: " + assignmentKey,
//            true,
//            SUPPORTED_ASSIGNMENTS.contains( assignmentKey )
//        ) ;
// //        No check as it was done by the parser.
//        final String assignmentValue = otherArgumentTree.getChildAt( 1 ).getText();
//        assignments.put( assignmentKey, assignmentValue ) ;
//      }
    }

    LOG.debug( "Parsed function '%s' url='%s'", getName(), urlAsString ) ;
    if( otherArguments.size() > 0 ) {
      for( String ancillaryArgument : otherArguments ) {
        LOG.debug( "  Ancillary argument='%s'", ancillaryArgument ) ;
      }
    }
    if( assignments.size() > 0 ) {
      for( String ancillaryArgumentKey : assignments.keySet() ) {
        LOG.debug( "  Assignment:'%s'<-'%s'",
            ancillaryArgumentKey,
            assignments.get( ancillaryArgumentKey )
        ) ;
      }
    }

    return new AbstractFunctionCall( location ) {
      public Result evaluate( CommandExecutionContext environment, Treepath< SyntacticTree > book ) {
        return InsertFunction.evaluate(
            environment,
            book,
            urlAsString,
            ImmutableSet.copyOf( otherArguments ),
            ImmutableMap.copyOf( assignments )
        ) ;
      }

      public CommandExecutionContext evaluate( CommandExecutionContext context ) {
        throw new UnsupportedOperationException( "evaluate" ) ;
      }
    } ;
  }

  private static AbstractFunctionCall.Result evaluate(
      CommandExecutionContext environment,
      Treepath< SyntacticTree > book,
      String urlAsString,
      Set< String > options,
      Map< String, String > assignments
  ) {
    final String fileName = urlAsString.substring( "file:".length() ) ; 
    final File insertedFile = fileName.startsWith( "/" ) ?
        new File( fileName ) :
        new File( environment.getBookDirectory(), fileName )
    ;
    if( options.contains( OPTION_RECURSE ) ) {
      return evaluateRecursive(
          environment,
          book,
          insertedFile,
          options.contains( OPTION_CREATECHAPTER ),
          assignments.get( ASSIGNMENT_STYLE )
      ) ;
    } else {
      return evaluateFlat(
          environment,
          book,
          insertedFile,
          options.contains( OPTION_CREATECHAPTER ),
          assignments.get( ASSIGNMENT_STYLE )
      ) ;
    }
  }

  @Deprecated
  private static AbstractFunctionCall.Result evaluateFlat(
      CommandExecutionContext environment,
      Treepath< SyntacticTree > book,
      File insertedFile,
      boolean createChapter,
      String styleName
  ) {
    final Part rawPart;
    try {
      rawPart = new Part( 
          insertedFile,
          environment.getSourceCharset(),
          environment.getRenderingCharset()
      ) ;
    } catch( MalformedURLException e ) {
      return new AbstractFunctionCall.Result(
          environment, book, Lists.newArrayList( Problem.createProblem( e ) ) ) ;
    }
    final Renderable partWithRelocation =
        rawPart.relocateResourcePaths( environment.getBaseDirectory() ) ;

    final SyntacticTree partTree = partWithRelocation.getDocumentTree() ;

    final SyntacticTree styleTree = createStyleTree( styleName ) ;

    if( null != partTree ) {
      if( createChapter ) {
          book = createChapterFromPartFilename( book, insertedFile, partTree, styleTree ) ;
      } else {
        for( SyntacticTree partChild : partTree.getChildren() ) {
          if( styleTree != null ) {
            partChild = TreeTools.addFirst( partChild, styleTree ) ;
          }
          book = TreepathTools.addChildLast( Treepath.create( book.getTreeAtStart() ), partChild ) ;
        }
      }
    }

    return new AbstractFunctionCall.Result( environment, book, rawPart.getProblems() ) ;
  }

  private static SyntacticTree createStyleTree( String styleName ) {
    if( null == styleName ) {
      return null ;
    } else {
      return new SimpleTree( NodeKind._STYLE.name(), new SimpleTree( styleName ) ) ;
    }
  }

  private static AbstractFunctionCall.Result evaluateRecursive(
      CommandExecutionContext environment,
      Treepath< SyntacticTree > book,
      File insertedFile,
      boolean createChapter,
      String styleName
  ) {
    final List< Problem > problems = Lists.newArrayList() ;
    final SyntacticTree styleTree = createStyleTree( styleName ) ;

    try {
      final Iterable< File > partFiles = scanPartFiles( insertedFile ) ;
      for( File partFile : partFiles ) {
        Part part = null ;
        try {
          part = new Part( 
              partFile, 
              environment.getSourceCharset(), 
              environment.getRenderingCharset() 
          ) ;
          Iterables.addAll( problems, part.getProblems() ) ;
        } catch( MalformedURLException e ) {
          problems.add( Problem.createProblem( e ) ) ;
        }
        if( null != part && null != part.getDocumentTree() ) {
          final SyntacticTree partTree =
              part.relocateResourcePaths( environment.getBaseDirectory() ).getDocumentTree() ;

          if( createChapter ) {
            book = createChapterFromPartFilename( book, partFile, partTree, styleTree );

          } else {
            for( SyntacticTree partChild : partTree.getChildren() ) {
              if( styleTree != null ) {
                partChild = TreeTools.addFirst( partChild, styleTree ) ;
              }
              final Treepath< SyntacticTree > updatedBook =
                  TreepathTools.addChildLast( book, partChild ) ;
              book = Treepath.create( updatedBook.getTreeAtStart() ) ;
            }
          }


        }
      }
    } catch( CommandParameterException e ) {
      problems.add( Problem.createProblem( e ) ) ;
    }

    return new AbstractFunctionCall.Result( environment, book, problems ) ;
  }

  private static Treepath< SyntacticTree > createChapterFromPartFilename(
      Treepath<SyntacticTree > book,
      File partFile,
      SyntacticTree partTree,
      SyntacticTree styleTree
  ) {
    final SyntacticTree word = new SimpleTree(
        WORD_.name(),
        new SimpleTree( FilenameUtils.getBaseName( partFile.getName() ) )
    ) ;
    final SyntacticTree title = new SimpleTree( NodeKind.LEVEL_TITLE.name(), word ) ;

    SyntacticTree chapterTree = TreeTools.addFirst(
        new SimpleTree( NodeKind._LEVEL.name(), partTree.getChildren() ),
        title
    ) ;

    if( styleTree != null ) {
      chapterTree = TreeTools.addFirst( chapterTree, styleTree ) ;
    }
    final Treepath< SyntacticTree > updatedBook = TreepathTools.addChildLast( book, chapterTree ) ;
    final SyntacticTree start = updatedBook.getTreeAtStart() ;
    book = Treepath.create( start ) ;
    return book;
  }

  private static Iterable< File > scanPartFiles( File directory )
      throws CommandParameterException
  {
    if( directory.isDirectory() ) {
      final List< File > files = Ordering.from( FileTools.ABSOLUTEPATH_COMPARATOR ).sortedCopy(
          FileTools.scanFiles( directory, StructureKind.PART.getFileExtensions() )
      );

      if( LOG.isDebugEnabled() ) {
        StringBuffer buffer = new StringBuffer(
            "Scan of '" + directory.getAbsolutePath() + "' found those files:" ) ;
        for( File file : files ) {
          try {
            buffer.append( "\n  " ).append( file.getCanonicalPath() ) ;
          } catch( IOException e ) {
            throw new RuntimeException( e ) ;
          }
        }
        LOG.debug( buffer.toString() ) ;
      }

      return files ;

    } else {
      throw new CommandParameterException(
          "Not a directory: '" + directory.getAbsolutePath() + "'" ) ;
    }
  }


}
