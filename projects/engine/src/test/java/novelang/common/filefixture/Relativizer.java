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
package novelang.common.filefixture;

import com.google.common.base.Preconditions;

/**
 * Transforms a resource path into an other, given a reference path.
 * 
 * @author Laurent Caillette
 */
public final class Relativizer {
  
  private final Directory root ;

  /*package*/ Relativizer( final Directory root ) {
    Preconditions.checkNotNull( root ) ;
    this.root = root ;
  }
  
  public String apply( final SchemaNode node ) {
    Preconditions.checkNotNull( node ) ;
    final String parentPath = root.getAbsoluteResourceName();
    final String childPath = node.getAbsoluteResourceName();
    Preconditions.checkArgument( 
        childPath.startsWith( parentPath ),
        "Parent path '%s' does not contain '%s'",
        parentPath,
        childPath
    ) ;
    return childPath.substring( parentPath.length() ) ;
  }
}
