package novelang.book.function.builtin.insert;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import novelang.part.Part;

/**
 * 
* @author Laurent Caillette
*/
public class PartCreator implements Callable< Part > {

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

  public Part call() throws Exception {
    return new Part( partFile, sourceCharset, renderingCharset ) ;
  }
}
