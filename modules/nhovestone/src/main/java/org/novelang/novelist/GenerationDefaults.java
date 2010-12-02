/*
 * Copyright (C) 2010 Laurent Caillette
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

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.novelang.designator.Tag;
import org.novelang.outfit.Husk;

/**
 * @author Laurent Caillette
 */
public interface GenerationDefaults {

  Random RANDOM = new Random( 0L ) ;

  WordGenerator.Configuration FOR_WORDS = org.novelang.outfit.Husk.create( WordGenerator.Configuration.class )
      .withLocale( SupportedLocales.DEFAULT_LOCALE )
      .withRandom( RANDOM )
      .withSignCount( 2, 12 )
      .withCircumflex( 0.01f )
  ;

  SentenceGenerator.Configuration FOR_SENTENCES = 
      Husk.create( SentenceGenerator.Configuration.class )
      .withLocale( SupportedLocales.DEFAULT_LOCALE )
      .withRandom( RANDOM )
      .withWordGenerator( new WordGenerator( FOR_WORDS ) )
      .withWordCount( 3, 20 )
      .withMiddlePunctuationSign( 18.0f )
      .withEndingPunctuation( true )
  ;

  SentenceGenerator.Configuration FOR_TITLES = 
      org.novelang.outfit.Husk.create( SentenceGenerator.Configuration.class )
      .withLocale( SupportedLocales.DEFAULT_LOCALE )
      .withRandom( RANDOM )
      .withWordGenerator( new WordGenerator( FOR_WORDS ) )
      .withWordCount( 1, 4 )
      .withMiddlePunctuationSign( 10.0f )
      .withEndingPunctuation( false )
  ;

  BodyGenerator.Configuration FOR_BODIES = org.novelang.outfit.Husk.create( BodyGenerator.Configuration.class )
      .withRandom( RANDOM )
      .withParagraphCountRange( 1, 4 )
      .withSentenceCountRange( 1, 5 )
      .withSentenceGenerator( new SentenceGenerator( FOR_SENTENCES ) )
  ;

  Set< Tag > NO_TAGS = ImmutableSet.of() ;

  Set< Tag > TEN_TAGS = ImmutableSet.of(
      new Tag( "Zero" ), new Tag( "One"), new Tag( "Two"),
      new Tag( "Three"), new Tag( "Four"), new Tag( "Five"),
      new Tag( "Six"), new Tag( "Seven"), new Tag( "Eight"),
      new Tag( "Nine")
  ) ;

  LevelGenerator.Configuration FOR_LEVELS =
      org.novelang.outfit.Husk.create( LevelGenerator.Configuration.class )
      .withRandom( RANDOM )
      .withMaximumDepth( 3 )
      .withSublevelCountRange( 0, 3 )
      .withSublevelProbability( 40.0f )
      .withPrelevelProbability( 100.0f )
      .withLockLevelCounterAtDepthOne( false )
      .withTitleGenerator( new SentenceGenerator( FOR_TITLES ) )
      .withBodyGenerator( new BodyGenerator( FOR_BODIES ) )
      .withTags( TEN_TAGS )
      .withTagAppearanceProbability( 5.0f )
  ;

}
