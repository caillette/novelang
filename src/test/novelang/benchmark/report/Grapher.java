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
import novelang.system.Log;
import novelang.system.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * Generates the graph representing {@link novelang.benchmark.scenario.Scenario#getMeasurements()}. 
 *
 * @author Laurent Caillette
 */
public class Grapher {

  private static final Log LOG = LogFactory.getLog( Grapher.class ) ;

  public static BufferedImage create(
      final String title,
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements
  ) {
    return create( title, upsizings, measurements, 600, 300 ) ;
  }

  public static BufferedImage create(
      final String title,
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements,
      final int widthPixels,
      final int heightPixels
  ) {

    final JFreeChart chart = ChartFactory.createXYLineChart(
        null,                      // chart title
        "Call count",              // domain axis label
        null,                      // range axis label
        null,                      // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        false,                     // tooltips
        false                      // urls
    ) ;

    final XYPlot plot = ( XYPlot ) chart.getPlot() ;
    plot.setBackgroundPaint( GRADIENT_PAINT ) ;

    addUpsizingsDataset( plot, upsizings );
    addMeasurementsDataset( plot, measurements );

    final NumberAxis domainAxis = ( NumberAxis ) plot.getDomainAxis() ;
    domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() ) ;

    final XYSplineRenderer measurementsRenderer = new XYSplineRenderer() ;
    for( int serieIndex = 0 ; serieIndex < measurements.size() ; serieIndex ++ ) {
      measurementsRenderer.setSeriesShapesVisible( serieIndex, false ) ;
      final BasicStroke stroke = new BasicStroke(
          serieIndex == 0 ? 3.0f : 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) ;
      measurementsRenderer.setSeriesStroke( serieIndex, stroke ) ;
    }
    plot.setRenderer( MEASUREMENTS_INDEX, measurementsRenderer ) ;

    final XYSplineRenderer upsizingRenderer = new XYSplineRenderer() ;
    upsizingRenderer.setSeriesShapesVisible( 0, false ) ;
    plot.setRenderer( UPSIZINGS_INDEX, upsizingRenderer ) ;

    plot.configureDomainAxes() ;
    plot.configureRangeAxes() ;


    final LegendTitle chartLegend = chart.getLegend() ;
    chartLegend.setBorder( 0.0, 0.0, 0.0, 0.0 ) ;
    chartLegend.setPosition( RectangleEdge.TOP );

    return chart.createBufferedImage( widthPixels, heightPixels );

  }

  private static void addMeasurementsDataset(
      final XYPlot plot,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements
  ) {
    final List< Version > versions = Lists.newArrayList( measurements.keySet() ) ;
    Collections.sort( versions, Ordering.from( Version.COMPARATOR ).reverse() ) ;

    final XYSeriesCollection measurementsDataset = new XYSeriesCollection() ;

    for( final Version version : versions ) {
      final MeasurementBundle< TimeMeasurement > measurementBundle = measurements.get( version ) ;
      int callIndex = 0 ;
      final XYSeries series = new XYSeries( version.getName() ) ;
      for( final TimeMeasurement measurement : measurementBundle ) {
        series.add(
            ( double ) ++ callIndex,
            convertToYValue( measurement )
        ) ;
      }
      measurementsDataset.addSeries( series ) ;

      addAnnotations( plot, measurementBundle ) ;

    }

    plot.setDataset( MEASUREMENTS_INDEX, measurementsDataset ) ;
    final NumberAxis measurementRangeAxis = new NumberAxis( "Response time (seconds)" ) ;
    measurementRangeAxis.setAutoRange( true ) ;
    plot.setRangeAxis( MEASUREMENTS_INDEX, measurementRangeAxis ) ;
    plot.mapDatasetToRangeAxis( MEASUREMENTS_INDEX, MEASUREMENTS_INDEX ); ;
  }


  private static void addUpsizingsDataset( final XYPlot plot, final List< Long > upsizings ) {
    final XYSeriesCollection upsizingsDataset = new XYSeriesCollection() ;
    final XYSeries series = new XYSeries( "Source size" ) ;
    int upsizingIndex = 0 ;
    double sum = 0.0 ;
    double adjustedSum = 0.0 ;
    for( final Long upsizing : upsizings ) {
      sum += upsizing ;
      adjustedSum = sum / 1024.0;
      series.add( ( double ) ++ upsizingIndex, adjustedSum ) ;
    }
//    LOG.debug( "Las adjusted sum added: " + upsizingIndex + " -> " + adjustedSum ) ;
    upsizingsDataset.addSeries( series ) ;
    plot.setDataset( UPSIZINGS_INDEX, upsizingsDataset ) ;

    final NumberAxis upsizingRangeAxis = new NumberAxis( "Document source size (KiB)" ) ;
    upsizingRangeAxis.setAutoRange( true ) ;
    plot.setRangeAxis( UPSIZINGS_INDEX, upsizingRangeAxis ) ;
    plot.setRangeAxisLocation( UPSIZINGS_INDEX, AxisLocation.BOTTOM_OR_RIGHT ) ;
    plot.mapDatasetToRangeAxis( UPSIZINGS_INDEX, UPSIZINGS_INDEX );
  }

  private static void addAnnotations(
      final XYPlot plot,
      final MeasurementBundle< TimeMeasurement > measurementBundle
  ) {
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


  private static final int MEASUREMENTS_INDEX = 0 ;
  private static final int UPSIZINGS_INDEX = 1 ;

  private static final Color COLOR_GRADIENT_DARK = new Color( 136, 167, 189 ) ;
  private static final Color COLOR_GRADIENT_LIGHT = new Color( 204, 237, 255 ) ;

  private static final GradientPaint GRADIENT_PAINT = new GradientPaint(
      0.0f, 0.0f, COLOR_GRADIENT_DARK,
      0.0f, 0.0f, COLOR_GRADIENT_LIGHT
  );


}
