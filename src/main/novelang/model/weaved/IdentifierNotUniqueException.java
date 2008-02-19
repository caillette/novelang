/*
 * Copyright (C) 2008 Laurent Caillette
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
package novelang.model.weaved;

import java.util.Collection;

import com.google.common.base.Objects;
import novelang.model.common.Tree;

/**
 * @author Laurent Caillette
 */
public class IdentifierNotUniqueException extends Exception {

  private final String identifier ;
  private final Collection< Tree > trees ;

  public IdentifierNotUniqueException( String identifier, Collection< Tree > trees ) {
    super( buildMessage( identifier, trees ) ) ;
    this.trees = trees ;
    this.identifier = identifier ;
  }

  private static final String buildMessage( String identifier, Collection< Tree > trees ) {
    identifier = Objects.nonNull( identifier ) ;
    trees = Objects.nonNull( trees ) ;
    final StringBuffer buffer = new StringBuffer() ;
    buffer.append( "More than one usage of identifier '" ).append( identifier ).append( "':" ) ;
    for( final Tree tree : trees ) {
      buffer.append( "\n    ").append( tree.getLocation() ) ;
    }
    return buffer.toString() ;
  }
}
