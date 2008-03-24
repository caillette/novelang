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
package novelang.model.implementation;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import novelang.model.common.MutableTree;
import novelang.model.common.NodeKind;
import novelang.model.common.Tree;
import novelang.model.common.TreeMetadata;

/**
 * @author Laurent Caillette
 */
public class MetadataHelper {

  private MetadataHelper() { }

  @Deprecated
  public static Tree addMetadata( Tree tree ) {
    final MutableTree newTree = new DefaultMutableTree( NodeKind.ofRoot( tree ) ) ;
    final MutableTree timestampNode = new DefaultMutableTree( NodeKind._META_TIMESTAMP ) ;
    timestampNode.addChild( new DefaultMutableTree( format( createTimestamp() ) ) ) ;
    newTree.addChild( timestampNode ) ;
    for( final Tree child : tree.getChildren() ) {
      newTree.addChild( child ) ;
    }
    return newTree ;
  }

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormat.forPattern( "yyyy-MM-dd kk:mm" ) ;

  private static DateTime createTimestamp() {
    return new DateTime() ;
  }

  private static String format( DateTime timestamp ) {
    return TIMESTAMP_FORMATTER.print( timestamp ) ;
  }

  public static TreeMetadata createMetadata( Tree tree ) {

    final String timestampAsString = format( createTimestamp() ) ;

    return new TreeMetadata() {
      public String getCreationTimestampAsString() {
        return timestampAsString ;
      }
    } ;
  }

}
