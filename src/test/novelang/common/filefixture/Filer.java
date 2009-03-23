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
package novelang.common.filefixture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Iterables;

/**
 * Performs filesystem-oriented operations on {@link Resource} and {@link Directory}.
 *
 * @author Laurent Caillette
 */
public class Filer {

  public static final int TIMEOUT_SECONDS = 5 ;

  private final File physicalTargetDirectory ;

  public Filer( File physicalTargetDirectory ) {
    Preconditions.checkArgument( physicalTargetDirectory.exists() ) ;
    Preconditions.checkArgument( physicalTargetDirectory.isDirectory() ) ;
    this.physicalTargetDirectory = physicalTargetDirectory ;
  }

  /**
   * Copies the given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   */
  public void copy( Directory directory ) {
    final File target = createPhysicalDirectory( physicalTargetDirectory, directory.getName() ) ;
    try {
      copyTo( directory, target ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  /**
   * Copies the content of the given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   */
  public void copyContent( Directory directory ) {
    try {
      copyTo( directory, physicalTargetDirectory ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  /**
   * Copies the content of given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   * @param physicalTargetDirectory a non-null object representing an existing directory.
   */
  private static void copyTo( Directory directory, File physicalTargetDirectory )
      throws IOException
  {
    for( Directory subdirectory : directory.getSubdirectories() ) {
      final File physicalDirectory =
          createPhysicalDirectory( physicalTargetDirectory, subdirectory.getName() ) ;
      copyTo( subdirectory, physicalDirectory ) ;
    }
    for( Resource resource : directory.getResources() ) {
      copyTo( resource, physicalTargetDirectory ) ;
    }
  }

  private static File copyTo( Resource resource, File physicalTargetDirectory )
      throws IOException
  {
    final File physicalFile = new File( physicalTargetDirectory, resource.getName() ) ;
    final FileOutputStream fileOutputStream = new FileOutputStream( physicalFile ) ;
    IOUtils.copy( resource.getInputStream(), fileOutputStream ) ;
    fileOutputStream.flush() ;
    fileOutputStream.close() ;
    return physicalFile ;
  }


  private static File createPhysicalDirectory( File physicalParentDirectory, String name ) {
    final File physicalDirectory = new File( physicalParentDirectory, name ) ;
    if( ! physicalDirectory.mkdir() ) {
      if( ! FileUtils.waitFor( physicalDirectory, TIMEOUT_SECONDS ) ) {
        throw new RuntimeException(
            "Failed to create file " + physicalDirectory.getAbsolutePath() ) ;
      }
    }
    return physicalDirectory ;
  }
  
  public File createFileObject( Directory scope, SchemaNode node ) {
    Preconditions.checkArgument( 
        ResourceSchema.isParentOfOrSameAs( scope, node ),
        "Directory '%s' expected to be parent of '%s'",
        scope.getUnderlyingResourcePath(),
        node.getUnderlyingResourcePath()
    ) ;
    final List< Directory > reverseParentHierarchy = Lists.newArrayList() ;

    if ( node != scope ) {
      Directory parent = node.getParent() ;
      while( true ) {
        if( null == parent ) {
          break ;
        } else {
          if( scope == parent ) {
//            reverseParentHierarchy.add( parent ) ;
            break ;
          } else {
            reverseParentHierarchy.add( parent ) ;
            parent = parent.getParent() ;
          }
        }
      }
    }

    File result = physicalTargetDirectory ;
    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;
    for( Directory directory : parentHierarchy ) {
      result = new File( result, directory.getName() ) ;
    }
    result = new File( result, node.getName() ) ;
    return result ;
    
  }
  
  public File createFileObject( SchemaNode node ) {
    
    final List< Directory > reverseParentHierarchy = Lists.newArrayList() ;

    Directory parent = node.getParent() ;
    
    while( true ) {
      if( null == parent ) {
        break ;
      } else {
        reverseParentHierarchy.add( parent ) ;
        parent = parent.getParent() ;
      }
    }

    File result = physicalTargetDirectory ;
    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;
    for( Directory directory : parentHierarchy ) {
      result = new File( result, directory.getName() ) ;
    }
    result = new File( result, node.getName() ) ;
    return result ;
  }
  
  
  /**
   * Copies given resource to some target directory, retaining directory hierarchy above, up to the
   * {@param scope} directory (included).
   * <pre>
copyScoped( ResourceTree.D0_1.dir, ResourceTree.D0_1_0.dir )

+ tree                     -->     + somewhere
  + d0                 < scope         + d0.1
    + d0.0                               + d0.1.0
        r0.0.0.txt       result >            r0.1.0.0.txt
    + d0.1             
        r0.1.0.txt
      + d0.1.0         
          r0.1.0.0.txt < target
     </pre>
   * @param scope a non-null object.
   * @param resource a non-null resource under {@code scope}.
   * @return a {@code File} object referencing the node to copy.
   */
  public File copyScoped( Directory scope, Resource resource ) {
    Preconditions.checkArgument( ResourceSchema.isParentOf( scope, resource ) ) ;
    final List< Directory > reverseParentHierarchy = Lists.newArrayList() ;

    Directory parent = resource.getParent() ;
    
    while( true ) {
      if( scope == parent ) {
        break ;
      } else {
        reverseParentHierarchy.add( parent ) ;
        parent = parent.getParent() ;
      }
    }

    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;

    File result = null ;
    File target = physicalTargetDirectory ;
    for( Directory directory : parentHierarchy ) {
      target = createPhysicalDirectory( target, directory.getName() ) ;
    }

    try {
      result = copyTo( resource, target ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }

    return result ;
    
  }
  
  

  /**
   * Copies given directory to some target directory, retaining directory hierarchy above, up to the
   * {@param scope} directory (included).
   * <pre>
copyScoped( ResourceTree.D0_1.dir, ResourceTree.D0_1_0.dir )

+ tree                     -->     + somewhere
  + d0                                 + d0.1
    + d0.0         < scope   result >     + d0.1.0
        r0.0.0.txt                           r0.1.0.0.txt
    + d0.1            
        r0.1.0.txt
      + d0.1.0         < target
          r0.1.0.0.txt
     </pre>
   * @param scope a non-null object.
   * @param origin a non-null object.
   * @return a {@code File} object referencing the node to copy.
   */
  public File copyScoped( Directory scope, Directory origin ) {
    
    Preconditions.checkArgument( ResourceSchema.isParentOf( scope, origin ) ) ;
    final List< Directory > reverseParentHierarchy = Lists.newArrayList() ;

    reverseParentHierarchy.add( origin ) ;

    Directory parent = origin.getParent() ;
    while( true ) {
      if( scope == parent ) {
        break ;
      } else {
        reverseParentHierarchy.add( parent ) ;
        parent = parent.getParent() ;
      }
    }

    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;

    File result = null ;
    File target = physicalTargetDirectory ;
    for( Directory directory : parentHierarchy ) {
      target = createPhysicalDirectory( target, directory.getName() ) ;
      if( origin == directory ) {
        result = target ;
      }
    }
    try {
      copyTo( origin, target ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }

    if( null == result ) {
      throw new IllegalStateException( "Ooops!" ) ;
    }
    return result ;
  }


}