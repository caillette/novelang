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

import com.google.common.collect.Lists;

/**
 * Represents a hierarchical structure made out of a class or interface declaring
 * {@link Resource} and {@link Directory}.
 *
 * @author Laurent Caillette
 */
/*package*/ class RegisteredStructure {

  private final Class declaringClass ;


  RegisteredStructure( Class declaringClass ) {
    this.declaringClass = declaringClass ;
  }

  private List< Resource > listDeclaredResources( Class declaringClass )
      throws IllegalAccessException
  {
    final List< Resource > list = Lists.newArrayList() ;
    final Field[] fields = declaringClass.getFields() ;
    for( Field field : fields ) {
      checkAllowed( field ) ;
      if( Resource.class.equals( field.getType() ) ) {
        final Resource resource = ( Resource ) field.get( null ) ;
//        resource.setField( field ) ;
      }

    }
    throw new UnsupportedOperationException( "listDeclaredResources" ) ;
  }

  private void checkAllowed( Field field ) {
    throw new UnsupportedOperationException( "checkAllowed" ) ;
  }
}
