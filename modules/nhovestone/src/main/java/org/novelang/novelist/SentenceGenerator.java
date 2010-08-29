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
package org.novelang.novelist;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.common.collect.Lists;
import org.novelang.outfit.Husk;

/**
 * @author Laurent Caillette
 */
public class SentenceGenerator implements Generator< Sentence > {

  private final Locale locale ;
  private final Random random ;
  private final Generator.ForWord wordGenerator ;
  private final Bounded.IntegerInclusiveExclusive wordCountRange;
  private final Bounded.Percentage middlePunctuationSign;
  private final boolean hasEndingPunctuation ;


  public SentenceGenerator( final Configuration configuration ) {
    this.locale = configuration.getLocale() ;
    this.random = configuration.getRandom() ;
    this.wordGenerator = configuration.getWordGenerator() ;
    this.wordCountRange = configuration.getWordCount() ;
    this.middlePunctuationSign = configuration.getMiddlePunctuationSign() ;
    this.hasEndingPunctuation = configuration.getEndingPunctuation() ;
  }

  public Sentence generate() {
    final int wordCount = this.wordCountRange.boundInteger( random ) ;
    final List< TextElement > textElements = Lists.newArrayList() ;
    wordGenerator.capitalizeNext() ;

    boolean lastAddedMiddlePunctuation = false ;
    for( int wordIndex = 1 ; wordIndex <= wordCount ; wordIndex ++ ) {
      final Word word = wordGenerator.generate() ;
      textElements.add( word ) ;
      if( lastAddedMiddlePunctuation ) {
        lastAddedMiddlePunctuation = false ;
      } else {
        if( ( wordIndex < wordCount ) &&
            middlePunctuationSign.hit( random )
        ) {
          textElements.add( Punctuation.getMiddle( locale, Bounded.newPercentage( random ) ) ) ;
          lastAddedMiddlePunctuation = true ;
        }
      }
    }
    if( hasEndingPunctuation ) {
      textElements.add( Punctuation.getEnding( locale, Bounded.newPercentage( random ) ) ) ;
    }

    return new Sentence( textElements ) ;
  }


  @Husk.Converter( converterClass = Bounded.class )
  public interface Configuration {

    Locale getLocale() ;
    Configuration withLocale( Locale locale ) ;
    
    Random getRandom() ;
    Configuration withRandom( Random random ) ;
    
    ForWord getWordGenerator() ;
    Configuration withWordGenerator( ForWord wordGenerator ) ;
    
    Bounded.IntegerInclusiveExclusive getWordCount() ;
    Configuration withWordCount( int lowerBound, int upperBound ) ;
    
    Bounded.Percentage getMiddlePunctuationSign() ;
    Configuration withMiddlePunctuationSign( float probability ) ;
    
    boolean getEndingPunctuation() ;
    Configuration withEndingPunctuation( boolean hasEnding ) ;

  }

}
