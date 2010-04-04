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
import java.net.URISyntaxException;
import java.util.Arrays;
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
import novelang.system.EnvironmentTools;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;

import static novelang.benchmark.KnownVersions.VERSION_0_35_0;
import static novelang.benchmark.KnownVersions.VERSION_0_38_1;
import static novelang.benchmark.KnownVersions.VERSION_0_41_0;

/**
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

    final ScenarioLibrary.ConfigurationForTimeMeasurement configuration =
        Husk.create( ScenarioLibrary.ConfigurationForTimeMeasurement.class )
        .withScenarioName( "Single Novella growing" )
        .withWarmupIterationCount( 100 )
        .withMaximumIterations( 1000 )
        .withScenariiDirectory( scenariiDirectory )
//        .withUpsizerFactory( ScenarioLibrary.createNovellaLengthUpsizerFactory( new Random( 0L ) ) )
        .withUpsizerFactory( ScenarioLibrary.createNovellaCountUpsizerFactory( new Random( 0L ) ) )
        .withInstallationsDirectory( versionsDirectory )
        .withVersions( VERSION_0_41_0, VERSION_0_38_1, VERSION_0_35_0 )
        .withFirstTcpPort( 9900 )
        .withMeasurer( new TimeMeasurer() )
    ;

    final Scenario< TimeMeasurement > scenario =
        new Scenario< TimeMeasurement >( configuration
        )
    ;

    scenario.run() ;

    final Map< Version, MeasurementBundle< TimeMeasurement > > measurements =
        scenario.getMeasurements() ;

    final BufferedImage image = Grapher.create( "Single ever-growing Novella", measurements ) ;
    ImageIO.write( image, "png", new File( scenariiDirectory, "graph-single-novella.png" ) ) ;


    System.exit( 0 ) ;
  }

}
