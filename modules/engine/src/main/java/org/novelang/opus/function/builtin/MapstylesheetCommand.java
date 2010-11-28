package org.novelang.opus.function.builtin;

import org.novelang.opus.CommandExecutionContext;
import org.novelang.rendering.RenditionMimeType;
import org.novelang.outfit.loader.ResourceName;
import org.novelang.common.Problem;
import org.novelang.common.Location;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

import java.util.Map;
import java.util.List;

/**
 * @author Laurent Caillette
 */
public class MapstylesheetCommand extends  AbstractCommand {
  
  private final Map< String, String > stylesheetMaps ;

  public MapstylesheetCommand(
      final Location location,
      final Map< String, String > stylesheetMaps
  ) {
    super( location ) ;
    Preconditions.checkNotNull( stylesheetMaps ) ;    
    this.stylesheetMaps = ImmutableMap.copyOf( stylesheetMaps ) ;
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
