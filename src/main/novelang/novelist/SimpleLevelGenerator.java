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

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import novelang.designator.Tag;
import novelang.system.Husk;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Laurent Caillette
 */
public class SimpleLevelGenerator implements Generator< Level > {

  private final Random random ;
  private final Generator< Sentence > titleGenerator ;
  private final Generator< ? extends TextElement > bodyGenerator ;
  private final Bounded.Percentage prelevelProbability ;
  private final Bounded.Percentage sublevelProbability ;
  private final Bounded.IntegerInclusiveExclusive sublevelCountRange;
  private final boolean lockLevelCounterAtDepthOne ;
  private final int maximumStackHeight ;
  private final Set< Tag > availableTags ;
  private final Bounded.Percentage tagAppearanceProbability ;


  public SimpleLevelGenerator( final Configuration configuration ) {
    this.random = checkNotNull( configuration.getRandom() ) ;
    this.prelevelProbability = checkNotNull( configuration.getPrelevelProbability() ) ;
    this.sublevelProbability = checkNotNull( configuration.getSublevelProbability() ) ;
    this.sublevelCountRange = checkNotNull( configuration.getSublevelCountRange() ) ;
    this.lockLevelCounterAtDepthOne = configuration.getLockLevelCounterAtDepthOne() ;
    this.titleGenerator = checkNotNull( configuration.getTitleGenerator() ) ;
    this.bodyGenerator = checkNotNull( configuration.getBodyGenerator() ) ;
    this.maximumStackHeight = configuration.getMaximumDepth() ;
    Preconditions.checkArgument( maximumStackHeight > 0 ) ;
    this.tagAppearanceProbability = checkNotNull( configuration.getTagAppearanceProbability() ) ;
    this.availableTags = checkNotNull( configuration.getTags() ) ;

    final int levelCounterStart = configuration.getLevelCounterStart();
    Preconditions.checkArgument( levelCounterStart >= 0 ) ;
    stack = new Stack( levelCounterStart ) ;
  }

  private Stack stack ;

  public Level generate() {

    if( stack.getLevelCounter() == 0 && stack.getHeight() == 1 ) {
      stack.incrementLevelCounter() ;
      if( prelevelProbability.hit( random ) ) {
        // Pre-title body.
        return new Level( bodyGenerator.generate() ) ;
      }
    }
    // If we get here we didn't return pre-title body so we continue with a plain, titled level.

    final Markup markup = createMarkup( stack.getHeight() ) ;
    final Sentence title = new Sentence(
        new Word( "`" + stack.countersHierarchyAsString() + "` " ),
        titleGenerator.generate()
    ) ;
    final Set< Tag > effectiveTags = Sets.newHashSet() ;
    for( final Tag tag : availableTags ) {
      if( tagAppearanceProbability.hit( random ) ) {
        effectiveTags.add( tag ) ;
      }
    }

    if( stack.maximumLevelReached() ) {
      stack = stack.pop() ;
      stack.incrementLevelCounter() ;
    } else {
      if( lockLevelCounterAtDepthOne && stack.getHeight() == 1 ) {
        stack = new Stack( stack, null /* No maximum level. */ ) ;
      } else if( sublevelProbability.hit( random ) && stack.getHeight() < maximumStackHeight ) {
        final int sublevelCount = this.sublevelCountRange.boundInteger( random ) ;
        stack = new Stack( stack, sublevelCount ) ;
      } else {
        stack.incrementLevelCounter() ;
      }
    }

    final Level level = new Level(
        markup,
        title,
        bodyGenerator.generate(),
        effectiveTags
    ) ;

    return level ;

  }

  private static Markup createMarkup( final int depth ) {
    final StringBuilder text = new StringBuilder( "=" ) ;
    for( int i = 0 ; i < depth ; i++ ) {
      text.append( "=" ) ;
    }
    return new Markup( text.toString() ) ;
  }


  /**
   * Keeps track of the counter at given depth.
   */
  private final static class Stack {

    private final Stack previous ;
    private final Integer maximumLevel;
    private int levelCounter ;

    public Stack( final int initialLevelCounter ) {
      this( null, null, initialLevelCounter ) ;
    }

    public Stack( final Stack previous, final Integer maximumLevel ) {
      this( previous, maximumLevel, 1 ) ;
    }

    public Stack(
        final Stack previous,
        final Integer maximumLevel,
        final int initialLevelCounter
    ) {
      this.previous = previous ;
      Preconditions.checkArgument( maximumLevel == null || maximumLevel >= 0, maximumLevel ) ;
      this.maximumLevel = maximumLevel ;
      levelCounter = initialLevelCounter ;
    }

    public boolean isBottom() {
      return previous == null ;
    }

    public Stack pop() {
      if( isBottom() ) {
        throw new IllegalStateException( "Can't pop the bottom, should not happen" ) ;
      }
      return previous ;
    }

    public int getLevelCounter() {
      return levelCounter;
    }

    private int getHeight() {
      return isBottom() ? 1 : 1 + previous.getHeight() ;
    }

    public void incrementLevelCounter() {
      if( maximumLevelReached() ) {
        throw new IllegalStateException( "Don't increment when maximum reached" ) ;
      }
      levelCounter++ ;
    }

    public boolean maximumLevelReached() {
      return maximumLevel != null && levelCounter > maximumLevel ;
    }

    public String countersHierarchyAsString() {
      if( isBottom() ) {
        return Integer.toString( levelCounter ) + "." ;
      } else {
        return previous.countersHierarchyAsString() + levelCounter + "." ;
      }
    }
  }


  @Husk.Converter( converterClass = Bounded.class )
  public interface Configuration {
    
    Configuration withRandom( Random random ) ;
    Random getRandom() ;

    Configuration withPrelevelProbability( float percentage ) ;
    Bounded.Percentage getPrelevelProbability() ;

    Configuration withLevelCounterStart( int counter ) ;

    /**
     * Default value of 0 suitable for the first levels in a document.
     * When generating one document with several {@link novelang.novelist.Novelist.Ghostwriter}s,
     * each one should have its own counter value.
     */
    int getLevelCounterStart() ;

    Configuration withSublevelProbability( float percentage ) ;
    Bounded.Percentage getSublevelProbability() ;

    Configuration withMaximumDepth( int depth ) ;
    int getMaximumDepth() ;

    Configuration withSublevelCountRange( int lowerBound, int upperBound ) ;
    Bounded.IntegerInclusiveExclusive getSublevelCountRange() ;

    Configuration withLockLevelCounterAtDepthOne( boolean lock ) ;
    boolean getLockLevelCounterAtDepthOne() ;

    Configuration withTitleGenerator( Generator< Sentence > generator ) ;
    Generator< Sentence > getTitleGenerator() ;

    Configuration withBodyGenerator( final Generator< ? extends TextElement > generator ) ;
    Generator< ? extends TextElement > getBodyGenerator() ;

    Configuration withTagAppearanceProbability( float percentage ) ;
    Bounded.Percentage getTagAppearanceProbability() ;

    Configuration withTags( Set< Tag > tags ) ;
    Set< Tag > getTags() ;
  }
  
}
