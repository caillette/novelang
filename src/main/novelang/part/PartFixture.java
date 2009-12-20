package novelang.part;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Useful factory methods for tests using {@link Part}.
 * 
 * @author Laurent Caillette
 */
public class PartFixture {
  
  public static Part createPart( final String content ) {
    return new Part( content ) ;
  }
  
  public static Part createPart( final File content ) throws MalformedURLException {
    return new Part( content ) ;
  }
  
  public static Part createStandalonePart( final String content ) {
    return new Part( content ).makeStandalone() ;
  }
  
  public static Part createStandalonePart( final File content ) throws MalformedURLException {
    return new Part( content ).makeStandalone() ;
  }
  
  
}
