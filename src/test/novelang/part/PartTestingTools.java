package novelang.part;

/**
 * Give access to some package-protected members of {@link Part} class to other tests.
 * 
 * @author Laurent Caillette
 */
public class PartTestingTools {
  
  public static Part create( final String content ) {
    return new Part( content ) ;
  }
  
}
