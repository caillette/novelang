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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.MissingResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * Transforms the hierarchical representation of resources into real files.
 *
 * @author Laurent Caillette
 */
public final class ResourceSchema {

  /**
   * Factory method.
   */
  public static Directory directory( String name ) {
    return new Directory( name ) ;
  }

  /**
   * Factory method.
   */
  public static Resource resource( String name ) {
    return new Resource( name ) ;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( ResourceSchema.class ) ;

  private ResourceSchema() { }


// ==============
// Initialization
// ==============

  public static void initialize( Class declaration ) {
    initialize( "", declaration ) ;
  }

  public static void initialize( String resourcePrefix, Class declaration ) {
    Preconditions.checkNotNull( declaration ) ;
    Preconditions.checkNotNull( resourcePrefix ) ;

    try {
      final Directory rootDirectory = makeDirectoryOfClass( declaration ) ;
      checkUnderlyingResources( resourcePrefix, rootDirectory ) ;
    } catch( IOException e ) {
      throw new RuntimeException( e ) ;
    } catch( DeclarationException e ) {
      throw new RuntimeException( e );
    } catch( IllegalAccessException e ) {
      throw new RuntimeException( e );
    }

  }

  private static void checkUnderlyingResources( String resourcePrefix, Directory directory )
      throws MissingResourceException, IOException
  {
    final String directoryPath = resourcePrefix + "/" + directory.getName() ;
    directory.setUnderlyingResourcePath( directoryPath ) ;
    for( Resource resource : directory.getResources() ) {
      resource.setParent( directory ) ;
      final String resourcePath = directoryPath + "/" + resource.getName() ;
      final InputStream inputStream = ResourceSchema.class.getResourceAsStream( resourcePath ) ;
      if( null == inputStream ) {
        throw new MissingResourceException( resourcePath, ResourceSchema.class.getName(), "" ) ;
      }
      inputStream.close() ;
      resource.setUnderlyingResourcePath( resourcePath ) ;
      LOGGER.debug( "Verified: {}", resource.getUnderlyingResourcePath() ) ;
    }
    for( Directory subDirectory : directory.getSubdirectories() ) {
      subDirectory.setParent( directory ) ;
      checkUnderlyingResources( directoryPath, subDirectory ) ;
    }
  }

  private static Directory makeDirectoryOfClass( Class declaringClass )
      throws DeclarationException, IllegalAccessException
  {
    final Directory directory = findDirectoryObject( declaringClass ) ;
    directory.setDeclaringClass( declaringClass ) ;
    directory.setResources( findResources( declaringClass ) ) ;
    directory.setDirectories( findDirectories( declaringClass ) ) ;
    return directory ;
  }

  private static List< Resource > findResources( Class declaringClass )
      throws IllegalAccessException, DeclarationException
  {
    final List< Resource > resources = Lists.newArrayList() ;
    final Field[] fields = declaringClass.getDeclaredFields() ;
    for( Field field : fields ) {
      checkAllowed( field ) ;
      if( Resource.class.equals( field.getType() ) ) {
        final Resource resource = ( Resource ) field.get( null ) ;
        resource.setDeclaringClass( declaringClass ) ;
        resources.add( resource ) ;
      }
    }
    return Ordering.natural().sortedCopy( resources ) ;
  }

  private static List< Directory > findDirectories( Class declaringClass )
      throws IllegalAccessException, DeclarationException
  {
    final List< Directory > directories = Lists.newArrayList() ;
    final Class[] interfaces = declaringClass.getDeclaredClasses() ;
    for( Class ynterface : interfaces ) {
      checkAllowed( ynterface ) ;
      final Directory directory = makeDirectoryOfClass( ynterface ) ;
      directories.add( directory ) ;
    }
    return Ordering.natural().sortedCopy( directories ) ;
  }

  private static void checkAllowed( Class ynterface ) throws DeclarationException {
    if( ynterface.isAnonymousClass() ) {
      throw new DeclarationException( "Misses requirements: " + ynterface ) ;
    }
  }

  private static Directory findDirectoryObject( Class ynterface )
      throws DeclarationException, IllegalAccessException
  {
    final Field[] fields = ynterface.getDeclaredFields() ;
    boolean found = false ;
    Directory directory = null ;
    for( Field field : fields ) {
      if( Directory.class.equals( field.getType() ) ) {
        if( found ) {
          throw new DeclarationException( "More than one field of " +
              Directory.class.getSimpleName() + " in " + ynterface.getName() ) ;
        } else {
          field.setAccessible( true ) ;
          directory = ( Directory )
              field.get( new novelang.common.filefixture.test.ResourceTree() {} ) ;
          found = true ;
        }
      }
    }
    if( found ) {
      return directory ;
    } else {
      throw new DeclarationException( "Missing directory declaration" ) ;
    }
  }

  private static void checkAllowed( Field field ) throws DeclarationException {
    final int modifiers = field.getModifiers() ;
    if( Modifier.isAbstract( modifiers )
     || Modifier.isNative( modifiers )
     || Modifier.isPrivate( modifiers )
     || Modifier.isProtected( modifiers )
     || Modifier.isProtected( modifiers )
    ) {
      throw new DeclarationException( "Field " + field + " has unsupported modifier" ) ;
    }
    if( Modifier.isFinal( modifiers )
     && Modifier.isPublic( modifiers )
     && Modifier.isStatic( modifiers )
    ) {
      return ;
    } else {
      throw new DeclarationException( "Field " + field + " misses one modifier or more" ) ;
    }
  }

// =================
// Various utilities
// =================

  /**
   * Returns true if {@code maybeParent} is one of the parents of {@code maybeChild}, false
   * otherwise.
   *
   * @param maybeParent a non-null object.
   * @param maybeChild a non-null object.
   */
  public static boolean isParentOf( Directory maybeParent, Directory maybeChild ) {
    Preconditions.checkNotNull( maybeParent ) ;
    Preconditions.checkNotNull( maybeChild ) ;
    return maybeParentOfOrSameAs( maybeParent, maybeChild.getParent() ) ;
  }

  /**
   * Returns true if {@code maybeParent} is one of the parent of {@code maybeChild},
   * or if it is the same object as  {@code maybeChild}.
   *
   * @param maybeParent a non-null object.
   * @param maybeChild a non-null object.
   */
  public static boolean isParentOfOrSameAs( Directory maybeParent, SchemaNode maybeChild ) {
    Preconditions.checkNotNull( maybeParent ) ;
    Preconditions.checkNotNull( maybeChild ) ;
    return maybeParentOfOrSameAs( maybeParent, maybeChild ) ;
  }

  private static boolean maybeParentOfOrSameAs( Directory maybeParent, SchemaNode maybeChild ) {
    if( null == maybeChild ) {
      return false ;
    } else if( maybeChild == maybeParent ) {
      return true ;
    } else {
      return maybeParentOfOrSameAs( maybeParent, maybeChild.getParent() ) ;
    }
  }
}
