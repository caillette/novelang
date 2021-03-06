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

package org.novelang.treemangling;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import static org.novelang.parser.NodeKind.*;

import org.novelang.parser.NodeKind;

/**
 * @author Laurent Caillette
 */
public class TreeManglingConstants {
  
  static final SyntacticTreeSet PARAGRAPH_NODEKINDS_CONTAINING_URL = SyntacticTreeSet.of(
      PARAGRAPH_REGULAR,
      PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_,
      PARAGRAPH_AS_LIST_ITEM_WITH_DOUBLE_HYPHEN_AND_NUMBER_SIGN
  ) ;
  
  static final SyntacticTreeSet CANDIDATE_URL_NAME_NODEKINDS = SyntacticTreeSet.of(
      BLOCK_INSIDE_DOUBLE_QUOTES,
      BLOCK_INSIDE_SQUARE_BRACKETS
  ) ;
  
  static final SyntacticTreeSet SEPARATOR_NODEKINDS = SyntacticTreeSet.of(
      WHITESPACE_,
      LINE_BREAK_
  ) ;
  
  static final SyntacticTreeSet NON_TRAVERSABLE_NODEKINDS = SyntacticTreeSet.of(
      WHITESPACE_,  // Avoid trapping inside "  " it contains
      LINE_BREAK_,  
      _ZERO_WIDTH_SPACE,
      WORD_,
      RASTER_IMAGE,
      VECTOR_IMAGE,
      BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
      BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS,
      LEVEL_INTRODUCER_INDENT_,
      LINES_OF_LITERAL,
      RESOURCE_LOCATION
  ) ;
      
  static final SyntacticTreeSet SKIPPED_NODEKINDS_FOR_URLMANGLER = NON_TRAVERSABLE_NODEKINDS.union(
      CELL_ROWS_WITH_VERTICAL_LINE
  ) ;

  public static final SyntacticTreeSet PARAGRAPHOID_NODEKINDS = PARAGRAPH_NODEKINDS_CONTAINING_URL.union(
      PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
      LINES_OF_LITERAL,
      RASTER_IMAGE,
      VECTOR_IMAGE,
      CELL_ROWS_WITH_VERTICAL_LINE
  ) ;

  public static final SyntacticTreeSet LEVEL_DECORATION_NODEKINDS = SyntacticTreeSet.of(
      LEVEL_TITLE,
      _IMPLICIT_TAG,
      _EXPLICIT_TAG,
      _IMPLICIT_IDENTIFIER,
      _EXPLICIT_IDENTIFIER
  ) ;

  private TreeManglingConstants() {  }

  /**
   * Just narrowing the type returned by {@link #toArray()}.
   */
  public static class SyntacticTreeSet extends AbstractSet< NodeKind > implements Set< NodeKind > {

    private final Set< NodeKind > delegate ;

    public SyntacticTreeSet( final Set< NodeKind > delegate ) {
      this.delegate = delegate;
    }

    @Override
    public Iterator< NodeKind > iterator() {
      return delegate.iterator() ;
    }

    @Override
    public int size() {
      return delegate.size() ;
    }

    @Override
    public NodeKind[] toArray() {
      return delegate.toArray( new NodeKind[ size() ] ) ;
    }
    
    public static SyntacticTreeSet of( final NodeKind... nodeKinds ) {
      return new SyntacticTreeSet( ImmutableSet.of( nodeKinds ) ) ;
    }
    
    public SyntacticTreeSet union( final NodeKind... nodeKinds ) {
      return new SyntacticTreeSet( ImmutableSet.copyOf( 
          Sets.union( this, of( nodeKinds ) )
      ) ) ;
    }
  }
}
