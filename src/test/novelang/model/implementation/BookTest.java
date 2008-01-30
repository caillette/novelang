/*
 * Copyright (C) 2006 Laurent Caillette
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
package novelang.model.implementation;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.ClassUtils;
import novelang.ResourceTools;
import novelang.ScratchDirectoryFixture;
import novelang.model.common.Location;

/**
 * 
 *
 * @author Laurent Caillette
 */
public class BookTest {

  @Test
  public void identifierLookupOk() throws IOException {
    book1.loadStructure() ;
    book1.loadParts() ;
    book1.gatherIdentifiers() ;

    Assert.assertNotNull( book1.getTree( "section0 w01" ) ) ;
    Assert.assertNotNull( book1.getTree( "section0 w01 w02" ) ) ;
    Assert.assertNotNull( book1.getTree( "section1 s11" ) ) ;

  }

// =======
// Fixture
// =======

  private static final String STRUCTURE_4 = TestResource.STRUCTURE_4.path() ;
  private static final String SECTIONS_1 = TestResource.SECTIONS_1.path() ;
  private static final String SPEECHSEQUENCE_1 = TestResource.SPEECHSEQUENCE_1.path() ;
  private File book1Directory;
  private String testName;
  private Book book1;
  private Location location;

  @Before
  public void setUp() throws IOException {
    testName = ClassUtils.getShortClassName( getClass() ) ;
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    book1Directory = scratchDirectoryFixture.getBook4Directory();
    ResourceTools.copyResourceToFile( getClass(), STRUCTURE_4, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), SECTIONS_1, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), SPEECHSEQUENCE_1, book1Directory ) ;
    book1 = new Book( testName, new File( book1Directory, STRUCTURE_4 ) );
  }

}
