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
package novelang.nhovestone.scenario;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import novelang.designator.Tag;
import novelang.nhovestone.Scenario;
import novelang.novelist.BodyGenerator;
import novelang.novelist.Generator;
import novelang.novelist.Level;
import novelang.novelist.LevelGenerator;
import novelang.novelist.Novelist;
import novelang.novelist.SentenceGenerator;
import novelang.novelist.WordGenerator;
import novelang.novelist.SupportedLocales;
import novelang.system.Husk;

/**
 * @author Laurent Caillette
 */
public class ScenarioLibrary {
  
  private ScenarioLibrary() { }

  public static LevelGenerator.Configuration createLevelGeneratorConfiguration(
      final Random random
  ) {

    final WordGenerator.Configuration forWords =
        Husk.create( WordGenerator.Configuration.class )
        .withLocale( SupportedLocales.DEFAULT_LOCALE )
        .withRandom( random )
        .withSignCount( 6, 6 )
        .withCircumflex( 0.0f )
    ;

    final SentenceGenerator.Configuration forSentences =
        Husk.create( SentenceGenerator.Configuration.class )
        .withLocale( SupportedLocales.DEFAULT_LOCALE )
        .withRandom( random )
        .withWordGenerator( new WordGenerator( forWords ) )
        .withWordCount( 6, 6 )
        .withMiddlePunctuationSign( 18.0f )
        .withEndingPunctuation( true )
    ;

    final SentenceGenerator.Configuration forTitles =
        Husk.create( SentenceGenerator.Configuration.class )
        .withLocale( SupportedLocales.DEFAULT_LOCALE )
        .withRandom( random )
        .withWordGenerator( new WordGenerator( forWords ) )
        .withWordCount( 5, 5 )
        .withMiddlePunctuationSign( 10.0f )
        .withEndingPunctuation( false )
    ;

    final BodyGenerator.Configuration forBodies =
        Husk.create( BodyGenerator.Configuration.class )
        .withRandom( random )
        .withParagraphCountRange( 4, 4 )
        .withSentenceCountRange( 4, 4 )
        .withSentenceGenerator( new SentenceGenerator( forSentences ) )
    ;


    final LevelGenerator.Configuration forLevels =
        Husk.create( LevelGenerator.Configuration.class )
        .withRandom( random )
        .withMaximumDepth( 3 )
        .withSublevelCountRange( 3, 3 )
        .withSublevelProbability( 100.0f )
        .withPrelevelProbability( 100.0f )
        .withLockLevelCounterAtDepthOne( false )
        .withTitleGenerator( new SentenceGenerator( forTitles ) )
        .withBodyGenerator( new BodyGenerator( forBodies ) )
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
      final LevelGenerator.Configuration configuration =
          createLevelGeneratorConfiguration( random )
          .withLevelCounterStart( number )
      ;
      return new LevelGenerator( configuration ) ;
    }
  }


  private static abstract class AbstractUpsizerFactory implements Upsizer.Factory< Long > {
    private final Random random ;

    private AbstractUpsizerFactory( final Random random ) {
      this.random = random ;
    }

    @SuppressWarnings( { "HardcodedFileSeparator" } )
    public final String getDocumentRequest() {
      return "/" + Novelist.BOOK_NAME_RADIX + ".html" ;
    }

    public final Upsizer< Long > create( final File directory ) throws IOException {
      return createUpsizer( new Novelist(
          directory, "novella", new LevelGeneratorSupplier( random ), 1 ) ) ;
    }

    protected abstract Upsizer< Long > createUpsizer( final Novelist novelist ) ;

  }

  public static Upsizer.Factory< Long > createNovellaLengthUpsizerFactory( final Random random ) {
    return new AbstractUpsizerFactory( random ) {
      @Override
      protected Upsizer< Long > createUpsizer( final Novelist novelist ) {
        return new NovelistUpsizer.NovellaeLength( novelist ) ;
      }
    } ;
  }

  public static Upsizer.Factory< Long > createNovellaCountUpsizerFactory( final Random random ) {
    return new AbstractUpsizerFactory( random ) {
      @Override
      protected Upsizer< Long > createUpsizer( final Novelist novelist ) {
        return new NovelistUpsizer.NovellaCount( novelist ) ;
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
      extends Scenario.Configuration< ConfigurationForTimeMeasurement, Long, TimeMeasurement > { }


}
