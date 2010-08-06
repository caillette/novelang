package novelang.opus.function.builtin;

import novelang.opus.function.Command;
import novelang.common.Location;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractCommand implements Command {
  
  private final Location location ;

  protected AbstractCommand( final Location location ) {
    this.location = location ; //Preconditions.checkNotNull( location ) ;
  }

  public Location getLocation() {
    return location ;
  }
}
