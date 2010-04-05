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
package novelang.benchmark.scenario;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import novelang.designator.Tag;
import novelang.novelist.Generator;
import novelang.novelist.Level;
import novelang.novelist.Novelist;
import novelang.novelist.SimpleBodyGenerator;
import novelang.novelist.SimpleLevelGenerator;
import novelang.novelist.SimpleSentenceGenerator;
import novelang.novelist.SimpleWordGenerator;
import novelang.novelist.SupportedLocales;
import novelang.system.Husk;

/**
 * @author Laurent Caillette
 */
public class ScenarioLibrary {
  
  private ScenarioLibrary() { }

  public static SimpleLevelGenerator.Configuration createLevelGeneratorConfiguration(
      final Random random
  ) {

    final SimpleWordGenerator.Configuration forWords =
        Husk.create( SimpleWordGenerator.Configuration.class )
        .withLocale( SupportedLocales.DEFAULT_LOCALE )
        .withRandom( random )
        .withSignCount( 10, 10 )
        .withCircumflex( 0.0f )
    ;

    final SimpleSentenceGenerator.Configuration forSentences =
        Husk.create( SimpleSentenceGenerator.Configuration.class )
        .withLocale( SupportedLocales.DEFAULT_LOCALE )
        .withRandom( random )
        .withWordGenerator( new SimpleWordGenerator( forWords ) )
        .withWordCount( 10, 10 )
        .withMiddlePunctuationSign( 18.0f )
        .withEndingPunctuation( true )
    ;

    final SimpleSentenceGenerator.Configuration forTitles =
        Husk.create( SimpleSentenceGenerator.Configuration.class )
        .withLocale( SupportedLocales.DEFAULT_LOCALE )
        .withRandom( random )
        .withWordGenerator( new SimpleWordGenerator( forWords ) )
        .withWordCount( 5, 5 )
        .withMiddlePunctuationSign( 10.0f )
        .withEndingPunctuation( false )
    ;

    final SimpleBodyGenerator.Configuration forBodies =
        Husk.create( SimpleBodyGenerator.Configuration.class )
        .withRandom( random )
        .withParagraphCountRange( 5, 5 )
        .withSentenceCountRange( 5, 5 )
        .withSentenceGenerator( new SimpleSentenceGenerator( forSentences ) )
    ;


    final SimpleLevelGenerator.Configuration forLevels =
        Husk.create( SimpleLevelGenerator.Configuration.class )
        .withRandom( random )
        .withMaximumDepth( 3 )
        .withSublevelCountRange( 3, 3 )
        .withSublevelProbability( 100.0f )
        .withPrelevelProbability( 100.0f )
        .withLockLevelCounterAtDepthOne( false )
        .withTitleGenerator( new SimpleSentenceGenerator( forTitles ) )
        .withBodyGenerator( new SimpleBodyGenerator( forBodies ) )
        .withTags( TAGS )
        .withTagAppearanceProbability( 10.0f )
        .withLockLevelCounterAtDepthOne( true )
    ;

    return forLevels ;

  }

  private static class LevelGeneratorSupplier implements Novelist.GeneratorSupplier< Level > {
    private final Random random ;

    private LevelGeneratorSupplier( final Random random ) {
      this.random = random ;
    }

    public Generator< ? extends Level > get( final int number ) {
      final SimpleLevelGenerator.Configuration configuration =
          createLevelGeneratorConfiguration( random )
          .withLevelCounterStart( number )
      ;
      return new SimpleLevelGenerator( configuration ) ;
    }
  }


  private static abstract class AbstractUpsizerFactory implements Upsizer.Factory {
    private final Random random ;

    private AbstractUpsizerFactory( final Random random ) {
      this.random = random ;
    }

    public final String getDocumentRequest() {
      return "/" + Novelist.BOOK_NAME_RADIX + ".html" ;
    }

    public final Upsizer create( final File directory ) throws IOException {
      return createUpsizer( new Novelist(
          directory, "novella", new LevelGeneratorSupplier( random ), 1 ) ) ;
    }

    protected abstract Upsizer createUpsizer( final Novelist novelist ) ;

  }

  public static Upsizer.Factory createNovellaLengthUpsizerFactory( final Random random ) {
    return new AbstractUpsizerFactory( random ) {
      @Override
      protected Upsizer createUpsizer( final Novelist novelist ) {
        return new Upsizer.NovellaeLength( novelist );
      }
    } ;
  }

  public static Upsizer.Factory createNovellaCountUpsizerFactory( final Random random ) {
    return new AbstractUpsizerFactory( random ) {
      @Override
      protected Upsizer createUpsizer( final Novelist novelist ) {
        return new Upsizer.NovellaCount( novelist );
      }
    } ;
  }

  private static final Set< Tag > TAGS = ImmutableSet.of(
      new Tag( "Zero" ), new Tag( "One" ), new Tag( "Two" ),
      new Tag( "Three" ), new Tag( "Four" ), new Tag( "Five" ),
      new Tag( "Six" ), new Tag( "Seven" ), new Tag( "Eight" ),
      new Tag( "Nine" )
  ) ;


  public interface ConfigurationForTimeMeasurement
      extends Scenario.Configuration< ConfigurationForTimeMeasurement, TimeMeasurement > { }


}
