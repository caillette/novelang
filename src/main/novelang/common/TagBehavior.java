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
package novelang.common;

/**
 * Defines how an AST nodes behaves regarding tags.
 * @author Laurent Caillette
*/
public enum TagBehavior {

  /**
   * Don't traverse nor look for a tag inside (default).
   */
  NON_TRAVERSABLE,


  /**
   * Traverse but don't look for a tag inside. Useful for top-level nodes like 
   * {@link novelang.parser.NodeKind#PART} or {@link novelang.parser.NodeKind#BOOK}. 
   */
  TRAVERSABLE,

  /**
   * May have a tag and subnodes may have a tag, too.
   */
  SCOPE,

  /**
   * May have a tag but subnodes may not.
   */
  TERMINAL;
}