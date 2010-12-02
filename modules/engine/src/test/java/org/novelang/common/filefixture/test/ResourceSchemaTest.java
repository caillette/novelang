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
package org.novelang.common.filefixture.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.novelang.testing.junit.NameAwareTestClassRunner;
import static org.apache.commons.lang.SystemUtils.FILE_SEPARATOR;
import org.novelang.testing.DirectoryFixture;
import org.novelang.common.filefixture.Directory;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceSchema;

/**
 * Tests for {@link org.novelang.common.filefixture.ResourceSchema}.
 *
 * @author Laurent Caillette
 */
@RunWith( value = NameAwareTestClassRunner.class )
public class ResourceSchemaTest {

  @Test
  public void objectCreation() {
    final Directory tree = ResourceTree.dir ;

    final List< Resource > treeResources = tree.getResources() ;
    assertTrue( tree.isInitialized() ) ;
    assertNull( tree.getParent() ) ;
    assertEquals( 0, treeResources.size() ) ;
    final List< Directory > treeDirectories = tree.getSubdirectories() ;
    assertEquals( 2, treeDirectories.size() ) ;

    final Directory d0 = tree.getSubdirectories().get( 0 ) ;
    assertTrue( d0.isInitialized() ) ;
    assertSame( tree, d0.getParent() ) ;
    assertEquals( "d0", d0.getName() ) ;
    final List< Resource > d0Resources = d0.getResources() ;
    assertEquals( 2, d0Resources.size() ) ;

    final Resource r0_0 = d0Resources.get( 0 );
    assertTrue( r0_0.isInitialized() ) ;
    assertSame( d0, r0_0.getParent() ) ;
    assertEquals( "r0.0.txt", r0_0.getName() ) ;

    final Resource r0_1 = d0Resources.get( 1 );
    assertTrue( r0_0.isInitialized() ) ;
    assertSame( d0, r0_1.getParent() ) ;
    assertEquals( "r0.1.txt", r0_1.getName() ) ;
    final List< Directory > d0Directories = d0.getSubdirectories() ;
    assertEquals( 2, d0Directories.size() ) ;

  }

  @Test
  public void parenthood() {
    assertTrue( ResourceSchema.isParentOf( ResourceTree.dir, ResourceTree.D0.dir ) ) ;
    assertTrue( ResourceSchema.isParentOf( ResourceTree.dir, ResourceTree.D0.D0_1.dir ) ) ;
    assertTrue( ResourceSchema.isParentOfOrSameAs( ResourceTree.dir, ResourceTree.dir ) ) ;
    assertTrue( ResourceSchema.isParentOfOrSameAs( ResourceTree.dir, ResourceTree.D0.dir ) ) ;
    assertFalse( ResourceSchema.isParentOfOrSameAs( ResourceTree.D0.dir, ResourceTree.dir ) ) ;
    assertFalse( ResourceSchema.isParentOfOrSameAs( ResourceTree.D0.dir, ResourceTree.D1.dir ) ) ;
    assertTrue( ResourceSchema.isParentOfOrSameAs( ResourceTree.dir, ResourceTree.D0.R0_0 ) ) ;
  }

  @Test
  public void copyContentOk() throws IOException {
    new ResourceInstaller( testDirectory ).copyContent( ResourceTree.dir ) ;
    final File treeFile = testDirectory ;
    verifyContent( treeFile ) ;
  }


  @Test
  public void copySingleResourceOk() throws IOException {
    final File resourceFile = new ResourceInstaller( testDirectory ).copy( ResourceTree.D0.R0_0 ) ;
    assertTrue( resourceFile.exists() ) ;
    assertFalse( resourceFile.isDirectory() ) ;
    assertEquals( resourceFile.getName(), ResourceTree.D0.R0_0.getName() ) ;
    assertEquals( testDirectory, resourceFile.getParentFile() ) ;
  }


  @Test
  public void copyOk() throws IOException {
    final File createdDirectory = new ResourceInstaller( testDirectory ).copy( ResourceTree.dir ) ;
    final File treeFile = new File( testDirectory, ResourceTree.dir.getName() ) ;
    verifyContent( treeFile ) ;
    assertEquals( ResourceTree.dir.getName(), createdDirectory.getName() ) ;
    assertEquals( testDirectory, createdDirectory.getParentFile() ) ;
  }


  @Test
  public void copyScopedDirectory() throws IOException {
    final File scoped = new ResourceInstaller( testDirectory ).
        copyScoped( ResourceTree.D0.dir, ResourceTree.D0.D0_1.D0_1_0.dir ) ;
    assertTrue( scoped.exists() ) ;
    assertEquals(
        testDirectory.getAbsolutePath() +
            FILE_SEPARATOR + "d0.1" + FILE_SEPARATOR + "d0.1.0",
        scoped.getAbsolutePath()
    ) ;

    verifyScopedCopyResult();
  }


  @Test
  public void copyScopedResource() throws IOException {
    final File scoped = new ResourceInstaller( testDirectory ).
        copyScoped( ResourceTree.D0.dir, ResourceTree.D0.D0_1.D0_1_0.R0_1_0_0 ) ;
    assertTrue( scoped.exists() ) ;
    assertEquals( 
        testDirectory.getAbsolutePath() +
            FILE_SEPARATOR + "d0.1" + FILE_SEPARATOR +  "d0.1.0" +
            FILE_SEPARATOR + "r0.1.0.0.txt",
        scoped.getAbsolutePath() 
    ) ;

    verifyScopedCopyResult();
  }

