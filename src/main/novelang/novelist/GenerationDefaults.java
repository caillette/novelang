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

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import novelang.designator.Tag;
import novelang.system.Pod;

/**
 * @author Laurent Caillette
 */
public interface GenerationDefaults {

  Random RANDOM = new Random( 0L ) ;

  SimpleWordGenerator.Configuration FOR_WORDS = new SimpleWordGenerator.Configuration(
      SupportedLocales.DEFAULT_LOCALE,
      RANDOM,
      Bounded.newInclusiveRange( 2, 12 ),
      Bounded.newPercentage( 0.01f )
  ) ;

  SimpleSentenceGenerator.Configuration FOR_SENTENCES = new SimpleSentenceGenerator.Configuration(
      SupportedLocales.DEFAULT_LOCALE,
      RANDOM,
      new SimpleWordGenerator( FOR_WORDS ),
      Bounded.newInclusiveRange( 5, 20 ),
      Bounded.newPercentage( 18.0f ),
      true
  ) ;

  SimpleSentenceGenerator.Configuration FOR_TITLES = new SimpleSentenceGenerator.Configuration(
      SupportedLocales.DEFAULT_LOCALE,
      RANDOM,
      new SimpleWordGenerator( FOR_WORDS ),
      Bounded.newInclusiveRange( 1, 4 ),
      Bounded.newPercentage( 10.0f ),
      false
  ) ;

  SimpleBodyGenerator.Configuration FOR_BODIES = new SimpleBodyGenerator.Configuration(
      RANDOM,
      Bounded.newInclusiveRange( 1, 4 ),
      Bounded.newInclusiveRange( 1, 5 ),
      new SimpleSentenceGenerator( FOR_SENTENCES )
  ) ;

  Set< Tag > NO_TAGS = ImmutableSet.of() ;

  Set< Tag > TEN_TAGS = ImmutableSet.of(
      new Tag( "Zero" ), new Tag( "One"), new Tag( "Two"),
      new Tag( "Three"), new Tag( "Four"), new Tag( "Five"),
      new Tag( "Six"), new Tag( "Seven"), new Tag( "Eight"),
      new Tag( "Nine")
  ) ;

  SimpleLevelGenerator.Configuration FOR_LEVELS =
      Pod.make( SimpleLevelGenerator.Configuration.class )
      .withRandom( RANDOM )
      .withMaximumDepth( 3 )
      .withSublevelCountRange( Bounded.newInclusiveRange( 0, 3 ) )
      .withSublevelProbability( Bounded.newPercentage( 40.0f ) )
      .withPrelevelProbability( Bounded.newPercentage( 100.0f ) )
      .withTitleGenerator( new SimpleSentenceGenerator( FOR_TITLES ) )
      .withBodyGenerator( new SimpleBodyGenerator( FOR_BODIES ) )
      .withTags( TEN_TAGS )
      .withTagAppearanceProbability( Bounded.newPercentage( 5.0f ) )
  ;

}
