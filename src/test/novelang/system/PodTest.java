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
  public void emptyPod() {
    Pod.create( Empty.class ) ;
  }

  @Test( expected = Pod.BadDeclarationException.class )
  public void typeMismatch() {
    Pod.create( Broken1.class ) ;
  }

  @Test( expected = Pod.BadDeclarationException.class )
  public void missingWith() {
    Pod.create( Broken2.class ) ;
  }

  @Test( expected = Pod.BadDeclarationException.class )
  public void missingGet() {
    Pod.create( Broken3.class ) ;
  }

  @Test( expected = Pod.BadDeclarationException.class )
  public void unknownMethodPrefix() {
    Pod.create( Broken4.class ) ;
  }



  @Test
  public void vanillaPod() {
    final Vanilla initial = Pod.create( Vanilla.class ) ;
    assertNull( initial.getString() ) ;
    assertEquals( 0L, ( long ) initial.getInt() ) ;
    final Vanilla updated = initial.withInt( 1 ).withString( "Foo" ).withFloat( 2.0f ) ;
    assertEquals( "Foo", updated.getString() ) ;
    assertEquals( 1L, ( long ) updated.getInt() ) ;
    assertEquals( 2.0, 0.0,( double ) updated.getFloat() ) ;

    // Check possible side-effects, too.
    assertNull( initial.getString() ) ;
    assertEquals( 0L, ( long ) initial.getInt() ) ;
  }

  @Test //@Ignore( "Not implemented" )
  public void conversion() {
    final ConvertiblePod initial = Pod.create( ConvertiblePod.class ) ;
    final ConvertiblePod updated = initial.withString( 1, 2.3f ) ;
    assertEquals( "1, 2.3", updated.getString() ) ;
    assertNull( initial.getString() ) ;
  }

// =======
// Fixture
// =======

  public interface Empty { }

  public interface Vanilla {

    String getString() ;
    Vanilla withString( String newString ) ;

    int getInt() ;
    Vanilla withInt( int newInt ) ;

    float getFloat() ;
    Vanilla withFloat( float newFloat ) ;

  }


@SuppressWarnings( { "UnusedDeclaration" } )
  public interface Broken1 {
    String getString() ;
    Broken1 withString( int i ) ;
  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public interface Broken2 {
    String getSomething() ;
  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public interface Broken3 {
    Broken3 withSomething() ;
  }

  public interface Broken4 {
    void unsupported() ;
  }


  @Pod.Converter( converterClass = SomeConverter.class )
  public interface ConvertiblePod {

    String getString() ;
    ConvertiblePod withString( int i, float f ) ;
  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public static final class SomeConverter {
    public static String convert( final int i, final float f ) {
      return "" + i + ", " + f ;
    }
  }
}
