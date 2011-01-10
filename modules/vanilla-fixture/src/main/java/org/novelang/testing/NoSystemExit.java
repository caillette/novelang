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
package org.novelang.testing;

import java.security.Permission;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

import static com.google.common.base.Preconditions.checkState;

/**
 * Traps calls to {@link System#exit(int)} by installing some hacky {@code SecurityManager}.
 * This class is thread-unsafe and installs a {@code SecurityManager} allowing everything
 * (except JVM termination).
 * 
 * @author Laurent Caillette
 */
public class NoSystemExit {

  private static final Logger LOGGER = LoggerFactory.getLogger( NoSystemExit.class ) ;

  private final SecurityManager previous ;
  private final AtomicBoolean installed = new AtomicBoolean( true ) ;

  public NoSystemExit() {
    this.previous = System.getSecurityManager() ;
    install() ;
  }

  public static class ExitTrappedException extends SecurityException { }

  private static void install() {
    final SecurityManager securityManager = new SecurityManager() {
      @java.lang.Override
      public void checkPermission( final Permission permission ) {
	    final String permissionName = permission.getName() ;
        if( permissionName.startsWith( "exitVM" ) ) {
          LOGGER.debug( "Checking permission " + permissionName ) ;
          throw new ExitTrappedException() ;
        }
      }
    } ;
    System.setSecurityManager( securityManager ) ;
  }

  public void uninstall() {
    checkState( installed.compareAndSet( true, false ), "Alread uninstalled" ) ;
    System.setSecurityManager( previous ) ;
  }


}
