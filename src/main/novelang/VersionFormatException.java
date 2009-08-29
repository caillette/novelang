package novelang;

/**
 * Thrown when version {@link Version#parse(String) parsing} fails.
 *  
 * @author Laurent Caillette
 */
public class VersionFormatException extends Exception {

  public VersionFormatException( final String unparseable ) {
    super( "Could not parse: '" + unparseable + "'" ) ;
  }
}
