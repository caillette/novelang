package org.novelang.opus.function.builtin;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.apache.commons.io.FilenameUtils;
import org.novelang.Version;
import org.novelang.VersionFormatException;

/**
 * Provides various file sorting means.
 * 
 * @author Laurent Caillette
 */
public abstract class FileOrdering< T > {


  public Iterable< File > sort( final Iterable< File > files ) throws CriteriaException {
    
    final List< CriterionCreationException > exceptions = Lists.newArrayList() ;
    final List< Wrapper > wrappers = Lists.newArrayList() ;
    final Comparator< T > comparator = createComparator() ;

    for( final File file : files ) {
      try {
        wrappers.add( new Wrapper( file, createCriterion( file ) ) ) ;
      } catch ( CriterionCreationException e ) {
        exceptions.add( e ) ;
      }
    }
    if( exceptions.size() > 0 ) {
      throw new CriteriaException( exceptions ) ;
    }

    final List< Wrapper > wrapperList = 
        Ordering.from( createWrapperComparator( comparator ) ).sortedCopy( wrappers ) ;
    final List< File > sortedFiles = Lists.newArrayList() ;
    for( final Wrapper wrapper : wrapperList ) {
      sortedFiles.add( wrapper.file ) ;
    }
    return ImmutableList.copyOf( sortedFiles ) ;
  }
  
  protected abstract Comparator< T > createComparator() ;

  protected abstract T createCriterion( final File file ) throws CriterionCreationException ;


  private Comparator< Wrapper > createWrapperComparator( final Comparator< T > comparator ) {
    return new Comparator< Wrapper >() {
      @Override
      public int compare( final Wrapper fileWrapper1, final Wrapper fileWrapper2 ) {
        return comparator.compare( fileWrapper1.criterion, fileWrapper2.criterion ) ;
      }
    } ;
  }

  private final class Wrapper {
    public final File file ;
    public final T criterion ;

    public Wrapper( final File file, final T criterion ) {
      this.file = Preconditions.checkNotNull( file ) ;
      this.criterion = Preconditions.checkNotNull( criterion ) ;
    }
  }
  
  public static class CriterionCreationException extends Exception {
    
    private final File file ;
    
    public CriterionCreationException( final File file, final String reason ) {
      super( reason ) ;
      this.file = file ;
    }

    public File getFile() {
      return file ;
    }
  }
  
  public static class CriteriaException extends Exception {
    private final Iterable< CriterionCreationException > exceptions ;

    public CriteriaException( final Iterable< CriterionCreationException > exceptions ) {
      super( "Could not create file sort criteria for: " + createMessageLines( exceptions ) ) ;
      this.exceptions = exceptions ; 
    }

    public Iterable< CriterionCreationException > getExceptions() {
      return exceptions ;
    }
  }
  
  private static String createMessageLines( 
      final Iterable< CriterionCreationException > exceptions 
  ) {
    final StringBuilder stringBuilder = new StringBuilder() ;
    for( final CriterionCreationException exception : exceptions ) {
      stringBuilder.
          append( "\n  " ).append( exception.getMessage() ).append( ": '" ).
          append( exception.getFile().getAbsolutePath() ).append( "'" ) 
      ;
    }
    return stringBuilder.toString() ;
  }

  public FileOrdering< T > inverse() {
    return new FileOrdering< T >() {

      @Override
      protected Comparator< T > createComparator() {
        final Comparator< T > originalComparator = FileOrdering.this.createComparator() ;
        return new Comparator< T >() {
          @Override
          public int compare( final T o1, final T o2 ) {
            return originalComparator.compare( o2, o1 ) ;
          }
        } ;
      }

      @Override
      protected T createCriterion( final File file ) throws CriterionCreationException {
        return FileOrdering.this.createCriterion( file ) ;
      }
    } ;
  }




// ================  
// Concrete classes
// ================  
  
  
  /**
   * Performs <a href="http://java.sun.com/docs/books/tutorial/collections/interfaces/order.html" >lexicographic</a>
   * sort on absolute path.
   */
  public static class ByAbsolutePath extends FileOrdering< String > {
    
    @Override
    protected Comparator< String > createComparator() {
      return new Comparator< String >() {
        @Override
        public int compare( final String s1, final String s2 ) {
          return s1.compareTo( s2 ) ; // Nulls not handled but should be OK.
        }
      } ;
    }

    @Override
    protected String createCriterion( final File file ) {
      return file.getAbsolutePath() ;
    }
  }
  
  public static final FileOrdering BY_ABSOLUTE_PATH = new ByAbsolutePath() ; 
  
  public static class ByVersionNumber extends FileOrdering< Version > {
    
    @Override
    protected Comparator< Version > createComparator() {
      return Version.COMPARATOR ;
    }

    @Override
    protected Version createCriterion( final File file ) throws CriterionCreationException {
      final String fileRadix = FilenameUtils.getBaseName( file.getName() ) ;
      try {
        return Version.parse( fileRadix ) ;
      } catch ( VersionFormatException e ) {
        throw new CriterionCreationException( file, e.getMessage() ) ;
      }
    }
  }
  
  public static final FileOrdering BY_VERSION_NUMBER = new ByVersionNumber() ; 
  
  public static final FileOrdering DEFAULT = BY_ABSOLUTE_PATH ;
  
}
