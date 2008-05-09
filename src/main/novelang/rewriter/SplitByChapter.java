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

package novelang.rewriter;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.nio.charset.Charset;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import novelang.model.common.Tree;
import novelang.model.common.NodeKind;
import novelang.model.common.Problem;
import novelang.model.common.TreeMetadata;
import novelang.model.common.MetadataHelper;
import novelang.model.implementation.Part;
import novelang.model.renderable.Renderable;
import novelang.rendering.GenericRenderer;
import novelang.rendering.NlpWriter;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.ConfigurationTools;

/**
 * Splits one Part file in many files with the name of its chapters (Identifier or Title).
 * If there is no name, the name '{@value #UNNAMED}' is assigned.
 * In case of duplicate names, a index is appended.
 * Original file is left unmodified but other existing files are overwritten.
 *
 * @author Laurent Caillette
 */
public class SplitByChapter {

  private static final Logger LOGGER = LoggerFactory.getLogger( SplitByChapter.class ) ;

  private final Part part ;
  private final File targetDirectory ;
  private final RenderingConfiguration configuration ;
  private static final String UNNAMED = "$unnamed$";

  public SplitByChapter(
      RenderingConfiguration configuration,
      File partFile,
      File targetDirectory
  ) {
    this.part = new Part( partFile ) ;
    this.targetDirectory = targetDirectory ;
    this.configuration = configuration ;

    if( ! targetDirectory.exists() ) {
      final String message = "Does not exist: " + targetDirectory.getAbsolutePath();
      LOGGER.error( message ) ;
      throw new IllegalArgumentException( message ) ;
    }
    if( ! targetDirectory.isDirectory() ) {
      final String message = "Not a directory: " + targetDirectory.getAbsolutePath();
      LOGGER.error( message ) ;
      throw new IllegalArgumentException( message ) ;
    }
  }

  private static final String HELP =
      ClassUtils.getShortClassName( SplitByChapter.class ) +
      " <book file> <target directory>"
  ;

  private void rewrite() throws IOException {
    final Map< String, Tree > chaptersByIdentifier = Maps.newHashMap() ;
    for( final Tree child : part.getTree().getChildren() ) {
      if( NodeKind.CHAPTER.isRoot( child ) ) {
        final String identifier = generateIdentifier( chaptersByIdentifier.keySet(), child ) ;
        final File chapterFile = new File( targetDirectory, identifier  + ".nlp" ) ;
        if( chapterFile.exists() ) {
          chapterFile.delete() ;
          LOGGER.info( "Deleted previously existing file '{}'", chapterFile.getAbsolutePath() ) ;
        }
        final FileOutputStream fileOutputStream = new FileOutputStream( chapterFile ) ;
        final Renderable renderable = new RenderableChapter( child, part.getEncoding() ) ;
        new GenericRenderer( new NlpWriter( configuration ) ).
            render( renderable, fileOutputStream ) ;
        LOGGER.info( "Wrote to file '{}'", chapterFile.getAbsolutePath() ) ;
      }
    }
  }

  private String generateIdentifier( Set< String > identifiers, Tree chapter ) {
    String flatName = UNNAMED;
    Tree chapterDesignator = extractSubtree( chapter, NodeKind.IDENTIFIER ) ;
    if( null == chapterDesignator ) {
      chapterDesignator = extractSubtree( chapter, NodeKind.TITLE );
    }
    if( null != chapterDesignator ) {
      flatName = flattenAsName( chapterDesignator ) ;
    }
    return buildIdentifier( identifiers, flatName );
  }

  private String buildIdentifier( Set< String > identifiers, String flatName ) {
    if( identifiers.contains( flatName ) ) {
      String newName = flatName ;
      int counter = 1 ;
      for( newName = flatName + "-" + counter ; identifiers.contains( newName ) ; counter++ ) ;
      return newName ;
    } else {
      return flatName ;
    }
  }

  private String flattenAsName( Tree tree ) {
    final Iterable< String > nameElements = getNameElements( tree ) ;
    final StringBuffer flattenedName = new StringBuffer() ;
    boolean first = true ;
    for( final String s : nameElements ) {
      if( first ) {
        first = false ;
      } else {
        flattenedName.append( "_" ) ;
      }
      flattenedName.append( s ) ;
    }
    return flattenedName.toString() ;
  }

  private Iterable< String > getNameElements( Tree tree ) {
    if( NodeKind.WORD.isRoot( tree ) ) {
      return Lists.immutableList( tree.getChildAt( 0 ).getText() ) ;
    } else if( 0 == tree.getChildCount() ) {
      return Lists.immutableList() ;
    } else {
      final List< String > strings = Lists.newArrayList() ;
      for( final Tree child : tree.getChildren() ) {
        strings.addAll( Lists.newArrayList( getNameElements( child ) ) ) ;
      }
      return strings ;
    }
  }

  private Tree extractSubtree( Tree tree, NodeKind nodeKind ) {
    for( final Tree child : tree.getChildren() ) {
      if( nodeKind.isRoot( child ) ) {
        return child ;
      }
    }
    return null ;
  }


  public static void main( String[] args ) throws IOException {
    if( 2 != args.length ) {
      throw new IllegalArgumentException( HELP ) ;
    }
    final SplitByChapter splitByChapter = new SplitByChapter(
        ConfigurationTools.buildRenderingConfiguration(),
        new File( args[ 0 ] ),
        new File( args[ 1 ] )
    ) ;
    splitByChapter.rewrite() ;
  }


  private static class RenderableChapter implements Renderable {
    private final Tree child ;
    private final Charset encoding ;

    private RenderableChapter( Tree child, Charset encoding ) {
      this.child = child;
      this.encoding = encoding;
    }

    public Iterable<Problem> getProblems() {
      return Lists.immutableList() ;
    }

    public Charset getEncoding() {
      return encoding ;
    }

    public boolean hasProblem() {
      return false ;
    }

    public Tree getTree() {
      return child;
    }

    public TreeMetadata getTreeMetadata() {
      return MetadataHelper.createMetadata( child, encoding ) ;
    }
  }
}
