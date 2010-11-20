/*
 * Copyright (C) 2010 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.novelang.outfit;

import org.fest.assertions.Assert;
import org.fest.assertions.Assertions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for {@link Husk}.
 *
 * @author Laurent Caillette
 */
public class HuskTest {

  @Test
  public void emptyHusk() {
    Husk.create( Empty.class ) ;
  }

  @Test
  public void huskToString() {
    final Empty empty = Husk.create( Empty.class ) ;
    Assertions.assertThat( empty.toString() ).startsWith( Empty.class.getName() ) ;
  }

  @Test( expected = Husk.BadDeclarationException.class )
  public void typeMismatch() {
    Husk.create( Broken1.class ) ;
  }

  @Test( expected = Husk.BadDeclarationException.class )
  public void missingWith() {
    Husk.create( Broken2.class ) ;
  }

  @Test( expected = Husk.BadDeclarationException.class )
  public void missingGet() {
    Husk.create( Broken3.class ) ;
  }

  @Test( expected = Husk.BadDeclarationException.class )
  public void unknownMethodPrefix() {
    Husk.create( Broken4.class ) ;
  }



  @Test
  public void vanillaHusk() {
    final Vanilla initial = org.novelang.outfit.Husk.create( Vanilla.class ) ;
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

  @Test
  public void supportNull() {
    final Vanilla initial = org.novelang.outfit.Husk.create( Vanilla.class ) ;
    final Vanilla updated = initial.withString( null ) ;
    assertNull( updated.getString() ) ;
  }

  @Test
  public void inheritingHusk() {
    final Child initialChild = Husk.create( Child.class ) ;
    assertNull( initialChild.getInteger() ) ;
    assertNull( initialChild.getString() ) ;
    final Child updatedChild1 = initialChild.withInteger( 1 ) ;
    assertEquals( 1L, ( long ) updatedChild1.getInteger().intValue() ) ;
    final Child updatedChild2 = initialChild.withString( "ppiirre" ) ;
    assertEquals( "ppiirre", updatedChild2.getString() ) ;
  }

  @Test
  public void conversion() {
    final ConvertibleHusk initial = Husk.create( ConvertibleHusk.class ) ;
    final ConvertibleHusk updated = initial.withString( 1, 2.3f ) ;
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
  public interface Parent< CONFIGURATION extends Parent > {
    String getString() ;
    CONFIGURATION withString( String newString ) ;
  }

  public interface Child extends Parent< Child > {
    Integer getInteger() ;
    Child withInteger( Integer newInteger ) ;
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


  @Husk.Converter( converterClass = SomeConverter.class )
  public interface ConvertibleHusk {

    String getString() ;
    ConvertibleHusk withString( int i, float f ) ;
  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public static final class SomeConverter {

    private SomeConverter() {}

    public static String convert( final int i, final float f ) {
      return "" + i + ", " + f ;
    }
  }
}