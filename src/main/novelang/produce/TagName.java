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
package novelang.produce;

import java.util.regex.Pattern;

/**
 * This is just a placeholder for the regex for a tag name.
 * This has to be kept in sync with the grammar manually, shame on me.
 *
 * @author Laurent Caillette
 */
public interface TagName {

  String TAG_NAME_PARAMETER = "tags" ;

  String TAGS_REGEX = "(" +
      TAG_NAME_PARAMETER + "\\=" +
      "(?:[a-zA-Z\\-_]+(?:,[a-zA-Z\\-_]+)*)" +
  ")" ;

}
