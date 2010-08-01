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
package novelang.nhovestone.report;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import novelang.KnownVersions;
import novelang.Version;
import novelang.nhovestone.MeasurementBundle;
import novelang.nhovestone.Scenario;
import novelang.nhovestone.scenario.TimeMeasurement;
import novelang.nhovestone.scenario.TimeMeasurer;
import novelang.common.FileTools;
import novelang.system.Log;
import novelang.system.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Laurent Caillette
 */
public class GrapherDemo {

  private static final Log LOG = LogFactory.getLog( GrapherDemo.class );
  

  public static List< Long > buildUpsizings() {
    final List< Long > list = Lists.newArrayList() ;
    list.add( 455L ) ;
    list.add( 525L ) ;
    list.add( 435L ) ;
    list.add( 400L ) ;
    list.add( 388L ) ;
    list.add( 482L ) ;
    list.add( 451L ) ;
    list.add( 478L ) ;
    list.add( 448L ) ;
    list.add( 411L ) ;
    list.add( 490L ) ;
    list.add( 476L ) ;
    list.add( 434L ) ;
    list.add( 472L ) ;
    list.add( 410L ) ;
    list.add( 460L ) ;
    list.add( 415L ) ;
    list.add( 463L ) ;
    list.add( 410L ) ;
    list.add( 409L ) ;
    list.add( 437L ) ;
    list.add( 501L ) ;
    list.add( 409L ) ;
    list.add( 404L ) ;
    list.add( 384L ) ;
    list.add( 442L ) ;
    list.add( 461L ) ;
    list.add( 438L ) ;
    list.add( 418L ) ;
    list.add( 419L ) ;
    list.add( 460L ) ;
    list.add( 470L ) ;
    list.add( 438L ) ;
    list.add( 472L ) ;
    list.add( 420L ) ;
    list.add( 430L ) ;
    list.add( 450L ) ;
    list.add( 418L ) ;
    list.add( 493L ) ;
    list.add( 449L ) ;
    list.add( 434L ) ;
    list.add( 411L ) ;
    list.add( 490L ) ;
    list.add( 476L ) ;
    list.add( 472L ) ;
    list.add( 463L ) ;
    list.add( 410L ) ;
    list.add( 410L ) ;
    list.add( 460L ) ;
    list.add( 415L ) ;
    list.add( 409L ) ;
    list.add( 437L ) ;

    return ImmutableList.copyOf( list ) ;
  }

  public static Map< Version, MeasurementBundle< TimeMeasurement > > buildMap() {

    final Map< Version, MeasurementBundle< TimeMeasurement > > map = Maps.newHashMap() ;
    
    final List< TimeMeasurement > timeMeasurements1 = Lists.newArrayList() ;
    
    timeMeasurements1.add( new TimeMeasurement( 137L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 160L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 368L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 221L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 249L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 182L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 533L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 805L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 511L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 351L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 341L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 279L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 329L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 496L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 445L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 524L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 172L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 374L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 810L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 1131L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 190L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 425L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 313L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 498L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 316L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 289L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 539L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 329L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 388L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 433L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 434L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 475L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 468L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 466L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 476L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 573L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 715L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 544L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 768L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 592L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 688L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 769L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 681L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 667L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 895L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 1188L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 1231L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 1319L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 1579L ) ) ;
    timeMeasurements1.add( new TimeMeasurement( 3492L ) ) ;

    final MeasurementBundle< TimeMeasurement > measurementBundle1 =
        new MeasurementBundle< TimeMeasurement >(
            timeMeasurements1, Scenario.Terminations.ITERATION_COUNT_EXCEEDED )
    ;

    map.put( KnownVersions.VERSION_0_38_1, measurementBundle1 ) ;



    final List< TimeMeasurement > timeMeasurements2 = Lists.newArrayList() ;

    timeMeasurements2.add( new TimeMeasurement( 214L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 132L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 436L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 200L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 250L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 334L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 493L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 582L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 239L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 299L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 224L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 263L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 333L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 361L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 308L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 325L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 284L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 274L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 1127L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 315L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 380L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 237L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 357L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 327L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 300L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 270L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 286L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 329L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 311L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 307L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 411L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 295L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 308L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 502L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 344L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 484L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 637L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 588L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 486L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 545L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 735L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 825L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 1103L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 1192L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 1069L ) ) ;
    timeMeasurements2.add( new TimeMeasurement( 1444L ) ) ;

    final MeasurementBundle< TimeMeasurement > measurementBundle2 =
        new MeasurementBundle< TimeMeasurement >(
            timeMeasurements2, TimeMeasurer.Terminations.STRAIN )
    ;

    map.put( KnownVersions.VERSION_0_41_0, measurementBundle2 ) ;

    return map ;

  }

  public static void main( final String[] args ) throws IOException {
    final File scenarioDirectory = FileTools.createFreshDirectory( "_scenario-demo" ) ;

    final BufferedImage image = Grapher.create( buildUpsizings(), buildMap(), false ) ;
    final File imageFile = new File( scenarioDirectory, "graph.png" );
    ImageIO.write( image, "png", imageFile ) ;

    LOG.info( "Wrote " + imageFile.getAbsolutePath() ) ;

  }
}
