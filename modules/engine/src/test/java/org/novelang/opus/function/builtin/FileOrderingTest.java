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

package org.novelang.opus.function.builtin;

import java.io.File;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for {@link FileOrdering}.
 * 
 * @author Laurent Caillette
 */
public class FileOrderingTest {
  
  @Test
  public void filenameOrdering() throws FileOrdering.CriteriaException {
    
    final File fileX = new File( "a/a/x.novella" ) ;
    final File fileY = new File( "a/a/y.novella" ) ;
    final File fileZ = new File( "a/b/z.novella" ) ;
    
    final Iterable< File > unsortedFiles = ImmutableList.of( fileY, fileZ, fileX ) ;
    final Iterable< File > sortedFiles = new FileOrdering.ByAbsolutePath().sort( unsortedFiles ) ;
    
    final Iterator< File > iterator = sortedFiles.iterator() ;
    assertEquals( "" + sortedFiles, fileX, iterator.next() ) ;
    assertEquals( "" + sortedFiles, fileY, iterator.next() ) ;
    assertEquals( "" + sortedFiles, fileZ, iterator.next() ) ;
    assertFalse( "" + sortedFiles, iterator.hasNext() ) ;
  }
  
  @Test
  public void filenameOrderingWithDoubleInvert() throws FileOrdering.CriteriaException {
    
    final File fileX = new File( "a/a/x.novella" ) ;
    final File fileY = new File( "a/a/y.novella" ) ;
    final File fileZ = new File( "a/b/z.novella" ) ;
    
    final Iterable< File > unsortedFiles = ImmutableList.of( fileY, fileZ, fileX ) ;
    final Iterable< File > sortedFiles = 
        new FileOrdering.ByAbsolutePath().inverse().inverse().sort( unsortedFiles ) ;
    
    final Iterator< File > iterator = sortedFiles.iterator() ;
    assertEquals( "" + sortedFiles, fileX, iterator.next() ) ;
    assertEquals( "" + sortedFiles, fileY, iterator.next() ) ;
    assertEquals( "" + sortedFiles, fileZ, iterator.next() ) ;
    assertFalse( "" + sortedFiles, iterator.hasNext() ) ;
  }
  
  @Test
  public void invertedFilenameOrdering() throws FileOrdering.CriteriaException {
    
    final File fileX = new File( "a/a/x.novella" ) ;
    final File fileY = new File( "a/a/y.novella" ) ;
    final File fileZ = new File( "a/b/z.novella" ) ;
    
    final Iterable< File > unsortedFiles = ImmutableList.of( fileY, fileZ, fileX ) ;
    final Iterable< File > sortedFiles = 
        new FileOrdering.ByAbsolutePath().inverse().sort( unsortedFiles );
    
    final Iterator< File > iterator = sortedFiles.iterator() ;
    assertEquals( "" + sortedFiles, fileZ, iterator.next() ) ;
    assertEquals( "" + sortedFiles, fileY, iterator.next() ) ;
    assertEquals( "" + sortedFiles, fileX, iterator.next() ) ;
    assertFalse( "" + sortedFiles, iterator.hasNext() ) ;
  }
  
  @Test
  public void versionOrdering() throws FileOrdering.CriteriaException {
    final File file_snapshot = new File( "a/SNAPSHOT.novella" ) ;
    final File file_1_1_2 =    new File( "b/1.1.2.novella" ) ;
    final File file_1_0_0 =    new File( "b/1.0.0.novella" ) ;
    final File file_0_1_2 =    new File( "a/0.2.1.novella" ) ;
    
    final Iterable< File > unsortedFiles = ImmutableList.of( 
        file_1_0_0, file_0_1_2, file_snapshot, file_1_1_2 ) ;
    final Iterable< File > sortedFiles = new FileOrdering.ByVersionNumber().sort( unsortedFiles ) ;
    
    final Iterator< File > iterator = sortedFiles.iterator() ;

    assertEquals( "" + sortedFiles, file_0_1_2, iterator.next() ) ;
    assertEquals( "" + sortedFiles, file_1_0_0, iterator.next() ) ;
    assertEquals( "" + sortedFiles, file_1_1_2, iterator.next() ) ;
    assertEquals( "" + sortedFiles, file_snapshot, iterator.next() ) ;
    assertFalse( "" + sortedFiles, iterator.hasNext() ) ;
  }
  
  @Test
  public void invertedVersionOrdering() throws FileOrdering.CriteriaException {
    final File file_snapshot = new File( "a/SNAPSHOT.novella" ) ;
    final File file_1_1_2 =    new File( "b/1.1.2.novella" ) ;
    final File file_1_0_0 =    new File( "b/1.0.0.novella" ) ;
    final File file_0_1_2 =    new File( "a/0.2.1.novella" ) ;
    
    final Iterable< File > unsortedFiles = ImmutableList.of( 
        file_1_0_0, file_0_1_2, file_snapshot, file_1_1_2 ) ;
    final Iterable< File > sortedFiles = 
        new FileOrdering.ByVersionNumber().inverse().sort( unsortedFiles ) ;
    
    final Iterator< File > iterator = sortedFiles.iterator() ;

    assertEquals( "" + sortedFiles, file_snapshot, iterator.next() ) ;
    assertEquals( "" + sortedFiles, file_1_1_2, iterator.next() ) ;
    assertEquals( "" + sortedFiles, file_1_0_0, iterator.next() ) ;
    assertEquals( "" + sortedFiles, file_0_1_2, iterator.next() ) ;
    assertFalse( "" + sortedFiles, iterator.hasNext() ) ;
  }
  
  @Test
  public void badVersionFormat() {
    final File bad1 = new File( "bad1.novella" ) ;
    final File bad2 = new File( "bad2.novella" ) ;
    
    final Iterable< File > unsortedFiles = ImmutableList.of( bad1, bad2 ) ;
    try {
      new FileOrdering.ByVersionNumber().sort( unsortedFiles ) ;
      Assert.fail( "Exception not caught" ) ;
    } catch ( FileOrdering.CriteriaException e ) {
      final Iterator< FileOrdering.CriterionCreationException > exceptions = 
          e.getExceptions().iterator() ;
      assertEquals( bad1, exceptions.next().getFile() ) ;
      assertEquals( bad2, exceptions.next().getFile() ) ;
      assertFalse( exceptions.hasNext() ) ;
    }
    
  }
}
