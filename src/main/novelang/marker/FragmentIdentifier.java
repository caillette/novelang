package novelang.marker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * Identifier for a Part's fragment.
 * 
 * @author Laurent Caillette
 */
public class FragmentIdentifier {
  
  private final List< String > segments ;
  private final String stringRepresentation ;

  /**
   * Constructor.
   * 
   * @param firstSegment Cannot be blank nor null.
   * @param otherSegments No blank strings nor nulls.
   */
  public FragmentIdentifier( final String firstSegment, final String... otherSegments ) {
    this( null, Lists.asList( firstSegment, otherSegments ) ) ; 
  }

  /**
   * Constructor.
   *
   * @param parent a non-null object. 
   * @param firstSegment Cannot be blank nor null.
   * @param otherSegments No blank strings nor nulls.
   */
  public FragmentIdentifier( 
      final FragmentIdentifier parent, 
      final String firstSegment, 
      final String... otherSegments 
  ) {
    this( 
        Preconditions.checkNotNull( parent ),
        Lists.asList( firstSegment, otherSegments ) 
    ) ;
  }

  public FragmentIdentifier(
      final FragmentIdentifier parent, 
      final FragmentIdentifier added
  ) {
    this( parent, added.segments ) ;
  }

  private FragmentIdentifier(
      final FragmentIdentifier parent,
      final Iterable< String > otherSegments
  ) {
    Preconditions.checkArgument( otherSegments.iterator().hasNext() ) ;
    final ImmutableList.Builder< String > listBuilder = ImmutableList.builder() ;
    final StringBuilder stringBuilder ;
    if ( parent == null ) {
      stringBuilder = new StringBuilder() ;
    } else {
      stringBuilder = new StringBuilder( parent.stringRepresentation ) ;
      listBuilder.addAll( parent.segments ) ;
    }
    for( final String segment : otherSegments ) {
      Preconditions.checkArgument( ! StringUtils.isBlank(segment ) ) ;
      stringBuilder.append( "\\" ).append( segment ) ;
      listBuilder.add( segment ) ;
    }
    segments = listBuilder.build() ;
    stringRepresentation = stringBuilder.toString() ;
  }

  public FragmentIdentifier( final Iterable< String > segments ) {
    this( null, segments ) ;    
  }

  /**
   * Returns the segment count. 
   * 
   * @return a value strictly greater than 0.
   */
  public int getSegmentCount() {
    return segments.size() ;
  }

  /**
   * Returns the segment at given index.
   * 
   * @param index a value of 0 or more but strictly smaller than {@link #getSegmentCount()}.
   * @return a non-null, not blank String.
   */
  public String getSegmentAt( final int index ) {
    return segments.get( index ) ;
  }

  

  @Override
  public int hashCode() {
    return stringRepresentation.hashCode() ;
  }

  @Override
  public boolean equals( Object o ) {
    if( o instanceof FragmentIdentifier ) {
      return stringRepresentation.equals( ( ( FragmentIdentifier ) o ).stringRepresentation ) ;  
    } else {
      return false ;
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + stringRepresentation + "]" ;
  }
}
