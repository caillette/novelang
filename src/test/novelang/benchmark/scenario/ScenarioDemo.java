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
package novelang.benchmark.scenario;

import novelang.benchmark.KnownVersions;
import novelang.benchmark.ProcessDriver;
import novelang.common.FileTools;
import novelang.novelist.Novelist;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Laurent Caillette
 */
public class ScenarioDemo {


  public static void main( final String[] args )
      throws IOException, InterruptedException, ProcessDriver.ProcessCreationFailedException
  {
    final File scenarioDirectory = FileTools.createFreshDirectory( "scenario-demo" ) ;
    final File versionsDirectory = new File( "distrib" ) ;

    final Novelist.LevelGeneratorSupplierWithDefaults levelGenerator =
        new Novelist.LevelGeneratorSupplierWithDefaults() ;

    final Upsizer.Factory upsizerFactory = new Upsizer.Factory() {

      public Upsizer create( final File directory ) throws IOException {
        return new Upsizer.ForNovelist(
            new Novelist( directory, "demo", levelGenerator, 1 ) ) ;
      }

      public String getDocumentRequest() {
        return "/" + Novelist.BOOK_NAME_RADIX + ".html" ;
      }
    } ;


    final Scenario< TimeMeasurement > scenario = new Scenario< TimeMeasurement >(
        scenarioDirectory,
        upsizerFactory,
        versionsDirectory,
        Arrays.asList( KnownVersions.VERSION_0_41_0 ),
        9900,
        new TimeMeasurer()
    ) ;

    scenario.run() ;
   }
}
