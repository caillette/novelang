/*
 * Copyright (C) 2010 Laurent Caillette
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

package org.novelang.parser.antlr;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeAdaptor;

import org.novelang.common.Location;
import org.novelang.common.LocationFactory;

/**
 * See <a href="http://www.antlr.org/api/Java" >online doco</a>.
 * 
 * @author Laurent Caillette
*/
/*package*/ class CustomTreeAdaptor extends CommonTreeAdaptor {

  private final LocationFactory locationFactory ;

  public CustomTreeAdaptor( final LocationFactory locationFactory ) {
    this.locationFactory = locationFactory;
  }

  @Override
  public Object create( final Token payload ) {
    final Location location ;
    if( payload == null ) {
      location = locationFactory.createLocation() ;
    } else {
      location = locationFactory.createLocation( payload.getLine(), payload.getCharPositionInLine() );

    }
    
    return new CustomTree( payload, location ) ;
  }

  /**
   * Parent's doco says:
   * "If oldRoot is a nil root, just copy or move the children to newRoot. 
   * If not a nil root, make oldRoot a child of newRoot."
   */
  @Override
  public Object becomeRoot( final Object newRoot, final Object oldRoot ) {
    final CustomTree result = ( CustomTree ) super.becomeRoot( newRoot, oldRoot ) ;
    result.setLocation( ( ( CustomTree ) oldRoot ).getLocation() ) ;
    return result ;
  }

  
}
