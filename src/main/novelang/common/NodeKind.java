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

package novelang.common;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public enum NodeKind {

  BOOK,
  PART,

  _META_TIMESTAMP,

  CHAPTER,
  SECTION,
  TITLE,
  IDENTIFIER,
  LOCUTOR,
  STYLE,

  PARAGRAPH_PLAIN,
  _SPEECH_SEQUENCE,
  PARAGRAPH_SPEECH,
  PARAGRAPH_SPEECH_CONTINUED,
  PARAGRAPH_SPEECH_ESCAPED,
  BLOCKQUOTE,
  LITTERAL,
  QUOTE,
  EMPHASIS,
  PARENTHESIS,
  SQUARE_BRACKETS,
  INTERPOLATEDCLAUSE,
  INTERPOLATEDCLAUSE_SILENTEND,
  ELLIPSIS_OPENING,
  APOSTROPHE_WORDMATE,
  WORD,
  URL,

  FUNCTION_CALL,
  FUNCTION_NAME,
  VALUED_ARGUMENT_PRIMARY,
  VALUED_ARGUMENT_ANCILLARY,
  VALUED_ARGUMENT_MODIFIER,
  VALUED_ARGUMENT_FLAG,
  VALUED_ARGUMENT_ASSIGNMENT,

  PUNCTUATION_SIGN,
  SIGN_COMMA( true ),
  SIGN_FULLSTOP( true ),
  SIGN_ELLIPSIS( true ),
  SIGN_QUESTIONMARK( true ),
  SIGN_EXCLAMATIONMARK( true ),
  SIGN_SEMICOLON( true ),
  SIGN_COLON( true ),

  ;


  private final boolean punctuationSign ;


  private NodeKind() {
    punctuationSign = false ;
  }

  NodeKind( boolean punctuationSign ) {
    this.punctuationSign = punctuationSign;
  }

  public boolean isPunctuationSign() {
    return punctuationSign;
  }

  public static NodeKind ofRoot( SyntacticTree tree ) {
    return Enum.valueOf( NodeKind.class, tree.getText() ) ;
  }

  private static final List< String > NAMES ;
  static {
    final List< String > names = Lists.newArrayList() ;
    for( NodeKind nodeKind : NodeKind.values() ) {
      names.add( nodeKind.name() ) ;
    }
    NAMES = Collections.unmodifiableList( names ) ;
  }

  /**
   * Returns if a given {@code Tree} is of expected kind.
   * @param tree may be null.
   */
  public boolean isRoot( SyntacticTree tree ) {
    if( null == tree ) {
      return false ;
    }
    final String text = tree.getText();
    return
        NAMES.contains( text ) &&
        name().equals( text )
    ;
  }

  public static boolean rootHasNodeKindName( SyntacticTree tree ) {
    if( null == tree ) {
      return false ;
    }
    final String text = tree.getText() ;
    return null != tree && NAMES.contains( text ) ;
  }

  public static void ensure( SyntacticTree tree, NodeKind nodeKind ) {
    Objects.nonNull( tree ) ;
    Objects.nonNull( nodeKind ) ;
    final String nodeText = Objects.nonNull( tree.getText() ) ;
    if( ! NAMES.contains( nodeText ) ) {
      throw new RuntimeException( "Not a known node kind: '" + nodeText + "'" ) ;
    }
    if( nodeKind != ofRoot( tree ) ) {
      throw new RuntimeException( "Expected: " + nodeKind + ", got: " + nodeText ) ;
    }
  }



}
