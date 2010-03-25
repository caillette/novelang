/*
 * Copyright (C) 2010 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.system;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * @author Laurent Caillette
 */
public final class Pod {

  private Pod() { }

  public static< T > T make( final Class< T > podClass ) {

    Preconditions.checkArgument( podClass.isInterface() ) ;
    final Method[] methods = podClass.getMethods() ;

    final Map< String, PropertyDeclaration > properties = Maps.newHashMap() ;

    for( final Method method : methods ) {
      final String methodName = method.getName();

      if( methodName.startsWith( "get" ) ) {

        if( method.getReturnType() == null ) {
          throw new BadDeclarationException(
              "Bad return type for " + methodName + ", should not be void" ) ;
        }
        if( method.getParameterTypes().length > 0 ) {
          throw new BadDeclarationException(
              "Bad parameter count for " + methodName + ", there should be no parameter" ) ;
        }
        final String propertyName = methodName.substring( 3 ) ;
        getForSure( properties, propertyName ).getter = method ;

      } else if( methodName.startsWith( "with" ) ) {
        if( method.getReturnType() != podClass ) {
          throw new BadDeclarationException(
              "Bad return type for " + methodName + ": " + method.getReturnType() +
              ", should be " + podClass.getName()
          ) ;
        }
        if( method.getParameterTypes().length == 0 ) {
          throw new BadDeclarationException(
              "Bad parameter count for " + methodName +
              ", there should be at least one parameter"
          ) ;
        }
        final String propertyName = methodName.substring( 4 ) ;
        getForSure( properties, propertyName ).updater = method ;

      } else throw new BadDeclarationException(
          "Unsupported property (must be named getXxx or withXxx):" + methodName ) ;
    }

    for( final Map.Entry< String, PropertyDeclaration > entry : properties.entrySet() ) {
      if( entry.getValue().getter == null ) {
        throw new BadDeclarationException( "Missing get" + entry.getKey() + " method" ) ;
      }
      if( entry.getValue().updater == null ) {
        throw new BadDeclarationException( "Missing with" + entry.getKey() + " method" ) ;
      }
      final Class< ? > updaterParameterType0 = entry.getValue().updater.getParameterTypes()[ 0 ] ;
      if( entry.getValue().getter.getReturnType() != updaterParameterType0 ) {
        throw new BadDeclarationException(
            "Incompatible types: '" +
            entry.getValue().updater.getName() + "' takes " + updaterParameterType0 +
            ", while '" +
            entry.getValue().getter.getName() + "' returns " +
                entry.getValue().getter.getReturnType()
        ) ;
      }
    }
    //noinspection unchecked
    return ( T ) Proxy.newProxyInstance(
        Pod.class.getClassLoader(),
        new Class< ? >[] { podClass },
        new PropertiesKeeper( podClass, EMPTY_MAP )
    ) ;
  }

  public static class BadDeclarationException extends RuntimeException {
    public BadDeclarationException( final String message ) {
      super( message );
    }
  }

  private static PropertyDeclaration getForSure(
      final Map< String, PropertyDeclaration > properties,
      final String propertyName
  ) {
    final PropertyDeclaration existingProperty = properties.get( propertyName ) ;
    if( existingProperty == null ) {
      final PropertyDeclaration newProperty = new PropertyDeclaration() ;
      properties.put( propertyName, newProperty ) ;
      return newProperty ;
    } else {
      return existingProperty ;
    }
  }


  private static class PropertyDeclaration {
    public Method getter = null ;
    public Method updater = null ;
  }


  private static final ImmutableMap< String ,Object > EMPTY_MAP = ImmutableMap.of() ;


  private static class PropertiesKeeper implements InvocationHandler {

    private final Class< ? > podClass ;
    private final Map< String, Object > values ;

    private PropertiesKeeper(
        final Class< ? > podClass,
        final Map< String, Object > values
    ) {
      this.podClass = podClass ;
      this.values = ImmutableMap.copyOf( values ) ;
    }

    public Object invoke(
        final Object proxy,
        final Method method,
        final Object[] args
    ) throws Throwable {
      final String methodName = method.getName() ;
      if( methodName.startsWith( "with" ) ) {
        final String propertyName = methodName.substring( 4 ) ;
        final Map< String, Object > updatedValues = Maps.newHashMap() ;
        updatedValues.putAll( values ) ;
        updatedValues.put( propertyName, args[ 0 ] ) ;
        return Proxy.newProxyInstance(
            Pod.class.getClassLoader(),
            new Class< ? >[] { podClass },
            new PropertiesKeeper( podClass, updatedValues )
        ) ;
      }
      if( methodName.startsWith( "get" ) ) {
        final String propertyName = methodName.substring( 3 ) ;
        final Object value = values.get( propertyName ) ;
        final Class< ? > returnType = method.getReturnType() ;
        if( value == null && returnType.isPrimitive() ) {
          return 0 ;
        } else {
          return value ;
        }
      }
      throw new IllegalStateException(
          "This should not happend: invoking unsupported method " + methodName ) ;

    }
  }


  public @interface Converter {
    Class< ? > converterClass() ; 
  }
}
