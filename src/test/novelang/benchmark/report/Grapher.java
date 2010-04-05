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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import novelang.Version;
import novelang.benchmark.scenario.MeasurementBundle;
import novelang.benchmark.scenario.Termination;
import novelang.benchmark.scenario.TimeMeasurement;
import novelang.benchmark.scenario.TimeMeasurer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

/**
 * Generates the graph representing {@link novelang.benchmark.scenario.Scenario#getMeasurements()}. 
 *
 * @author Laurent Caillette
 */
public class Grapher {
  private static final Color COLOR_GRADIENT_DARK = new Color( 136, 167, 189 );
  private static final Color COLOR_GRADIENT_LIGHT = new Color( 204, 237, 255 );

  public static BufferedImage create(
      final String title,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements
  ) {
    return create( title, measurements, 600, 300 ) ;
  }

  public static BufferedImage create(
      final String title,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements,
      final int widthPixels,
      final int heightPixels
  ) {

    final List< Version > versions = Lists.newArrayList( measurements.keySet() ) ;
    Collections.sort( versions, Ordering.from( Version.COMPARATOR ).reverse() ) ;


    final XYSeriesCollection dataset = new XYSeriesCollection() ;


    for( final Version version : versions ) {
      final MeasurementBundle< TimeMeasurement > measurementBundle = measurements.get( version ) ;
      int i = 0 ;
      final XYSeries series = new XYSeries( version.getName() ) ;
      for( final TimeMeasurement measurement : measurementBundle ) {
        series.add(
            ( double ) ++ i,
            convertToYValue( measurement )
        ) ;
      }
      dataset.addSeries( series ) ;
    }


    final JFreeChart chart = ChartFactory.createXYLineChart(
        title,                     // chart title
        "HttpDaemon call count",   // domain axis label
        "Duration (seconds)",      // range axis label
        dataset,                   // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        false,                     // tooltips
        false                      // urls
    ) ;


    final XYPlot plot = ( XYPlot ) chart.getPlot() ;


    final GradientPaint gradientPaint = new GradientPaint(
        0.0f, 0.0f, COLOR_GRADIENT_DARK,
        0.0f, 0.0f, COLOR_GRADIENT_LIGHT
    );
    plot.setBackgroundPaint( gradientPaint ) ;

    final XYSplineRenderer renderer = new XYSplineRenderer() ;
    for( int serieIndex = 0 ; serieIndex < measurements.size() ; serieIndex ++ ) {
      renderer.setSeriesShapesVisible( serieIndex, false ) ;
      final BasicStroke stroke = new BasicStroke(
          serieIndex == 0 ? 3.0f : 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) ;
      renderer.setSeriesStroke( serieIndex, stroke ) ;
    }
    plot.setRenderer( renderer ) ;

    final NumberAxis domainAxis = ( NumberAxis ) plot.getDomainAxis() ;
    domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() ) ;

    for( final Version version : versions ) {
      final MeasurementBundle< TimeMeasurement > measurementBundle = measurements.get( version ) ;
      final int measurementCount = measurementBundle.getMeasurementCount();
      final String annotationText ;
      annotationText = calculateTerminationText( measurementBundle.getTermination() ) ;
      if( annotationText != null && measurementCount > 0 ) {
        final TimeMeasurement measurement =
            measurementBundle.getMeasurement( measurementCount - 1 ) ;
        if( measurement != null ) {
          final XYTextAnnotation annotation = new XYTextAnnotation(
              annotationText,
              ( double ) measurementCount,
              convertToYValue( measurement )
          ) ;
          annotation.setTextAnchor( TextAnchor.BOTTOM_RIGHT ) ;
          plot.addAnnotation( annotation ) ;
        }
      }
    }

    final LegendTitle chartLegend = chart.getLegend() ;
//    chartLegend.setBackgroundPaint( COLOR_GRADIENT_LIGHT ) ;
    chartLegend.setBorder( 0.0, 0.0, 0.0, 0.0 ); ;


    final BufferedImage bufferedImage = chart.createBufferedImage( widthPixels, heightPixels ) ;

    return bufferedImage ;

  }

  private static String calculateTerminationText( final Termination termination ) {
    final String annotationText ;
    if( termination == TimeMeasurer.Terminations.STRAIN ) {
      annotationText = null ;
    } else {
      annotationText = termination.getName() ;
    }
    return annotationText;
  }

  private static double convertToYValue( final TimeMeasurement measurement ) {
    return ( double ) measurement.getTimeMilliseconds() / 1000.0;
  }


}
