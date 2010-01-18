package novelang.parser.unicode;

import org.junit.Test;
import org.junit.Assert;

/**
 * Tests for {@link UnicodeNames}.
 *
 * @author Laurent Caillette
 */
public class UnicodeNamesTest {

  @Test
  public void someUnicode16DefinedValue() {

    final String name = UnicodeNames.getUnicodeName( ( short ) 0xC1 ) ;
    Assert.assertEquals( "", name ) ;

  }
}
