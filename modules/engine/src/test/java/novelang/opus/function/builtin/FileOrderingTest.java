package novelang.opus.function.builtin;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Iterator;

import com.google.common.collect.ImmutableList;

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