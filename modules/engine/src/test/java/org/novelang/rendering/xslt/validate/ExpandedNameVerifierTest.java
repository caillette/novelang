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
package org.novelang.rendering.xslt.validate;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.novelang.common.Location;

/**
 * Tests for {@link ExpandedNameVerifier}.
 *
 * @author Laurent Caillette
 */
public class ExpandedNameVerifierTest {

  @Test
  public void testVerifyOk() {
    final ExpandedNameVerifier verifier =
        new ExpandedNameVerifier( ImmutableSet.of( "foo", "bar" ) ) ;
    verifier.setXmlPrefix( "n" ) ;
    verifier.verify( SOME_LOCATION, "n:foo/m:bar" ) ;
    final Iterable<BadExpandedName> badExpandedNames =
        verifier.getBadExpandedNames() ;
    Assert.assertFalse( badExpandedNames.iterator().hasNext() ) ;
  }

  @Test
  public void testVerifyBad() {
    final ExpandedNameVerifier verifier =
        new ExpandedNameVerifier( ImmutableSet.of( "foo", "bar" ) ) ;
    verifier.setXmlPrefix( "n" ) ;
    verifier.verify( SOME_LOCATION, "n:foo/n:baz" ) ;
    final List<BadExpandedName> badExpandedNames =
        Lists.newArrayList( verifier.getBadExpandedNames() ) ;
    Assert.assertEquals( 1, badExpandedNames.size() ) ;
  }


// =======
// Fixture
// =======

  private static final Location SOME_LOCATION = new Location( "test", 11, 22 );

}
