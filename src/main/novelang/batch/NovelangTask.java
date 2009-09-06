package novelang.batch;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import novelang.configuration.parse.BatchParameters;

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
  private File styleDirectory ;
  private String documentRequest ;
  private String contentProperty ;
  
  public File getContentRoot() {
    return contentRoot ;
  }

  public void setContentRoot( File contentRoot ) {
    this.contentRoot = contentRoot ;
  }

  public File getStyleDirectory() {
    return styleDirectory;
  }

  public void setStyleDirectory( File styleDirectory ) {
    this.styleDirectory = styleDirectory;
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
    final List< String > arguments = Lists.newArrayList() ;
    if( contentRoot != null ) {
      arguments.add( BatchParameters.OPTIONPREFIX + BatchParameters.OPTIONNAME_CONTENT_ROOT ) ;
      arguments.add( contentRoot.getAbsolutePath() ) ;
    }
    if( styleDirectory != null ) {
      arguments.add( BatchParameters.OPTIONPREFIX + BatchParameters.OPTIONNAME_STYLE_DIRECTORY ) ;
      arguments.add( styleDirectory.getAbsolutePath() ) ;
    }
    if( documentRequest == null ) {
      throw new BuildException( "documentRequest cannot be null" ) ;
    } else {
      arguments.add( documentRequest ) ;
    }


    final DocumentGenerator generator = new DocumentGenerator() ;
    try {
      generator.main(
          DocumentGenerator.COMMAND_NAME,
          false,
          arguments.toArray( new String[ arguments.size() ] ), getProject().getBaseDir()
      ) ;
    } catch( Exception e ) {
      throw new BuildException( e ) ;
    }

    getProject().setProperty( contentProperty, "Done it!" ) ;
  }


}
