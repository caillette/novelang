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
package org.novelang.novelist;

/**
 * Represents something that can finally become a {@code String} in the generated
 * document. Having concrete classes for this generated elements (instead of bare {@code String}s)
 * makes generation easier, allowing to apply {@code instanceof} in some corner-cases.
 * 
 * @author Laurent Caillette
 */
public interface TextElement {

  String getLiteral() ;

  TextElement EMPTY = new TextElement() {
    @Override
    public String getLiteral() {
      return "" ;
    }
  } ;

  TextElement NULL = new TextElement() {
    @Override
    public String getLiteral() {
      return "<NULL text element>" ;
    }
  } ;
}
