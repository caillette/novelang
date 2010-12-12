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
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.designator.Tag;
import org.novelang.outfit.DefaultCharset;

import static org.novelang.parser.NodeKind.*;

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


  public static DocumentMetadata createMetadata() {
    return createMetadata( DefaultCharset.RENDERING, null ) ;
  }
  
  public static DocumentMetadata createMetadata( final Charset charset, final Page page ) {

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

      @Override
      public Page getPage() {
        return page ;
      }
    } ;
  }

  /**
   * Decorates a tree with metadata.
   * @return the same tree with a new first {@link org.novelang.parser.NodeKind#_META}.
   */
  public static SyntacticTree createMetadataDecoration( 
      final SyntacticTree tree, 
      final Set< Tag > tagset 
  ) {

    final List< SyntacticTree > children = Lists.newArrayList() ;

    children.add(
        new SimpleTree(
            _WORD_COUNT,
            new SimpleTree( "" + countWords( tree ) )
        )
    ) ;

    if( ! tagset.isEmpty() ) {
      final Iterable< SyntacticTree > tagsAsTrees = Tag.toSyntacticTrees( _EXPLICIT_TAG, tagset ) ;
      final SyntacticTree tagsTree = new SimpleTree( _TAGS, tagsAsTrees ) ;
      children.add( tagsTree ) ;
    }

    return new SimpleTree(
        _META,
        children
    ) ;
  }


  /**
   * Adds a {@link org.novelang.parser.NodeKind#_META}/{@link org.novelang.parser.NodeKind#_PAGE}
   * element.
   *
   * @param page a possibly null object.
   * @return the root of the tree reflecting this addition, or the original tree if {@code page}
   *         is null.
   */
  public static SyntacticTree createMetadataDecoration(
      final SyntacticTree tree,
      final Page page
  ) {
    if( page == null ) {
      return tree ;
    }

    final SyntacticTree pageTree = new SimpleTree(
        _PAGE,
        new SimpleTree( _PAGE_IDENTIFIER, new SimpleTree( page.getPageIdentifier().getName() ) ),
        new SimpleTree( _PAGE_PATH, new SimpleTree( page.getPath() ) )
    ) ;

    final Treepath< SyntacticTree > treepath = Treepath.create( tree ) ;

    Treepath< SyntacticTree > treepathToMetaChild = null ;
    for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
      final SyntacticTree child = tree.getChildAt( i ) ;
      if( child.isOneOf( _META ) ) {
        treepathToMetaChild = Treepath.create( treepath, i ) ;
        break ;
      }
    }

    if( treepathToMetaChild == null ) {
      treepathToMetaChild =
          TreepathTools.addChildAt( treepath, new SimpleTree( _META, pageTree ), 0 ) ;
    } else {
      for( int i = 0 ; i < treepathToMetaChild.getTreeAtEnd().getChildCount() ; i ++ ) {
        final SyntacticTree child = treepathToMetaChild.getTreeAtEnd().getChildAt( i ) ;
        if( child.isOneOf( _PAGE ) ) {
          throw new IllegalArgumentException( "Already has a " + _PAGE + " child" ) ;
        }
      }      
      treepathToMetaChild = TreepathTools.addChildLast( treepathToMetaChild, pageTree ) ;
    }

    return treepathToMetaChild.getTreeAtStart() ;
  }

}
