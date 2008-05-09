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

package novelang.model.common;

import java.nio.charset.Charset;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import novelang.model.common.MutableTree;
import novelang.model.common.NodeKind;
import novelang.model.common.Tree;
import novelang.model.common.TreeMetadata;
import novelang.model.implementation.DefaultMutableTree;

/**
 * @author Laurent Caillette
 */
public class MetadataHelper {

  private MetadataHelper() { }

  public static int countWords( Tree tree ) {
    if( NodeKind.WORD.isRoot( tree ) ) {
      return 1 ;
    } else {
      int childCount = 0 ;
      for( Tree child : tree.getChildren() ) {
        childCount += countWords( child ) ;
      }
      return childCount ;
    }
  }

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormat.forPattern( "yyyy-MM-dd kk:mm" ) ;

  private static DateTime createTimestamp() {
    return new DateTime() ;
  }

  private static String format( DateTime timestamp ) {
    return TIMESTAMP_FORMATTER.print( timestamp ) ;
  }

  public static TreeMetadata createMetadata( Tree tree, final Charset encoding ) {

    final String timestampAsString = format( createTimestamp() ) ;
    final int wordCount = countWords( tree ) ;

    return new TreeMetadata() {
      public String getCreationTimestampAsString() {
        return timestampAsString ;
      }

      public int getWordCount() {
        return wordCount ;
      }

      public Charset getEncoding() {
        return encoding ;
      }
    } ;
  }

}
