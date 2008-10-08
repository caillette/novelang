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

import java.util.Set;
import java.util.List;

import org.apache.fop.fonts.EmbedFontInfo;
import org.apache.fop.fonts.FontTriplet;
import novelang.configuration.FontQuadruplet;
import novelang.configuration.FopFontStatus;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.base.Predicate;

/**
 * @author Laurent Caillette
 */
public class SyntheticFontMap {
  public static Iterable<FontQuadruplet> retainPriorityAboveZero(
      Iterable< FontQuadruplet > quadruplets
  ) {
    return Iterables.filter( quadruplets, new Predicate< FontQuadruplet >() {
      public boolean apply( FontQuadruplet quadruplet ) {
        return quadruplet.getFontTriplet().getPriority() > 0 ;
      }
    } ) ;
  }

  public static Iterable< FontQuadruplet > retainPriorityZero(
      Iterable< FontQuadruplet > quadruplets
  ) {
    return Iterables.filter( quadruplets, new Predicate< FontQuadruplet >() {
      public boolean apply( FontQuadruplet quadruplet ) {
        return quadruplet.getFontTriplet().getPriority() == 0 ;
      }
    } ) ;
  }

  public static Multimap< String, FontQuadruplet > mapQuadrupletsByCleanNames(
      Iterable< FontQuadruplet > quadruplets
  ) {
    final Multimap< String, FontQuadruplet > map = Multimaps.newHashMultimap() ;
    for( FontQuadruplet quadruplet : quadruplets ) {
      map.put( quadruplet.getFontTriplet().getName(), quadruplet ) ;
    }
    return ImmutableMultimap.copyOf( map ) ;
  }

  public static Multimap< String, FontQuadruplet > extractTripletsWithKnownName(
      Set< String > names,
      Iterable< FontQuadruplet > quadruplets
  ) {
    final Multimap< String, FontQuadruplet > extracted = Multimaps.newHashMultimap() ;
    for( FontQuadruplet quadruplet : quadruplets ) {
      final String name = quadruplet.getFontTriplet().getName() ;
      if( names.contains( name ) ) {
        extracted.put( name, quadruplet ) ;
      }
    }
    return extracted ;
  }

  public static Multimap< String, FontQuadruplet > createSyntheticFontMap(
      FopFontStatus fontStatus
  ) {
    final List< FontQuadruplet > quadruplets = Lists.newArrayList() ;

    for( EmbedFontInfo fontInfo : fontStatus.getFontInfos() ) {
      for( Object fontTripletAsObject : fontInfo.getFontTriplets() ) {
        final FontTriplet fontTriplet = ( FontTriplet ) fontTripletAsObject ;
        final FontQuadruplet fontQuadruplet =
            new FontQuadruplet( fontInfo.getEmbedFile(), fontTriplet ) ;
        quadruplets.add( fontQuadruplet ) ;
      }
    }
    final Iterable< FontQuadruplet > quadrupletsPriorityAboveZero =
        retainPriorityAboveZero( quadruplets ) ;
    final Iterable< FontQuadruplet > quadrupletsPriorityZero =
        retainPriorityZero( quadruplets ) ;
    final Multimap< String, FontQuadruplet > quadrupletsByCleanNames =
        Multimaps.newArrayListMultimap(
            mapQuadrupletsByCleanNames( quadrupletsPriorityAboveZero ) ) ;
    quadrupletsByCleanNames.putAll(
        extractTripletsWithKnownName( quadrupletsByCleanNames.keySet(), quadrupletsPriorityZero )
    ) ;

    return quadrupletsByCleanNames ;
  }
}