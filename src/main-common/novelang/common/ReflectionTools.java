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
package novelang.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.NullArgumentException;
import com.google.common.base.Preconditions;

/**
 * Utility clas for accessing private members.
 *
 * @author Gwenael Treguier
 * @author Laurent Caillette
 */
public final class ReflectionTools {
  
  private ReflectionTools() { }

  public static Object getFieldValue(
      final Object object,
      final String fieldName
  ) {
    if ( null == object ) {
      throw new NullArgumentException( "object" ) ;
    }
    return getFieldValue( object, object.getClass(), fieldName ) ;
  }

  public static Object getFieldValue(
      final Object object,
      final Class declaringClass,
      final String fieldName
  ) {
    if ( null == declaringClass ) {
      throw new NullArgumentException( "declaringClass" ) ;
    }
    if ( null == fieldName ) {
      throw new NullArgumentException( "fieldName" ) ;
    }
    final Field field ;
    try {
      field = declaringClass.getDeclaredField( fieldName ) ;
    } catch ( NoSuchFieldException e ) {
      throw new RuntimeException( e ) ;
    }
    field.setAccessible( true ) ;
    final Object fieldValue ;
    try {
      fieldValue = field.get( object ) ;
    } catch ( IllegalAccessException e ) {
      throw new RuntimeException( e ) ;
    }
    return fieldValue ;
  }
  
  public static Method getMethod( 
      Class declaringClass, 
      String methodName, 
      Class... parameterTypes 
  ) {
    Preconditions.checkArgument( null != declaringClass ) ;
    Preconditions.checkArgument( null != methodName ) ;
    Preconditions.checkArgument( ! "".equals( methodName ) ) ;
    
    final Method method ;

    try {
      method = declaringClass.getMethod( methodName, parameterTypes ) ;
    } catch ( NoSuchMethodException e ) {
      throw new RuntimeException( e ) ;
    }

    if( null == method ) {
      throw new RuntimeException( 
          "Could not get declared method " + declaringClass.getName() + "#" + methodName ) ;
    }
    
    return method ;
    
    
  }
}
