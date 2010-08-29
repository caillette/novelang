package org.novelang.opus.function;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.novelang.opus.function.builtin.FileOrdering;
import org.novelang.opus.function.builtin.InsertCommand;
import org.novelang.opus.function.builtin.MapstylesheetCommand;
import org.novelang.opus.function.builtin.insert.LevelHead;
import org.novelang.common.SyntacticTree;
import org.novelang.designator.FragmentIdentifier;
import org.novelang.parser.NodeKind;
import org.novelang.parser.NodeKindTools;

import static org.novelang.parser.NodeKind.*;

/**
 * Creates instances of {@link Command}.
 * 
 * @author Laurent Caillette
 */
public class CommandFactory {
  
  /**
   * Creates the {@link Command} instance from given {@code SyntacticTree}'s root.
   * 
   * @param treeOfCommand A {@code SyntacticTree} instance of appropriate node type
   * @return a non-null object.
   */
  public static Command createFunctionCall( final SyntacticTree treeOfCommand )
      throws CommandParameterException
  {
    
    final NodeKind nodeKind = NodeKindTools.ofRoot( treeOfCommand ) ;
    switch( nodeKind ) {
      
      case COMMAND_INSERT_ :
        final String fileName = getTextOfChild( treeOfCommand, URL_LITERAL, true ) ;
        final boolean recurse = hasChild( treeOfCommand, COMMAND_INSERT_RECURSE_ ) ;
        final FileOrdering fileOrdering = createFileOrdering( 
            getTextOfChild( treeOfCommand, COMMAND_INSERT_SORT_, false ) ) ;
        final LevelHead levelHead =
            hasChild( treeOfCommand, COMMAND_INSERT_CREATELEVEL_ ) ? LevelHead.CREATE_LEVEL :
            hasChild( treeOfCommand, COMMAND_INSERT_NOHEAD_ ) ? LevelHead.NO_HEAD : null
        ;
        final String levelAboveAsString =
            getTextOfChild( treeOfCommand, COMMAND_INSERT_LEVELABOVE_, false ) ;
        final String styleName = getTextOfChild( treeOfCommand, COMMAND_INSERT_STYLE_, false ) ;
        final Iterable< FragmentIdentifier > fragmentIdentifiers = 
            getFragmentIdentifiers( treeOfCommand ) ;

        return new InsertCommand(
            treeOfCommand.getLocation(),
            fileName,
            recurse,
            fileOrdering, 
            levelHead,
            levelAboveAsString == null ? 0 : Integer.parseInt( levelAboveAsString ),
            styleName,
            fragmentIdentifiers
        ) ;
      
      case COMMAND_MAPSTYLESHEET_ :
        final Map< String, String > styleMap = Maps.newHashMap() ;
        for( final SyntacticTree child : treeOfCommand.getChildren() ) {
          styleMap.put( child.getChildAt( 0 ).getText(), child.getChildAt( 1 ).getText() ) ;
        }
        return new MapstylesheetCommand( treeOfCommand.getLocation(), styleMap ) ;
      
      default : throw new IllegalArgumentException( "Unsupported: " + nodeKind ) ;
    }
    
  }
  
  private static Iterable< SyntacticTree > getChildren( 
      final SyntacticTree tree, 
      final NodeKind childNodeKind 
  ) {
    final List< SyntacticTree > filtered = Lists.newArrayList() ;
    for( final SyntacticTree child : tree.getChildren() ) {
      if( child.isOneOf( childNodeKind ) ) {
        filtered.add( child ) ;
      }
    }
    return filtered ;
  }

  
  private static Iterable<FragmentIdentifier> getFragmentIdentifiers( final SyntacticTree tree ) {
    final ImmutableList.Builder< FragmentIdentifier > fragmentIdentifiersBuilder = 
        ImmutableList.builder() ;
    final Iterable< SyntacticTree > childrenWithAbsoluteIdentifier = 
        getChildren( tree, COMPOSITE_IDENTIFIER ) ;
    for( final SyntacticTree child : childrenWithAbsoluteIdentifier ) {
      fragmentIdentifiersBuilder.add( getFragmentIdentifier( child ) ) ;
    }
    return fragmentIdentifiersBuilder.build() ;
  }
  
  
  private static FragmentIdentifier getFragmentIdentifier( final SyntacticTree tree ) {
    Preconditions.checkArgument( tree.isOneOf( COMPOSITE_IDENTIFIER ) ) ;
    final List< String > segments = Lists.newArrayList() ;
    for( final SyntacticTree child : tree.getChildren() ) {
      segments.add( child.getText() ) ;
    }
    return new FragmentIdentifier( segments ) ; 
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
  
  private static final Map< String, FileOrdering > FILE_ORDERINGS_BY_NAME = 
      new ImmutableMap.Builder< String, FileOrdering >().
      put( "path", FileOrdering.BY_ABSOLUTE_PATH ).
      put( "version", FileOrdering.BY_VERSION_NUMBER ).
      build() 
  ;
      
  
  private static FileOrdering createFileOrdering( final String ordering ) 
      throws CommandParameterException 
  {
    if( ordering == null ) {
      return null ;
    }
    // We suppose the parser did its job and returned a sort method followed by an order flag.
    final char sortOrderChar = ordering.charAt( ordering.length() - 1 ) ;
    final String sortMethodName = ordering.substring( 0, ordering.length() - 1 ) ;
    final boolean reverse ;
    
    switch( sortOrderChar ) {
      case '+' :
        reverse = false ;
        break ;
      case '-' :
        reverse = true ;
        break ;
      default :
        throw new IllegalArgumentException( 
            "Missing sort order at the end, must be '+' or '-', " + 
            " the parser should have detected that" 
        ) ;
    }
    final FileOrdering fileOrdering = FILE_ORDERINGS_BY_NAME.get( sortMethodName ) ;
    if( null == fileOrdering ) {
      throw new CommandParameterException( "Unknown ordering: '" + sortMethodName + "'" ) ;
    }
    
    if( reverse ) {
      return fileOrdering.inverse() ;
    } else {
      return fileOrdering ;
    }
    
  }
}
