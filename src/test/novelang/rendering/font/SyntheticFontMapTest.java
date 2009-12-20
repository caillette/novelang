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
package novelang.rendering.font;

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
 * Tests for {@link novelang.rendering.font.SyntheticFontMap#createSyntheticFontMap(novelang.configuration.FopFontStatus)}.
 *
 * @author Laurent Caillette
 */
public class SyntheticFontMapTest {

  @Test
  public void testFamily3Plus1() {


    final Multimap< String,FontQuadruplet > syntheticMap =
        SyntheticFontMap.createSyntheticFontMap( FONT_STATUS ) ;

    final Set< String > fontNames = syntheticMap.keySet() ;
    Assert.assertEquals( Sets.newHashSet( FONT_ONE, FONT_TWO ), fontNames ) ;

    final Collection< FontQuadruplet > quadrupletsOne = syntheticMap.get( FONT_ONE ) ;
    Assert.assertEquals( 4, quadrupletsOne.size() ) ;
    final Set< FontTriplet > tripletsOne = Sets.newHashSet() ;
    for( final FontQuadruplet quadruplet : quadrupletsOne ) {
      final FontTriplet triplet = quadruplet.getFontTriplet();
      if( tripletsOne.contains( triplet ) ) {
        Assert.fail( "Already present: " + triplet + "in " + tripletsOne ) ;
      }
      tripletsOne.add( triplet ) ;
    }
    Assert.assertEquals(
        Sets.newHashSet(
            TRIPLET_FONT_ONE_NORMAL_NORMAL,
            TRIPLET_FONT_ONE_NORMAL_BOLD,
            TRIPLET_FONT_ONE_ITALIC_NORMAL,
            TRIPLET_FONT_ONE_ITALIC_BOLD
        ),
        tripletsOne
    ) ;

    final Collection< FontQuadruplet > quadrupletsTwo = syntheticMap.get( FONT_TWO ) ;
    Assert.assertEquals( 1, quadrupletsTwo.size() ) ;
    Assert.assertEquals(
        TRIPLET_FONT_TWO_NORMAL_NORMAL,
        quadrupletsTwo.iterator().next().getFontTriplet()
    ) ;

  }

// =======
// Fixture
// =======

  private static final String FONT_ONE = "Font One" ;
  private static final String FONT_1 = "Font 1" ;
  private static final String FONT_TWO = "Font Two" ;
  private static final String FONT_2 = "Font 2" ;

  public static final String FAILED_FONT_X = "Font X" ;
  public static final String FAILED_FONT_Y = "Font Y" ;

  private static final String TTF_ONE_NORMAL_NORMAL = "One-normal-normal.ttf" ;
  private static final String TTF_ONE_ITALIC_NORMAL = "One-italic-normal.ttf" ;
  private static final String TTF_ONE_NORMAL_BOLD = "One-normal-bold.ttf" ;
  private static final String TTF_ONE_ITALIC_BOLD = "One-italic-bold.ttf" ;
  private static final String TTF_TWO_NORMAL_NORMAL = "Two-normal-normal.ttf" ;

  private static final FontTriplet TRIPLET_FONT_ONE_NORMAL_NORMAL =
      tripletNormalNormal( FONT_ONE, 0 ) ;
  private static final FontTriplet TRIPLET_FONT_ONE_NORMAL_BOLD =
      tripletNormalBold( FONT_ONE, 5 ) ;
  private static final FontTriplet TRIPLET_FONT_ONE_ITALIC_NORMAL =
      tripletItalicNormal( FONT_ONE, 7 ) ;
  private static final FontTriplet TRIPLET_FONT_ONE_ITALIC_BOLD =
      tripletItalicBold( FONT_ONE, 8 ) ;

  private static final FontTriplet TRIPLET_FONT_NORMAL_BOLD =
      tripletNormalBold( FONT_1, 0 ) ;
  private static final FontTriplet TRIPLET_FONT_ITALIC_NORMAL =
      tripletItalicNormal( FONT_1, 0 ) ;
  private static final FontTriplet TRIPLET_FONT_ITALIC_BOLD =
      tripletItalicBold( FONT_1, 0 ) ;

  private static final FontTriplet TRIPLET_FONT_TWO_NORMAL_NORMAL =
      tripletNormalNormal( FONT_TWO, 7 ) ;
  private static final FontTriplet TRIPLET_FONT_2_NORMAL_NORMAL =
      tripletNormalNormal( FONT_2, 0 ) ;

  private static final EmbedFontInfo INFO_FONT_ONE_NORMAL_NORMAL = embedFontInfo(
      TTF_ONE_NORMAL_NORMAL,
      TRIPLET_FONT_ONE_NORMAL_NORMAL
  ) ;
  private static final EmbedFontInfo INFO_FONT_ONE_ITALIC_NORMAL = embedFontInfo(
      TTF_ONE_ITALIC_NORMAL,
      TRIPLET_FONT_ONE_ITALIC_NORMAL,
      TRIPLET_FONT_ITALIC_NORMAL
  ) ;
  private static final EmbedFontInfo INFO_FONT_ONE_NORMAL_BOLD = embedFontInfo(
      TTF_ONE_NORMAL_BOLD,
      TRIPLET_FONT_ONE_NORMAL_BOLD,
      TRIPLET_FONT_NORMAL_BOLD
  ) ;
  private static final EmbedFontInfo INFO_FONT_ONE_ITALIC_BOLD = embedFontInfo(
      TTF_ONE_ITALIC_BOLD,
      TRIPLET_FONT_ONE_ITALIC_BOLD,
      TRIPLET_FONT_ITALIC_BOLD
  ) ;

  private static final EmbedFontInfo INFO_FONT_TWO_NORMAL_NORMAL = embedFontInfo(
      TTF_TWO_NORMAL_NORMAL,
      TRIPLET_FONT_2_NORMAL_NORMAL,
      TRIPLET_FONT_TWO_NORMAL_NORMAL
  ) ;

  private static final Iterable< EmbedFontInfo > FONT_INFOS = Lists.newArrayList(
      INFO_FONT_ONE_NORMAL_NORMAL,
      INFO_FONT_ONE_ITALIC_NORMAL,
      INFO_FONT_ONE_NORMAL_BOLD,
      INFO_FONT_ONE_ITALIC_BOLD,
      INFO_FONT_TWO_NORMAL_NORMAL
  ) ;

  public static final Map< String, EmbedFontInfo > FAILED_FONTS = ImmutableMap.of(
      FAILED_FONT_X, new EmbedFontInfo( "", false, null, "" ),
      FAILED_FONT_Y, new EmbedFontInfo( "", false, null, "" )
  ) ;

  public static final FopFontStatus FONT_STATUS = new FopFontStatus(
      FONT_INFOS,
      FAILED_FONTS ) ;

  private static EmbedFontInfo embedFontInfo( 
      final String embedFontFile, 
      final FontTriplet... fontTriplets 
  ) {
    return new EmbedFontInfo(
        null, // metrics file, no need for that.
        true, // kerning, no need for that.
        Arrays.asList( fontTriplets ),
        embedFontFile
    ) ;
  }

  private static FontTriplet tripletNormalNormal( final String name, final int priority ) {
    return new FontTriplet( name, Font.STYLE_NORMAL, Font.WEIGHT_NORMAL, priority ) ;
  }

  private static FontTriplet tripletItalicNormal( final String name, final int priority ) {
    return new FontTriplet( name, Font.STYLE_ITALIC, Font.WEIGHT_NORMAL, priority ) ;
  }

  private static FontTriplet tripletNormalBold( final String name, final int priority ) {
    return new FontTriplet( name, Font.STYLE_NORMAL, Font.WEIGHT_BOLD, priority ) ;
  }

  private static FontTriplet tripletItalicBold( final String name, final int priority ) {
    return new FontTriplet( name, Font.STYLE_ITALIC, Font.WEIGHT_BOLD, priority ) ;
  }

}