  @Test
  public void copyResourceWithPath() throws IOException {
    final File scoped = new ResourceInstaller( testDirectory ).
        copyWithPath( ResourceTree.D0.dir, ResourceTree.D0.D0_1.D0_1_0.R0_1_0_0 ) ;
    assertTrue( scoped.exists() ) ;
    assertEquals(
        testDirectory.getAbsolutePath() +
            FILE_SEPARATOR + "d0" +
            FILE_SEPARATOR + "d0.1" + FILE_SEPARATOR +  "d0.1.0" +
            FILE_SEPARATOR + "r0.1.0.0.txt",
        scoped.getAbsolutePath()
    ) ;

    verifyCopyWithPathResult() ;
  }

  @Test
  public void createFileObjectNoScope() {
    final ResourceInstaller resourceInstaller = new ResourceInstaller( testDirectory ) ;
    resourceInstaller.copyWithPath( ResourceTree.D0.D0_0.R0_0_0 ) ;
    final File file = resourceInstaller.createFileObject( ResourceTree.D0.D0_0.R0_0_0 ) ;
    assertEquals(
        testDirectory.getAbsolutePath() +
            FILE_SEPARATOR + "tree" + FILE_SEPARATOR + "d0" +
            FILE_SEPARATOR + "d0.0" + FILE_SEPARATOR + "r0.0.0.txt",
        file.getAbsolutePath() 
    ) ;
  }


  @Test( expected = AssertionError.class )
  public void createFileObjectOnNonExistingResourceFails() {
    final ResourceInstaller resourceInstaller = new ResourceInstaller( testDirectory ) ;
    resourceInstaller.createFileObject( ResourceTree.D0.D0_0.R0_0_0 ) ;
  }


  @Test
  public void createFileObjectInScopeWithResource() {
    final ResourceInstaller resourceInstaller = new ResourceInstaller( testDirectory ) ;
    final File file = resourceInstaller.createFileObject(
        ResourceTree.D0.dir, 
        ResourceTree.D0.D0_1.D0_1_0.R0_1_0_0 
    ) ;
    assertEquals( 
        testDirectory.getAbsolutePath() +
            FILE_SEPARATOR + "d0.1" + FILE_SEPARATOR + "d0.1.0" +
            FILE_SEPARATOR + "r0.1.0.0.txt",
        file.getAbsolutePath() 
    ) ;
  }


  @Test
  public void createFileObjectInScopeWithDirectory() {
    final ResourceInstaller resourceInstaller = new ResourceInstaller( testDirectory ) ;
    final File file = resourceInstaller.createFileObject(
        ResourceTree.D0.dir, 
        ResourceTree.D0.D0_1.dir 
    ) ;
    assertEquals( 
        testDirectory.getAbsolutePath() + FILE_SEPARATOR + "d0.1",
        file.getAbsolutePath() 
    ) ;
  }
  
  @Test
  public void relativizeResourcePath() {
    assertEquals( 
        "/d0.1/d0.1.0",
        ResourceSchema.relativizer(  ResourceTree.D0.dir ).apply( ResourceTree.D0.D0_1.D0_1_0.dir )
    ) ; 
  }

// =======
// Fixture
// =======

  private File testDirectory ;

  @Before
  public void before() throws IOException {
    final String testName = NameAwareTestClassRunner.getTestName();
    testDirectory = new DirectoryFixture( testName ).getDirectory() ;

    if( ! ResourceTree.dir.isInitialized() ) {
      ResourceSchema.initialize( ResourceTree.class ) ;
    }

  }

  private void verifyContent( final File treeFile ) {
    assertTrue( "treeFile=" + treeFile.getAbsolutePath(), treeFile.exists() ) ;
    assertTrue( treeFile.isDirectory() ) ;

    final File d0File = new File( treeFile, "d0" ) ;
    assertTrue( d0File.isDirectory() ) ;
    assertTrue( d0File.exists() ) ;

    final File r0_0File = new File( d0File, "r0.0.txt" ) ;
    assertTrue( r0_0File.isFile() ) ;
    assertTrue( r0_0File.exists() ) ;
  }

  private void verifyScopedCopyResult() {
    final File d0_1 = new File( testDirectory, "d0.1" ) ;
    final File d0_1_0 = new File( d0_1, "d0.1.0" ) ;
    final File r0_1_0_0 = new File( d0_1_0, "r0.1.0.0.txt" ) ;

    assertTrue( d0_1.exists() ) ;
    assertEquals( 1, d0_1.listFiles().length ) ;
    assertTrue( d0_1_0.exists() ) ;
    assertEquals( 1, d0_1_0.listFiles().length ) ;
    assertTrue( r0_1_0_0.exists() ) ;
  }
  

  private void verifyCopyWithPathResult() {
    final File d0 = new File( testDirectory, "d0" ) ;
    final File d0_1 = new File( d0, "d0.1" ) ;
    final File d0_1_0 = new File( d0_1, "d0.1.0" ) ;
    final File r0_1_0_0 = new File( d0_1_0, "r0.1.0.0.txt" ) ;

    assertTrue( d0.exists() ) ;
    assertEquals( 1, d0.listFiles().length ) ;
    assertTrue( d0_1.exists() ) ;
    assertEquals( 1, d0_1.listFiles().length ) ;
    assertTrue( d0_1_0.exists() ) ;
    assertEquals( 1, d0_1_0.listFiles().length ) ;
    assertTrue( r0_1_0_0.exists() ) ;
  }



}
