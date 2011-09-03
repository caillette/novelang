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
package org.novelang.nhovestone.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.novelang.Version;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.nhovestone.MeasurementBundle;
import org.novelang.nhovestone.Termination;
import org.novelang.nhovestone.scenario.TimeMeasurement;
import org.novelang.nhovestone.scenario.TimeMeasurer;
import org.novelang.novella.VectorImageTools;

/**
 * Generates the graph representing {@link org.novelang.nhovestone.Scenario#getMeasurements()}.
 *
 * @author Laurent Caillette
 */
public class Grapher {

  private static final Logger LOGGER = LoggerFactory.getLogger( Grapher.class ) ;

  private static final int DEFAULT_WIDTH_PIXELS = 600;
  private static final int DEFAULT_HEIGHT_PIXELS = 300;

  private static final int DEFAULT_WIDTH_VECTORUNIT = 500 ;
  private static final int DEFAULT_HEIGHT_VECTORUNIT = 250 ;

  /**
   * Using pixels because it's the only way to control image size.
   * Otherwise the image display remains the same whatever the absolute size is,
   * and whatever is done to the viewport, including SVG scaling (but except rotating it with
   * fox:transform but this requires block-container with absolute positioning).
   * So we're depending on renderer's resolution here. 
   */
  private static final String VECTORUNIT = "px" ;

  private static final Charset CHARSET = Charsets.UTF_8;

  private Grapher() { }

  public static BufferedImage create(
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements,
      final boolean showUpsizingCount 
  ) {
    return create(
        upsizings,
        measurements,
        showUpsizingCount,
        DEFAULT_WIDTH_PIXELS,
        DEFAULT_HEIGHT_PIXELS
    ) ;
  }

  public static BufferedImage create(
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements,
      final boolean showUpsizingCount, 
      final int widthPixels,
      final int heightPixels
  ) {
    final JFreeChart chart = createChart( upsizings, measurements, showUpsizingCount ) ;
    return chart.createBufferedImage( widthPixels, heightPixels ) ;
  }

  public static void exportChartAsSvg(
      final File svgFile,
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements
 )
      throws IOException
  {
    final JFreeChart chart = createChart( upsizings, measurements, false ) ;
    exportChartAsSvg(
        svgFile,
        chart,
        DEFAULT_WIDTH_VECTORUNIT,
        DEFAULT_HEIGHT_VECTORUNIT,
        VECTORUNIT
    ) ;
  }


  /**
     * Exports a JFreeChart to a SVG file.
     *
     * @param svgFile the output file.
     * @param chart JFreeChart to export
     * @param width
     * @param height
     * @throws IOException if writing the svgFile fails.
     *
     * @author Dolf Trieschnig http://dolf.trieschnigg.nl/jfreechart
     */
  public static void exportChartAsSvg(
      final File svgFile,
      final JFreeChart chart,
      final int width,
      final int height,
      final String dimensionUnit
  ) throws IOException {
    final DOMImplementation domImpl =
        GenericDOMImplementation.getDOMImplementation();
    final Document w3cDocument = domImpl.createDocument( null, "svg", null ) ;

    final SVGGraphics2D svgGenerator = new SVGGraphics2D( w3cDocument ) ;

    final java.awt.geom.Rectangle2D bounds = new Rectangle( width, height ) ;
    chart.draw( svgGenerator, bounds ) ;

    // Don't know how to set properties of the root element.
    // Accessing to svgGenerator.getRoot() has no effect.
    // So we're taking the long way here.
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
    svgGenerator.stream( new OutputStreamWriter( byteArrayOutputStream, CHARSET ), true ) ;

    LOGGER.debug(
        "Streamed this document: \n", new String( byteArrayOutputStream.toByteArray(), CHARSET ) ) ;

    final org.dom4j.Document dom4jDocument ;
    try {
      dom4jDocument = VectorImageTools.loadSvgAsDom4jDocument( 
          new InputSource( new ByteArrayInputStream( byteArrayOutputStream.toByteArray() ) ) ) ;
    } catch( DocumentException e ) {
      throw new RuntimeException( e ) ;
    }
    setSvgDimensions( dom4jDocument.getRootElement(), width, height, dimensionUnit ) ;

    final OutputFormat prettyPrint = OutputFormat.createPrettyPrint();
    prettyPrint.setEncoding( CHARSET.name() ) ;


    final OutputStream outputStream = new FileOutputStream( svgFile ) ;
    try {
      final XMLWriter writer = new XMLWriter( outputStream, prettyPrint ) ;
      writer.write( dom4jDocument ) ;
      writer.flush() ;
    } finally {
      outputStream.close() ;
    }

    LOGGER.info( "Wrote '", svgFile.getAbsolutePath(), "'." ) ;

  }

  private static void setSvgDimensions(
      final org.dom4j.Element element,
      final int width,
      final int height,
      final String dimensionUnit
  ) {
    element.addAttribute( "width", width + dimensionUnit ) ;
    element.addAttribute( "height", height + dimensionUnit ) ;
  }


