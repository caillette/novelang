/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.build.antlr;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import junit.framework.Assert;
import org.novelang.common.TagBehavior;
import org.junit.Test;


/**
 *
 * @author Laurent Caillette
 */
public class TokenEnumerationGeneratorTest {

  @Test
  public void testFindTokens() {
    final String tokensDeclaration =
        "SOME_UNUSUED_STUFF ; \n" +
        "tokens { \n" +
        " STUFF ; // punctuationsign=true tagbehavior=SCOPE \n" +
        " OTHER_STUFF;\n" +
        "}"
    ;
    final Iterable< TokenGenerator.Item > tokens =
        TokenGenerator.findAntlrTokens( tokensDeclaration ) ;
    final List< TokenGenerator.Item > tokenList = ImmutableList.copyOf( tokens ) ;
    // Don't assert on token number because of synthetic tokens.

    final TokenGenerator.Item stuff = getItemWithName( tokenList, "STUFF" ) ;
    Assert.assertNotNull( stuff ) ;
    Assert.assertTrue( stuff.punctuationSign ) ;
    Assert.assertEquals( TagBehavior.SCOPE.name(), stuff.tagBehavior.name() ) ;

    final TokenGenerator.Item otherStuff = getItemWithName( tokenList, "OTHER_STUFF" ) ;
    Assert.assertNotNull( otherStuff ) ;
    Assert.assertFalse( otherStuff.punctuationSign ); ;
    Assert.assertEquals( TagBehavior.NON_TRAVERSABLE.name(), otherStuff.tagBehavior.name() ) ;
  }


// =======
// Fixture
// =======

  private static TokenGenerator.Item getItemWithName(
      final List< TokenGenerator.Item > items,
      final String name
  ) {
    final Predicate< TokenGenerator.Item > predicate =
        new Predicate< TokenGenerator.Item >() {
          @Override
          public boolean apply( final TokenGenerator.Item item ) {
            return name.equals( item.name ) ;
          }
        }
    ;
    final Iterable< TokenGenerator.Item > itemsWithName =
        Iterables.filter( items, predicate ) ;
    final int elementCount = Iterables.size( itemsWithName );
    switch( elementCount ) {
      case 0 : return null ;
      case 1 : return itemsWithName.iterator().next() ;
      default : throw new IllegalArgumentException(
          "More than one element with name '" + name + "' inside " + itemsWithName ) ;
    }

  }

}