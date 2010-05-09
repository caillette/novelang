/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package novelang.build.documentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.lang.CharUtils;
import com.google.common.collect.Ordering;
import novelang.parser.GeneratedLexemes;
import novelang.parser.SourceUnescape;
import novelang.parser.shared.Lexeme;
import novelang.rendering.RenderingEscape;
import novelang.system.DefaultCharset;

/**
 * Generates a source document containing all characters supported in input.
 *
 * @author Laurent Caillette
 */
public class LexemeTable {

  private static final Log LOG = LogFactory.getLog( LexemeTable.class );

  public static void main( final String[] args ) throws FileNotFoundException {
    if( args.length != 1 ) {
      throw new IllegalArgumentException( "Expected argument: destination file name" ) ;
    }
    writeSourceDocument( new File( args[ 0 ] ) ) ;
  }

//  private final RenderingEscape.CharsetEncodingCapability renderingCapability ;

  public static void writeSourceDocument( final File file ) throws FileNotFoundException {
    writeSourceDocument( file, DefaultCharset.SOURCE ) ;
  }

  public static void writeSourceDocument(
      final File file,
      final Charset sourceCharset
  ) throws FileNotFoundException {
    final FileOutputStream outputStream = new FileOutputStream( file ) ;
    final PrintWriter printWriter ;
    try {
      printWriter = new PrintWriter( new OutputStreamWriter( outputStream, sourceCharset ), true ) ;
      writeSourceDocument( printWriter ) ;
      printWriter.flush() ;
      printWriter.close() ;
    } finally {
      try {
        outputStream.close() ;
      } catch( IOException e ) {
        LOG.error( "Couldn't close file '" + file.getAbsolutePath() + "' properly", e ) ;
      }
    }
  }

  private static void writeSourceDocument( final PrintWriter writer ) {

    writer.println( "== Characters supported in source documents" ) ;
    writer.println() ;
    writer.println( "| Escape name | Alias | Hex | Dec | Preview |" ) ;

    final List< Lexeme > lexemes = Ordering.from( COMPARATOR ).sortedCopy(
        GeneratedLexemes.getLexemes().values() ) ;
    for( final Lexeme lexeme : lexemes ) {
      final String unicode = CharUtils.unicodeEscaped( lexeme.getCharacter() ).substring( 1 ) ;
      final String decimal = String.format( "%04d", ( int ) lexeme.getCharacter().charValue() ) ;
      final String longEscapeName =
          SourceUnescape.unicodeUpperNameToEscapeName( lexeme.getUnicodeName() );
      writer.append( "| " ) ;
      writer.append( doubleEscape( longEscapeName ) ) ;
      writer.append( " | " ) ;
      writer.append( doubleEscape( lexeme.getHtmlEntityName() ) ) ;
      writer.append( " | " ) ;
      writer.append( unicode ) ;
      writer.append( " | " ) ;
      writer.append( decimal ) ;
      writer.append( " | " ) ;
      writer.append( simpleEscape( longEscapeName ) ) ;
      writer.append( " |" ) ;
      writer.println() ;
    }

  }

  private static String simpleEscape( final String name ) {
    if( null == name ) {
      return " " ;
    } else {
      return SourceUnescape.ESCAPE_START + name + SourceUnescape.ESCAPE_END ;
    }
  }

  private static String doubleEscape( final String name ) {
    if( null == name ) {
      return " " ;
    } else {
      return
          RenderingEscape.unconditionalEscapeToSource( SourceUnescape.ESCAPE_START ) +
          name +
          RenderingEscape.unconditionalEscapeToSource( SourceUnescape.ESCAPE_END )
      ;
    }
  }

  private static final Comparator< Lexeme > COMPARATOR = new Comparator< Lexeme >() {
    public int compare( final Lexeme lexeme1, final Lexeme lexeme2 ) {
      return lexeme1.getCharacter().compareTo( lexeme2.getCharacter() ) ;
    }
  };
}
