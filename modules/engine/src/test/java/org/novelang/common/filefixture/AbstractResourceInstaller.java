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
package org.novelang.common.filefixture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.novelang.testing.DirectoryFixture;

/**
 * Performs filesystem-oriented operations on {@link Resource} and {@link Directory}.
 *
 * @author Laurent Caillette
 */
/*package*/ abstract class AbstractResourceInstaller {

  public abstract File getTargetDirectory() ;

  /**
   * Copies the given {@code directory} into the {@code physicalTargetDirectory}.
   *
   * @param directory a non-null object.
   * @return created directory.
   * @see #copyTo(Directory, java.io.File)
   */
  public File copy( final Directory directory ) {
    final File target = createPhysicalDirectory( getTargetDirectory(), directory.getName() ) ;
    try {
      copyTo( directory, target ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
    return target ;
  }

  /**
   * Copies the given {@code singleResource} into the {@code physicalTargetDirectory}.
   *
   * @param singleResource a non-null object.
   * @return created file referencing {@code singleResource}.
   */
  public File copy( final Resource singleResource ) {
    try {
      return copyTo( singleResource, getTargetDirectory() ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  /**
   * Copies the content of the given {@code directory} into the {@code physicalTargetDirectory}.
   *
   * @param directory a non-null object.
   * @see #copy(Directory)
   */
  public void copyContent( final Directory directory ) {
    try {
      copyTo( directory, getTargetDirectory() ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  /**
   * Copies the content of given {@code directory} into the {@code physicalTargetDirectory}.
   * @param directory a non-null object.
   * @param physicalTargetDirectory a non-null object representing an existing directory.
   */
  private static void copyTo( final Directory directory, final File physicalTargetDirectory )
      throws IOException
  {
    for( final Directory subdirectory : directory.getSubdirectories() ) {
      final File physicalDirectory =
          createPhysicalDirectory( physicalTargetDirectory, subdirectory.getName() ) ;
      copyTo( subdirectory, physicalDirectory ) ;
    }
    for( final Resource resource : directory.getResources() ) {
      copyTo( resource, physicalTargetDirectory ) ;
    }
  }

  public File createFileObject( final Directory scope, final SchemaNode node ) {
    Preconditions.checkArgument( 
        ResourceSchema.isParentOfOrSameAs( scope, node ),
        "Directory '%s' expected to be parent of '%s'",
        scope.getAbsoluteResourceName(),
        node.getAbsoluteResourceName()
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

    File result = getTargetDirectory();
    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;
    for( final Directory directory : parentHierarchy ) {
      result = new File( result, directory.getName() ) ;
    }
    result = new File( result, node.getName() ) ;
    return result ;
    
  }

  /**
   * Creates a {@code File} object corresponding to a resource with a relocated path.
   *
   * @param node a non-null object.
   * @return a non-null object.
   */
  public File createFileObject( final SchemaNode node ) {
    
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

    File result = getTargetDirectory();
    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;
    for( final Directory directory : parentHierarchy ) {
      result = new File( result, directory.getName() ) ;
    }
    result = new File( result, node.getName() ) ;
    if( ! result.exists() ) {
      throw new AssertionError( "Should exist: '" + result.getPath() + "'"  ) ;
    }
    return result ;
  }
  
  
  /**
   * Copies given resource to some target directory, retaining directory hierarchy above, up to the
   * {@param scope} directory (not included).
   * <pre>
copyScoped( ResourceTree.D0.dir, ResourceTree.D0_1_0.dir )

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
  public File copyScoped( final Directory scope, final Resource resource ) {
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
    File target = getTargetDirectory();
    for( final Directory directory : parentHierarchy ) {
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
   * Does the same as {@link #copyWithPath(Directory, Resource)} with a path being
   * the parent's {@code Directory} of the resource.
   */
  public File copyWithPath( final Resource resource ) {
    final Directory root = resource.getRoot() ;
    if( root == null ) {
      return copy( resource ) ;     
    } else {
      return copyWithPath( root, resource ) ;
    }
  }


  /**
   * Copies given resource to some target directory, retaining directory hierarchy above, up to the
   * {@param scope} directory (not included).
   * <pre>
copyScoped( ResourceTree.D0.dir, ResourceTree.D0_1_0.dir )

+ tree                     -->     + somewhere
  + d0                 < scope         + d0
    + d0.0                               + d0.1
        r0.0.0.txt                         + d0.1.0
    + d0.1               result >              r0.1.0.0.txt
        r0.1.0.txt
      + d0.1.0
          r0.1.0.0.txt < target
     </pre>
   * @param scope a non-null object.
   * @param resource a non-null resource under {@code scope}.
   * @return a {@code File} object referencing the node to copy.
   */
  public File copyWithPath( final Directory scope, final Resource resource ) {
    Preconditions.checkArgument( ResourceSchema.isParentOf( scope, resource ) ) ;
    final List< Directory > reverseParentHierarchy = Lists.newArrayList() ;

    Directory parent = resource.getParent() ;
    Directory oldParent = null ;

    while( true ) {
      if( scope == oldParent ) {
          break ;
      }
      oldParent = parent ;
      reverseParentHierarchy.add( parent ) ;
      parent = parent.getParent() ;
    }

    final Iterable< Directory > parentHierarchy = Iterables.reverse( reverseParentHierarchy ) ;

    File result = null ;
    File target = getTargetDirectory();
    for( final Directory directory : parentHierarchy ) {
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
  public File copyScoped( final Directory scope, final Directory origin ) {
    
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
    File target = getTargetDirectory() ;
    for( final Directory directory : parentHierarchy ) {
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

// ================
// Internal cooking
// ================




  private static File copyTo( final Resource resource, final File physicalTargetDirectory )
      throws IOException
  {
    final File physicalFile = new File( physicalTargetDirectory, resource.getName() ) ;
    final FileOutputStream fileOutputStream = new FileOutputStream( physicalFile ) ;
    IOUtils.copy( resource.getInputStream(), fileOutputStream ) ;
    fileOutputStream.flush() ;
    fileOutputStream.close() ;
    return physicalFile ;
  }


  private static File createPhysicalDirectory( 
      final File physicalParentDirectory, 
      final String name 
  ) {
    final File physicalDirectory = new File( physicalParentDirectory, name ) ;
    if( ! physicalDirectory.mkdir() ) {
      if( ! FileUtils.waitFor( physicalDirectory, DirectoryFixture.TIMEOUT_SECONDS ) ) {
        throw new AssertionError(
            "Failed to create file " + physicalDirectory.getAbsolutePath() ) ;
      }
    }
    return physicalDirectory ;
  }


}