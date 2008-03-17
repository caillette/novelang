/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.rendering;

import static novelang.model.common.NodeKind.*;

import java.util.Set;

import novelang.model.common.NodeKind;
import com.google.common.collect.Sets;

/**
 * Determines wether a whitespace should be added between two Nodes of known type.
 * 
 * @author Laurent Caillette
 */
public class WhitespaceTrigger {

  private static final Set< Sequence > SEQUENCES = Sets.newHashSet() ;
  static {
    add( EMPHASIS, WORD ) ;
    add( INTERPOLATEDCLAUSE, WORD ) ;
    add( PARENTHESIS, WORD ) ;
    add( PUNCTUATION_SIGN,  WORD ) ;
    add( QUOTE, WORD ) ;
    add( SQUARE_BRACKETS, WORD ) ;
    add( WORD, EMPHASIS ) ;
    add( WORD, PARENTHESIS ) ;
    add( WORD, QUOTE ) ;
    add( WORD, SQUARE_BRACKETS ) ;
    add( WORD, WORD ) ;
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
