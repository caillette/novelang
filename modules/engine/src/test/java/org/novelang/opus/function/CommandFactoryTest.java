/*
 * Copyright (C) 2011 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.novelang.opus.function;

import java.util.Iterator;
import java.util.Map;

import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

import org.novelang.designator.FragmentIdentifier;
import org.novelang.opus.function.builtin.FileOrdering;
import org.novelang.opus.function.builtin.InsertCommand;
import org.novelang.opus.function.builtin.MapstylesheetCommand;
import org.novelang.opus.function.builtin.insert.LevelHead;

/**
 * Tests for{@link CommandFactory}.
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "HardcodedFileSeparator" } )
public class CommandFactoryTest {
  
  @Test
  public void insertCommandWithDefaults() throws CommandParameterException {
    final Command command = CommandFactory.createFunctionCall(
        tree(
            COMMAND_INSERT_,
            tree( URL_LITERAL, "file:/wxy" )
        )
    ) ;
    assertTrue( "Got: " + command, command instanceof InsertCommand ) ;
    assertEquals( "/wxy", extractFileName( command ) ) ;
    assertFalse( extractRecurse( command ) ) ;
    assertSame( FileOrdering.DEFAULT, extractFileOrdering( command ) ) ;
    assertNull( extractLevelHead( command ) ) ;
    assertEquals( 0L, ( long ) extractLevelAbove( command ) );
    assertNull( extractStyleName( command ) ) ;
    assertFalse( extractFragmentIdentifiers( command ).iterator().hasNext() ) ;
  }
  
  @Test
  public void insertCommandWithAllOptions() throws CommandParameterException {
    final Command command = CommandFactory.createFunctionCall(
        tree(
            COMMAND_INSERT_,
            tree( URL_LITERAL, "file:x/y/z.novella" ),
            tree( COMMAND_INSERT_RECURSE_ ),
            tree( COMMAND_INSERT_SORT_, "path+" ),
            tree( COMMAND_INSERT_CREATELEVEL_ ),
            tree( COMMAND_INSERT_LEVELABOVE_, "3" ),
            tree( COMMAND_INSERT_STYLE_, "whatever" ),
            tree( ABSOLUTE_IDENTIFIER, tree( "yyy" ) ),
            tree( ABSOLUTE_IDENTIFIER, tree( "zzz" ) )
        )    
    ) ;
    assertTrue( "Got: " + command, command instanceof InsertCommand ) ;
    assertEquals( "x/y/z.novella", extractFileName( command ) ) ;
    assertTrue( extractRecurse( command ) ) ;
    assertTrue( extractFileOrdering( command ) instanceof FileOrdering.ByAbsolutePath ) ;
    assertSame( LevelHead.CREATE_LEVEL, extractLevelHead( command ) ) ;
    assertEquals( 3L, ( long ) extractLevelAbove( command ) ) ;
    assertEquals( "whatever", extractStyleName( command ) ) ;
    final Iterator< FragmentIdentifier > absoluteIdentifiers = 
        extractFragmentIdentifiers( command ).iterator() ;
    assertEquals( new FragmentIdentifier( "yyy" ), absoluteIdentifiers.next() ) ;
    assertEquals( new FragmentIdentifier( "zzz" ), absoluteIdentifiers.next() ) ;
    assertFalse( absoluteIdentifiers.hasNext() ) ;
  }
  
  @Test
  public void mapstylesheetCommand() throws CommandParameterException {
    final Command command = CommandFactory.createFunctionCall(
        tree(
            COMMAND_MAPSTYLESHEET_,
            tree( COMMAND_MAPSTYLESHEET_ASSIGNMENT_, tree( "abc" ), tree( "def" ) ),
            tree( COMMAND_MAPSTYLESHEET_ASSIGNMENT_, tree( "ghi" ), tree( "jkl" ) )
        )
    ) ;
    assertTrue( "Got: " + command, command instanceof MapstylesheetCommand ) ;
    final Map< String, String > stylesheetMap = extractStylesheetMaps( command ) ;
    assertEquals( 2L, ( long ) stylesheetMap.size() ) ;
    assertEquals( "def", stylesheetMap.get( "ABC" ) ) ;
    assertEquals( "jkl", stylesheetMap.get( "GHI" ) ) ;
    
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
  
  private static LevelHead extractLevelHead( final Command insertCommand ) {
    return Reflection.field( "levelHead" ).ofType( LevelHead.class ).in( insertCommand ).get() ;
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
  
  private static Iterable<FragmentIdentifier> extractFragmentIdentifiers( 
      final Command insertCommand 
  ) {
    return Reflection.field( "fragmentIdentifiers" ).
        ofType( new TypeRef < Iterable< FragmentIdentifier > >() {} ).
        in( insertCommand ).
        get() 
    ;
  }

}
