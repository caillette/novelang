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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link Pod}.
 *
 * @author Laurent Caillette
 */
public class PodTest {


  @Test
  public void vanillaPod() {
    final VanillaPod initialBun = Pod.make( VanillaPod.class ) ;
    assertNull( initialBun.getString() ) ;
    assertEquals( 0L, ( long ) initialBun.getInt() ) ;
    final VanillaPod updated = initialBun.withInt( 1 ).withString( "Foo" ).withFloat( 2.0f ) ;
    assertEquals( "Foo", updated.getString() ) ;
    assertEquals( 1L, ( long ) updated.getInt() ) ;
    assertEquals( 2.0, 0.0,( double ) updated.getFloat() ) ;

    // Check possible side-effects, too.
    assertNull( initialBun.getString() ) ;
    assertEquals( 0L, ( long ) initialBun.getInt() ) ;

  }

// =======
// Fixture
// =======

  public interface VanillaPod {

    String getString() ;
    VanillaPod withString( String newString ) ;

    int getInt() ;
    VanillaPod withInt( int newInt ) ;

    float getFloat() ;
    VanillaPod withFloat( float newFloat ) ;

  }

  @Pod.Converter( converterClass = SomeConverter.class )
  public interface ConvertedPod {

    String getString() ;
    ConvertedPod withString( int i, float f ) ;
  }

  public static final class SomeConverter {
    public static String convert( final int i, final float f ) {
      return "" + i + ", " + f ;
    }
  }
}
