package novelang.parser.unicode ;

import java.io.BufferedInputStream;
import java.io.IOException ;
import java.io.InputStream ;
import java.io.PushbackInputStream ;
import java.nio.charset.Charset;

import com.google.common.base.Preconditions;

/**
 * This {@code InputStream} recognizes unicode BOM and skips bytes if {@link #getEncoding()}
 * method is called before any of the {@code read(...)} methods.
 * <p>
 * Copied from
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4508058" >Sun's bug database</a>
 * <p>
 * See <a href="http://www.unicode.org/unicode/faq/utf_bom.html" >Unicode BOM FAQ</a>
 * <p>
 * BOMs:
 * <ul>
 * <li> 00 00 FE FF    = UTF-32, big-endian
 * <li> FF FE 00 00    = UTF-32, little-endian
 * <li> FE FF          = UTF-16, big-endian
 * <li> FF FE          = UTF-16, little-endian
 * <li> EF BB BF       = UTF-8
 * <li> Win2k Notepad: Unicode format = UTF-16LE
 * </ul>
 * <p>
 * Usage pattern:
 * <pre>
 * String enc = "ISO-8859-1" ; // or NULL to use systemdefault
 * FileInputStream fis = new FileInputStream( file ) ;
 * UnicodeInputStream uin = new UnicodeInputStream( fis, enc ) ;
 * enc = uin.getEncoding() ; // check for BOM and skip bytes
 * InputStreamReader in ;
 * if (enc == null) in = new InputStreamReader(uin) ;
 * else in = new InputStreamReader(uin, enc) ;
 * </pre>
 *
 * @author Thomas Weidenfeller for original pseudocode.
 * @author Aki Nieminen for implementation.
 * @author Laurent Caillette for minor changes.
 */
public class UnicodeInputStream extends InputStream {
  
  final PushbackInputStream internalInputStream;
  final Charset defaultEncoding ;
  private boolean initialized = false ;
  private Charset encoding = null ;

  private static final int BOM_SIZE = 4 ;
  private static final int BUFFER_SIZE = 1024 * 32 ;

  @SuppressWarnings( { "IOResourceOpenedButNotSafelyClosed" } )
  public UnicodeInputStream( final InputStream in, final Charset defaultEncoding ) {
    final BufferedInputStream bufferedInputStream = new BufferedInputStream( in, BUFFER_SIZE ) ;
    internalInputStream = new PushbackInputStream( bufferedInputStream, BOM_SIZE ) ;
    this.defaultEncoding = Preconditions.checkNotNull( defaultEncoding ) ;
  }

  public Charset getEncoding() {
    if( ! initialized ) {
      try {
        initialize() ;
      } catch( IOException ex ) {
        throw new IllegalStateException( "Initialization failed", ex ) ;
      }
    }
    return encoding ;
  }

  /**
   * Read-ahead four bytes and check for BOM marks. Extra
   * bytes are
   * unread back to the stream, only BOM bytes are skipped.
   */
  protected void initialize() throws IOException {
    if( initialized ) return ;

    final byte[] bom = new byte[ BOM_SIZE ] ;
    final int n;
    final int unread ;
    n = internalInputStream.read( bom, 0, bom.length ) ;

    if( ( bom[ 0 ] == ( byte ) 0xEF ) && ( bom[ 1 ] == ( byte ) 0xBB ) &&
        ( bom[ 2 ] == ( byte ) 0xBF )
    ) {
      encoding = Charset.forName( "UTF-8" ) ;
      unread = n - 3 ;
    } else if( ( bom[ 0 ] == ( byte ) 0x00 ) && ( bom[ 1 ] == ( byte ) 0x00 ) &&
        ( bom[ 2 ] == ( byte ) 0xFE ) && ( bom[ 3 ] == ( byte ) 0xFF )
    ) {
      encoding = Charset.forName( "UTF-32BE" ) ;
      unread = n - 4 ;
    } else if( ( bom[ 0 ] == ( byte ) 0xFF ) && ( bom[ 1 ] == ( byte ) 0xFE ) &&
        ( bom[ 2 ] == ( byte ) 0x00 ) && ( bom[ 3 ] == ( byte ) 0x00 )
    ) {
      encoding = Charset.forName( "UTF-32LE" ) ;
      unread = n - 4 ;
    } else if( ( bom[ 0 ] == ( byte ) 0xFE ) && ( bom[ 1 ] == ( byte ) 0xFF ) ) {
      encoding = Charset.forName( "UTF-16BE" ) ;
      unread = n - 2 ;
    } else if( ( bom[ 0 ] == ( byte ) 0xFF ) && ( bom[ 1 ] == ( byte ) 0xFE ) ) {
      encoding = Charset.forName( "UTF-16LE" ) ;
      unread = n - 2 ;
    } else {
      // Unicode BOM mark not found, unread all bytes
      encoding = defaultEncoding ;
      unread = n ;
    }
//    System.out.println( "read=" + n + ", unread=" + unread ) ;

    if( unread > 0 ) {
      internalInputStream.unread( bom, ( n - unread ), unread ) ;
    }

    initialized = true ;
  }

  public void close() throws IOException {
    initialized = true ;
    internalInputStream.close() ;
  }

  public int read() throws IOException {
    initialized = true ;
    return internalInputStream.read() ;
   }
}
