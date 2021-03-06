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
package org.novelang.common.tree;

/**
 * This is a sample of derived class from {@link ImmutableTree}.
 *
 * @author Laurent Caillette
 */
public class MyTree extends ImmutableTree< MyTree > {

  private final String payload ;

  public MyTree( final String payload, final Iterable< MyTree > children ) {
    super( children ) ;
    this.payload = payload ;
  }

  public MyTree( final String payload, final MyTree... children ) {
    super( children ) ;
    this.payload = payload ;
  }

  public String getPayload() {
    return payload;
  }

  @Override
  public MyTree adopt( final Iterable< MyTree > newChildren ) {
    return new MyTree( payload, newChildren ) ;
  }

  /**
   * Syntactic sugar.
   */
  public static MyTree create( final String payload, final MyTree... children ) {
    return new MyTree( payload, children ) ;
  }

  @Override
  public String toString() {
    return getPayload() ;
  }
}
