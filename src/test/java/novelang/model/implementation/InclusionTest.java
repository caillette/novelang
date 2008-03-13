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
import java.util.Arrays;
import java.io.File;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Ignore;
import org.apache.commons.lang.ClassUtils;
import novelang.model.common.Location;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

/**
 * @author Laurent Caillette
 */
public class InclusionTest {

  @Test
  public void noParagraphRange() {
    checkAllThere( 3, Arrays.asList( 1, 2, 3 ) ) ;
  }

  @Test
  public void allParagraphs() {
    addParagraph( 1 ) ;
    addParagraph( 2 ) ;
    addParagraph( 3 ) ;
    checkAllThere( 3, Arrays.asList( 1, 2, 3 ) ) ;
  }

  @Test
  public void allParagraphsWithReversedIndexing() {
    addParagraph( -2 ) ;
    addParagraph( -1 ) ;
    addParagraph( 0 ) ;
    checkAllThere( 3, Arrays.asList( 1, 2, 3 ) ) ;

  }

  @Test
  public void allParagraphsByRange() {
    addParagraphRange( 1, 3 ); ;
    checkAllThere( 3, Arrays.asList( 1, 2, 3 ) ) ;

  }

  @Test
  public void allParagraphsByRangeWithReversedIndexing() {
    addParagraphRange( 0, -2 ); ;
    checkAllThere( 3, Arrays.asList( 1, 2, 3 ) ) ;

  }

  @Test
  public void allParagraphsWithReversedIndexingAndInversedBounds() {
    addParagraphRange( -2, 0 ); ;
    checkAllThere( 3, Arrays.asList( 1, 2, 3 ) ) ;
  }

  @Test
  public void sparseParagraphs() {
    addParagraph( 1 ) ;
    addParagraph( 3 ) ;
    addParagraph( 5 ) ;
    addParagraphRange( 7, 9 ) ;
    checkAllThere( 10, Arrays.asList( 1, 3, 5, 7, 8, 9 ) ) ;
  }

  @Test
  @Ignore
  public void weirdInterval() {
    addParagraphRange( -1, 1 ) ;
    checkAllThere( 3, Arrays.asList( 1, 2 ) ) ;    
  }




// =======
// Fixture
// =======

  private List< Integer > findIncludedParagraphs( int paragraphCount ) {
    final List< Integer > includedParagraphs = Lists.newArrayList() ;
    Iterables.addAll( includedParagraphs, inclusion.calculateParagraphIndexes( paragraphCount ) ) ;
    return includedParagraphs ;
  }

  private void checkAllThere( int paragraphCount, Iterable< Integer > expected ) {
    final List< Integer > included = findIncludedParagraphs( paragraphCount ) ;
    Assert.assertEquals( expected, included ) ;
  }

  private void addParagraph( int index ) {
    inclusion.addParagraph( inclusion.createLocation( 0, 0 ), index ) ;
  }

  private void addParagraphRange( int start, int stop ) {
    inclusion.addParagraphRange( inclusion.createLocation( 0, 0 ), start, stop ) ;
  }

  private Inclusion inclusion ;

  @Before
  public void setUp() {
    final String bookName = ClassUtils.getShortClassName( getClass() );
    final Book book = new Book( bookName, new File( bookName ) ) ;
    final Location location = book.createLocation( 0, 0 ) ;
    inclusion = ( Inclusion ) book.createChapter( location )
        .createSection( location ).createInclusion( location, "i" ) ;
  }

}
