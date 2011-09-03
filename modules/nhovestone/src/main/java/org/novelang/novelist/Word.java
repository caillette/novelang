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

import com.google.common.base.Preconditions;

/**
 * Single word, instead of {@code String}, for better typing.
 *
 * @author Laurent Caillette
 */
public class Word implements TextElement {

  private final String literal ;

  public Word( final String literal ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( literal ) ) ;
    this.literal = literal ;
  }

  @Override
  public String getLiteral() {
    return literal ;
  }
}
