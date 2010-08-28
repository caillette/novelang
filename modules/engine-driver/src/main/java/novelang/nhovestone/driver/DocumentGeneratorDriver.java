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

import java.io.File;
import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import novelang.batch.DocumentGenerator;
import novelang.logger.Logger;
import novelang.logger.LoggerFactory;
import novelang.system.Husk;

/**
 * Starts and stops an {@link novelang.produce.DocumentProducer} in its deployment directory,
 * in a separate JVM.
 *
 * @author Laurent Caillette
 */
public class DocumentGeneratorDriver extends EngineDriver {

  private static final Logger LOGGER = LoggerFactory.getLogger( DocumentGeneratorDriver.class ) ;


  public DocumentGeneratorDriver( final Configuration configuration ) {
    super(
        enrichWithProgramArguments( configuration ),
        DocumentGenerator.COMMAND_NAME,
        PROCESS_STARTED_SENSOR
    ) ;
    Preconditions.checkNotNull( configuration.getOutputDirectory() ) ;
  }

  private static EngineDriver.Configuration enrichWithProgramArguments(
      final Configuration configuration
  )
  {
    return configuration
        .withProgramOtherOptions(
            "--" + novelang.configuration.parse.BatchParameters.OPTIONNAME_OUTPUTDIRECTORY,
            "" + configuration.getOutputDirectory()
        )
    ;
  }




  /**
   * Always performs a non-forced shutdown.
   */
  @Override
  public Integer shutdown( final boolean force ) throws InterruptedException, IOException {
    if( force ) {
      LOGGER.warn( "Forced shutdown not supported." ) ;
    }
    return super.shutdown( false ) ;
  }

  /**
   * Always returns true. This works because normal usage is to wait for natural process
   * termination.
   */
  private static final Predicate< String > PROCESS_STARTED_SENSOR = Predicates.alwaysTrue() ;


  @Husk.Converter( converterClass = ConfigurationHelper.class )
  public interface Configuration extends EngineDriver.Configuration< Configuration > {
    File getOutputDirectory() ;
    Configuration withOutputDirectory( File directory ) ;

  }

}
