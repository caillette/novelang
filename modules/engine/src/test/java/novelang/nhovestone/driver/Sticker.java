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
package novelang.nhovestone.driver;

/**
 * Represents some kind of unique identifier to attach to created system processes as Java
 * system property.
 *
 * @see novelang.nhovestone.driver.VirtualMachineTools#findVirtualMachineWith(Sticker, boolean) 
 *
 * @author Laurent Caillette
 */
public final class Sticker {

  @SuppressWarnings( { "StaticNonFinalField" } )
  private static Long lastGenerated = null ;

  private static final Object LOCK = new Object() ;

  private final long value ;

  private Sticker( final long value ) {
    this.value = value ;
  }

  @SuppressWarnings( { "SleepWhileHoldingLock", "CallToNativeMethodWhileLocked" } )
  public static Sticker create() {
    long generated = System.currentTimeMillis() ;
    synchronized( LOCK ) {
      if( lastGenerated != null ) {
        while( generated <= lastGenerated ) {
          try {
            Thread.sleep( 1 ) ;
          } catch( InterruptedException e ) {
            throw new RuntimeException( "Should never happen", e ) ;
          }
          generated = System.currentTimeMillis() ;
        }
      }
      lastGenerated = generated ;
    }
    return new Sticker( generated ) ;
  }

  
  @Override
  public boolean equals( final Object other ) {
    if( this == other ) {
      return true ;
    }
    if( other == null || getClass() != other.getClass() ) {
      return false ;
    }

    final Sticker sticker = ( Sticker ) other ;

    if( value != sticker.value ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    return ( int ) ( value ^ ( value >>> 32 ) ) ;
  }

  public String asString() {
    return Long.toString( value ) ;
  }
}
