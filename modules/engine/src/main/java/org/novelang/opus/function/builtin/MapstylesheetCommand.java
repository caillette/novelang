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

package org.novelang.opus.function.builtin;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.novelang.common.Location;
import org.novelang.common.Problem;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.opus.CommandExecutionContext;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.rendering.RenditionMimeType;

/**
 * @author Laurent Caillette
 */
public class MapstylesheetCommand extends  AbstractCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger( MapstylesheetCommand.class ) ;
  
  private final Map< String, String > stylesheetMaps ;

  public MapstylesheetCommand(
      final Location location,
      final Map< String, String > stylesheetMaps
  ) {
    super( location ) ;
    // Hack: upper-casing so we have a chance to match enum's name.
    final ImmutableMap.Builder< String, String > builder = ImmutableMap.builder() ;
    for( final Map.Entry< String, String > stringEntry : stylesheetMaps.entrySet() ) {
      builder.put( stringEntry.getKey().toUpperCase(), stringEntry.getValue() ) ;
    }
    this.stylesheetMaps = builder.build() ;
  }

  @Override
  public CommandExecutionContext evaluate( final CommandExecutionContext context ) {

    final Map< RenditionMimeType, ResourceName > moreStylesheetMappings = Maps.newHashMap() ;
    final List< Problem > problems = Lists.newArrayList() ;
    
    for( final String key : stylesheetMaps.keySet() ) {
      if( RenditionMimeType.contains( key ) ) {
        final RenditionMimeType renditionMimeType = RenditionMimeType.valueOf( key.toUpperCase() ) ;
        final ResourceName stylesheet ;
        final String stylesheetName = stylesheetMaps.get( key ) ;
        try {
          stylesheet = new ResourceName( stylesheetName ) ;
          moreStylesheetMappings.put( renditionMimeType, stylesheet ) ;
        } catch( IllegalArgumentException e ) {
          problems.add( Problem.createProblem(
              "Incorrect stylesheet name: '" + stylesheetName + "'" ) ) ;
        }
      } else {
        problems.add( Problem.createProblem( "Unknown MIME type: '" + key + "'" ) ) ;
      }
    }
    
    if( problems.isEmpty() ) {
      try {
        LOGGER.debug( "Additional mappings: ", moreStylesheetMappings ) ;
        return context.addMappings( moreStylesheetMappings ) ;
      } catch( CommandExecutionContext.DuplicateStylesheetMappingException e ) {
        problems.add( Problem.createProblem( e ) ) ;
      }
    }
    return context.addProblems( problems ) ;

  }

  static {

    
  }
  
}
