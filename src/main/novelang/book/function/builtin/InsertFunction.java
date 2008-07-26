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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import novelang.book.Environment;
import novelang.common.Location;
import novelang.common.NodeKind;
import static novelang.common.NodeKind.*;
import novelang.common.Problem;
import novelang.common.SimpleTree;
import novelang.common.StructureKind;
import novelang.common.SyntacticTree;
import novelang.common.tree.TreepathTools;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreeTools;
import novelang.book.function.FunctionCall;
import novelang.book.function.FunctionDefinition;
import static novelang.book.function.FunctionTools.verify;
import novelang.book.function.IllegalFunctionCallException;
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
 * The <code>${@value #OPTION_CREATECHAPTERS}</code> option creates chapters with the short name of
 * originating file.
 * <pre>
 * insert file:&lt;path-to-partfiles&gt; [${@value #OPTION_RECURSE}] [${@value #OPTION_CREATECHAPTERS}]
 * </pre>
 *
 * @author Laurent Caillette
 */
public class InsertFunction implements FunctionDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger( InsertFunction.class ) ;

  private static final String OPTION_RECURSE = "recurse" ;
  private static final String OPTION_CREATECHAPTERS = "createchapters" ;

  private static final Set< String > SUPPORTED_OPTIONS = ImmutableSet.of(
      OPTION_RECURSE,
      OPTION_CREATECHAPTERS
  ) ;

  private static final Comparator< ? super File > FILE_COMPARATOR = new Comparator< File >() {
    public int compare( File file1, File file2 ) {
      return file1.getAbsolutePath().compareTo( file2.getAbsolutePath() ) ;
    }
  } ;

  public String getName() {
    return "insert" ;
  }

  public FunctionCall instantiate(
      Location location,
      SyntacticTree functionCall
  ) throws IllegalFunctionCallException {

    verify( "No primary argument", true, functionCall.getChildCount() >= 2 ) ;
    final SyntacticTree primaryArgument = functionCall.getChildAt( 1 ) ;
    verify( "No value for primary argument",
        VALUED_ARGUMENT_PRIMARY.name(), primaryArgument.getText() ) ;
    verify( "No value for primary argument", 1, primaryArgument.getChildCount() ) ;
    final SyntacticTree url = primaryArgument.getChildAt( 0 ) ;
    verify( URL.name(), url.getText() ) ;
    final String urlAsString = url.getChildAt( 0 ).getText() ;

    final List< String > otherArguments = Lists.newArrayList() ;
    for( int i = 2 ; i < functionCall.getChildCount() ; i++ ) {
      final SyntacticTree otherArgumentTree = functionCall.getChildAt( i ) ;
      if( NodeKind.VALUED_ARGUMENT_FLAG.name().equals( otherArgumentTree.getText() ) ) {
        final String option = otherArgumentTree.getChildAt( 0 ).getText();
        verify( "Not a supported option: " + option, true, SUPPORTED_OPTIONS.contains( option ) ) ;
        otherArguments.add( option ) ;

      }      
    }

    LOGGER.debug( "Parsed function '{}' url='{}'", getName(), urlAsString ) ;
    if( otherArguments.size() > 0 ) {
      for( String ancillaryArgument : otherArguments ) {
        LOGGER.debug( "  Ancillary argument='{}'", ancillaryArgument ) ;
      }
    }

    return new FunctionCall( location ) {
      public Result evaluate( Environment environment, Treepath book ) {
        return InsertFunction.evaluate(
            environment,
            book,
            urlAsString,
            ImmutableSet.copyOf( otherArguments )
        ) ;
      }
    } ;
  }

  private static FunctionCall.Result evaluate(
      Environment environment,
      Treepath book,
      String urlAsString,
      Set options
  ) {
    final String fileName = urlAsString.substring( "file:".length() ) ; 
    final File insertedFile = fileName.startsWith( "/" ) ?
        new File( fileName ) :
        new File( environment.getBaseDirectory(), fileName )
    ;
    if( options.contains( OPTION_RECURSE ) ) {
      return evaluateRecursive(
          environment, book, insertedFile, options.contains( OPTION_CREATECHAPTERS ) ) ;
    } else {
      return evaluateFlat( environment, book, insertedFile );
    }
  }

  private static FunctionCall.Result evaluateFlat(
      Environment environment,
      Treepath book,
      File insertedFile
  ) {
    final Part part;
    try {
      part = new Part( insertedFile ) ;
    } catch( MalformedURLException e ) {
      return new FunctionCall.Result(
          environment, book, Lists.newArrayList( Problem.createProblem( e ) ) ) ;
    }
    final SyntacticTree partTree = part.getDocumentTree() ;

    if( null != partTree ) {
      for( final SyntacticTree partChild : partTree.getChildren() ) {
        book = TreepathTools.addChildLast( Treepath.create( book.getTreeAtStart() ), partChild ) ;
      }
    }

    return new FunctionCall.Result( environment, book, part.getProblems() ) ;
  }

  private static FunctionCall.Result evaluateRecursive(
      Environment environment,
      Treepath book,
      File insertedFile,
      boolean createChapters
  ) {
    final List< Problem > problems = Lists.newArrayList() ;
    try {
      final Iterable< File > partFiles = scanPartFiles( insertedFile ) ;
      for( File partFile : partFiles ) {
        Part part = null ;
        try {
          part = new Part( partFile ) ;
        } catch( MalformedURLException e ) {
          problems.add( Problem.createProblem( e ) ) ;
        }
        if( null != part && null != part.getDocumentTree() ) {
          final SyntacticTree partTree = part.getDocumentTree() ;

          if( createChapters ) {
            final SyntacticTree word = new SimpleTree(
                WORD.name(),
                new SimpleTree( FilenameUtils.getBaseName( partFile.getName() ) )
            ) ;
            final SyntacticTree title = new SimpleTree( TITLE.name(), word ) ;

            final SyntacticTree chapterTree = TreeTools.addFirst(
                ( SyntacticTree ) new SimpleTree( CHAPTER.name(), partTree.getChildren() ),
                title
            ) ;

            final Treepath updatedBook = TreepathTools.addChildLast( book, chapterTree ) ;
            book = Treepath.create( updatedBook.getTreeAtStart() ) ;

          } else {
            for( final SyntacticTree partChild : partTree.getChildren() ) {
              final Treepath updatedBook = TreepathTools.addChildLast( book, partChild ) ;
              book = Treepath.create( updatedBook.getTreeAtStart() ) ;
            }
          }
        }
      }
    } catch( IllegalFunctionCallException e ) {
      problems.add( Problem.createProblem( e ) ) ;
    }

    return new FunctionCall.Result( environment, book, problems ) ;
  }

  private static Iterable< File > scanPartFiles( File directory )
      throws IllegalFunctionCallException
  {
    if( directory.isDirectory() ) {
      final Collection fileCollection =
          FileUtils.listFiles( directory, StructureKind.PART.getFileExtensions(), true ) ;
      final List< File > files = Lists.newArrayList() ;
      for( Object o : fileCollection ) {
        files.add( ( File ) o ) ;
      }
      final Iterable< File > sortedFiles = Lists.sortedCopy( files, FILE_COMPARATOR ) ;

      if( LOGGER.isDebugEnabled() ) {
        StringBuffer buffer = new StringBuffer(
            "Scan of '" + directory.getAbsolutePath() + "' found those files:" ) ;
        for( File file : sortedFiles ) {
          try {
            buffer.append( "\n  " ).append( file.getCanonicalPath() ) ;
          } catch( IOException e ) {
            throw new RuntimeException( e ) ;
          }
        }
        LOGGER.debug( buffer.toString() ) ;
      }

      return sortedFiles ;

    } else {
      throw new IllegalFunctionCallException(
          "Not a directory: '" + directory.getAbsolutePath() + "'" ) ;
    }
  }

}
