package novelang.book.function;

import novelang.book.CommandExecutionContext;

/**
 * Represents an occurence of a command call inside a Book file.
 * 
 * @author Laurent Caillette
 */
public interface Command {
  
  CommandExecutionContext evaluate( CommandExecutionContext context ) ;
  
}
