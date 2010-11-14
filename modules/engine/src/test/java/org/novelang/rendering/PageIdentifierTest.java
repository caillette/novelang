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
package org.novelang.rendering;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * A page name for a multipage document.
 * The name is filesystem and URL friendly.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "ResultOfObjectAllocationIgnored" } )
public class PageIdentifierTest {

  @Test( expected = NullPointerException.class )
  public void nullName() {
    new PageIdentifier( null ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void incorrect0() {
    new PageIdentifier( "" ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void incorrect1() {
    new PageIdentifier( "$" ) ;
  }

  @Test( expected = IllegalArgumentException.class )
  public void incorrect2() {
    new PageIdentifier( "Foo_" ) ;
  }

  @Test
  public void correct() {
    assertThat( new PageIdentifier( "foo-Bar_123" ).getName() ).isEqualTo( "foo-Bar_123" ) ;
  }

  @Test
  public void equalsAndHashCode() {
    assertThat( new PageIdentifier( "P" ) ).isEqualTo( new PageIdentifier( "P" ) ) ;
    assertThat( new PageIdentifier( "P" ).hashCode() )
        .isEqualTo( new PageIdentifier( "P" ).hashCode() ) ;

    assertThat( new PageIdentifier( "P" ) ).isNotEqualTo( new PageIdentifier( "No" ) ) ;
  }

}
