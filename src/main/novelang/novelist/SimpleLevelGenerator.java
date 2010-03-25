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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import novelang.designator.Tag;

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
  private final int maximumStackHeight ;
  private final Set< Tag > availableTags ;
  private final Bounded.Percentage tagAppearanceProbability ;


  public SimpleLevelGenerator( final Configuration configuration ) {
    this.random = checkNotNull( configuration.getRandom() ) ;
    this.prelevelProbability = checkNotNull( configuration.getPrelevelProbability() ) ;
    this.sublevelProbability = checkNotNull( configuration.getSublevelProbability() ) ;
    this.sublevelCountRange = checkNotNull( configuration.getSublevelCountRange() ) ;
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

    final Level level = new Level(
        markup,
        title,
        bodyGenerator.generate(),
        effectiveTags
    ) ;
    if( stack.maximumLevelReached() ) {
      stack = stack.pop() ;
      stack.incrementLevelCounter() ;
    } else {
      if( sublevelProbability.hit( random ) && stack.getHeight() < maximumStackHeight ) {
        final int sublevelCount = this.sublevelCountRange.boundInteger( random ) ;
        stack = new Stack( stack, sublevelCount ) ;
      } else {
        stack.incrementLevelCounter() ;
      }
    }
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
      previous = null ;
      maximumLevel = null ;
      levelCounter = initialLevelCounter ;
    }

    public Stack( final Stack previous, final int maximumLevel ) {
      this.previous = previous ;
      Preconditions.checkArgument( maximumLevel >= 0 ) ;
      this.maximumLevel = maximumLevel ;
      levelCounter = 1 ;
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

    /** @return true if maximum not reached. */
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


  public interface Configuration {
    
    Configuration withRandom( Random random ) ;
    Random getRandom() ;

    Configuration withPrelevelProbability( Bounded.Percentage percentage ) ;
    Bounded.Percentage getPrelevelProbability() ;

    Configuration withLevelCounterStart( int counter ) ;

    /**
     * Default value of 0 suitable for the first levels in a document.
     * When generating one document with several {@link novelang.novelist.Novelist.GhostWriter}s,
     * each one should have its own counter value.
     */
    int getLevelCounterStart() ;

    Configuration withSublevelProbability( Bounded.Percentage percentage ) ;
    Bounded.Percentage getSublevelProbability() ;

    Configuration withMaximumDepth( int depth ) ;
    int getMaximumDepth() ;

    Configuration withSublevelCountRange( Bounded.IntegerInclusiveExclusive range ) ;
    public Bounded.IntegerInclusiveExclusive getSublevelCountRange() ;

    Configuration withTitleGenerator( Generator< Sentence > generator ) ;
    Generator< Sentence > getTitleGenerator() ;

    Configuration withBodyGenerator( final Generator< ? extends TextElement > generator ) ;
    Generator< ? extends TextElement > getBodyGenerator() ;

    Configuration withTagAppearanceProbability( Bounded.Percentage percentage ) ;
    public Bounded.Percentage getTagAppearanceProbability() ;

    Configuration withTags( Set< Tag > tags ) ;
    Set< Tag > getTags() ;
  }
}
