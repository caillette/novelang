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
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import novelang.batch.DocumentGenerator;
import novelang.configuration.parse.DaemonParameters;
import novelang.daemon.HttpDaemon;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;

/**
 * Starts and stops an {@link novelang.produce.DocumentProducer} in its deployment directory,
 * in a separate JVM.
 *
 * @author Laurent Caillette
 */
public class DocumentGeneratorDriver extends EngineDriver {

  private static final Log LOG = LogFactory.getLog( DocumentGeneratorDriver.class ) ;


  public DocumentGeneratorDriver( final Configuration configuration ) {
    super(
        configuration,
        DocumentGenerator.COMMAND_NAME,
        PROCESS_STARTED_SENSOR
    ) ;
  }




  /**
   * Always performs a non-forced shutdown.
   */
  @Override
  public void shutdown( final boolean force ) throws InterruptedException {
    if( force ) {
      LOG.warn( "Forced shutdown not supported." ) ;
    }
    super.shutdown( false ) ;
  }

  /**
   * Always returns true. This works because normal usage is to wait for natural process
   * termination.
   */
  private static final Predicate< String > PROCESS_STARTED_SENSOR = new Predicate< String >() {
    public boolean apply( final String lineInConsole ) {
      return true ;
    }
  } ;


  @Husk.Converter( converterClass = ConfigurationHelper.class )
  public interface Configuration extends EngineDriver.Configuration< Configuration > {

  }

}
