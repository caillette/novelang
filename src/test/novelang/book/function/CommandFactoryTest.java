package novelang.book.function;

import static novelang.parser.NodeKind.*;
import static novelang.parser.antlr.TreeFixture.tree;
import novelang.book.function.builtin.InsertCommand;
import novelang.book.function.builtin.MapstylesheetCommand;
import novelang.book.function.builtin.FileOrdering;
import novelang.part.FragmentIdentifier;

import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Map;
import java.util.Iterator;

/**
 * Tests for{@link CommandFactory}.
 * 
 * @author Laurent Caillette
 */
public class CommandFactoryTest {
  
  @Test
  public void insertCommandWithDefaults() throws CommandParameterException {
    final Command command = new CommandFactory().createFunctionCall(
        tree(
            COMMAND_INSERT_,
            tree( URL_LITERAL, "file:/wxy" )
        )
    ) ;
    assertTrue( "Got: " + command, command instanceof InsertCommand ) ;
    assertEquals( "/wxy", extractFileName( command ) ) ;
    assertFalse( extractRecurse( command ) ) ;
    assertSame( FileOrdering.DEFAULT, extractFileOrdering( command ) ) ;
    assertFalse( extractCreateLevel( command ) ) ;
    assertEquals( 0, extractLevelAbove( command ) );
    assertNull( extractStyleName( command ) ) ;
    assertFalse( extractFragmentIdentifiers( command ).iterator().hasNext() ) ;
  }
  
  @Test
  public void insertCommandWithAllOptions() throws CommandParameterException {
    final Command command = new CommandFactory().createFunctionCall(
        tree(
            COMMAND_INSERT_,
            tree( URL_LITERAL, "file:x/y/z.nlp" ),
            tree( COMMAND_INSERT_RECURSE_ ),
            tree( COMMAND_INSERT_SORT_, "path+" ),
            tree( COMMAND_INSERT_CREATELEVEL_ ),
            tree( COMMAND_INSERT_LEVELABOVE_, "3" ),
            tree( COMMAND_INSERT_STYLE_, "whatever" ),
            tree( ABSOLUTE_IDENTIFIER, tree( "w" ), tree( "x" ) ),
            tree( ABSOLUTE_IDENTIFIER, tree( "y" ), tree( "z" ) )
        )    
    ) ;
    assertTrue( "Got: " + command, command instanceof InsertCommand ) ;
    assertEquals( "x/y/z.nlp", extractFileName( command ) ) ;
    assertTrue( extractRecurse( command ) ) ;
    assertTrue( extractFileOrdering( command ) instanceof FileOrdering.ByAbsolutePath ) ;
    assertTrue( extractCreateLevel( command ) ) ;
    assertEquals( 3, extractLevelAbove( command ) ) ;
    assertEquals( "whatever", extractStyleName( command ) ) ;
    final Iterator< FragmentIdentifier > absoluteIdentifiers = 
        extractFragmentIdentifiers( command ).iterator() ;
    assertEquals( new FragmentIdentifier( "w", "x" ), absoluteIdentifiers.next() ) ;
    assertEquals( new FragmentIdentifier( "y", "z" ), absoluteIdentifiers.next() ) ;
    assertFalse( absoluteIdentifiers.hasNext() ) ;
  }
  
  @Test
  public void mapstylesheetCommand() throws CommandParameterException {
    final Command command = new CommandFactory().createFunctionCall(
        tree(
            COMMAND_MAPSTYLESHEET_,
            tree( COMMAND_MAPSTYLESHEET_ASSIGNMENT_, tree( "abc" ), tree( "def" ) ),
            tree( COMMAND_MAPSTYLESHEET_ASSIGNMENT_, tree( "ghi" ), tree( "jkl" ) )
        )
    ) ;
    assertTrue( "Got: " + command, command instanceof MapstylesheetCommand ) ;
    final Map< String, String > stylesheetMap = extractStylesheetMaps( command ) ;
    assertEquals( 2, stylesheetMap.size() ) ;
    assertEquals( "def", stylesheetMap.get( "abc" ) ) ;
    assertEquals( "jkl", stylesheetMap.get( "ghi" ) ) ;
    
  }


// =======
// Fixture
// =======  
  
  private static String extractFileName( final Command insertCommand ) {
    return Reflection.field( "fileName" ).ofType( String.class ).in( insertCommand ).get() ;
  }
  
  private static String extractStyleName( final Command insertCommand ) {
    return Reflection.field( "styleName" ).ofType( String.class ).in( insertCommand ).get() ;
  }
  
  private static boolean extractRecurse( final Command insertCommand ) {
    return Reflection.field( "recurse" ).ofType( Boolean.TYPE ).in( insertCommand ).get() ;
  }
  
  private static boolean extractCreateLevel( final Command insertCommand ) {
    return Reflection.field( "createLevel" ).ofType( Boolean.TYPE ).in( insertCommand ).get() ;
  }

  private static int extractLevelAbove( final Command insertCommand ) {
    return Reflection.field( "levelAbove" ).ofType( Integer.TYPE ).in( insertCommand ).get() ;
  }

  private static FileOrdering extractFileOrdering( final Command insertCommand ) {
    return Reflection.field( "fileOrdering" ).ofType( FileOrdering.class ).
        in( insertCommand ).get() ;
  }

  private static Map< String, String > extractStylesheetMaps( final Command mapstylesheetCommand ) {
    return Reflection.field( "stylesheetMaps" ).
        ofType( new TypeRef < Map< String, String > >() {} ).
        in( mapstylesheetCommand ).
        get() 
    ;
  }
  
  private static Iterable< FragmentIdentifier > extractFragmentIdentifiers( 
      final Command insertCommand 
  ) {
    return Reflection.field( "fragmentIdentifiers" ).
        ofType( new TypeRef < Iterable< FragmentIdentifier > >() {} ).
        in( insertCommand ).
        get() 
    ;
  }
  
}
