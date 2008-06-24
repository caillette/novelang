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

package novelang.model.weaved;

import java.util.Collection;

import com.google.common.base.Objects;
import novelang.model.common.SyntacticTree;

/**
 * @author Laurent Caillette
 */
public class IdentifierNotUniqueException extends Exception {

  private final String identifier ;
  private final Collection< SyntacticTree > trees ;

  public IdentifierNotUniqueException( String identifier, Collection< SyntacticTree > trees ) {
    super( buildMessage( identifier, trees ) ) ;
    this.trees = trees ;
    this.identifier = identifier ;
  }

  private static final String buildMessage( String identifier, Collection< SyntacticTree > trees ) {
    identifier = Objects.nonNull( identifier ) ;
    trees = Objects.nonNull( trees ) ;
    final StringBuffer buffer = new StringBuffer() ;
    buffer.append( "More than one usage of identifier '" ).append( identifier ).append( "':" ) ;
    for( final SyntacticTree tree : trees ) {
      buffer.append( "\n    ").append( tree.getLocation() ) ;
    }
    return buffer.toString() ;
  }
}
