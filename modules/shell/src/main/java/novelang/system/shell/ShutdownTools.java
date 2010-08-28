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
package novelang.system.shell ;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;

/**
 * Uses <a href="http://java.sun.com/javase/6/docs/jdk/api/attach/spec/index.html" >Attach API</a>
 * to shut JVMs down.
 * It shuts down every local JVM started with {@value #SHUTDOWN_TATTOO_PROPERTYNAME} system
 * property. The {@link ProcessShell#start()} method should
 * take care of this.
 *
 * @author Laurent Caillette
 */
public class ShutdownTools

{
  private static final Logger LOGGER = LoggerFactory.getLogger( ShutdownTools.class ) ;

  private ShutdownTools() { }


  public static final String SHUTDOWN_TATTOO_PROPERTYNAME = "novelang.system.shell.tattoo" ;

  public static void shutdownAllTattooedVirtualMachines() {
    shutdownAllTattooedVirtualMachines( AgentFileInstaller.getInstance().getJarFile() ) ;
  }

  private static void shutdownAllTattooedVirtualMachines( final File shutdownAgentJar ) {
    final List< VirtualMachineDescriptor > descriptors = VirtualMachine.list() ;
    for( final VirtualMachineDescriptor descriptor : descriptors ) {
      try {
        final VirtualMachine virtualMachine = VirtualMachine.attach( descriptor ) ;
        try {
          final Properties properties = virtualMachine.getSystemProperties() ;
          if( properties.containsKey( SHUTDOWN_TATTOO_PROPERTYNAME ) ) {
            shutdown( virtualMachine, shutdownAgentJar ) ;
          }
        } finally {
          virtualMachine.detach() ;
        }
      } catch( AttachNotSupportedException e ) {
        // Don't complain if the process already died.
        if( ! e.getMessage().startsWith( "no such process" ) ) {
          LOGGER.warn( "Counldn't attach, may be normal if other VM did shut down", e.getMessage() ) ;
        }
      } catch( IOException e ) {
        // Don't complain if the process already died.
        if( ! e.getMessage().startsWith( "no such process" ) ) {
          LOGGER.warn( "Counldn't attach, may be normal if other VM did shut down", e.getMessage() ) ;
        }

      }
    }
  }

  private static void shutdown( final VirtualMachine virtualMachine, final File shutdownAgentJar ) {
    final String agentJarAbsolutePath = shutdownAgentJar.getAbsolutePath() ;
    LOGGER.warn( "Forcing shutdown of Virtual Machine '", virtualMachine.id(), "' using '",
        agentJarAbsolutePath, "'..." ) ;
    try {
      virtualMachine.loadAgent( agentJarAbsolutePath ) ;
    } catch( Exception e ) {
      LOGGER.warn( "Couldn't load agent on ", virtualMachine.id(),
          " (maybe it's shutting down?): ", e.getMessage() ) ;
    }
  }

  @SuppressWarnings( { "FieldCanBeLocal", "StaticNonFinalField" } )
  private static boolean shutdownHookInstalled = false ;
  private static final Object LOCK = new Object() ;

  public static void installShutdownHookIfNeeded( final File shutdownAgentJar ) {
    synchronized( LOCK ) {
      if( !shutdownHookInstalled ) {
        LOGGER.info( "Installing shutdown hook..." ) ;
        final Thread thread = new Thread(
            new Runnable() {

              @Override
              public void run() {
                LOGGER.info( "Executing shutdown hook..." ) ;
                shutdownAllTattooedVirtualMachines( shutdownAgentJar ) ;
              }
            },
            "Shut all tattooed VMs down"
        ) ;
        thread.setDaemon( true ) ;
        Runtime.getRuntime().addShutdownHook( thread ) ;
        shutdownHookInstalled = true ;
      }
    }
  }

}
