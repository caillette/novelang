/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.novella;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import org.novelang.common.FileTools;
import org.novelang.common.Problem;
import org.novelang.common.ProblemCollector;
import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.NodeKind;

/**
 * Transforms the path of embeddable resources (like {@link NodeKind#RASTER_IMAGE} or
 * {@link NodeKind#VECTOR_IMAGE}) that are initially relative to the Novella they are referenced from,
 * into a path relative to the base directory of the content.
 * This is done by changing the text of the {@link NodeKind#RESOURCE_LOCATION} node.
 * Also reads resolution of bitmap images and adds corresponding nodes.
 *
 * @author Laurent Caillette
 */
public class ImageFixer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger( ImageFixer.class );
  
  private final File baseDirectory;
  private final File referrerDirectory ;
  private final ProblemCollector problemCollector ;

  public ImageFixer(
      final File baseDirectory, 
      final File referrerDirectory, 
      final ProblemCollector problemCollector 
  ) {
    Preconditions.checkNotNull( baseDirectory ) ;
    Preconditions.checkArgument( baseDirectory.exists(), "Does not exist: '%s'", baseDirectory ) ;
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
        "Created ",
        ClassUtils.getShortClassName( getClass() ),
        " contentRoot: '",
        baseDirectory.getAbsolutePath(),
        "', referrerDirectory: '",
            referrerDirectory.getAbsolutePath(),
        "'"
    ) ;
  }

  public SyntacticTree relocateResources( final SyntacticTree tree ) {
    return relocateAllResources( Treepath.create( tree ) ).getTreeAtEnd() ;
  }

  private Treepath<SyntacticTree> relocateAllResources( final Treepath< SyntacticTree > treepath ) {
    Treepath<SyntacticTree> treepath1 = treepath;
    final SyntacticTree tree = treepath1.getTreeAtEnd();
    if( tree.isOneOf( NodeKind.RASTER_IMAGE, NodeKind.VECTOR_IMAGE ) ) {
      treepath1 = fixImage( treepath1 );
    } else {
      final int childCount = tree.getChildCount();
      for( int i = 0 ; i < childCount ; i++ ) {
        treepath1 = relocateAllResources( Treepath.create( treepath1, i ) ).getPrevious();
      }
    }
    return treepath1;
  }

  private Treepath< SyntacticTree > fixImage(
      final Treepath< SyntacticTree > treepathToImage
  ) {
    Treepath< SyntacticTree > newTreepath ;
    final SyntacticTree imageTree = treepathToImage.getTreeAtEnd() ;
    for( int i = 0 ; i < imageTree.getChildCount() ; i++ ) {
      final SyntacticTree child = imageTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind.RESOURCE_LOCATION ) ) {
        final String oldLocation = child.getChildAt( 0 ).getText() ;
        final String newLocation ;
        try {
          newLocation = relocate( oldLocation ) ;
        } catch ( ImageFixerException e ) {
          LOGGER.debug(
              ClassUtils.getShortClassName( getClass() ),
              " got exception: ", // Just debug level, exception will raise later.
              e.getMessage()
          ) ;
          problemCollector.collect( Problem.createProblem( e ) ) ;
          return treepathToImage ; // Leave unchanged.
        }
        LOGGER.debug( "Replacing '", oldLocation, "' by '", newLocation, "'" ) ;
        final Treepath< SyntacticTree > treepathToResourceLocation = 
            Treepath.create( treepathToImage, i, 0 ) ;
        newTreepath = TreepathTools.replaceTreepathEnd(
            treepathToResourceLocation, 
            new SimpleTree( newLocation ) 
        ).getPrevious().getPrevious() ;
        newTreepath = addImageMetadata( newTreepath, newLocation ) ;
        return newTreepath ;
      }
    }    
    throw new IllegalArgumentException( 
        "Missing child " + NodeKind.RESOURCE_LOCATION + " in " + imageTree.toStringTree() ) ;
  }

  private Treepath< SyntacticTree > addImageMetadata(
      final Treepath< SyntacticTree > treepathToImage,
      final String imageLocation
  ) {
      final File imageFile = new File( baseDirectory, imageLocation ) ;
      final NodeKind nodeKind = NodeKind.valueOf( treepathToImage.getTreeAtEnd().getText() ) ;
      Treepath< SyntacticTree > newTreepath = treepathToImage ;
      try {
        //noinspection EnumSwitchStatementWhichMissesCases
        switch( nodeKind ) {
          case RASTER_IMAGE :
            newTreepath = addRasterImageMetadata( newTreepath, imageFile ) ;
            break ;
          case VECTOR_IMAGE :
            newTreepath = addVectorImageMetadata( newTreepath, imageFile ) ;
            break ;
          default :
            break ;
        }
      } catch( Exception e ) {
        final String message = "Could not read '" + imageLocation + "'";
        problemCollector.collect( Problem.createProblem( message ) ) ;
        LOGGER.warn( e, message ) ;
      }
    return newTreepath ;
  }

  private static Treepath< SyntacticTree > addRasterImageMetadata(
      final Treepath< SyntacticTree > treepathToImage,
      final File imageFile
  ) throws IOException {
    LOGGER.debug( "Extracting raster image metadata from '", imageFile.getAbsolutePath(), "'..." ) ;
    Treepath< SyntacticTree > newTreepath = treepathToImage ;

    final BufferedImage bufferedImage = ImageIO.read( imageFile ) ;

    newTreepath = addImageMetadata(
        newTreepath,
        NodeKind._IMAGE_WIDTH,
        bufferedImage.getWidth() + "px"
    ) ;

    newTreepath = addImageMetadata(
        newTreepath,
        NodeKind._IMAGE_HEIGHT,
        bufferedImage.getHeight() + "px"
    ) ;
    return newTreepath ;
  }

  /**
   * Decorates the referenced {@link NodeKind#VECTOR_IMAGE} with {@link NodeKind#_IMAGE_WIDTH }
   * and {@link NodeKind#_IMAGE_HEIGHT}.
   * TODO: Seems that loading XML document triggers a connection to Internet (w3c.org).
   */
  private static Treepath< SyntacticTree > addVectorImageMetadata(
      final Treepath< SyntacticTree > treepathToImage,
      final File imageFile
  ) throws IOException, XMLStreamException {

    LOGGER.debug( "Extracting vector image metadata from '",
        imageFile.getAbsolutePath(),
        "'..."
    ) ;
    String width = null ;
    String height = null ;

    final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance() ;
    final InputStream inputStream = new FileInputStream( imageFile ) ;
    try {

      xmlInputFactory.setProperty( "javax.xml.stream.resolver", ENTITY_RESOLVER );
      final XMLStreamReader reader = xmlInputFactory.createXMLStreamReader( inputStream ) ;
      for( int event = reader.next() ;
          event != XMLStreamConstants.END_DOCUMENT ;
          event = reader.next()
      ) {
        if( event == XMLStreamConstants.START_ELEMENT
            && "svg".equals( reader.getName().getLocalPart() )
        ) {
          width = reader.getAttributeValue( "", "width" ) ;
          height = reader.getAttributeValue( "", "height" ) ;
          break ;
        }
      }
    } finally {
      inputStream.close() ;
    }

    return addImageMetadata( treepathToImage, width, height );
  }

  /**
   * @param width maybe null.
   * @param height maybe null.
   */
  private static Treepath< SyntacticTree > addImageMetadata(
      final Treepath< SyntacticTree > treepathToImage,
      final String width,
      final String height
  ) {
    Treepath< SyntacticTree > newTreepath = treepathToImage ;
    if( ! StringUtils.isBlank( width ) ) {
      newTreepath = addImageMetadata(
          newTreepath,
          NodeKind._IMAGE_WIDTH,
          width
      ) ;
    }
    if( ! StringUtils.isBlank( height ) ) {    
      newTreepath = addImageMetadata(
          newTreepath,
          NodeKind._IMAGE_HEIGHT,
          height
      ) ;
    }
    return newTreepath ;
  }

  private static Treepath<SyntacticTree> addImageMetadata(
      final Treepath<SyntacticTree> treepathToImage,
      final NodeKind sizeNodeKind,
      final String value
  ) {
    final SyntacticTree withTree = new SimpleTree( sizeNodeKind, new SimpleTree( value ) );
    return TreepathTools.addChildLast( treepathToImage, withTree ).getPrevious();
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
  protected String relocate( final String nameRelativeToReferrer ) 
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
        FileTools.urlifyPath( FileTools.relativizePath( baseDirectory, absoluteResourceFile ) ) ;
    
    return resourceNameRelativeToBase ;
  }


  /**
   * Always returns an empty {@code InputStream} with the effect of disabling any entity inclusion.
   */
  private static final XMLResolver ENTITY_RESOLVER = new XMLResolver() {
    @Override
    public InputStream resolveEntity(
        final String publicId,
        final String systemId,
        final String baseURI,
        final String namespace
    )
    {
      return new ByteArrayInputStream( new byte[] { } ) ;
    }

  } ;

}
