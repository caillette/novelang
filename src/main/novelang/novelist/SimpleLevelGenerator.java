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
    this.random = configuration.random ;
    this.titleGenerator = configuration.titleGenerator ;
    this.bodyGenerator = configuration.bodyGenerator ;
    this.prelevelProbability = configuration.prelevelProbability ;
    this.sublevelProbability = configuration.sublevelProbability ;
    this.sublevelCountRange = configuration.sublevelCount ;
    this.maximumStackHeight = configuration.maximumDepth ;
    this.availableTags = configuration.tags ;
    this.tagAppearanceProbability = configuration.tagAppearanceProbability ;
  }

  private Stack stack = new Stack() ;

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

    public Stack() {
      previous = null ;
      maximumLevel = null ;
      levelCounter = 0 ;
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


  public final static class Configuration {
    private final Random random ;
    private final Bounded.Percentage prelevelProbability ;
    private final Bounded.Percentage sublevelProbability ;
    private final int maximumDepth ;
    private final Bounded.IntegerInclusiveExclusive sublevelCount ;
    private final Generator< Sentence > titleGenerator ;
    private final Generator< ? extends TextElement > bodyGenerator ;
    private final Bounded.Percentage tagAppearanceProbability;
    private final Set< Tag > tags ;


    public Configuration(
        final Random random,
        final int maximumDepth,
        final Bounded.IntegerInclusiveExclusive sublevelCount,
        final Bounded.Percentage sublevelProbability,
        final Bounded.Percentage prelevelProbability,
        final Generator< Sentence > titleGenerator,
        final Generator< ? extends TextElement > bodyGenerator,
        final Set<Tag> tags,
        final Bounded.Percentage tagAppearanceProbability
    ) {
      this.random = Preconditions.checkNotNull( random ) ;
      this.prelevelProbability = Preconditions.checkNotNull( prelevelProbability ) ;
      this.sublevelCount = Preconditions.checkNotNull( sublevelCount ) ;
      this.sublevelProbability = Preconditions.checkNotNull( sublevelProbability ) ;
      this.titleGenerator = titleGenerator ;
      this.bodyGenerator = bodyGenerator ;
      Preconditions.checkArgument( maximumDepth > 0 ) ;
      this.maximumDepth = maximumDepth ;
      this.tagAppearanceProbability = Preconditions.checkNotNull( tagAppearanceProbability ) ;
      this.tags = Preconditions.checkNotNull( tags ) ;
    }
  }
}
