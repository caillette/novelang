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
package novelang.model.structural;

import novelang.model.common.LocationFactory;
import novelang.model.common.Location;
import novelang.model.implementation.Part;

/**
 * @author Laurent Caillette
 */
public interface StructuralBook extends LocationFactory {
  
  void collect( Exception ex ) ;

  Iterable< Exception > getProblems() ;

  Part createPart( String partFileName, Location location ) ;

  StructuralChapter createChapter( Location location ) ;


}
