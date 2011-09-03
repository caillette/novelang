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

package org.novelang.designator;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.fest.reflect.core.Reflection;

/**
 * Encapsulation violation of {@link Tag} class.
 * 
 * @author Laurent Caillette
 */
public class TagTestTools {

  private TagTestTools() { }

  public static String getTagAsString( final Tag tag ) {
    return Reflection.field( "name" ).ofType( String.class ).in( tag ).get() ;
  }

  public static ImmutableSet< Tag > createTagSet( final String... tagNames ) {
    final ImmutableSet.Builder< Tag > builder = ImmutableSet.builder() ;
    for( final String tagName : tagNames ) {
      builder.add( new Tag( tagName ) ) ;
    }
    return builder.build() ;
  }

  public static String getTagSetAsString( final ImmutableSet< Tag > tags, final String separator ) {
    final Set< String > tagsAsString= Sets.newHashSet() ;
    for( final Tag tag : tags ) {
      tagsAsString.add( TagTestTools.getTagAsString( tag ) ) ;
    }
    return Joiner.on( ";" ).join( tagsAsString ) ;

  }

}
