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
package novelang.model.function.builtin;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.FileUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import novelang.model.book.Environment;
import novelang.model.common.Location;
import static novelang.model.common.NodeKind.URL;
import static novelang.model.common.NodeKind.VALUED_ARGUMENT_PRIMARY;
import static novelang.model.common.NodeKind.VALUED_ARGUMENT_ANCILLARY;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import novelang.model.common.Treepath;
import novelang.model.common.TreeTools;
import novelang.model.common.NodeKind;
import novelang.model.common.StructureKind;
import novelang.model.function.FunctionCall;
import novelang.model.function.FunctionDefinition;
import novelang.model.function.IllegalFunctionCallException;
import static novelang.model.function.FunctionTools.verify;
import novelang.model.implementation.Part;

/**
 * @author Laurent Caillette
 */
public class InsertFunction implements FunctionDefinition {

  private static final Logger LOGGER = LoggerFactory.getLogger( InsertFunction.class ) ;

  private static final String OPTION_RECURSE = "recurse" ;
  private static final Set< String > SUPPORTED_OPTIONS = Sets.immutableSet( OPTION_RECURSE ) ;
  private static final IOFileFilter PART_FILE_FILTER =
      new SuffixFileFilter( StructureKind.PART.getFileExtensions() ) ;

  public String getName() {
    return "insert" ;
  }

  public FunctionCall instantiate(
      Location location,
      Tree functionCall
  ) throws IllegalFunctionCallException {

    verify( "No primary argument", true, functionCall.getChildCount() >= 2 ) ;
    final Tree primaryArgument = functionCall.getChildAt( 1 ) ;
    verify( "No value for primary argument",
        VALUED_ARGUMENT_PRIMARY.name(), primaryArgument.getText() ) ;
    verify( "No value for primary argument", 1, primaryArgument.getChildCount() ) ;
    final Tree url = primaryArgument.getChildAt( 0 ) ;
    verify( URL.name(), url.getText() ) ;
    final String urlAsString = url.getChildAt( 0 ).getText() ;

    final List< String > otherArguments = Lists.newArrayList() ;
    for( int i = 2 ; i < functionCall.getChildCount() ; i++ ) {
      final Tree otherArgumentTree = functionCall.getChildAt( i ) ;
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
            Sets.immutableSet( otherArguments )
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
          if( null != part ) {
            final Tree partTree = part.getDocumentTree() ;

            for( final Tree partChild : partTree.getChildren() ) {
              final Treepath updatedBook = TreeTools.addChildAtRight( book, partChild ) ;
              book = Treepath.create( updatedBook.getTop() ) ;
            }
          }
        }
      } catch( IllegalFunctionCallException e ) {
        problems.add( Problem.createProblem( e ) ) ;
      }

      return new FunctionCall.Result( book, problems ) ;


    } else {

      final Part part;
      try {
        part = new Part( insertedFile ) ;
      } catch( MalformedURLException e ) {
        return new FunctionCall.Result( book, Lists.newArrayList( Problem.createProblem( e ) ) ) ;
      }
      final Tree partTree = part.getDocumentTree() ;

      for( final Tree partChild : partTree.getChildren() ) {
        book = TreeTools.addChildAtRight( book, partChild ) ;
      }

      return new FunctionCall.Result( book, part.getProblems() ) ;
    }
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
      return files ;
    } else {
      throw new IllegalFunctionCallException(
          "Not a directory: '" + directory.getAbsolutePath() + "'" ) ;
    }
  }

}
