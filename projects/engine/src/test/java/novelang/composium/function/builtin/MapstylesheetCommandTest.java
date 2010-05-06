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
package novelang.composium.function.builtin;

import java.io.File;

import novelang.ResourceTools;
import org.junit.Assert;
import org.junit.Test;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.composium.CommandExecutionContext;
import novelang.composium.function.CommandParameterException;
import novelang.common.Location;
import novelang.loader.ResourceName;
import novelang.rendering.RenditionMimeType;
import com.google.common.collect.ImmutableMap;

/**
 * Tests for {@link novelang.composium.function.builtin.MapstylesheetCommand}.
 *
 * @author Laurent Caillette
 */
public class MapstylesheetCommandTest {

  private static final Log LOG = LogFactory.getLog( MapstylesheetCommandTest.class ) ;

  @Test
  public void correctMapping() throws CommandParameterException {
    final ImmutableMap< String, String > map = ImmutableMap.of(
        "html", "dir/stylesheet.xsl",
        "pdf", "other/pdf.xsl"
    ) ;
    final MapstylesheetCommand definition = new MapstylesheetCommand(
        new Location( "", -1, -1 ),
        map
    ) ;
    LOG.debug( "Stylesheet map: \n%s", map ) ;


    final CommandExecutionContext result = definition.evaluate(
        new CommandExecutionContext( new File( "" ), ResourceTools.getExecutorService() ) );

    Assert.assertEquals(
        new ResourceName( "dir/stylesheet.xsl" ),
        result.getCustomStylesheets().get( RenditionMimeType.HTML )
    ) ;

    Assert.assertEquals(
        new ResourceName( "other/pdf.xsl" ),
        result.getCustomStylesheets().get( RenditionMimeType.PDF )
    ) ;

  }




}