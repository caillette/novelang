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

package org.novelang.rendering;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.*;

/**
 * Handles tricky rules about inserting spaces at the right place.
 * 
 * @author Laurent Caillette
 */
public class Spaces {

  private static final Logger LOGGER = LoggerFactory.getLogger( Spaces.class );
  private static final Set< Sequence > SEQUENCES = Sets.newHashSet() ;
  
  
  static {

    final Set< NodeKind > blocks = ImmutableSet.of(
        BLOCK_AFTER_TILDE,
        BLOCK_INSIDE_DOUBLE_QUOTES,
        BLOCK_INSIDE_ASTERISK_PAIRS,
        BLOCK_INSIDE_HYPHEN_PAIRS,
        BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE,
        BLOCK_INSIDE_PARENTHESIS,
        BLOCK_INSIDE_SOLIDUS_PAIRS,
        BLOCK_INSIDE_SQUARE_BRACKETS,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS,
        BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS
    ) ;

    final Set< NodeKind > canonicalStuff = ImmutableSet.< NodeKind >builder().
        addAll( blocks ).
        add(
            _URL,
            WORD_
        ).build()
    ;


    for( final NodeKind first : canonicalStuff ) {
      for( final NodeKind second : canonicalStuff ) {
        add( first, second ) ;
      }
    }
    
    for( final NodeKind second : canonicalStuff ) {
      add( PUNCTUATION_SIGN, second ) ;
    }

    add( PUNCTUATION_SIGN, APOSTROPHE_WORDMATE ) ;

    for( final NodeKind first : blocks ) {
      add( first, APOSTROPHE_WORDMATE ) ;
    }


    logSequences() ;

    
  }

// =====================
// Literal normalization
// =====================

  public static final char ZERO_WIDTH_SPACE = '\u200b' ;
  public static final char NO_BREAK_SPACE = '\u00a0' ;

  public static String normalizeLiteral( final String rawLiteral ) {
    String s = rawLiteral.trim();
    s = s.replaceAll( " +", "" + NO_BREAK_SPACE ) ;
    return s ;
  }


// =======  
// Logging
// =======
  
  private static void logSequences() {
    int maximumNodeKindLength = 0 ;
    for( final NodeKind nodeKind : NodeKind.values() ) {
      maximumNodeKindLength = Math.max( maximumNodeKindLength, nodeKind.name().length() ) ;
    }
    final String format =
        "  " + 
        "%-" + maximumNodeKindLength + "s" + 
        " -> " + 
        "%-" + maximumNodeKindLength + "s" + 
        "\n" 
    ;
    final StringBuilder stringBuilder = new StringBuilder() ;
    for( final Sequence sequence : SEQUENCES ) {
      stringBuilder.append( 
          String.format( format, sequence.first.toString(), sequence.second.toString() ) ) ;
    }
    LOGGER.debug( "Added following sequences:\n", stringBuilder.toString() ) ;
  }

  
  
// ============
// Boring stuff
// ============

  private static void add( final NodeKind nodeKind1, final NodeKind nodeKind2 ) {
    SEQUENCES.add( new Sequence( nodeKind1, nodeKind2 ) ) ;
  }

  public static boolean isTrigger( final NodeKind first, final NodeKind second ) {

    if( null == first ) {
      return false ;
    }

    final Sequence sequence = new Sequence( first, second ) ;
    return SEQUENCES.contains( sequence ) ;
  }

  private static final class Sequence {
    private final NodeKind first, second ;


    public Sequence( final NodeKind first, final NodeKind second ) {
      this.first = first;
      this.second = second;
    }

    public NodeKind getFirst() {
      return first;
    }

    public NodeKind getSecond() {
      return second;
    }


    @Override
    public boolean equals( final Object o ) {
      if( this == o ) {
        return true ;
      }
      if( o == null || getClass() != o.getClass() ) {
        return false ;
      }

      final Sequence sequence = ( Sequence ) o ;

      if( first != sequence.first ) {
        return false;
      }
      if( second != sequence.second ) {
        return false ;
      }

      return true ;
    }

    @Override
    public int hashCode() {
      int result = first != null ? first.hashCode() : 0 ;
      result = 31 * result + ( second != null ? second.hashCode() : 0 ) ;
      return result ;
    }
  }

}
