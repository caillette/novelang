/*
 * Copyright (C) 2006 Laurent Caillette
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

/**
 * @author Laurent Caillette
 */
public enum TestResource {

  BLOCKQUOTE_1( "/blockquote-1.sample"),
  PARAGRAPHBODY_1( "/paragraphbody-1.sample"),
  PARAGRAPHBODY_2( "/paragraphbody-2.sample"),
  PARENTHESIS_1( "/parenthesis-1.sample"),
  QUOTES_1( "/quotes-1.sample"),
  SECTIONS_1( "/sections-1.sample"),
  SECTIONS_2( "/sections-2.sample"),
  SECTIONS_3( "/sections-3.sample"),
  SPEECHSEQUENCE_1( "/speechsequence-1.sample"),
  SPEECHSEQUENCE_2( "/speechsequence-2.sample"),

  STRUCTURE_1( "/structure-1.sample"),
  STRUCTURE_2( "/structure-2.sample"),
  STRUCTURE_3( "/structure-3.sample"),
  STRUCTURE_4( "/structure-4.sample"),
  ;

  private final String path ;

  TestResource( String path ) {
    this.path = path ;
  }


  public String path() {
    return path;
  }
}
