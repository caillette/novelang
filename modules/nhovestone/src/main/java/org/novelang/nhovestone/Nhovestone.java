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
package org.novelang.nhovestone;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import javax.imageio.ImageIO;
import org.apache.commons.lang.SystemUtils;
import org.novelang.Version;
import org.novelang.VersionFormatException;
import org.novelang.common.FileTools;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.nhovestone.report.Grapher;
import org.novelang.nhovestone.scenario.ScenarioLibrary;
import org.novelang.nhovestone.scenario.TimeMeasurement;
import org.novelang.nhovestone.scenario.TimeMeasurer;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.EnvironmentTools;
import org.novelang.outfit.Husk;
import org.novelang.outfit.LogbackConfigurationTools;
import org.novelang.outfit.shell.ProcessCreationException;
import org.novelang.outfit.shell.ProcessInitializationException;
import org.apache.commons.io.output.FileWriterWithEncoding;

import static org.novelang.KnownVersions.VERSION_0_35_0;
import static org.novelang.KnownVersions.VERSION_0_38_1;
import static org.novelang.KnownVersions.VERSION_0_41_0;

/**
 * Main class generating the Nhovestone report.
 *
 * @author Laurent Caillette
 */
public class Nhovestone {

  private Nhovestone() { }


  public static void main( final String... arguments )
      throws
      IOException,
      ProcessCreationException,
      VersionFormatException,
      InterruptedException,
      ProcessInitializationException
  {
    LogbackConfigurationTools.fixLogDirectory( new File( SystemUtils.USER_DIR ) ) ;
    LoggerFactory.configurationComplete() ;

    final Logger log = LoggerFactory.getLogger( Nhovestone.class );
    log.info( "Running with command-line arguments ", ImmutableList.of( arguments ), "..." ) ;
    EnvironmentTools.logSystemProperties() ;


    final File scenariiDirectory ;
    final File versionsDirectory ;
    final Iterable< Version > versions ;
    if( arguments.length == 3 ) {
      scenariiDirectory = FileTools.createFreshDirectory( arguments[ 0 ] ) ;
      versionsDirectory = new File( arguments[ 1 ] ) ;
      versions = NhovestoneTools.parseVersions( arguments[ 2 ] ) ;
    } else {
      if( arguments.length != 0 ) {
        throw new IllegalArgumentException( "Usage: " + Nhovestone.class.getSimpleName() + 
            " [ < scenarii-dir > < distrib-dir > < comma-separated-versions > ]" ) ;
      }
      scenariiDirectory = FileTools.createFreshDirectory( "_nhovestone" ) ;
      versionsDirectory = new File( "distrib" ) ;
      versions = Arrays.asList( VERSION_0_41_0, VERSION_0_38_1, VERSION_0_35_0 ) ;
    }


    run( log, scenariiDirectory, versionsDirectory, versions, 1000, 10000, 32 ) ;

    System.exit( 0 ) ;
  }

  public static void run(
      final Logger log,
      final File scenariiDirectory,
      final File versionsDirectory,
      final Iterable<Version> versions,
      final int warmupIterationCount,
      final int maximumIterations,
      final int jvmHeapSizeMegabytes
  )
      throws
      IOException,
      ProcessCreationException,
      InterruptedException,
      ProcessInitializationException
  {
    final ScenarioLibrary.ConfigurationForTimeMeasurement baseConfiguration =
        Husk.create( ScenarioLibrary.ConfigurationForTimeMeasurement.class )
        .withWarmupIterationCount( warmupIterationCount )
        .withMaximumIterations( maximumIterations )
        .withScenariiDirectory( scenariiDirectory )
        .withInstallationsDirectory( versionsDirectory )
        .withVersions( versions )
        .withFirstTcpPort( 9900 )
        .withJvmHeapSizeMegabytes( jvmHeapSizeMegabytes )
        .withMeasurer( new TimeMeasurer() )
    ;

    runScenario( baseConfiguration
        .withScenarioName( "Single ever-growing Novella" )
        .withUpsizerFactory( ScenarioLibrary.createNovellaLengthUpsizerFactory( new Random( 0L ) ) )
        ,
        false,
        log
    ) ;

    runScenario( baseConfiguration
        .withScenarioName( "Increasing Novella count" )
        .withUpsizerFactory( ScenarioLibrary.createNovellaCountUpsizerFactory( new Random( 0L ) ) )
        ,
        true,
        log
    ) ;

    writeNhovestoneParameters(
        new File( scenariiDirectory, "report-parameters.novella" ),
        baseConfiguration
    ) ;
  }


