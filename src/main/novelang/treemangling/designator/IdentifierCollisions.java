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
package novelang.treemangling.designator;

import novelang.common.SyntacticTree;

/**
 * For returning several values from the same function call.
 *
 * @author Laurent Caillette
 */
public interface IdentifierCollisions {

  /**
   * @param tree has a {@link novelang.common.SyntacticTree#getNodeKind() kind} of ...IDENTIFIER.
   */
  boolean implicitIdentifierCollides( SyntacticTree tree ) ;

}
