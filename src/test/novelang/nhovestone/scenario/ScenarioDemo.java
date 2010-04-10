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
package novelang.nhovestone.scenario;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import novelang.Version;
import novelang.nhovestone.ProcessDriver;
import novelang.nhovestone.report.Grapher;
import novelang.common.FileTools;
import novelang.novelist.Novelist;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;

import static novelang.nhovestone.KnownVersions.VERSION_0_38_1;
import static novelang.nhovestone.KnownVersions.VERSION_0_41_0;

/**
 * @author Laurent Caillette
 */
public class ScenarioDemo {

  private static final Log LOG = LogFactory.getLog( ScenarioDemo.class ) ;


  public static void main( final String[] args )
      throws IOException, InterruptedException, ProcessDriver.ProcessCreationFailedException
  {
    final File scenarioDirectory = FileTools.createFreshDirectory( "_scenario-demo" ) ;
    final File versionsDirectory = new File( "distrib" ) ;

    final Novelist.LevelGeneratorSupplierWithDefaults levelGenerator =
        new Novelist.LevelGeneratorSupplierWithDefaults() ;

    final ScenarioLibrary.ConfigurationForTimeMeasurement configuration =
        Husk.create( ScenarioLibrary.ConfigurationForTimeMeasurement.class )
        .withScenarioName( "Single Novella growing" )
        .withWarmupIterationCount( 1 )
        .withMaximumIterations( 10 )
        .withScenariiDirectory( scenarioDirectory )
        .withUpsizerFactory( ScenarioLibrary.createNovellaLengthUpsizerFactory( new Random( 0L ) ) )
        .withInstallationsDirectory( versionsDirectory )
        .withVersions( Arrays.asList( VERSION_0_41_0, VERSION_0_38_1/*, VERSION_0_35_0*/ ) )
        .withFirstTcpPort( 9900 )
        .withMeasurer( new TimeMeasurer() )
    ;

    final Scenario< Long, TimeMeasurement > scenario =
        new Scenario< Long, TimeMeasurement >( configuration ) ;

    scenario.run() ;

    final Map< Version, MeasurementBundle< TimeMeasurement > > measurements =
        scenario.getMeasurements() ;
    final List< Long > upsizings = scenario.getUpsizings() ;

    final BufferedImage image = Grapher.create( upsizings, measurements, false ) ;
    ImageIO.write( image, "png", new File( scenarioDirectory, "graph.png" ) ) ;

  }
}
