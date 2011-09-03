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

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class BodyGenerator implements Generator< Body > {

  private final Random random ;
  private final Bounded.IntegerInclusiveExclusive sentenceCountRange ;
  private final Bounded.IntegerInclusiveExclusive paragraphCountRange ;
  private final Generator< Sentence > sentenceGenerator ;

  public BodyGenerator( final Configuration configuration ) {
    random = configuration.getRandom() ;
    sentenceCountRange = configuration.getSentenceCountRange() ;
    paragraphCountRange = configuration.getParagraphCountRange() ;
    sentenceGenerator = configuration.getSentenceGenerator() ;
  }

  @Override
  public Body generate() {
    final int sentenceCount = sentenceCountRange.boundInteger( random ) ;
    final int paragraphCount = paragraphCountRange.boundInteger( random ) ;
    final List< TextElement > textElements = Lists.newArrayList() ;
    for( int paragraphIndex = 1 ; paragraphIndex <= paragraphCount ; paragraphIndex ++ ) {
      for( int sentenceIndex = 1 ; sentenceIndex <= sentenceCount ; sentenceIndex ++ ) {
        textElements.add( sentenceGenerator.generate() ) ;
      }
      if( paragraphIndex < paragraphCount ) {
        textElements.add( Body.INTERSTICE ) ;
      }
    }
    return new Body( textElements ) ;
  }

  @org.novelang.outfit.Husk.Converter( converterClass = Bounded.class )
  public interface Configuration {
    Random getRandom() ;
    Configuration withRandom( Random random ) ;
    
    Bounded.IntegerInclusiveExclusive getSentenceCountRange() ;
    Configuration withSentenceCountRange( int lowerBound, int upperBound ) ;
    
    Bounded.IntegerInclusiveExclusive getParagraphCountRange() ;
    Configuration withParagraphCountRange( int lowerBound, int upperBound ) ;
    
    Generator< Sentence > getSentenceGenerator() ;
    Configuration withSentenceGenerator( Generator< Sentence > generator ) ;
  }
}
