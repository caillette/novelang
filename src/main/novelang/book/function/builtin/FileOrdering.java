package novelang.book.function.builtin;

import novelang.Version;
import novelang.VersionFormatException;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

/**
 * Provides various file sorting means.
 * 
 * @author Laurent Caillette
 */
/*package*/ abstract class FileOrdering< T > {
  
  private final Iterable< Wrapper > wrappers ;
  private final Comparator< T > comparator ;

  public FileOrdering( 
      final Iterable< File > files, 
      final Comparator< T > comparator 
  ) 
      throws CriteriaException
  {
    final List< CriterionCreationException > exceptions = Lists.newArrayList() ;
    final List< Wrapper > wrappers = Lists.newArrayList() ;
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
    this.wrappers = ImmutableList.copyOf( wrappers ) ;
    this.comparator = comparator ;
  }
  
  public abstract T createCriterion( final File file ) throws CriterionCreationException ;

  public final Iterable< File > sort() {
    final List< Wrapper > wrapperList = 
        Ordering.from( createWrapperComparator( comparator ) ).sortedCopy( wrappers ) ;
    final List< File > sortedFiles = Lists.newArrayList() ;
    for( final Wrapper wrapper : wrapperList ) {
      sortedFiles.add( wrapper.file ) ;
    }
    return ImmutableList.copyOf( sortedFiles ) ;
  }
  
  private Comparator< Wrapper > createWrapperComparator( final Comparator< T > comparator ) {
    return new Comparator< Wrapper >() {
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
  
  public static class ByAbsolutePath extends FileOrdering< String > {
    
    public ByAbsolutePath( final Iterable< File > files ) throws CriteriaException {
      super( files, new Comparator< String >() {
        public int compare( final String s1, final String s2 ) {
          return s1.compareTo( s2 ) ; // Nulls not handled but should be OK.
        }
      } ) ;
    }

    public String createCriterion( final File file ) {
      return file.getAbsolutePath() ;
    }
  }
  
  public static class ByVersionNumber extends FileOrdering< Version > {
    
    public ByVersionNumber( final Iterable< File > files ) throws CriteriaException {
      super( files, Version.COMPARATOR ) ;
    }

    public Version createCriterion( final File file ) throws CriterionCreationException {
      final String fileRadix = FilenameUtils.getBaseName( file.getName() ) ;
      try {
        return Version.parse( fileRadix ) ;
      } catch ( VersionFormatException e ) {
        throw new CriterionCreationException( file, e.getMessage() ) ;
      }
    }
  }
  
}
