package novelang.book.function;

import novelang.common.SyntacticTree;
import static novelang.parser.NodeKind.*;
import novelang.parser.NodeKindTools;
import novelang.parser.NodeKind;
import novelang.book.function.builtin.InsertCommand;
import novelang.book.function.builtin.MapstylesheetCommand;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Creates instances of {@link AbstractFunctionCall}.
 * 
 * @author Laurent Caillette
 */
public class CommandFactory {

  /**
   * Creates the {@link AbstractFunctionCall} instance from given {@code SyntacticTree}'s root.
   * 
   * @param treeOfCommand A {@code SyntacticTree} instance of appropriate node type
   * @return
   */
  public Command createFunctionCall( final SyntacticTree treeOfCommand ) 
      throws CommandParameterException
  {
    
    final NodeKind nodeKind = NodeKindTools.ofRoot( treeOfCommand ) ;
    switch( nodeKind ) {
      
      case COMMAND_INSERT_ :
        final String fileName = getTextOfChild( treeOfCommand, URL_LITERAL, true ) ;
        final boolean recurse = hasChild( treeOfCommand, COMMAND_INSERT_RECURSE_ ) ;
        final boolean createLevel = hasChild( treeOfCommand, COMMAND_INSERT_CREATELEVEL_ ) ;
        final String styleName = getTextOfChild( treeOfCommand, COMMAND_INSERT_STYLE_, false ) ;
        return new InsertCommand( fileName, recurse, createLevel, styleName ) ;
      
      case COMMAND_MAPSTYLESHEET_ :
        final Map< String, String > styleMap = Maps.newHashMap() ;
        for( final SyntacticTree child : treeOfCommand.getChildren() ) {
          styleMap.put( child.getChildAt( 0 ).getText(), child.getChildAt( 1 ).getText() ) ;
        }
        return new MapstylesheetCommand( styleMap ) ;
      
      default : throw new IllegalArgumentException( "Unsupported: " + nodeKind ) ;
    }
    
  }
  
  private static String getTextOfChild( 
      final SyntacticTree tree, 
      final NodeKind childNodeKind, 
      final boolean mandatory 
  ) throws CommandParameterException {
    
    for( final SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( childNodeKind ) ) {
        return child.getChildAt( 0 ).getText() ;
      }
    }
    if ( mandatory ) {
      throw new CommandParameterException( 
          "Found no URL. " + 
          "This should not happen if the parser created this tree: " + tree 
      ) ;
    } else {
      return null ;
    }

  }
  
  private static boolean hasChild( final SyntacticTree tree, final NodeKind nodeKind ) {
    for( final SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( nodeKind ) ) {
        return true ;
      }
    }
    return false ;
  }
}
