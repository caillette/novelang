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

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.io.File;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.base.Preconditions;

/**
 * Represents a hierarchical structure made out of a class or interface declaring
 * {@link Resource} and {@link Directory}.
 *
 * @author Laurent Caillette
 */
/*package*/ class RegisteredStructure {

  private final Directory rootDirectory;
  private final File physicalDirectory ;

  RegisteredStructure( Class declaringClass, File physicalDirectory )
      throws DeclarationException, IllegalAccessException
  {
    Preconditions.checkNotNull( declaringClass ) ;
    Preconditions.checkNotNull( physicalDirectory ) ;
    rootDirectory = makeDirectoryOfClass( declaringClass ) ;
    this.physicalDirectory = physicalDirectory;
  }

  public Directory getRootDirectory() {
    return rootDirectory;
  }

  public File getPhysicalDirectory() {
    return physicalDirectory;
  }

  private Directory makeDirectoryOfClass( Class declaringClass )
      throws DeclarationException, IllegalAccessException
  {
    final Directory directory = findDirectoryObject( declaringClass ) ;
    directory.setDeclaringClass( declaringClass ) ;
    directory.setResources( findResources( declaringClass ) ) ;
    directory.setDirectories( findDirectories( declaringClass ) ) ;
    return directory ;
  }

  private List< Resource > findResources( Class declaringClass )
      throws IllegalAccessException, DeclarationException
  {
    final List< Resource > resources = Lists.newArrayList() ;
    final Field[] fields = declaringClass.getDeclaredFields() ;
    for( Field field : fields ) {
      checkAllowed( field ) ;
      if( Resource.class.equals( field.getType() ) ) {
        final Resource resource = ( Resource ) field.get( null ) ;
        resource.setField( field ) ;
        resources.add( resource ) ;
      }
    }
    return Ordering.natural().sortedCopy( resources ) ;
  }

  private List< Directory > findDirectories( Class declaringClass )
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

  private void checkAllowed( Class ynterface ) throws DeclarationException {
    if( ynterface.isAnonymousClass() ) {
      throw new DeclarationException( "Misses requirements: " + ynterface ) ;
    }
  }

  private Directory findDirectoryObject( Class ynterface )
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
          directory = ( Directory ) field.get( new novelang.common.filefixture.test.ResourceTree() {} ) ;
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

  private void checkAllowed( Field field ) throws DeclarationException {
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
}
