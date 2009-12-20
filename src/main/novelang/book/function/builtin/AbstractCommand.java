package novelang.book.function.builtin;

import novelang.book.function.Command;
import novelang.common.Location;

import com.google.common.base.Preconditions;

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
