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
  SECTION_IDENTIFIER,
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
  INTERPOLATEDCLAUSE,
  INTERPOLATEDCLAUSE_SILENTEND,
  WORD,
  WORDTRAIL,
  ;

  public static NodeKind getToken( Tree tree ) {
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
   * @param nodeKind may not be null.
   */
  public static boolean is( Tree tree, NodeKind nodeKind ) {
    Objects.nonNull( nodeKind ) ;
    final String text = tree.getText();
    // TODO is everything useful here?
    return
        null != tree &&
        NAMES.contains( text ) &&
        nodeKind.name().equals( text )
    ;
  }

  public static void ensure( Tree tree, NodeKind nodeKind ) {
    Objects.nonNull( tree ) ;
    Objects.nonNull( nodeKind ) ;
    final String nodeText = Objects.nonNull( tree.getText() ) ;
    if( ! NAMES.contains( nodeText ) ) {
      throw new RuntimeException( "Not a known node kind: '" + nodeText + "'" ) ;
    }
    if( nodeKind != getToken( tree ) ) {
      throw new RuntimeException( "Expected: " + nodeKind + ", got: " + nodeText ) ;
    }
  }



}
