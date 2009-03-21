/*
 * Copyright (C) 2008 Laurent Caillette
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

package novelang.rendering;

import java.util.Set;

import com.google.common.collect.Sets;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;

/**
 * Determines when a whitespace should be added between two Nodes of known type.
 * 
 * @author Laurent Caillette
 */
public class WhitespaceTrigger {

  private static final Set< Sequence > SEQUENCES = Sets.newHashSet() ;
  static {
    add( BLOCK_INSIDE_SOLIDUS_PAIRS, BLOCK_INSIDE_SOLIDUS_PAIRS ) ;
    add( BLOCK_INSIDE_SOLIDUS_PAIRS, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
    add( BLOCK_INSIDE_SOLIDUS_PAIRS, WORD_ ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, BLOCK_INSIDE_PARENTHESIS ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, WORD_ ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, BLOCK_INSIDE_PARENTHESIS ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, WORD_ ) ;
    add( BLOCK_INSIDE_HYPHEN_PAIRS, WORD_ ) ;
    add( BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE, WORD_ ) ;
    add( BLOCK_INSIDE_PARENTHESIS, WORD_ ) ;
    add( PUNCTUATION_SIGN, BLOCK_INSIDE_SOLIDUS_PAIRS ) ;
    add( PUNCTUATION_SIGN, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( PUNCTUATION_SIGN, BLOCK_INSIDE_HYPHEN_PAIRS ) ;
    add( PUNCTUATION_SIGN, BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE ) ;
    add( PUNCTUATION_SIGN, BLOCK_INSIDE_PARENTHESIS ) ;
    add( PUNCTUATION_SIGN, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
    add( PUNCTUATION_SIGN, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ) ;
    add( PUNCTUATION_SIGN, BLOCK_INSIDE_SQUARE_BRACKETS ) ;
    add( PUNCTUATION_SIGN, _EXTERNAL_LINK ) ;
    add( PUNCTUATION_SIGN, WORD_ ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_INSIDE_SOLIDUS_PAIRS ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_INSIDE_HYPHEN_PAIRS ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_INSIDE_SQUARE_BRACKETS ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_INSIDE_PARENTHESIS ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
    add( BLOCK_INSIDE_DOUBLE_QUOTES, WORD_ ) ;
    add( BLOCK_INSIDE_SQUARE_BRACKETS, WORD_ ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, BLOCK_INSIDE_PARENTHESIS ) ;
    add( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, WORD_ ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_INSIDE_SOLIDUS_PAIRS ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_INSIDE_HYPHEN_PAIRS ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_INSIDE_PARENTHESIS ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, BLOCK_INSIDE_SQUARE_BRACKETS ) ;
    add( WORD_AFTER_CIRCUMFLEX_ACCENT, WORD_ ) ;
    add( WORD_, BLOCK_INSIDE_SOLIDUS_PAIRS ) ;
    add( WORD_, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( WORD_, BLOCK_INSIDE_HYPHEN_PAIRS ) ;
    add( WORD_, BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE ) ;
    add( WORD_, BLOCK_INSIDE_PARENTHESIS ) ;
    add( WORD_, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ) ;
    add( WORD_, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( WORD_, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
    add( WORD_, BLOCK_INSIDE_SQUARE_BRACKETS ) ;
    add( WORD_, _EXTERNAL_LINK ) ;
    add( WORD_, WORD_ ) ;
    add( _EXTERNAL_LINK, BLOCK_INSIDE_SOLIDUS_PAIRS ) ;
    add( _EXTERNAL_LINK, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( _EXTERNAL_LINK, BLOCK_INSIDE_HYPHEN_PAIRS ) ;
    add( _EXTERNAL_LINK, BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE ) ;
    add( _EXTERNAL_LINK, BLOCK_INSIDE_PARENTHESIS ) ;
    add( _EXTERNAL_LINK, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ) ;
    add( _EXTERNAL_LINK, BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ) ;
    add( _EXTERNAL_LINK, BLOCK_INSIDE_DOUBLE_QUOTES ) ;
    add( _EXTERNAL_LINK, BLOCK_INSIDE_SQUARE_BRACKETS ) ;
    add( _EXTERNAL_LINK, _EXTERNAL_LINK ) ;
    add( _EXTERNAL_LINK, WORD_ ) ;
  }

  private static void add( NodeKind nodeKind1, NodeKind nodeKind2 ) {
    SEQUENCES.add( new Sequence( nodeKind1, nodeKind2 ) ) ;
  }

  public static boolean isTrigger( NodeKind first, NodeKind second ) {

    if( null == first ) {
      return false ;
    }

    final Sequence sequence = new Sequence( first, second ) ;
    return SEQUENCES.contains( sequence ) ;
  }

  private static final class Sequence {
    private final NodeKind first, second ;


    public Sequence( NodeKind first, NodeKind second ) {
      this.first = first;
      this.second = second;
    }

    public NodeKind getFirst() {
      return first;
    }

    public NodeKind getSecond() {
      return second;
    }


    public boolean equals( Object other ) {
      if( this == other ) {
        return true;
      }

      if( null == other ) {
        return false ;
      }

      final Sequence sequence = ( Sequence ) other ;
      return
          first == sequence.first &&
          second == sequence.second
      ;
    }

    public int hashCode() {
      return
          ( null == first ? 0 : first.hashCode() * 31 ) +
          ( null == second ? 0 : second.hashCode() )
      ;
    }
  }

}
