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

import novelang.parser.NodeKind ;
import novelang.common.*;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transforms the path of embeddable resources (like {@link NodeKind#RASTER_IMAGE})
 * that are initially relative to the Part they are referenced from, into a path
 * relative to the base directory of the content.
 * This is done by changing the text of the {@link NodeKind#RESOURCE_LOCATION} node. 
 * 
 * @author Laurent Caillette
 */
public class ResourceAbsolutizer {
  
  private static final Logger LOGGER = LoggerFactory.getLogger( ResourceAbsolutizer.class ) ;
  
  private final File contentRoot ;
  private final File referrerDirectory ;
  private final ProblemCollector problemCollector ;

  public ResourceAbsolutizer( 
      File contentRoot, 
      File referrerDirectory, 
      ProblemCollector problemCollector 
  ) {
    this.contentRoot = contentRoot ;
    this.referrerDirectory = referrerDirectory ;
    this.problemCollector = problemCollector ;
    LOGGER.debug( 
        "Created " + ClassUtils.getShortClassName( getClass() ) + " " + 
        "contentRoot: '" + contentRoot.getAbsolutePath() + "', " +
        "referrerDirectory: '" + referrerDirectory.getAbsolutePath() + "'"        
    ) ;
  }
  
  public SyntacticTree absolutizeResources( final SyntacticTree tree ) {
    return absolutizeAllResources( Treepath.create( tree ) ).getTreeAtEnd() ;
  }
  
  private Treepath< SyntacticTree > absolutizeAllResources( Treepath< SyntacticTree > treepath ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( NodeKind.RASTER_IMAGE ) ) {
      treepath = absolutizeResource( treepath ) ;
    } else {
      final int childCount = tree.getChildCount() ;
      for( int i = 0 ; i < childCount ; i++ ) {
        treepath = absolutizeAllResources( Treepath.create( treepath, i ) ).getPrevious() ;        
      }
    }
    return treepath ;
  }

  private Treepath< SyntacticTree > absolutizeResource( 
      Treepath< SyntacticTree > treepathToRasterImage 
  ) {
    final SyntacticTree rasterImageTree = treepathToRasterImage.getTreeAtEnd() ;
    for( int i = 0 ; i < rasterImageTree.getChildCount() ; i++ ) {
      final SyntacticTree child = rasterImageTree.getChildAt( i ) ;
      if( child.isOneOf( NodeKind.RESOURCE_LOCATION ) ) {
        final String oldLocation = child.getChildAt( 0 ).getText() ;
        final File newLocationAsFile ;
        try {
          newLocationAsFile = absolutizeFile( contentRoot, referrerDirectory, oldLocation ) ;
        } catch ( AbsolutizerException e ) {
          problemCollector.collect( Problem.createProblem( e ) ) ;          
          return treepathToRasterImage ; // Leave unchanged.
        }
        final String newLocationAsString = newLocationAsFile.getAbsolutePath() ;
        LOGGER.debug( "Replacing '" + oldLocation + "' by '" + newLocationAsString + "'" ) ;
        final Treepath< SyntacticTree > treepathToResourceLocation = 
            Treepath.create( treepathToRasterImage, i, 0 ) ;
        return TreepathTools.replaceTreepathEnd( 
            treepathToResourceLocation, 
            new SimpleTree( newLocationAsString ) 
        ).getPrevious().getPrevious() ;
      }
    }    
    throw new IllegalArgumentException( 
        "Missing child " + NodeKind.RESOURCE_LOCATION + " in " + rasterImageTree.toStringTree() ) ;
  }

  /**
   * Returns an absolute file, given a directory and a relative name.
   * 
   * @param referrerDirectory a non-null object which must represent a directory.
   * @param relativeName a non-null, non-empty String which may start by a {@code ./}.
   *     File separator is a solidus.
   * @return a non-null object representing an existing resource file.
   * 
   * @throws IllegalArgumentException if one of the preconditions on arguments is violated.
   * @throws AbsolutizerException if the resource does not exist, or if the resulting file 
   *     is not located under given {@code directory}. 
   */
  protected static File absolutizeFile( File base, File referrerDirectory, String relativeName ) 
      throws AbsolutizerException 
  {
    Preconditions.checkNotNull( referrerDirectory ) ;
    Preconditions.checkArgument( referrerDirectory.exists() ) ;
    Preconditions.checkArgument( referrerDirectory.isDirectory() ) ;
    Preconditions.checkArgument( 
        ! StringUtils.isBlank( relativeName ), "Not expecting: '%s'", relativeName ) ;
    
    final File absoluteResourceFile;
    try {
      absoluteResourceFile = new File( referrerDirectory, relativeName ).getCanonicalFile() ;
    } catch ( IOException e ) {
      throw new RuntimeException( "Should not happen: " + e.getMessage(), e ) ;
    }

    if( ! FileTools.isParentOf( referrerDirectory, absoluteResourceFile ) ) {
      throw new AbsolutizerException( 
          "Given resource '" + relativeName + "' resolved outside of '" + referrerDirectory + "'"  ) ;
    }
    if( ! absoluteResourceFile.exists() ) {
      throw new AbsolutizerException( 
          "Does not exist: '" + absoluteResourceFile.getAbsolutePath() + "'" ) ;
    }
    
    return absoluteResourceFile ;
  }
}
