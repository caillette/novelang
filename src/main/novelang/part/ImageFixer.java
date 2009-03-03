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
package novelang.part ;

import com.google.common.base.Preconditions;
import novelang.common.*;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.parser.NodeKind;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Transforms the path of embeddable resources (like {@link NodeKind#RASTER_IMAGE} or
 * {@link NodeKind#VECTOR_IMAGE}) that are initially relative to the Part they are referenced from,
 * into a path relative to the base directory of the content.
 * This is done by changing the text of the {@link NodeKind#RESOURCE_LOCATION} node.
 * Also reads resolution of bitmap images and adds corresponding nodes.
 *
 * @author Laurent Caillette
 */
public class ImageFixer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger( ImageFixer.class ) ;
  
  private final File baseDirectory;
  private final File referrerDirectory ;
  private final ProblemCollector problemCollector ;

  public ImageFixer(
      File baseDirectory, 
      File referrerDirectory, 
      ProblemCollector problemCollector 
  ) {
    Preconditions.checkNotNull( baseDirectory ) ;
    Preconditions.checkArgument( baseDirectory.exists() ) ;
    Preconditions.checkArgument( baseDirectory.isDirectory() ) ;
    Preconditions.checkNotNull( referrerDirectory ) ;
    Preconditions.checkArgument( referrerDirectory.exists() ) ;
    Preconditions.checkArgument( referrerDirectory.isDirectory() ) ;
    
    Preconditions.checkArgument( 
        FileTools.isParentOfOrSameAs( baseDirectory, referrerDirectory ),
        "Base directory '%s' should be parent of referrer directory '%s'",
        baseDirectory, 
        referrerDirectory 
    ) ;
    this.baseDirectory = baseDirectory;
    this.referrerDirectory = referrerDirectory ;
    this.problemCollector = problemCollector ;
    LOGGER.debug( 
        "Created " + ClassUtils.getShortClassName( getClass() ) + " " + 
        "contentRoot: '" + baseDirectory.getAbsolutePath() + "', " +
        "referrerDirectory: '" + referrerDirectory.getAbsolutePath() + "'"        
    ) ;
  }

  public SyntacticTree relocateResources( final SyntacticTree tree ) {
    return relocateAllResources( Treepath.create( tree ) ).getTreeAtEnd() ;
  }
  
  private Treepath< SyntacticTree > relocateAllResources( Treepath< SyntacticTree > treepath ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( NodeKind.RASTER_IMAGE, NodeKind.VECTOR_IMAGE ) ) {
      treepath = fixImage( treepath ) ;
    } else {
      final int childCount = tree.getChildCount() ;
      for( int i = 0 ; i < childCount ; i++ ) {
        treepath = relocateAllResources( Treepath.create( treepath, i ) ).getPrevious() ;        
      }
    }
    return treepath ;
  }

  private Treepath< SyntacticTree > fixImage(
      Treepath< SyntacticTree > treepathToImage
  ) {
    final SyntacticTree imageTree = treepathToImage.getTreeAtEnd() ;
    for( int i = 0 ; i < imageTree.getChildCount() ; i++ ) {
      final SyntacticTree child = imageTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind.RESOURCE_LOCATION ) ) {
        final String oldLocation = child.getChildAt( 0 ).getText() ;
        final String newLocation ;
        try {
          newLocation = relocate( oldLocation ) ;
        } catch ( ImageFixerException e ) {
          LOGGER.debug( "{} got exception: {}", // Just debug level, exception will raise later.
              ClassUtils.getShortClassName( getClass() ), e.getMessage() ) ;
          problemCollector.collect( Problem.createProblem( e ) ) ;          
          return treepathToImage ; // Leave unchanged.
        }
        LOGGER.debug( "Replacing '" + oldLocation + "' by '" + newLocation + "'" ) ;
        final Treepath< SyntacticTree > treepathToResourceLocation = 
            Treepath.create( treepathToImage, i, 0 ) ;
        treepathToImage = TreepathTools.replaceTreepathEnd(
            treepathToResourceLocation, 
            new SimpleTree( newLocation ) 
        ).getPrevious().getPrevious() ;
        treepathToImage = addImageMetadata( treepathToImage, newLocation ) ;
        return treepathToImage ;
      }
    }    
    throw new IllegalArgumentException( 
        "Missing child " + NodeKind.RESOURCE_LOCATION + " in " + imageTree.toStringTree() ) ;
  }

  private Treepath< SyntacticTree > addImageMetadata(
      Treepath< SyntacticTree > treepathToImage,
      String imageLocation
  ) {
    if( treepathToImage.getTreeAtEnd().isOneOf( NodeKind.RASTER_IMAGE ) ) {
      LOGGER.debug( "Extracting metadata from '{}'...", imageLocation ) ;
      final File imageFile = new File( baseDirectory, imageLocation ) ;
      try {
        final BufferedImage bufferedImage = ImageIO.read( imageFile ) ;

        treepathToImage = addImageMetadata(
            treepathToImage,
            NodeKind._PIXEL_WIDTH,
            "" + bufferedImage.getWidth()
        ) ;

        treepathToImage = addImageMetadata(
            treepathToImage,
            NodeKind._PIXEL_HEIGHT,
            "" + bufferedImage.getHeight()
        ) ;

      } catch( IOException e ) {
        final String message = "Could not read '" + imageLocation + "'";
        problemCollector.collect( Problem.createProblem( message ) ) ;
        LOGGER.warn( message, e ) ;
      }
    }
    return treepathToImage ;
  }

  private static Treepath<SyntacticTree> addImageMetadata(
      Treepath< SyntacticTree > treepathToImage,
      NodeKind sizeNodeKind,
      String value
  ) {
    final SyntacticTree withTree = new SimpleTree( sizeNodeKind.name(), new SimpleTree( value ) ) ;
    treepathToImage = TreepathTools.addChildLast( treepathToImage, withTree ).getPrevious() ;
    return treepathToImage;
  }


  /**
   * Returns an absolute file, given a directory and a relative name.
   * 
   * @param nameRelativeToReferrer a non-null, non-empty String which may start by a {@code ./}.
   *     File separator is a solidus.
   * @return a non-null object representing an existing resource file.
   * 
   * @throws IllegalArgumentException if one of the preconditions on arguments is violated.
   * @throws ImageFixerException if the resource does not exist, or if the resulting file
   *     is not located under given {@code directory}. 
   */
  protected String relocate( String nameRelativeToReferrer ) 
      throws ImageFixerException
  {  
    Preconditions.checkArgument( 
        ! StringUtils.isBlank( nameRelativeToReferrer ), 
        "Not expecting: '%s'", nameRelativeToReferrer 
    ) ;
    
    final File absoluteResourceFile ;
    {
      final File relocated ;
      if( nameRelativeToReferrer.startsWith( "/" ) ) {
        relocated = new File( baseDirectory, nameRelativeToReferrer ) ;
      } else {
        relocated = new File( referrerDirectory, nameRelativeToReferrer ) ;
      }
      try {
        absoluteResourceFile = relocated.getCanonicalFile() ;
      } catch ( IOException e ) {
        throw new RuntimeException( "Should not happen: " + e.getMessage(), e ) ;
      }
    }

    if( ! FileTools.isParentOf( baseDirectory, absoluteResourceFile ) ) {
      throw new ImageFixerException(
          "Given resource '" + nameRelativeToReferrer + "' " + 
          "resolved outside of '" + baseDirectory + "'"  
      ) ;
    }
    if( ! absoluteResourceFile.exists() ) {
      throw new ImageFixerException(
          "Does not exist: '" + absoluteResourceFile.getAbsolutePath() + "'" ) ;
    }
    
    final String resourceNameRelativeToBase = "/" +
        FileTools.relativizePath( baseDirectory, absoluteResourceFile ) ;
    
    return resourceNameRelativeToBase ;
  }
}
