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
package novelang.benchmark.report;

import com.google.common.collect.Lists;
import novelang.Version;
import novelang.benchmark.scenario.MeasurementBundle;
import novelang.benchmark.scenario.TimeMeasurement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Laurent Caillette
 */
public class Grapher {

  public static BufferedImage create(
      final String title,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements
  ) {

    final List< Version > versions = Lists.newArrayList( measurements.keySet() ) ;
    Collections.sort( versions ) ;


    final DefaultCategoryDataset dataset = new DefaultCategoryDataset() ;

    for( final Version version : versions ) {
      final MeasurementBundle< TimeMeasurement > measurementBundle = measurements.get( version ) ;
      int i = 0 ;
      for( final TimeMeasurement measurement : measurementBundle ) {
        dataset.addValue(
            ( double ) measurement.getTimeMilliseconds() / 1000.0,
            version.getName(),
            ( Comparable ) i ++
        ) ;
      }
    }

    final JFreeChart chart = ChartFactory.createLineChart(
        title,                     // chart title
        "Pass",                    // domain axis label
        "Duration (s)",            // range axis label
        dataset,                   // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        false,                     // tooltips
        false                      // urls
    ) ;


    final CategoryPlot plot = ( CategoryPlot ) chart.getPlot() ;
    plot.setBackgroundPaint( Color.lightGray ) ;
    plot.setRangeGridlinePaint( Color.white ) ;

    // customise the range axis...
    final NumberAxis rangeAxis = ( NumberAxis ) plot.getRangeAxis() ;
    rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() ) ;
    rangeAxis.setAutoRangeIncludesZero( true ) ;
    
    final BufferedImage bufferedImage = chart.createBufferedImage( 800, 400 ) ;

    return bufferedImage ;

  }

  

}
