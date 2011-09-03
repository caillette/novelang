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

package org.novelang.daemon;

import org.fest.reflect.core.Reflection;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link LocalhostOnlyHandler}.
 * 
 * @author Laurent Caillette
 */
public class LocalhostOnlyHandlerTest {
  
  @Test
  public void localHostIpv4() {
    expect( "127.0.0.1", true ) ;
    expect( "127.0.0.3", true ) ;
  }

  @Test
  public void localHostIpv6() {
    expect( "0:0:0:0:0:0:0:1%0", true ) ;        
  }
  
  @Test
  public void remoteHostIpv4() {
    expect( "/10.103.6.90", false ) ;
    expect( "foo.net/69.122.147.176", false ) ;
  }

  @Test
  public void remoteHostIpv6() {
    expect( "2001:db8:85a3:0:0:8a2e:370:7334", false ) ;        
  }
  

// =======
// Fixture  
// =======
  
  private static void expect( final String hostname, final boolean localhost ) {
    Assert.assertEquals( "'" + hostname + "'", localhost, callIsLocalhostMethod( hostname ) ) ;
  }

  private static Boolean callIsLocalhostMethod( final String hostname ) {
    return Reflection.staticMethod( "isLocalhost" ).
        withReturnType( Boolean.TYPE ).
        withParameterTypes( String.class ).
        in( LocalhostOnlyHandler.class ).
        invoke( hostname )
    ;
  }

}
