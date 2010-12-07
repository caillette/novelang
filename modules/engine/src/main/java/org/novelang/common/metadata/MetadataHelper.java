/*
 * Copyright (C) 2010 Laurent Caillette
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

package org.novelang.common.metadata;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.novelang.common.SimpleTree;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Tree;
import org.novelang.designator.Tag;
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKind.WORD_;
import static org.novelang.parser.NodeKind._EXPLICIT_TAG;
import static org.novelang.parser.NodeKind._TAGS;

/**
 * @author Laurent Caillette
 */
public class MetadataHelper {

  private MetadataHelper() { }

  public static int countWords( final Tree tree ) {
    if( tree instanceof SyntacticTree ) {
      final SyntacticTree syntacticTree = ( SyntacticTree ) tree ;
      if( WORD_ == syntacticTree.getNodeKind() ) {
        return 1 ;
      }
    }
    if( null != tree ) {
      int childCount = 0 ;
      for( int i = 0 ; i < tree.getChildCount() ; i++ ) {
        childCount += countWords( tree.getChildAt( i ) ) ;
      }
      return childCount ;
    } else {
      return 0 ;
    }
  }

  public static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormat.forPattern( "yyyy-MM-dd kk:mm" ) ;

  private static DateTime createTimestamp() {
    return new DateTime() ;
  }


  public static DocumentMetadata createMetadata( final Charset charset ) {

    final ReadableDateTime timestamp = createTimestamp() ;

    return new DocumentMetadata() {
      @Override
      public ReadableDateTime getCreationTimestamp() {
        return timestamp ;
      }

      @Override
      public Charset getCharset() {
        return charset ;
      }
    } ;
  }

  /**
   * Decorates a tree with metadata.
   * @return the same tree with a new first {@link NodeKind#_META}.
   */
  public static SyntacticTree createMetadataDecoration( 
      final SyntacticTree tree, 
      final Set< Tag > tagset 
  ) {

    final List< SyntacticTree > children = Lists.newArrayList() ;

    children.add(
        new SimpleTree(
            NodeKind._WORD_COUNT,
            new SimpleTree( "" + countWords( tree ) )
        )
    ) ;

    if( tagset.size() > 0 ) {
      final Iterable< SyntacticTree > tagsAsTrees = Tag.toSyntacticTrees( _EXPLICIT_TAG, tagset ) ;
      final SyntacticTree tagsTree = new SimpleTree( _TAGS, tagsAsTrees ) ;
      children.add( tagsTree ) ;
    }

    return new SimpleTree(
        NodeKind._META,
        children
    ) ;
  }


}
