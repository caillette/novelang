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
/*  
  public AsRuntime asRuntime() {
    return new AsRuntime( this ) ;
  }
  
  public class AsRuntime extends RuntimeException {
    public AsRuntime( VersionFormatException e ) {
      super( e ) ;
      setStackTrace( e.getStackTrace() ) ;
    }

    @Override
    public String getMessage() {
      return getCause().getMessage() ;
    }

    @Override
    public VersionFormatException getCause() {
      return ( VersionFormatException ) super.getCause() ;
    }
     
  }
*/  
}
