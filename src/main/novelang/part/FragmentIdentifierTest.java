package novelang.part;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
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
    assertEquals( 1, fragmentIdentifier.size() ) ;
    assertEquals( "0", fragmentIdentifier.get( 0 ) ) ;
    assertEquals( "FragmentIdentifier[\\0]", fragmentIdentifier.toString() ) ;
    assertEquals( new FragmentIdentifier( "0" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromTwoStrings() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( "0", "1" ) ;
    assertEquals( 2, fragmentIdentifier.size() ) ;
    assertEquals( "0", fragmentIdentifier.get( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.get( 1 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1]", fragmentIdentifier.toString() ) ;
    assertEquals( new FragmentIdentifier( "0", "1" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromThreeStrings() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( "0", "1", "2" ) ;
    assertEquals( 3, fragmentIdentifier.size() ) ;
    assertEquals( "0", fragmentIdentifier.get( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.get( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.get( 2 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1\\2]", fragmentIdentifier.toString() ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromTwoIdentifiers() {
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier(
        new FragmentIdentifier( "0", "1" ),
        new FragmentIdentifier( "2", "3" ) 
    ) ;
    assertEquals( 4, fragmentIdentifier.size() ) ;
    assertEquals( "0", fragmentIdentifier.get( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.get( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.get( 2 ) ) ;
    assertEquals( "3", fragmentIdentifier.get( 3 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1\\2\\3]", fragmentIdentifier.toString() ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2", "3" ), fragmentIdentifier ) ;
  }


  @Test
  public void fromParentPlusOneString() {
    final FragmentIdentifier parent = new FragmentIdentifier( "0", "1" ) ;
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( parent, "2" ) ;
    assertEquals( 3, fragmentIdentifier.size() ) ;
    assertEquals( "0", fragmentIdentifier.get( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.get( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.get( 2 ) ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2" ), fragmentIdentifier ) ;
  }
  
  
  @Test
  public void fromParentPlusTwoStrings() {
    final FragmentIdentifier parent = new FragmentIdentifier( "0", "1" ) ;
    final FragmentIdentifier fragmentIdentifier = new FragmentIdentifier( parent, "2", "3" ) ;
    assertEquals( 4, fragmentIdentifier.size() ) ;
    assertEquals( "0", fragmentIdentifier.get( 0 ) ) ;
    assertEquals( "1", fragmentIdentifier.get( 1 ) ) ;
    assertEquals( "2", fragmentIdentifier.get( 2 ) ) ;
    assertEquals( "3", fragmentIdentifier.get( 3 ) ) ;
    assertEquals( "FragmentIdentifier[\\0\\1\\2\\3]", fragmentIdentifier.toString() ) ;
    assertEquals( new FragmentIdentifier( "0", "1", "2", "3" ), fragmentIdentifier ) ;
  }
  
  
  
  
}
