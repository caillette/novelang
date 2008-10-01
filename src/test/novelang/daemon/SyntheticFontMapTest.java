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
package novelang.daemon;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.Font;
import org.apache.fop.fonts.FontTriplet;
import org.junit.Assert;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import novelang.configuration.FontQuadruplet;
import novelang.configuration.FopFontStatus;

/**
 * Tests for {@link FontListHandler#createSyntheticFontMap(novelang.configuration.FopFontStatus)}.
 *
 * @author Laurent Caillette
 */
public class SyntheticFontMapTest {

  @Test
  public void testFamily3Plus1() {

    final FontTriplet tripletFontOneNormalNormal = tripletNormalNormal( FONT_ONE, 0 ) ;
    final FontTriplet tripletFontOneNormalBold = tripletNormalBold( FONT_ONE, 5 ) ;
    final FontTriplet tripletFontOneItalicNormal = tripletItalicNormal( FONT_ONE, 7 ) ;
    final FontTriplet tripletFontOneItalicBold = tripletItalicBold( FONT_ONE, 8 ) ;

    final FontTriplet tripletFont1NormalBold = tripletNormalBold( FONT_1, 0 ) ;
    final FontTriplet tripletFont1ItalicNormal = tripletItalicNormal( FONT_1, 0 ) ;
    final FontTriplet tripletFont1ItalicBold = tripletItalicBold( FONT_1, 0 ) ;

    final FontTriplet tripletFontTwoNormalNormal = tripletNormalNormal( FONT_TWO, 7 ) ;
    final FontTriplet tripletFont2NormalNormal = tripletNormalNormal( FONT_2, 0 ) ;

    final EmbedFontInfo infoFontOneNormalNormal = embedFontInfo(
        TTF_ONE_NORMAL_NORMAL,
        tripletFontOneNormalNormal
    ) ;
    final EmbedFontInfo infoFontOneItalicNormal = embedFontInfo(
        TTF_ONE_ITALIC_NORMAL,
        tripletFontOneItalicNormal,
        tripletFont1ItalicNormal
    ) ;
    final EmbedFontInfo infoFontOneNormalBold = embedFontInfo(
        TTF_ONE_NORMAL_BOLD,
        tripletFontOneNormalBold,
        tripletFont1NormalBold
    ) ;
    final EmbedFontInfo infoFontOneItalicBold = embedFontInfo(
        TTF_ONE_ITALIC_BOLD,
        tripletFontOneItalicBold,
        tripletFont1ItalicBold
    ) ;

    final EmbedFontInfo infoFontTwoNormalNormal = embedFontInfo(
        TTF_TWO_NORMAL_NORMAL,
        tripletFont2NormalNormal,
        tripletFontTwoNormalNormal
    ) ;

    final Iterable< EmbedFontInfo > fontInfos = Lists.newArrayList(
        infoFontOneNormalNormal,
        infoFontOneItalicNormal,
        infoFontOneNormalBold,
        infoFontOneItalicBold,
        infoFontTwoNormalNormal
    ) ;
    final Map< String, EmbedFontInfo > failedFonts = ImmutableMap.of() ;

    final FopFontStatus fontStatus = new FopFontStatus( fontInfos, failedFonts ) ;

    final Multimap< String,FontQuadruplet > syntheticMap =
        FontListHandler.createSyntheticFontMap( fontStatus ) ;

    final Set< String > fontNames = syntheticMap.keySet() ;
    Assert.assertEquals( Sets.newHashSet( FONT_ONE, FONT_TWO ), fontNames ) ;

    final Collection< FontQuadruplet > quadrupletsOne = syntheticMap.get( FONT_ONE ) ;
    Assert.assertEquals( 4, quadrupletsOne.size() ) ;
    final Set< FontTriplet > tripletsOne = Sets.newHashSet() ;
    for( FontQuadruplet quadruplet : quadrupletsOne ) {
      final FontTriplet triplet = quadruplet.getFontTriplet();
      if( tripletsOne.contains( triplet ) ) {
        Assert.fail( "Already present: " + triplet + "in " + tripletsOne ) ;
      }
      tripletsOne.add( triplet ) ;
    }
    Assert.assertEquals(
        Sets.newHashSet(
            tripletFontOneNormalNormal,
            tripletFontOneNormalBold,
            tripletFontOneItalicNormal,
            tripletFontOneItalicBold
        ),
        tripletsOne
    ) ;

    final Collection< FontQuadruplet > quadrupletsTwo = syntheticMap.get( FONT_TWO ) ;
    Assert.assertEquals( 1, quadrupletsTwo.size() ) ;
    Assert.assertEquals(
        tripletFontTwoNormalNormal,
        quadrupletsTwo.iterator().next().getFontTriplet()
    ) ;

  }

  private EmbedFontInfo embedFontInfo( String embedFontFile, FontTriplet... fontTriplets ) {
    return new EmbedFontInfo(
        null, // metrics file, no need for that.
        true, // kerning, no need for that.
        Arrays.asList( fontTriplets ),
        embedFontFile
    );
  }

// =======
// Fixture
// =======

  private static final String FONT_ONE = "Font One" ;
  private static final String FONT_1 = "Font 1" ;
  private static final String FONT_TWO = "Font Two" ;
  private static final String FONT_2 = "Font 2" ;

  private static final String TTF_ONE_NORMAL_NORMAL = "One-normal-normal.ttf" ;
  private static final String TTF_ONE_ITALIC_NORMAL = "One-italic-normal.ttf" ;
  private static final String TTF_ONE_NORMAL_BOLD = "One-normal-bold.ttf" ;
  private static final String TTF_ONE_ITALIC_BOLD = "One-italic-bold.ttf" ;
  private static final String TTF_TWO_NORMAL_NORMAL = "Two-normal-normal.ttf" ;

  private static FontTriplet tripletNormalNormal( String name, int priority ) {
    return new FontTriplet( name, Font.STYLE_NORMAL, Font.WEIGHT_NORMAL, priority ) ;
  }

  private static FontTriplet tripletItalicNormal( String name, int priority ) {
    return new FontTriplet( name, Font.STYLE_ITALIC, Font.WEIGHT_NORMAL, priority ) ;
  }

  private static FontTriplet tripletNormalBold( String name, int priority ) {
    return new FontTriplet( name, Font.STYLE_NORMAL, Font.WEIGHT_BOLD, priority ) ;
  }

  private static FontTriplet tripletItalicBold( String name, int priority ) {
    return new FontTriplet( name, Font.STYLE_ITALIC, Font.WEIGHT_BOLD, priority ) ;
  }

}
