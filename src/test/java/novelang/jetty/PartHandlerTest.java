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
package novelang.jetty;

import org.junit.Test;
import org.junit.Assert;

/**
 * @author Laurent Caillette
 */


public class PartHandlerTest {

  @Test
  public void interpretTarget() {
    Assert.assertEquals( "/part.ext", PartHandler.extractPath( "/parts/part.ext" ) ) ;
    Assert.assertEquals( "/part-_.ext", PartHandler.extractPath( "/parts/part-_.ext" ) ) ;
    Assert.assertEquals( "/stuff/part.ext", PartHandler.extractPath( "/parts/stuff/part.ext" ) ) ;
    Assert.assertEquals( "/dir/subdir/part.ext", PartHandler.extractPath( "/parts/dir/subdir/part.ext" ) ) ;

    Assert.assertNull( PartHandler.extractPath( "/PARTS/part.ext" ) ) ;
    Assert.assertNull( PartHandler.extractPath( "/parts/../part.ext" ) ) ;
    Assert.assertNull( PartHandler.extractPath( "/parts//part.ext" ) ) ;
    Assert.assertNull( PartHandler.extractPath( "/parts/part." ) ) ;

  }

}