  private static void writeNhovestoneParameters( 
      final File parametersFile, 
      final Scenario.Configuration< ?, ?, ? > configuration 
  ) throws IOException {
    final Writer writer = new FileWriterWithEncoding( parametersFile, DefaultCharset.SOURCE ) ;
    final PrintWriter printWriter = new PrintWriter( writer ) ;
    try {
      printWriter.println( "== VERSIONS" ) ;
      printWriter.println( "" ) ;
      for( final Version version : configuration.getVersions() ) {
        printWriter.println( "- `" + version.getName() + "`" ) ;  
      }
      printWriter.println( "" ) ;
      printWriter.println( "== NHOVESTONEPARAMETERS" ) ;
      printWriter.println( "" ) ;
      printRow( printWriter, "Warmup iterations", configuration.getWarmupIterationCount() ) ;
      printRow( printWriter, "Maximum iterations", configuration.getMaximumIterations() ) ;
      printRow( printWriter, "JVM heap size (MB)", configuration.getJvmHeapSizeMegabytes() ) ;
      printWriter.println( "" ) ;
      printWriter.println( "== JVMCHARACTERISTICS" ) ;
      printWriter.println( "" ) ;
      printRow( printWriter, "java.version" ) ;
      printRow( printWriter, "java.vm.name" ) ;
      printRow( printWriter, "os.arch" ) ;
      printRow( printWriter, "os.name" ) ;
      printRow( printWriter, "os.version" ) ;
      printRow( printWriter, "Available processors", Runtime.getRuntime().availableProcessors() ) ;
    } finally {
      printWriter.close() ;
    }
  }
  
  private static void printRow( final PrintWriter printWriter, final String systemPropertyName ) {
    final String systemPropertyValue = System.getProperty( systemPropertyName ) ;
    if( systemPropertyValue != null ) {
      printRow( printWriter, systemPropertyName, systemPropertyValue ) ;
    }
  }
  
  private static void printRow( 
      final PrintWriter printWriter, 
      final String cell1, 
      final int cell2 
  ) {
    printRow( printWriter, cell1, "" + cell2 ) ;
  }
  
  private static void printRow( 
      final PrintWriter printWriter, 
      final String cell1, 
      final String cell2 
  ) {
    printWriter.println( "| `" + cell1 + "` | `" + cell2 +  "` | " ) ;
  }

  private static void runScenario(
      final ScenarioLibrary.ConfigurationForTimeMeasurement configuration,
      final boolean showUpsizingCount,
      final Logger logger

  )
      throws
      IOException,
      ProcessCreationException,
      InterruptedException,
      ProcessInitializationException
  {
    final Scenario< Long, TimeMeasurement > scenario =
        new Scenario< Long, TimeMeasurement >( configuration ) ;

    scenario.run() ;

    final Map< Version, MeasurementBundle< TimeMeasurement > > measurements =
        scenario.getMeasurements() ;

    final List< Long > upsizings = scenario.getUpsizings() ;

    final BufferedImage image = Grapher.create( upsizings, measurements, showUpsizingCount ) ;

    final File imageDestinationFile = new File(
        configuration.getScenariiDirectory(),
        FileTools.sanitizeFileName( configuration.getScenarioName() ) + ".png"
    ) ;

    ImageIO.write( image, "png", imageDestinationFile ) ;

    logger.info( "Wrote ", imageDestinationFile.getAbsolutePath() ) ;
  }

}
