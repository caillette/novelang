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
package novelang.rendering;

import java.nio.charset.Charset;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import novelang.common.*;
import novelang.parser.shared.Lexeme;
import novelang.parser.GeneratedLexemes;
import novelang.parser.NodeKind;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Laurent Caillette
 */
public class RenderingTools {

  private RenderingTools() {
  }

  /**
   * Produces a text-only version of some {@code SyntacticTree}.
   */
  public static String textualize( final SyntacticTree tree, final Charset charset )
      throws Exception
  {
    return textualize( tree, charset, new PlainTextWriter( charset ) ) ;
  }

  private static String textualize(
      final SyntacticTree tree,
      final Charset charset,
      final FragmentWriter fragmentWriter
  )
      throws Exception
  {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
    new GenericRenderer( fragmentWriter ).render(
        new RenderableTree( tree, charset ),
        byteArrayOutputStream
    ) ;

    return new String( byteArrayOutputStream.toByteArray(), charset.name() ) ;
  }

  /**
   * Produces a designator name from some {@code SyntacticTree}.
   */
  public static String markerize( final SyntacticTree tree, final Charset charset )
      throws Exception
  {
    // Produce a String replacing delimiters like ()[]"" and punctuation signs by '_'.
    String s = textualize( 
        tree,
        charset,
        new PlainTextWriter( charset, DELIMITERS ) {
          @Override
          public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
            write( kinship, asLiteral( word ) ) ;
          }
        }
    ) ;
    s = s.replaceAll( "[,.;?!:]+", "_" ) ;

    // Replace diacritics by their "naked" version.
    s = replaceAll( GeneratedLexemes.getLexemes(), s ) ;

    // Collapse ' ', '-', '_'.
    s = s.replaceAll( " +", " " ) ;
    s = s.replaceAll( "-+", "-" ) ;
    s = s.replaceAll( "_+", "_" ) ;

    // Collapse sequences that would fool camel-casing.
    s = s.replaceAll( " _", "_" ) ;
    s = s.replaceAll( "_ ", "_" ) ;
    
    
    // Camel-casing: for every word preceded by a ' ' force the 1st letter to upper case.
    final StringBuffer buffer = new StringBuffer() ;
    final Matcher matcher = WORD_BUT_FIRST.matcher( s ) ;
    while( matcher.find() ) {
      final String word = matcher.group( 1 ) ;
        matcher.appendReplacement( buffer, firstCharacterToUpperCase( word ) ) ;
    }
    matcher.appendTail( buffer ) ;
    s = buffer.toString() ;

    // Trim '-' and '_' at the beginning.
    s = s.replaceAll( "\\A-+", "" ) ;
    s = s.replaceAll( "\\A_+", "" ) ;

    // Trim '-' and '_' at the end.
    s = s.replaceAll( "-+\\z", "" ) ;
    s = s.replaceAll( "_+\\z", "" ) ;


    // Collapse ' ', '-', '_' again, could have created some again.
    s = s.replaceAll( " +", " " ) ;
    s = s.replaceAll( "-+", "-" ) ;
    s = s.replaceAll( "_+", "_" ) ;

    // Collapse remaining sequences.
    s = s.replaceAll( "-_", "_" ) ;
    s = s.replaceAll( "_-", "_" ) ;

    // Discard every character which is not a letter, a digit, a '_' or a '-'.
    s = s.replaceAll( "[^0-9a-zA-Z\\-\\_]+", "" ) ;
    return s ;
  }

  private static String asLiteral( final String s )
  {
    return s.replaceAll( "[^0-9a-zA-Z]+", "-" ) ;
  }

  private static String firstCharacterToUpperCase(final String s ) {
    return Character.toUpperCase( s.charAt( 0 ) ) + s.substring( 1 ) ;
  }

  private static final Pattern WORD_BUT_FIRST = Pattern.compile(
  //   v beware of the leading space.
      " ([0-9a-zA-Z]+(?:-[0-9a-zA-Z]+)*)" ) ;
  
  private static String replaceAll( final Map< Character, Lexeme > characterMap, final String s ) {
    final StringBuilder stringBuilder = new StringBuilder() ;
    for( final char c : s.toCharArray() ) {
      final Lexeme lexeme = characterMap.get( c ) ;
      if( lexeme != null && lexeme.hasDiacriticlessRepresentation() ) {
        stringBuilder.append( lexeme.getAscii62() ) ;
      } else {
        stringBuilder.append( c ) ;
      }
    }
    return stringBuilder.toString() ;
  }

  private static final String DELIMITER_REPLACEMENT = "_" ;

  private static final PlainTextWriter.DelimiterPair TAGGING_PAIR =
      PlainTextWriter.pair(DELIMITER_REPLACEMENT, DELIMITER_REPLACEMENT) ;

  private static final Map< NodeKind, PlainTextWriter.DelimiterPair > DELIMITERS =
      new ImmutableMap.Builder< NodeKind, PlainTextWriter.DelimiterPair>().
      put( NodeKind.BLOCK_INSIDE_DOUBLE_QUOTES, TAGGING_PAIR ).
      put( NodeKind.BLOCK_INSIDE_PARENTHESIS, TAGGING_PAIR ).
      put( NodeKind.BLOCK_INSIDE_HYPHEN_PAIRS, TAGGING_PAIR ).
      put( NodeKind.BLOCK_INSIDE_SQUARE_BRACKETS, TAGGING_PAIR ).
      put( NodeKind.BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, TAGGING_PAIR ).
      build()
  ;

  

  public static class RenderableTree implements Renderable {
    private final SyntacticTree tree ;
    private final Charset charset ;

    public RenderableTree( final SyntacticTree tree, final Charset charset ) {
      this.tree = tree ;
      this.charset = charset ;
    }

    public Iterable<Problem> getProblems() {
      return ImmutableList.of() ;
    }

    public Charset getRenderingCharset() {
      return charset;
    }

    public boolean hasProblem() {
      return false ;
    }

    public SyntacticTree getDocumentTree() {
      return tree;
    }

    public StylesheetMap getCustomStylesheetMap() {
      return null ;
    }
  }
}
