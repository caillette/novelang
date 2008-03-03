/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.gunit;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang.StringUtils;

/**
 * Wraps a call to {@code org.antlr.gunit.Interp} for running several suites from one program.
 * GUnit's main class is invoked dynamically as we don't need GUnit in project classpath.
 *
 * @author Laurent Caillette
 */
public class Launcher {

  public static void main( String[] args ) throws Throwable {

    if( args.length < 1 ) {
      throw new IllegalArgumentException( "No command-line parameters" ) ;
    }
    final Class gUnitInterpreterClass = Class.forName( "org.antlr.gunit.Interp" ) ;
    final Method mainMethod = gUnitInterpreterClass.getMethod(
        "main",
        String[].class
    ) ;

    for( String arg : args ) {

      try {
        // Hack for making runtime type-safety enforcement happy.
        //noinspection RedundantArrayCreation
        mainMethod.invoke( null, new Object[] { new String[] { arg } } ) ;
      } catch( InvocationTargetException e ) {
        throw e.getCause() ;
      }
    }

  }
}