  private static JFreeChart createChart(
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle< TimeMeasurement > > measurements,
      final boolean showUpsizingCount
  ) {
    final JFreeChart chart = ChartFactory.createXYLineChart(
        null,                      // chart title
        "Total source size (KiB)", // domain axis label
        null,                      // range axis label
        null,                      // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        false,                     // tooltips
        false                      // urls
    ) ;

    final XYPlot plot = ( XYPlot ) chart.getPlot() ;
    plot.setBackgroundPaint( BACKGROUND_GRADIENT_PAINT ) ;

    final List< Double > cumulatedUpsizings = cumulate( upsizings ) ;

    addMeasurementsDataset( plot, cumulatedUpsizings, measurements );

    final NumberAxis domainAxis = ( NumberAxis ) plot.getDomainAxis() ;
    domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() ) ;
    domainAxis.setAxisLinePaint( NULL_COLOR ) ;
    domainAxis.setAutoRangeStickyZero( false ) ;

    if( showUpsizingCount ) {
      final NumberAxis novellaCountAxis = new NumberAxis( "Novella count" );
      novellaCountAxis.setLowerBound( 1.0 ) ;
      novellaCountAxis.setUpperBound( ( double ) cumulatedUpsizings.size() ) ;
      novellaCountAxis.setAxisLinePaint( NULL_COLOR ) ;
      plot.setDomainAxis( UPSIZINGS_KEY, novellaCountAxis ) ;
      plot.setDomainAxisLocation( UPSIZINGS_KEY, AxisLocation.TOP_OR_RIGHT ) ;
    }

    final XYSplineRenderer measurementsRenderer = new XYSplineRenderer() ;
    for( int serieIndex = 0 ; serieIndex < measurements.size() ; serieIndex ++ ) {
      measurementsRenderer.setSeriesShapesVisible( serieIndex, false ) ;
      final BasicStroke stroke = new BasicStroke(
          serieIndex == 0 ? 3.0f : 1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) ;
      measurementsRenderer.setSeriesStroke( serieIndex, stroke ) ;
    }
    measurementsRenderer.setSeriesFillPaint( 0, new Color( 255, 0, 0, 255 ) ) ;
    plot.setRenderer( MEASUREMENTS_KEY, measurementsRenderer ) ;

    final XYBarRenderer upsizingBarRenderer = new XYBarRenderer() ;
    upsizingBarRenderer.setSeriesFillPaint( 0, UPSIZING_GRADIENT_PAINT ) ;
    upsizingBarRenderer.setSeriesPaint( 0, COLOR_BACKGROUND_DARK ) ;
    plot.setRenderer( UPSIZINGS_KEY, upsizingBarRenderer ) ;


    final LegendTitle chartLegend = chart.getLegend() ;
    chartLegend.setBorder( 0.0, 0.0, 0.0, 0.0 ) ;
    chartLegend.setPosition( RectangleEdge.TOP );
    return chart;
  }

  private static void addMeasurementsDataset(
      final XYPlot plot,
      final List< Double > cumulatedUpsizings,
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
            ( double ) cumulatedUpsizings.get( ++ callIndex ),
            convertToYValue( measurement )
        ) ;
      }
      measurementsDataset.addSeries( series ) ;

      addAnnotations( plot, measurementBundle ) ;

    }

    plot.setDataset( MEASUREMENTS_KEY, measurementsDataset ) ;
    final NumberAxis measurementRangeAxis = new NumberAxis( "Response time (seconds)" ) ;
    measurementRangeAxis.setAutoRange( true ) ;
    measurementRangeAxis.setAxisLinePaint( NULL_COLOR ) ;
    plot.setRangeAxis( MEASUREMENTS_KEY, measurementRangeAxis ) ;
    plot.mapDatasetToRangeAxis( MEASUREMENTS_KEY, MEASUREMENTS_KEY ) ;
  }


  private static List< Double > cumulate( final List< Long > upsizings ) {
    double sum = 0.0 ;
    final List< Double > cumulated = Lists.newArrayList() ;
    for( final Long upsizing : upsizings ) {
      sum += ( double ) ( upsizing ) ;
      cumulated.add( sum / 1024.0 ) ;
    }
    return Collections.unmodifiableList( cumulated ) ;
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


  private static final int MEASUREMENTS_KEY = 0 ;
  private static final int UPSIZINGS_KEY = 1 ;

  private static final Color COLOR_BACKGROUND_DARK = new Color( 136, 167, 189 ) ;
  private static final Color COLOR_BACKGROUND_LIGHT = new Color( 204, 237, 255 ) ;

  private static final GradientPaint BACKGROUND_GRADIENT_PAINT = new GradientPaint(
      0.0f, 0.0f, COLOR_BACKGROUND_DARK,
      0.0f, 0.0f, COLOR_BACKGROUND_LIGHT
  );

  private static final Color COLOR_UPSIZING_DARK = new Color( 136, 167, 189, 20 ) ;
  private static final Color COLOR_UPSIZING_LIGHT = new Color( 204, 237, 255, 200 ) ;

  private static final GradientPaint UPSIZING_GRADIENT_PAINT =
      new GradientPaint( 0.0f, 0.0f, COLOR_UPSIZING_DARK, 0.0f, 0.0f, COLOR_UPSIZING_LIGHT ) ;

  private static final Color NULL_COLOR = new Color( 0, 0, 0, 0 ) ;

  static void writeBitmapImage(
      final File imageFile,
      final List< Long > upsizings,
      final Map< Version, MeasurementBundle<TimeMeasurement > > versionMap
  ) throws IOException {
    final BufferedImage image = create( upsizings, versionMap, false ) ;
    ImageIO.write( image, "png", imageFile ) ;
    LOGGER.info( "Wrote '", imageFile.getAbsolutePath(), "'." ) ;
  }
}
