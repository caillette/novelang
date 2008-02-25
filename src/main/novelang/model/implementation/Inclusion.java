/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.implementation;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import novelang.model.structural.StructuralInclusion;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.weaved.WeavedInclusion;
import novelang.model.weaved.UnknownIdentifierException;
import com.google.common.collect.Lists;
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public class Inclusion
    extends ContextualizedElement
    implements StructuralInclusion, WeavedInclusion
{

  private static final Logger LOGGER = LoggerFactory.getLogger( Inclusion.class ) ;
  private final List< ParagraphRange > paragraphRanges = Lists.newArrayList() ;
  private final String identifier ;
  private boolean collateWithPrevious ;

  public Inclusion(
      BookContext context,
      Location location,
      int position,
      String identifier
  ) {
    super( context.derive( "inclusion[" + position + "]" ), location ) ;
    this.identifier = identifier ;
  }

  public void addParagraphRange( Location location, int from, int to ) {
    paragraphRanges.add( new ParagraphRange( location, from, to ) ) ;
    LOGGER.debug(
        "Added paragraph range {}..{} to {} from {}",
        new Object[] { from, to, this, location }
    ) ;
  }

  public void addParagraph( Location location, int index ) {
    paragraphRanges.add( new ParagraphRange( location, index ) ) ;
    LOGGER.debug(
        "Added paragraph range {} to {} from {}",
        new Object[] { index, this, location } 
    ) ;
  }


  public void setCollateWithPrevious( boolean collateWithPrevious ) {
    this.collateWithPrevious = collateWithPrevious;
    LOGGER.debug( "CollateWithPrevious set to '{}' for {}", collateWithPrevious, this ) ;
  }

  public Iterable< Tree > buildTrees( Map< String, Tree > identifiers ) {
    final List< Tree > trees = Lists.newArrayList() ;
    // TODO treat paragraphs correctly.
    final Tree sectionTree = identifiers.get( identifier ) ;
    if( null == sectionTree ) {
      LOGGER.warn( "Unknown identifier '{}' found by {}", identifier, this ) ;
      collect( new UnknownIdentifierException( identifier ) ) ;
    } else {
      for( Tree tree : sectionTree.getChildren() ) {
        trees.add( tree ) ;
      }
    }
    return trees ;
  }


  public Iterable< Integer > calculateParagraphIndexes( int paragraphCount ) {
    final List< Integer > toBeIncluded = Lists.newArrayList() ;

    if( paragraphRanges.isEmpty() ) {
      for( Integer i = 1 ; i <= paragraphCount ; i++ ) {
        toBeIncluded.add( i ) ;
      }
    } else {

      for( final ParagraphRange range : paragraphRanges ) {
        final Iterable< Integer > paragraphsInRange = range.expand() ;

        for( Integer paragraph : paragraphsInRange ) {
          if( paragraph > paragraphCount ) {
            // TODO report problem.
          }
          if( paragraph == 0 ) {
            paragraph = paragraphCount ;
          } else  if( paragraph < 0 ) {
            if( paragraph < ( 0 - paragraphCount ) ) {
              // TODO report problem.
            } else {
              paragraph = paragraphCount + paragraph ;
            }
          }
          if( toBeIncluded.contains( paragraph ) ) {
            // TODO report problem.
          } else {
            toBeIncluded.add( paragraph ) ;
          }
        }
      }
    }

    return toBeIncluded ;
  }

  private static class ParagraphRange {

    private final Location location ;
    private final int start ;
    private final int end ;

    public ParagraphRange( Location location, int index ) {
      this.location = Objects.nonNull( location ) ;
      this.start = index ;
      this.end = index ;
    }

    public ParagraphRange( Location location, int start, int end ) {
      this.location = Objects.nonNull( location ) ;
      this.start = start;
      this.end = end;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public Iterable< Integer > expand() {
      final List< Integer > paragraphs = Lists.newArrayList() ;
      if( start < end ) {
        for( int i = start ; i <= end ; i++ ) {
          paragraphs.add( i ) ;
        }
      } else {
        for( int i = end; i <= start ; i++ ) {
          paragraphs.add( i ) ;
        }
      }
      return paragraphs ;
    }
  }
}
