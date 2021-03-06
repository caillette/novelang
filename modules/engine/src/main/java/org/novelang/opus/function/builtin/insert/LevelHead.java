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
package org.novelang.opus.function.builtin.insert;

/**
 * @author Laurent Caillette
 */
public enum LevelHead {

  /**
   * Enclose added Novella into a Level with Novella's file name.
   */
  CREATE_LEVEL,

  /**
   * When inserting from an Identifier and inserted fragment's root is a
   * {@link org.novelang.parser.NodeKind#_LEVEL}, then add the children instead of the root itself.
   */
  NO_HEAD

}
