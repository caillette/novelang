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

/**
 * Defines how an AST nodes behaves regarding tags and identifiers.
 *
 * @author Laurent Caillette
*/
public enum TagBehavior {

  /**
   * Don't traverse nor look for a tag inside (default).
   */
  NON_TRAVERSABLE,


  /**
   * Traverse but don't look for a tag inside. Useful for top-level nodes like 
   * {@link org.novelang.parser.NodeKind#NOVELLA} or {@link org.novelang.parser.NodeKind#OPUS}.
   */
  TRAVERSABLE,

  /**
   * May have a tag and subnodes may have a tag, too.
   */
  SCOPE,

  /**
   * May have a tag but subnodes may not.
   */
  TERMINAL
}
