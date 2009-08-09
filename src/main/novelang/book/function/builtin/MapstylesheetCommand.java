package novelang.book.function.builtin;

import novelang.book.CommandExecutionContext;
import novelang.book.function.Command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author Laurent Caillette
 */
public class MapstylesheetCommand implements Command {
  
  private final Map< String, String > stylesheetMaps ;

  public MapstylesheetCommand( Map< String, String > stylesheetMaps ) {
    Preconditions.checkNotNull( stylesheetMaps ) ;    
    this.stylesheetMaps = ImmutableMap.copyOf( stylesheetMaps ) ;
  }

  public CommandExecutionContext evaluate( CommandExecutionContext context ) {
    throw new UnsupportedOperationException( "evaluate" ) ;
  }
  
}
