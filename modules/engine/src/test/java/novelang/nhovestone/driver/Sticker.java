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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents some kind of unique identifier to attach to created system processes as Java
 * system property.
 *
 * @see novelang.nhovestone.driver.VirtualMachineTools#findVirtualMachineWith(Sticker, boolean) 
 *
 * @author Laurent Caillette
 */
public final class Sticker {

  private static final AtomicLong lastGenerated = new AtomicLong( System.currentTimeMillis() ) ;

  private final long value ;

  private Sticker( final long value ) {
    this.value = value ;
  }

  /**
   * Returns a unique value as long as nobody sets the system clock to a past value.
   * This seems preferable to use some persistent thing.
   * As far as I know, this method is thread-safe. If two threads write the {@link #lastGenerated}
   * with the same millisecond value, there is one which will detect its write made no change,
   * so it will wait for one turn.
   *
   * @return a non-null object holding a unique value.
   */
  public static Sticker create() {
    long generated = System.currentTimeMillis() ;
    for( ; generated == lastGenerated.getAndSet( generated ) ;
         generated = System.currentTimeMillis()
    ) {
      try {
        Thread.sleep( RESOLUTION ) ;
      } catch( InterruptedException e ) {
        throw new RuntimeException( "Should never happen", e ) ;
      }
    }
    return new Sticker( generated ) ;
  }


  /**
   * See <a href="http://blogs.sun.com/dholmes/entry/inside_the_hotspot_vm_clocks" >this article</a>.
   */
  private static final long RESOLUTION = 15L ;


  
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
