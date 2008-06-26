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

package novelang.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.MalformedURLException;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.RenderingConfiguration;
import novelang.common.MetadataHelper;
import novelang.common.NodeKind;
import novelang.common.Problem;
import novelang.common.TreeMetadata;
import novelang.common.SyntacticTree;
import novelang.common.Renderable;
import novelang.part.Part;
import novelang.rendering.GenericRenderer;
import novelang.rendering.NlpWriter;

/**
 * Splits one Part file in many files with the name of its chapters (Identifier or Title).
 * If there is no name, the name '{@value #UNNAMED}' is assigned.
 * In case of duplicate names, a index is appended.
 * Original file is left unmodified but other existing files are overwritten.
 *
 * TODO: duplicate names support doesn't work. 
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
  ) throws MalformedURLException {
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
    final Map< String, SyntacticTree > chaptersByIdentifier = Maps.newHashMap() ;
    for( final SyntacticTree child : part.getDocumentTree().getChildren() ) {
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

  private String generateIdentifier( Set< String > identifiers, SyntacticTree chapter ) {
    String flatName = UNNAMED;
    SyntacticTree chapterDesignator = extractSubtree( chapter, NodeKind.IDENTIFIER ) ;
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

  private String flattenAsName( SyntacticTree tree ) {
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

  private Iterable< String > getNameElements( SyntacticTree tree ) {
    if( NodeKind.WORD.isRoot( tree ) ) {
      return ImmutableList.of( tree.getChildAt( 0 ).getText() ) ;
    } else if( 0 == tree.getChildCount() ) {
      return ImmutableList.of() ;
    } else {
      final List< String > strings = Lists.newArrayList() ;
      for( final SyntacticTree child : tree.getChildren() ) {
        strings.addAll( Lists.newArrayList( getNameElements( child ) ) ) ;
      }
      return strings ;
    }
  }

  private SyntacticTree extractSubtree( SyntacticTree tree, NodeKind nodeKind ) {
    for( final SyntacticTree child : tree.getChildren() ) {
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
    private final SyntacticTree child ;
    private final Charset encoding ;

    private RenderableChapter( SyntacticTree child, Charset encoding ) {
      this.child = child;
      this.encoding = encoding;
    }

    public Iterable<Problem> getProblems() {
      return ImmutableList.of() ;
    }

    public Charset getEncoding() {
      return encoding ;
    }

    public boolean hasProblem() {
      return false ;
    }

    public SyntacticTree getDocumentTree() {
      return child;
    }

    public TreeMetadata getTreeMetadata() {
      return MetadataHelper.createMetadata( child, encoding ) ;
    }
  }
}
