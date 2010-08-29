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
package org.novelang.opus.function.builtin;

import java.io.File;

import com.google.common.collect.ImmutableMap;
import org.novelang.ResourceTools;
import org.novelang.common.Location;
import org.novelang.loader.ResourceName;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.opus.CommandExecutionContext;
import org.novelang.opus.function.CommandParameterException;
import org.novelang.rendering.RenditionMimeType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link org.novelang.opus.function.builtin.MapstylesheetCommand}.
 *
 * @author Laurent Caillette
 */
public class MapstylesheetCommandTest {

  private static final Logger LOGGER = LoggerFactory.getLogger( MapstylesheetCommandTest.class ) ;

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
    LOGGER.debug( "Stylesheet map: \n", map ) ;


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