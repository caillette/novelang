/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ---------------------
 * MyXYAreaRenderer.java
 * ---------------------
 * (C) Copyright 2005, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYAreaRenderer.java,v 1.12.2.5 2005/12/22 15:53:05 mungady Exp $
 *
 * Changes:
 * --------
 * 22-Dec-2005 : Version 1 (DG);
 *
 *
 *
 * Found on:
 * http://www.jfree.org/phpBB2/viewtopic.php?f=10&t=15596
 */
package org.novelang.nhovestone.report;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.util.ShapeUtilities;

/**
 * Custom renderer.
 *
 * @author David Gilbert (for Object Refinery Limited).
 */
public class EnhancedXYAreaRenderer extends XYAreaRenderer {

  /**
   * A state object used by this renderer.
   */
  static class XYAreaRendererState extends XYItemRendererState {

    /**
     * Working storage for the area under one series.
     */
    public Polygon area;

    /**
     * Working line that can be recycled.
     */
    public Line2D line;

    /**
     * Creates a new state.
     *
     * @param info the plot rendering info.
     */
    public XYAreaRendererState( final PlotRenderingInfo info ) {
      super( info );
      this.area = new Polygon();
      this.line = new Line2D.Double();
    }

  }

  /**
   * Constructs a new renderer.
   */
  public EnhancedXYAreaRenderer() {
    this( AREA );
  }

  /**
   * Constructs a new renderer.
   *
   * @param type the type of the renderer.
   */
  public EnhancedXYAreaRenderer( final int type ) {
    this( type, null, null );
  }

  /**
   * Constructs a new renderer.
   * <p/>
   * To specify the type of renderer, use one of the constants: SHAPES, LINES,
   * SHAPES_AND_LINES, AREA or AREA_AND_SHAPES.
   *
   * @param type             the type of renderer.
   * @param toolTipGenerator the tool tip generator to use
   *                         (<code>null</code> permitted).
   * @param urlGenerator     the URL generator (<code>null</code> permitted).
   */
  public EnhancedXYAreaRenderer(
      final int type,
      final XYToolTipGenerator toolTipGenerator,
      final XYURLGenerator urlGenerator
  ) {
    super( type, toolTipGenerator, urlGenerator );
  }

  /**
   * Initialises the renderer and returns a state object that should be
   * passed to all subsequent calls to the drawItem() method.
   *
   * @param g2       the graphics device.
   * @param dataArea the area inside the axes.
   * @param plot     the plot.
   * @param data     the data.
   * @param info     an optional info collection object to return data back to
   *                 the caller.
   * @return A state object for use by the renderer.
   */
  @Override
  public XYItemRendererState initialise(
      final Graphics2D g2,
      final Rectangle2D dataArea,
      final XYPlot plot,
      final XYDataset data,
      final PlotRenderingInfo info
  ) {
    final XYAreaRendererState state = new XYAreaRendererState( info ) ;
    return state;
  }

