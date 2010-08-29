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
package org.novelang.nhovestone;

import java.util.List;

import com.google.common.collect.Lists;
import org.novelang.Version;
import org.novelang.VersionFormatException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Laurent Caillette
 */
public class NhovestoneToolsTest {

  @Test
  public void parseOneSnapshot() throws VersionFormatException {
    verify( "SNAPSHOT", "SNAPSHOT" ) ;
  }

  @Test
  public void parseOneNonSnapshot() throws VersionFormatException {
    verify( "0.43.0", "0.43.0" ) ;
  }

  @Test
  public void parseTwoVersions() throws VersionFormatException {
    verify( "SNAPSHOT,0.43.0", "SNAPSHOT", "0.43.0" ) ;
  }


// =======
// Fixture
// =======

  private static void verify(
      final String commaSeparatedVersions,
      final String... expectedVersions
  ) throws VersionFormatException
  {
   final Iterable< Version > parsedVersions = NhovestoneTools.parseVersions( commaSeparatedVersions ) ;
    final List< Version > actualVersions = Lists.newArrayList( parsedVersions ) ;
    assertEquals( ( long ) expectedVersions.length, ( long ) actualVersions.size() ) ;
    for( int i = 0 ; i < expectedVersions.length ; i ++ ) {
      assertEquals( expectedVersions[ i ], actualVersions.get( i ).getName() ) ;
    }
  }


}
