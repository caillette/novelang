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

import com.google.common.base.Preconditions;

import static novelang.novelist.RandomizationTools.boundInteger;
import static novelang.novelist.RandomizationTools.percentage;

/**
 * Endless iterator creating {@link novelang.novelist.Word} instances.
 *
 * @author Laurent Caillette
 */
public class SimpleWordGenerator implements Generator.ForWord {

  private final Random random ;
  private final int minimumSignCount;
  private final int maximumSignCount;
  private final LetterFrequency frequency ;
  private final float circumflexPercentChances ;


  public SimpleWordGenerator( final Configuration configuration ) {
    this.random = configuration.random ;
    this.minimumSignCount = configuration.minimumSignCount ;
    this.maximumSignCount = configuration.maximumSignCount ;
    this.frequency = LetterFrequency.getFrequency( configuration.locale ) ;
    this.circumflexPercentChances = configuration.circumflexPercentChances ;
  }


  public Word generate() {
    final int letterCount = boundInteger( random, minimumSignCount, maximumSignCount ) ;
    final StringBuilder builder = new StringBuilder() ;
    for( int letterIndex = 1 ; letterIndex <= letterCount ; letterIndex ++ ) {
      Character c = frequency.get( percentage( random ) );
      if( letterIndex == 1 && testAndClearCapitalize() ) {
          c = Character.toTitleCase( c ) ;
      }
      if( letterIndex > 1 &&
          letterIndex < letterCount &&
          RandomizationTools.percentChances( random, circumflexPercentChances )
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



  public static class Configuration {
    private final Locale locale ;
    private final Random random ;
    private final int minimumSignCount ;
    private final int maximumSignCount ;
    private final float circumflexPercentChances ;

    public Configuration(
        final Locale locale,
        final Random random,
        final int minimumSignCount,
        final int maximumSignCount,
        final float circumflexPercentChances
    ) {
      this.locale = Preconditions.checkNotNull( locale ) ;
      this.random = Preconditions.checkNotNull( random ) ;
      Preconditions.checkArgument( minimumSignCount > 0 ) ;
      Preconditions.checkArgument( maximumSignCount >= minimumSignCount ) ;
      this.minimumSignCount = minimumSignCount ;
      this.maximumSignCount = maximumSignCount ;
      this.circumflexPercentChances = circumflexPercentChances ;
    }
  }
}