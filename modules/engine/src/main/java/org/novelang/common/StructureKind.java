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

package org.novelang.common;

import com.google.common.collect.ObjectArrays;

/**
 * Tells about the structural kind of document source: Opus or Novella.
 *
 * @author Laurent Caillette
 */
public enum StructureKind {
  OPUS( "nlb", "opus" ),
  NOVELLA( "nlp", "novella" );

  private final String[] fileExtension ;


  StructureKind( final String... fileExtension ) {
    this.fileExtension = fileExtension ;
  }

  public String[] getFileExtensions() {
    return fileExtension.clone() ;
  }

  public static String[] getAllFileExtensions() {
    return ObjectArrays.concat(
        OPUS.getFileExtensions(),
        NOVELLA.getFileExtensions(),
        String.class
    ) ; 
  }

}
