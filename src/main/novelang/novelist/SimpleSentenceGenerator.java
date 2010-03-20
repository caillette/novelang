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
package novelang.novelist;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import static novelang.novelist.RandomizationTools.percentChances;
import static novelang.novelist.RandomizationTools.percentage;

/**
 * @author Laurent Caillette
 */
public class SimpleSentenceGenerator implements Generator< Sentence > {

  private final Locale locale ;
  private final Random random ;
  private final Generator.ForWord wordGenerator ;
  private final int minimumWordCount ;
  private final int maximumWordCount ;
  private final float middlePunctuationSignPercentChances ;


  public SimpleSentenceGenerator( final Configuration configuration ) {
    this.locale = configuration.locale ;
    this.random = configuration.random ;
    this.wordGenerator = configuration.wordGenerator ;
    this.minimumWordCount = configuration.minimumWordCount ;
    this.maximumWordCount = configuration.maximumWordCount ;
    this.middlePunctuationSignPercentChances = configuration.middlePunctuationSignPercentChances ;
  }

  public Sentence generate() {
    final int wordCount =
        RandomizationTools.boundInteger( random, minimumWordCount, maximumWordCount ) ;
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
            percentChances( random, middlePunctuationSignPercentChances )
        ) {
          textElements.add( Punctuation.getMiddle( locale, percentage( random ) ) ) ;
          lastAddedMiddlePunctuation = true ;
        }
      }
    }
    textElements.add( Punctuation.getEnding( locale, percentage( random ) ) ) ;

    return new Sentence( textElements ) ;
  }


  public static class Configuration {
    private final Locale locale ;
    private final Random random ;
    private final ForWord wordGenerator ;
    private final int minimumWordCount ;
    private final int maximumWordCount ;
    private final float middlePunctuationSignPercentChances;

    public Configuration(
        final Locale locale,
        final Random random,
        final ForWord wordGenerator,
        final int minimumWordCount,
        final int maximumWordCount,
        final float middlePunctuationSignPercentChances
    ) {
      this.middlePunctuationSignPercentChances = middlePunctuationSignPercentChances;
      this.locale = Preconditions.checkNotNull( locale ) ;
      this.random = Preconditions.checkNotNull( random ) ;
      this.wordGenerator = Preconditions.checkNotNull( wordGenerator ) ;
      Preconditions.checkArgument( minimumWordCount > 0 ) ;
      Preconditions.checkArgument( maximumWordCount >= minimumWordCount ) ;
      this.minimumWordCount = minimumWordCount ;
      this.maximumWordCount = maximumWordCount ;
    }
  }

}
