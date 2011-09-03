/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.rendering;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;

import org.novelang.common.Nodepath;
import org.novelang.common.Problem;
import org.novelang.common.Renderable;
import org.novelang.common.StylesheetMap;
import org.novelang.common.SyntacticTree;
import org.novelang.designator.Tag;
import org.novelang.outfit.DefaultCharset;
import org.novelang.parser.GeneratedLexemes;
import org.novelang.parser.NodeKind;
import org.novelang.parser.shared.Lexeme;

/**
 * @author Laurent Caillette
 */
public class RenderingTools {

  private RenderingTools() { }

  /**
   * Produces a text-only version of some {@code SyntacticTree}.
   */
  public static String textualize( final SyntacticTree tree, final Charset charset ) 
      throws UnsupportedEncodingException 
  {
    return textualize( tree, charset, new PlainTextWriter( charset ) ) ;
  }

  private static String textualize(
      final SyntacticTree tree,
      final Charset charset,
      final FragmentWriter fragmentWriter
  ) throws UnsupportedEncodingException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
    try {
      new GenericRenderer( fragmentWriter, false ).render(
          new RenderableTree( tree, charset ),
          byteArrayOutputStream,
          null,
          null // TODO find something better 
      ) ;
    } catch ( Exception e ) {
      throw new RuntimeException( "Should not happen, no IO expected", e ) ;
    }

    return new String( byteArrayOutputStream.toByteArray(), charset.name() ) ;
  }


  public static ImmutableSet< Tag > toTagSet( final Set< String > tagsAsStrings) {
    final ImmutableSet.Builder< Tag > tagSet = ImmutableSet.builder() ;
    for( final String tagAsString : tagsAsStrings ) {
      final String cleanString = toCleanStringForTag( tagAsString ) ;
      if( ! StringUtils.isBlank( cleanString ) ) {
        final Tag tag = new Tag( cleanString ) ;
        tagSet.add( tag ) ;
      }
    }
    return tagSet.build() ;
  }


  public static Set< Tag > toImplicitTagSet( final SyntacticTree tree ) {
    final String identifier = toImplicitIdentifier( tree ) ;
    return Tag.toTagSet( identifier.split( "_" ) ) ;
  }

  /**
   * Produces a designator name from some {@code SyntacticTree}.
   */
  public static String toImplicitIdentifier( final SyntacticTree tree ) {
    // Produce a String replacing delimiters like ()[]"" and punctuation signs by '_'.
    String s = null;
    try {
      s = textualize( 
          tree,
          DefaultCharset.RENDERING,
          new PlainTextWriter( DefaultCharset.RENDERING, DELIMITERS ) {
            @Override
            public void writeLiteral( final Nodepath kinship, final String word ) throws Exception {
              write( kinship, asLiteral( word ) ) ;
            }
          }
      );
    } catch ( UnsupportedEncodingException e ) {
      throw new RuntimeException( "Should not happen with default encoding", e ) ;
    }

    s = toCleanStringForTag( s );
    return s ;
  }

  private static String toCleanStringForTag( String s ) {
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
    return s;
  }

  private static String asLiteral( final String s ) {
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
//      put( NodeKind.BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, TAGGING_PAIR ).
//      put( NodeKind.BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, TAGGING_PAIR ).
      build()
  ;


  public static class RenderableTree implements Renderable {
    private final SyntacticTree tree ;
    private final Charset charset ;

    public RenderableTree( final SyntacticTree tree, final Charset charset ) {
      this.tree = tree ;
      this.charset = charset ;
    }

    @Override
    public Iterable<Problem> getProblems() {
      return ImmutableList.of() ;
    }

    @Override
    public Charset getRenderingCharset() {
      return charset;
    }

    @Override
    public boolean hasProblem() {
      return false ;
    }

    @Override
    public SyntacticTree getDocumentTree() {
      return tree;
    }

    @Override
    public StylesheetMap getCustomStylesheetMap() {
      return null ;
    }
  }
}
