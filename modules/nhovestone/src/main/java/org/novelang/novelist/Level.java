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
package org.novelang.novelist;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.novelang.designator.Tag;

/**
 * This level is different of the one use for document production (the one under
 * {@link org.novelang.parser.NodeKind#_LEVEL}), in the sense it may have no title.
 * This is useful to generate some paragraphs before the first level title of its depth.
 *
 * @author Laurent Caillette
 */
public class Level implements TextElement {

  private final TextElement markup;
  private final TextElement title ;
  private final TextElement body ;
  private final Iterable< Tag > tags ;

  public Level( final TextElement body ) {
    this.markup = TextElement.EMPTY ;
    this.title = TextElement.EMPTY ;
    this.body = body ;
    this.tags = ImmutableList.of() ;
  }

  public Level(
      final TextElement markup,
      final TextElement title,
      final TextElement body,
      final Iterable< Tag > tags
  ) {
    this.markup = markup;
    this.title = title ;
    this.body = body ;
    this.tags = tags ;
  }


  @Override
  public String getLiteral() {
    final StringBuilder builder = new StringBuilder() ;

    final String tagsAsString = Joiner.on( " " ).join(
        Iterables.transform( tags, Tag.FUNCTION_TOSOURCESTRING ) ) ;
    if( ! StringUtils.isBlank( tagsAsString ) ) {
      builder.append( "  " ) ;
      builder.append( tagsAsString ) ;
      builder.append( "\n" ) ;
    }

    if( ! StringUtils.isBlank( markup.getLiteral() ) ) {
      builder.append( markup.getLiteral() ) ;
      builder.append( " " ) ;
    }
    if( ! StringUtils.isBlank( title.getLiteral() ) ) {
      builder.append( title.getLiteral() ) ;
      builder.append( "\n\n" ) ;
    }
    if( ! StringUtils.isBlank( body.getLiteral() ) ) {
      builder.append( body.getLiteral() ) ;
      builder.append( "\n\n" ) ;
    }

    return builder.toString() ;
  }
}
