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
import java.util.Set;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import novelang.common.SimpleTree;
import novelang.common.SyntacticTree;
import novelang.common.tree.Tree;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.base.Function;

/**
 * @author Laurent Caillette
 */
public class MetadataHelper {

  private MetadataHelper() { }

  public static int countWords( Tree tree ) {
    if( tree instanceof SyntacticTree ) {
      final SyntacticTree syntacticTree = ( SyntacticTree ) tree ;
      if( WORD_.isRoot( syntacticTree ) ) {
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

  /**
   * Returns the set of values for {@link NodeKind#TAG}s.
   *
   * @return a non-null, possibly empty set.
   */
  public static Set< String > findTags( SyntacticTree tree ) {
    if( tree.isOneOf( WORD_, WORD_AFTER_CIRCUMFLEX_ACCENT, _STYLE ) ) { 
      return ImmutableSet.of() ;
    }
    if( tree.isOneOf( TAG ) ) {
      return ImmutableSet.of( tree.getChildAt( 0 ).getText() ) ;
    }
    final Set tagset = Sets.newLinkedHashSet() ;
    for( SyntacticTree child : tree.getChildren() ) {
        tagset.addAll( findTags( child ) ) ;
    }
    return tagset ;
  }

  public static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormat.forPattern( "yyyy-MM-dd kk:mm" ) ;

  private static DateTime createTimestamp() {
    return new DateTime() ;
  }


  public static DocumentMetadata createMetadata( final Charset charset ) {

    final ReadableDateTime timestamp = createTimestamp() ;

    return new DocumentMetadata() {
      public ReadableDateTime getCreationTimestamp() {
        return timestamp ;
      }

      public Charset getCharset() {
        return charset ;
      }
    } ;
  }

  /**
   * Decorates a tree with metadata.
   * @return the same tree with a new first {@link NodeKind#_META}.
   */
  public static SyntacticTree createMetadataDecoration( SyntacticTree tree ) {

    final List< SyntacticTree > children = Lists.newArrayList() ;

    children.add(
        new SimpleTree(
            NodeKind._WORD_COUNT.name(),
            new SimpleTree( "" + countWords( tree ) )
        )
    ) ;

    final Set< String > tagset = findTags( tree ) ;
    if( tagset.size() > 0 ) {
      final Iterable< SyntacticTree > tagsAsTrees ;
      tagsAsTrees = Iterables.transform( Ordering.natural().sortedCopy( tagset ), STRING_TO_TAG ) ;
      final SyntacticTree tagsTree = new SimpleTree( _TAGS.name(), tagsAsTrees ) ;
      children.add( tagsTree ) ;
    }

    return new SimpleTree(
        NodeKind._META.name(),
        children
    ) ;
  }

  private static final Function< String, SyntacticTree > STRING_TO_TAG =
      new Function< String, SyntacticTree >() {
        public SyntacticTree apply( String tagname ) {
          return new SimpleTree( TAG, new SimpleTree( tagname ) ) ;
        }
      }
  ;
}
