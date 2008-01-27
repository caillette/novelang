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

import java.io.IOException;
import java.io.File;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.lang.ClassUtils;
import novelang.ScratchDirectoryFixture;
import novelang.ResourceTools;
import novelang.model.common.Location;

/**
 * @author Laurent Caillette
 */
public class PartTest {
  private static final String STRUCTURE_1 = "/structure-1.sample";
  private static final String SECTIONS_1 = "/sections-1.sample";
  private File book1Directory;
  private String testName;
  private Book book1;
  private Location location;

  @Test
  public void loadPartOk() throws IOException {
    final Part part = book1.createPart( SECTIONS_1, location ) ;
    Assert.assertTrue( part.load() ) ;
    Assert.assertNotNull( part.getTree() ) ;
    Assert.assertFalse( part.getProblems().iterator().hasNext() ) ;
  }

  @Test
  public void findIdentifiersOk() throws IOException {
    final Part part = book1.createPart( SECTIONS_1, location ) ;
    part.load() ;
    part.spotIdentifiers() ;
  }

  @Before
  public void setUp() throws IOException {
    testName = ClassUtils.getShortClassName( getClass() );
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    book1Directory = scratchDirectoryFixture.getBook1Directory();
    ResourceTools.copyResourceToFile( getClass(), STRUCTURE_1, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), SECTIONS_1, book1Directory ) ;
    book1 = new Book( testName, new File( book1Directory, STRUCTURE_1 ) );
    location = book1.createLocation( 0, 0 );
  }

}
