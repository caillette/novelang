package novelang.part;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Useful factory methods for tests using {@link Part}.
 * 
 * @author Laurent Caillette
 */
public class PartFixture {
  
  public static Part createPart( String content ) {
    return new Part( content ) ;
  }
  
  public static Part createPart( File content ) throws MalformedURLException {
    return new Part( content ) ;
  }
  
  public static Part createStandalonePart( String content ) {
    return new Part( content ).makeStandalone() ;
  }
  
  public static Part createStandalonePart( File content ) throws MalformedURLException {
    return new Part( content ).makeStandalone() ;
  }
  
  
}
