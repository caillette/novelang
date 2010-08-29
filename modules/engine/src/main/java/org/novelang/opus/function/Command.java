package org.novelang.opus.function;

import org.novelang.opus.CommandExecutionContext;

/**
 * Represents an occurence of a command call inside a Opus file.
 * 
 * @author Laurent Caillette
 */
public interface Command {
  
  CommandExecutionContext evaluate( CommandExecutionContext context ) ;
  
}
