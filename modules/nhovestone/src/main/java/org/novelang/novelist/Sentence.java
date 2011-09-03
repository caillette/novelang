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
package org.novelang.novelist;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

/**
 * Represents a sequence of {@link TextElement}s (mainly {@link Word}s and {@link Punctuation}
 * signs).
 *
 * @author Laurent Caillette
 */
public class Sentence implements TextElement {

  private final List< TextElement > textElements ;

  public Sentence( final TextElement... textElements ) {
    this( Arrays.asList( textElements ) ) ;
  }

  public Sentence( final List< TextElement > textElements ) {
    this.textElements = ImmutableList.copyOf( textElements ) ;
  }


  public Iterator< TextElement > iterator() {
    return textElements.iterator() ;
  }

  @Override
  public String getLiteral() {
    final StringBuilder text = new StringBuilder() ;
    final PeekingIterator< TextElement > peekingIterator = Iterators.peekingIterator( iterator() ) ;

    while( peekingIterator.hasNext() ) {
      final TextElement textElement = peekingIterator.next() ;
      text.append( textElement.getLiteral() ) ;
      if( peekingIterator.hasNext() && peekingIterator.peek() instanceof Word ) {
        text.append( " " ) ;
      }
    }

    return text.toString() ;
  }
}
