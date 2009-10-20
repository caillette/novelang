package novelang.marker;

import novelang.marker.FragmentIdentifier;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;
import com.google.common.collect.Lists;

/**
 * Tests for {@link FragmentIdentifier}.
 * 
 * @author Laurent Caillette
 */
public class FragmentIdentifierTest {
  
  @Test( expected = NullPointerException.class )
  public void noNullParent() {
    new FragmentIdentifier( ( FragmentIdentifier ) null, "ignored" ) ;
  }

  
  @Test( expected = NullPointerException.class )
  public void firstSegmentCannotBeNull() {
    new FragmentIdentifier( null ) ;
  }

  
  @Test( expected = IllegalArgumentException.class )
  public void firstSegmentCannotBeEmptyString() {
    new FragmentIdentifier( "" ) ;
  }

  
  @Test( expected = IllegalArgumentException.class )
  public void secondSegmentCannotBeNull() {
    new FragmentIdentifier( "0", new String[] { null } ) ;
  }

  
  @Test( expected = IllegalArgumentException.class )
  public void secondSegmentCannotBeEmpty() {
    new FragmentIdentifier( "0", "" ) ;
  }

  
  @Test( expected = IllegalArgumentException.class )
  public void listCannotBeEmpty() {
    new FragmentIdentifier( Lists.< String >newArrayList() ) ;
  }

  
  @Test( expected = IllegalArgumentException.class )
  public void listMayNotContainNull() {
    new FragmentIdentifier( Lists.< String >newArrayList( "y", null ) ) ;
  }

  
  @Test( expected = IllegalArgumentException.class )
  public void listMayNotContainEmptyString() {
    new FragmentIdentifier( Lists.< String >newArrayList( "y", "" ) ) ;
  }

  
  @Test
  public void fromOneString() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( "0" ) ;
    assertEquals( 1, fragmentIdentifier.getSegmentCount() ) ;
    assertEquals( "0", fragmentIdentifier.getSegmentAt( 0 ) ) ;
    assertEquals( "FragmentIdentifier[\\0]", fragmentIdentifier.toString() ) ;
    assertEquals( "\\\\0", fragmentIdentifier.getAbsoluteRepresentation() ) ;
    assertEquals( new FragmentIdentifier( "0" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromTwoStrings() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( "0", "1" ) ;
    assertEquals( 2, fragmentIdentifier.getSegmentCount() ) ;
    assertEquals( "0", fragmentIdentifier.getSegmentAt( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.getSegmentAt( 1 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1]", fragmentIdentifier.toString() ) ;
    assertEquals( "\\\\0\\1", fragmentIdentifier.getAbsoluteRepresentation() ) ;
    assertEquals( new FragmentIdentifier( "0", "1" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromThreeStrings() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( "0", "1", "2" ) ;
    assertEquals( 3, fragmentIdentifier.getSegmentCount() ) ;
    assertEquals( "0", fragmentIdentifier.getSegmentAt( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.getSegmentAt( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.getSegmentAt( 2 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1\\2]", fragmentIdentifier.toString() ) ;
    assertEquals( "\\\\0\\1\\2", fragmentIdentifier.getAbsoluteRepresentation() ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromTwoIdentifiers() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier(
        new FragmentIdentifier( "0", "1" ),
        new FragmentIdentifier( "2", "3" ) 
    ) ;
    assertEquals( 4, fragmentIdentifier.getSegmentCount() ) ;
    assertEquals( "0", fragmentIdentifier.getSegmentAt( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.getSegmentAt( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.getSegmentAt( 2 ) ) ;
    assertEquals( "3", fragmentIdentifier.getSegmentAt( 3 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1\\2\\3]", fragmentIdentifier.toString() ) ;
    assertEquals( "\\\\0\\1\\2\\3", fragmentIdentifier.getAbsoluteRepresentation() ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2", "3" ), fragmentIdentifier ) ;
  }


  @Test
  public void fromParentPlusOneString() {
    final FragmentIdentifier parent = new FragmentIdentifier( "0", "1" ) ;
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( parent, "2" ) ;
    assertEquals( 3, fragmentIdentifier.getSegmentCount() ) ;
    assertEquals( "0", fragmentIdentifier.getSegmentAt( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.getSegmentAt( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.getSegmentAt( 2 ) ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromParentPlusTwoStrings() {
    final FragmentIdentifier parent = new FragmentIdentifier( "0", "1" ) ;
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( parent, "2", "3" ) ;
    assertEquals( 4, fragmentIdentifier.getSegmentCount() ) ;
    assertEquals( "0", fragmentIdentifier.getSegmentAt( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.getSegmentAt( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.getSegmentAt( 2 ) ) ;
    assertEquals( "3", fragmentIdentifier.getSegmentAt( 3 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1\\2\\3]", fragmentIdentifier.toString() ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2", "3" ), fragmentIdentifier ) ;
  }
  
  @Test
  public void isParent() {
    final FragmentIdentifier fragment0 = new FragmentIdentifier( "0" ) ;
    final FragmentIdentifier fragment01 = new FragmentIdentifier( "0", "1" ) ;
    final FragmentIdentifier fragment012 = new FragmentIdentifier( "0", "1", "2" ) ;
    
    assertTrue( fragment0.isParentOf( fragment0 ) ) ;
    assertTrue( fragment0.isParentOf( fragment01 ) ) ;
    assertTrue( fragment0.isParentOf( fragment012 ) ) ;
    
    assertFalse( fragment01.isParentOf( fragment0 ) ) ;
    assertTrue( fragment01.isParentOf( fragment01 ) ) ;
    assertTrue( fragment01.isParentOf( fragment012 ) ) ;
    
    assertFalse( fragment012.isParentOf( fragment0 ) ) ;
    assertFalse( fragment012.isParentOf( fragment01 ) ) ;
    assertTrue( fragment012.isParentOf( fragment012 ) ) ;
    
  }
  
  
  @Test
  public void getParent() {
    final FragmentIdentifier fragment0 = new FragmentIdentifier( "0" ) ;
    final FragmentIdentifier fragment01 = new FragmentIdentifier( "0", "1" ) ;
    final FragmentIdentifier fragment012 = new FragmentIdentifier( "0", "1", "2" ) ;
    
    assertNull( fragment0.getParent() ) ;
    assertEquals( fragment0, fragment01.getParent() ) ;
    assertEquals( fragment01, fragment012.getParent() ) ;    
  }
  
  
  
}
