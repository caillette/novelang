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

import static com.google.common.base.Preconditions.checkState;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;

/**
 * Traps calls to {@link System#exit(int)} by installing some hacky {@code SecurityManager}.
 * This class is thread-unsafe and installs a {@code SecurityManager} allowing everything
 * (except JVM termination).
 * 
 * @author Laurent Caillette
 */
public class NoSystemExit {

  private static final Logger LOGGER = LoggerFactory.getLogger( NoSystemExit.class ) ;

  public static final NoSystemExit INSTANCE = new NoSystemExit() ;

  /**
   * Synchronize access on every field on this object.
   */
  private final Object lock = new Object() ;

  private SecurityManager previous = null ;
  private boolean installed = false ;


  public void install() {
    synchronized( lock ) {
      checkState( ! installed, "Alread installed" ) ;
      this.previous = System.getSecurityManager() ;
      final SecurityManager securityManager = new NoExitSecurityManager() ;
      System.setSecurityManager( securityManager ) ;
      installed = true ;
    }
  }

  public void uninstall() {
    synchronized( lock ) {
      checkState( installed, "Not installed" ) ;
      System.setSecurityManager( previous ) ;
      installed = false ;
    }
  }

  public static class ExitTrappedException extends SecurityException { }


  private static class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkPermission( final Permission permission ) {
    final String permissionName = permission.getName() ;
      if( permissionName.startsWith( "exitVM" ) ) {
        LOGGER.debug( "Checking permission " + permissionName ) ;
        throw new ExitTrappedException() ;
      }
    }
  }
}
