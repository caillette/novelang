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

package novelang.common.metadata;

import java.nio.charset.Charset;

import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import novelang.common.SyntacticTree;
import novelang.common.SimpleTree;
import novelang.common.tree.Tree;
import novelang.parser.NodeKind;

/**
 * @author Laurent Caillette
 */
public class MetadataHelper {

  private MetadataHelper() { }

  public static int countWords( Tree tree ) {
    if( tree instanceof SyntacticTree ) {
      final SyntacticTree syntacticTree = ( SyntacticTree ) tree ;
      if( NodeKind.WORD_.isRoot( syntacticTree ) ) {
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
      return -1 ;
    }
  }

  public static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormat.forPattern( "yyyy-MM-dd kk:mm" ) ;

  private static DateTime createTimestamp() {
    return new DateTime() ;
  }


  public static DocumentMetadata createMetadata( final Charset encoding ) {

    final ReadableDateTime timestamp = createTimestamp() ;

    return new DocumentMetadata() {
      public ReadableDateTime getCreationTimestamp() {
        return timestamp ;
      }

      public Charset getEncoding() {
        return encoding ;
      }
    } ;
  }

  /**
   * Decorates a tree with metadata.
   * @return the same tree with a new first {@link NodeKind#_META}.
   */
  public static SyntacticTree createMetadataDecoration( SyntacticTree tree ) {
    return new SimpleTree(  
        NodeKind._META.name(),
        new SimpleTree(
            NodeKind._WORD_COUNT.name(),
            new SimpleTree( "" + countWords( tree ) )
        )
    );
  }
}
