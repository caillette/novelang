/*
 * Copyright (C) 2010 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.benchmark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import novelang.Version;
import novelang.VersionFormatException;
import novelang.benchmark.report.Grapher;
import novelang.benchmark.scenario.MeasurementBundle;
import novelang.benchmark.scenario.Scenario;
import novelang.benchmark.scenario.ScenarioLibrary;
import novelang.benchmark.scenario.TimeMeasurement;
import novelang.benchmark.scenario.TimeMeasurer;
import novelang.common.FileTools;
import novelang.system.DefaultCharset;
import novelang.system.EnvironmentTools;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.io.output.FileWriterWithEncoding;

import static novelang.benchmark.KnownVersions.VERSION_0_35_0;
import static novelang.benchmark.KnownVersions.VERSION_0_38_1;
import static novelang.benchmark.KnownVersions.VERSION_0_41_0;

/**
 * Main class generating the Nhovestone report.
 *
 * @author Laurent Caillette
 */
public class Nhovestone {

  private static final Log LOG = LogFactory.getLog( Nhovestone.class );


  public static void main( final String[] args )
      throws
      IOException,
      URISyntaxException,
      ProcessDriver.ProcessCreationFailedException,
      VersionFormatException,
      InterruptedException
  {

    EnvironmentTools.logSystemProperties() ;

    final File scenariiDirectory = FileTools.createFreshDirectory( "_scenario-demo" ) ;
    final File versionsDirectory = new File( "distrib" ) ;

    final ScenarioLibrary.ConfigurationForTimeMeasurement baseConfiguration =
        Husk.create( ScenarioLibrary.ConfigurationForTimeMeasurement.class )
        .withWarmupIterationCount( 100 )
        .withMaximumIterations( 1000 )
        .withScenariiDirectory( scenariiDirectory )
        .withInstallationsDirectory( versionsDirectory )
        .withVersions( VERSION_0_41_0, VERSION_0_38_1, VERSION_0_35_0 )
        .withFirstTcpPort( 9900 )
        .withJvmHeapSizeMegabytes( 32 )
        .withMeasurer( new TimeMeasurer() )
    ;

    runScenario( baseConfiguration
        .withScenarioName( "Single ever-growing Novella" )
        .withUpsizerFactory( ScenarioLibrary.createNovellaLengthUpsizerFactory( new Random( 0L ) ) )
        ,
        false
    ) ;

    runScenario( baseConfiguration
        .withScenarioName( "Increasing Novella count" )
        .withUpsizerFactory( ScenarioLibrary.createNovellaCountUpsizerFactory( new Random( 0L ) ) )
        ,
        true
    ) ;
    
    writeNhovestoneParameters( 
        new File( scenariiDirectory, "report-parameters.nlp" ), 
        baseConfiguration 
    ) ;

    System.exit( 0 ) ;
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
      final boolean showUpsizingCount 

  )
      throws IOException, ProcessDriver.ProcessCreationFailedException, InterruptedException
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

    LOG.info( "Wrote " + imageDestinationFile.getAbsolutePath() ) ;
  }

}
