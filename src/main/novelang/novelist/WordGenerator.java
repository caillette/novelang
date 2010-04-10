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

import java.util.Locale;
import java.util.Random;

import novelang.system.Husk;

/**
 * Endless iterator creating {@link novelang.novelist.Word} instances.
 *
 * @author Laurent Caillette
 */
public class WordGenerator implements Generator.ForWord {

  private final Random random ;
  private final Bounded.IntegerInclusiveExclusive signCount ;
  private final LetterDistribution distribution ;
  private final Bounded.Percentage circumflex ;


  public WordGenerator( final Configuration configuration ) {
    this.random = configuration.getRandom() ;
    this.signCount = configuration.getSignCount() ;
    this.distribution = LetterDistribution.getFrequency( configuration.getLocale() ) ;
    this.circumflex = configuration.getCircumflex() ;
  }


  public Word generate() {
    final int letterCount = signCount.boundInteger( random ) ;
    final StringBuilder builder = new StringBuilder() ;
    for( int letterIndex = 1 ; letterIndex <= letterCount ; letterIndex ++ ) {
      Character c = distribution.get( Bounded.newPercentage( random ) );
      if( letterIndex == 1 && testAndClearCapitalize() ) {
          c = Character.toTitleCase( c ) ;
      }
      if( letterIndex > 1 &&
          letterIndex < letterCount &&
          circumflex.hit( random )
      ) {
        builder.append( "^" ) ;  
      }
      builder.append( c ) ;
    }
    return new Word( builder.toString() ) ;
  }


  private boolean capitalizeNext = false ;

  public void capitalizeNext() {
    capitalizeNext = true ;
  }

  private boolean testAndClearCapitalize() {
    final boolean value = capitalizeNext ;
    capitalizeNext = false ;
    return value ;
  }



  @Husk.Converter( converterClass = Bounded.class )
  public interface Configuration {

    Locale getLocale() ;
    Configuration withLocale( Locale locale ) ;
    
    Random getRandom() ;
    Configuration withRandom( Random random ) ;
    
    Bounded.IntegerInclusiveExclusive getSignCount() ;
    Configuration withSignCount( int lowerBound, int upperBound ) ;
    
    Bounded.Percentage getCircumflex() ;
    Configuration withCircumflex( float probability ) ;
    
    
  }
}