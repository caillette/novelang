/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.common;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public enum NodeKind {

  _BOOK,
  PART,
  CHAPTER,
  SECTION,
  TITLE,
  IDENTIFIER,
  LOCUTOR,

  PARAGRAPH_PLAIN,
  _SPEECH_SEQUENCE,
  PARAGRAPH_SPEECH,
  PARAGRAPH_SPEECH_CONTINUED,
  PARAGRAPH_SPEECH_ESCAPED,
  BLOCKQUOTE,
  QUOTE,
  EMPHASIS,
  PARENTHESIS,
  SQUARE_BRACKETS,
  INTERPOLATEDCLAUSE,
  INTERPOLATEDCLAUSE_SILENTEND,
  ELLIPSIS_OPENING,
  APOSTROPHE_WORDMATE,
  WORD,

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

  public static NodeKind ofRoot( Tree tree ) {
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
  public boolean isRoot( Tree tree ) {
    if( null == tree ) {
      return false ;
    }
    final String text = tree.getText();
    return
        NAMES.contains( text ) &&
        name().equals( text )
    ;
  }

  public static boolean rootHasNodeKindName( Tree tree ) {
    if( null == tree ) {
      return false ;
    }
    final String text = tree.getText() ;
    return null != tree && NAMES.contains( text ) ;
  }

  public static void ensure( Tree tree, NodeKind nodeKind ) {
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
