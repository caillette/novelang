package novelang.composium.function.builtin.insert;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import novelang.novella.Novella;

/**
 * 
* @author Laurent Caillette
*/
public class PartCreator implements Callable<Novella> {

  private final File partFile ;
  private final Charset sourceCharset ;
  private final Charset renderingCharset ;

  public PartCreator(
      final File partFile,
      final Charset sourceCharset,
      final Charset renderingCharset
  ) {
    this.partFile = partFile;
    this.sourceCharset = sourceCharset;
    this.renderingCharset = renderingCharset;
  }

  public Novella call() throws Exception {
    return new Novella( partFile, sourceCharset, renderingCharset ) ;
  }
}
