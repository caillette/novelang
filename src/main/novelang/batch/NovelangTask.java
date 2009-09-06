package novelang.batch;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * Transforms a given Novelang document into a String.
 * <p>
 * Parameters:
 * <ul>
 *   <li><code>content-root</code> The directory where content files reside. 
 *         Relative paths get resolved from here.
 *   </li>
 * </ul>
 * <pre>
 * <novelang
 *     content-root="" 
 *     log-directory="" 
 *     document-request="" 
 *     source-encoding="" 
 *     stylesheet=""
 *     destination-encoding=""
 *     content-property="html" 
 * />
 * </pre>
 * 
 * @author Laurent Caillette
 */
public class NovelangTask extends Task {

// ===============  
// Task properties
// ===============  
  
  private File contentRoot ;
  private String documentRequest ;
  private String contentProperty ;
  
  public File getContentRoot() {
    return contentRoot ;
  }

  public void setContentRoot( File contentRoot ) {
    this.contentRoot = contentRoot ;
  }

  public String getDocumentRequest() {
    return documentRequest ;
  }

  public void setDocumentRequest( String documentRequest ) {
    this.documentRequest = documentRequest ;
  }

  public String getContentProperty() {
    return contentProperty ;
  }

  public void setContentProperty( String contentProperty ) {
    this.contentProperty = contentProperty;
  }

  
  
// =========
// Execution
// =========
  
  
  @Override
  public void execute() throws BuildException {
    super.execute() ;
  }


}
