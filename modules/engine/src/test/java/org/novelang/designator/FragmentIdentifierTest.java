package org.novelang.designator;

import org.junit.Test;

import static org.junit.Assert.*;
import com.google.common.collect.Lists;

/**
 * Tests for {@link FragmentIdentifier}.
 * 
 * @author Laurent Caillette
 */
public class FragmentIdentifierTest {
  
  @Test( expected = NullPointerException.class )
  public void noNull() {
    new FragmentIdentifier( null ) ;
  }

  

  @Test( expected = IllegalArgumentException.class )
  public void noBlank() {
    new FragmentIdentifier( "" ) ;
  }

  
  @Test
  public void equals() {
    assertEquals( new FragmentIdentifier( "0" ), new FragmentIdentifier( "0" ) ) ;
  }

}
