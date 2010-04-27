package novelang.composium.function;

import novelang.composium.CommandExecutionContext;

/**
 * Represents an occurence of a command call inside a Composium file.
 * 
 * @author Laurent Caillette
 */
public interface Command {
  
  CommandExecutionContext evaluate( CommandExecutionContext context ) ;
  
}