  /**
   * Draws the visual representation of a single data item.
   *
   * @param g2             the graphics device.
   * @param state          the renderer state.
   * @param dataArea       the area within which the data is being drawn.
   * @param info           collects information about the drawing.
   * @param plot           the plot (can be used to obtain standard color information
   *                       etc).
   * @param domainAxis     the domain axis.
   * @param rangeAxis      the range axis.
   * @param dataset        the dataset.
   * @param series         the series index (zero-based).
   * @param item           the item index (zero-based).
   * @param crosshairState crosshair information for the plot
   *                       (<code>null</code> permitted).
   * @param pass           the pass index.
   */
  @Override
  public void drawItem(
      final Graphics2D g2,
      final XYItemRendererState state,
      final Rectangle2D dataArea,
      final PlotRenderingInfo info,
      final XYPlot plot,
      final ValueAxis domainAxis,
      final ValueAxis rangeAxis,
      final XYDataset dataset,
      final int series,
      final int item,
      final CrosshairState crosshairState,
      final int pass
  ) {
    if( !getItemVisible( series, item ) ) {
      return;
    }
    final XYAreaRendererState areaState = ( XYAreaRendererState ) state;

    // get the data point...
    final double x1 = dataset.getXValue( series, item );
    double y1 = dataset.getYValue( series, item );
    if( Double.isNaN( y1 ) ) {
      y1 = 0.0;
    }
    final double transX1 = domainAxis.valueToJava2D( x1, dataArea, plot.getDomainAxisEdge() );
    final double transY1 = rangeAxis.valueToJava2D( y1, dataArea, plot.getRangeAxisEdge() );

    // get the previous point and the next point so we can calculate a
    // "hot spot" for the area (used by the chart entity)...
    final int itemCount = dataset.getItemCount( series );
    final double x0 = dataset.getXValue( series, Math.max( item - 1, 0 ) );
    double y0 = dataset.getYValue( series, Math.max( item - 1, 0 ) );
    if( Double.isNaN( y0 ) ) {
      y0 = 0.0;
    }
    final double transX0 = domainAxis.valueToJava2D( x0, dataArea,
        plot.getDomainAxisEdge() );
    final double transY0 = rangeAxis.valueToJava2D( y0, dataArea,
        plot.getRangeAxisEdge() );

    final double x2 = dataset.getXValue( series, Math.min( item + 1,
        itemCount - 1 ) );
    double y2 = dataset.getYValue( series, Math.min( item + 1,
        itemCount - 1 ) );
    if( Double.isNaN( y2 ) ) {
      y2 = 0.0;
    }
    final double transX2 = domainAxis.valueToJava2D( x2, dataArea, plot.getDomainAxisEdge() );
    final double transY2 = rangeAxis.valueToJava2D( y2, dataArea, plot.getRangeAxisEdge() );

    final double transZero = rangeAxis.valueToJava2D( 0.0, dataArea, plot.getRangeAxisEdge() );
    final Polygon hotspot ;
    if( plot.getOrientation() == PlotOrientation.HORIZONTAL ) {
      hotspot = new Polygon();
      hotspot.addPoint( ( int ) transZero,
          ( int ) ( ( transX0 + transX1 ) / 2.0 ) );
      hotspot.addPoint( ( int ) ( ( transY0 + transY1 ) / 2.0 ),
          ( int ) ( ( transX0 + transX1 ) / 2.0 ) );
      hotspot.addPoint( ( int ) transY1, ( int ) transX1 );
      hotspot.addPoint( ( int ) ( ( transY1 + transY2 ) / 2.0 ),
          ( int ) ( ( transX1 + transX2 ) / 2.0 ) );
      hotspot.addPoint( ( int ) transZero,
          ( int ) ( ( transX1 + transX2 ) / 2.0 ) );
    } else {  // vertical orientation
      hotspot = new Polygon();
      hotspot.addPoint( ( int ) ( ( transX0 + transX1 ) / 2.0 ),
          ( int ) transZero );
      hotspot.addPoint( ( int ) ( ( transX0 + transX1 ) / 2.0 ),
          ( int ) ( ( transY0 + transY1 ) / 2.0 ) );
      hotspot.addPoint( ( int ) transX1, ( int ) transY1 );
      hotspot.addPoint( ( int ) ( ( transX1 + transX2 ) / 2.0 ),
          ( int ) ( ( transY1 + transY2 ) / 2.0 ) );
      hotspot.addPoint( ( int ) ( ( transX1 + transX2 ) / 2.0 ),
          ( int ) transZero );
    }

    if( item == 0 ) {  // create a new area polygon for the series
      areaState.area = new Polygon();
      // the first point is (x, 0)
      final double zero = rangeAxis.valueToJava2D( 0.0, dataArea,
          plot.getRangeAxisEdge() );
      if( plot.getOrientation() == PlotOrientation.VERTICAL ) {
        areaState.area.addPoint( ( int ) transX1, ( int ) zero );
      } else if( plot.getOrientation() == PlotOrientation.HORIZONTAL ) {
        areaState.area.addPoint( ( int ) zero, ( int ) transX1 );
      }
    }

    // Add each point to Area (x, y)
    if( plot.getOrientation() == PlotOrientation.VERTICAL ) {
      areaState.area.addPoint( ( int ) transX1, ( int ) transY1 );
    } else if( plot.getOrientation() == PlotOrientation.HORIZONTAL ) {
      areaState.area.addPoint( ( int ) transY1, ( int ) transX1 );
    }

    final PlotOrientation orientation = plot.getOrientation();
    final Paint paint = getItemPaint( series, item );
    final Stroke stroke = getItemStroke( series, item );
    g2.setPaint( paint );
    g2.setStroke( stroke );

    Shape shape;
    if( getPlotShapes() ) {
      shape = getItemShape( series, item );
      if( orientation == PlotOrientation.VERTICAL ) {
        shape = ShapeUtilities.createTranslatedShape( shape, transX1,
            transY1 );
      } else if( orientation == PlotOrientation.HORIZONTAL ) {
        shape = ShapeUtilities.createTranslatedShape( shape, transY1,
            transX1 );
      }
      g2.draw( shape );
    }

    if( getPlotLines() ) {
      if( item > 0 ) {
        if( plot.getOrientation() == PlotOrientation.VERTICAL ) {
          areaState.line.setLine( transX0, transY0, transX1, transY1 );
        } else if( plot.getOrientation() == PlotOrientation.HORIZONTAL ) {
          areaState.line.setLine( transY0, transX0, transY1, transX1 );
        }
        g2.draw( areaState.line );
      }
    }

    // Check if the item is the last item for the series.
    // and number of items > 0.  We can't draw an area for a single point.
    if( getPlotArea() && item > 0 && item == ( itemCount - 1 ) ) {

      if( orientation == PlotOrientation.VERTICAL ) {
        // Add the last point (x,0)
        areaState.area.addPoint( ( int ) transX1, ( int ) transZero );
      } else if( orientation == PlotOrientation.HORIZONTAL ) {
        // Add the last point (x,0)
        areaState.area.addPoint( ( int ) transZero, ( int ) transX1 );
      }

      Paint fillPaint = getItemFillPaint( series, item );
      if( fillPaint instanceof GradientPaint ) {
        final GradientPaint gp = ( GradientPaint ) fillPaint;
        final GradientPaintTransformer t = new StandardGradientPaintTransformer();
        fillPaint = t.transform( gp, areaState.area.getBounds() );
      }
      g2.setPaint( fillPaint );
      g2.fill( areaState.area );

      // draw an outline around the Area.
      if( isOutline() ) {
        g2.setStroke( getItemOutlineStroke( series, item ) );
        g2.setPaint( getItemOutlinePaint( series, item ) );
        g2.draw( areaState.area );
      }
    }

    updateCrosshairValues(
        crosshairState, x1, y1, transX1, transY1, orientation
    );

    // collect entity and tool tip information...
    if( state.getInfo() != null ) {
      final EntityCollection entities = state.getEntityCollection();
      if( entities != null && hotspot != null ) {
        String tip = null;
        final XYToolTipGenerator generator
            = getToolTipGenerator( series, item );
        if( generator != null ) {
          tip = generator.generateToolTip( dataset, series, item );
        }
        String url = null;
        if( getURLGenerator() != null ) {
          url = getURLGenerator().generateURL( dataset, series, item );
        }
        final XYItemEntity entity = new XYItemEntity( hotspot, dataset,
            series, item, tip, url );
        entities.add( entity );
      }
    }

  }

}
