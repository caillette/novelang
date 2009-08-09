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
package novelang.book.function;

import novelang.common.Location;
import novelang.common.SyntacticTree;

/**
 * Factory for {@link AbstractFunctionCall} objects.
 * 
 * @author Laurent Caillette
 */
public interface FunctionDefinition {

  String getName() ;

  AbstractFunctionCall instantiate(
      Location location,
      SyntacticTree functionCall
  ) throws CommandParameterException;

}
