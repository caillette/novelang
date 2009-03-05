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
package novelang.common.filefixture.test;

import java.io.File;

import static novelang.common.filefixture.FileFixture.resource;
import static novelang.common.filefixture.FileFixture.directory;
import static novelang.common.filefixture.FileFixture.register;
import novelang.common.filefixture.Directory;
import novelang.common.filefixture.Resource;

/**
 * This class is used for testing {@link novelang.common.filefixture.FileFixture}.
 * It doesn't register itself as we want let the tests chose the target directory.
 *
 * @author Laurent Caillette
 */
public final class ResourceTree {

  interface D0 {
    Directory dir = directory( "d0" ) ;
    Resource R0_0 = resource( "r0.0.txt" ) ;
    Resource R0_1 = resource( "r0.1.txt" ) ;

    interface D0_0 {
      Directory dir = directory( "d0.0" ) ;
      Resource R0_0_0 = resource( "r0.0.0.txt" ) ;
    }

    interface D0_1 {
      Directory dir = directory( "d0.1" ) ;
      Resource R0_1_0 = resource( "r0.1.0.txt" ) ;
    }

  }

  interface D1 {
    Directory dir = directory( "d1" ) ;

    interface D1_0 {
      Directory dir = directory( "d1.0" ) ;
      Resource R1_0_0 = resource( "r1.0.0.txt" ) ;
    }
  }

}
