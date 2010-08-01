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

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Find {@link VirtualMachine} instances, started with a {@link Sticker} value set by the
 * {@value #SYSTEMPROPERTYNAME_STICKER} system property.
 *
 * @author Laurent Caillette
 */
public class VirtualMachineTools {

  private static final Log LOG = LogFactory.getLog( VirtualMachineTools.class );
  public static final String SYSTEMPROPERTYNAME_STICKER = "-Dnovelang.nhovestone.driver.sticker" ;

  /**
   * Returns a handler to the JVM process started with given {@code sticker} as system property.
   *
   * @param sticker a non-null object.
   * @param keepAttached true to return a {@link com.sun.tools.attach.VirtualMachine} that's already
   *        attached to the process.
   * @return a possibly null value.
   */
  public static VirtualMachine findVirtualMachineWith(
      final Sticker sticker,
      final boolean keepAttached
  ) {
    Preconditions.checkNotNull( sticker ) ;

    final List< VirtualMachine > virtualMachinesFound = findVirtualMachines(
        new Predicate< Properties >() {
          public boolean apply( final Properties systemProperties  ) {
            final String stickerValue = systemProperties.getProperty( SYSTEMPROPERTYNAME_STICKER ) ;
            return sticker.asString().equals( stickerValue ) ;
          }
        },
        true,
        keepAttached
    ) ;

    if( virtualMachinesFound.isEmpty() ) {
      return null ;
    } else {
      return virtualMachinesFound.get( 0 ) ;
    }

  }

  /**
   * Returns the list of every {@link VirtualMachine} referencing a JVM process started with
   * the {@value #SYSTEMPROPERTYNAME_STICKER} system property.
   *
   * @param keepAttached true to return a {@link com.sun.tools.attach.VirtualMachine} that's already
   *        attached to the process.
   * @return a non-null but possibly empty list containing no nulls.
   */
  public static List< VirtualMachine > findVirtualMachinesWithSticker(
      final boolean keepAttached
  ) {
    return findVirtualMachines(
        new Predicate< Properties >() {
          public boolean apply( final Properties systemProperties  ) {
            return systemProperties.containsKey( SYSTEMPROPERTYNAME_STICKER ) ;
          }
        },
        true,
        keepAttached
    ) ;
  }


  private static List< VirtualMachine > findVirtualMachines(
      final Predicate< Properties > predicateOnSystemProperties,
      final boolean stopAtFirstMatch,
      final boolean keepAttached
  ) {
    final List< VirtualMachineDescriptor > descriptors = VirtualMachine.list() ;
    final List< VirtualMachine > result = Lists.newArrayList() ;
    for( final VirtualMachineDescriptor descriptor : descriptors ) {
      final VirtualMachine virtualMachine ;
      try {
        virtualMachine = VirtualMachine.attach( descriptor );
        try {
          final Properties systemProperties = virtualMachine.getSystemProperties() ;
          if( predicateOnSystemProperties.apply( systemProperties ) ) {
            result.add( virtualMachine ) ;
            if( stopAtFirstMatch && ! result.isEmpty() ) {
              break ;
            }
          }
        } finally {
          if( ! keepAttached ) {
            try {
              virtualMachine.detach() ;
            } catch( IOException e ) {
              LOG.error( "Couldn't detach from Virtual Machine properly", e ) ;
            }
          }
        }
      } catch( AttachNotSupportedException e ) {
        LOG.info( "Couldn't attach to " + descriptor ) ;
      } catch( IOException e ) {
        throw new RuntimeException( e ) ;
      }
    }
    return result ;
  }


}
