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
package org.novelang.common.metadata;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.novelang.common.SyntacticTree;
import org.novelang.designator.Tag;
import org.novelang.parser.NodeKind;
import org.novelang.parser.antlr.TreeFixture;
import org.novelang.treemangling.TagMangler;

import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link MetadataHelper}.
 *
 * @author Laurent Caillette
 */
public class MetadataHelperTest {

  @Test
  public void generateDocumentMetadataNoTags() {

    final SyntacticTree tree = tree( OPUS,
        tree( NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "foo" ),
            tree( WORD_, "bar" )
        )
    ) ;

    final SyntacticTree meta = MetadataHelper.createMetadataDecoration( 
        tree, ImmutableSet.< Tag >of() ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree( _META,
            tree( _WORD_COUNT, "2" )
        ),
        meta
    ) ;

  }

  @Test
  public void generateDocumentMetadataIncludingTags() {

    final SyntacticTree tree = tree( OPUS,
        tree( NodeKind.PARAGRAPH_REGULAR,
            tree( _EXPLICIT_TAG, "t" ),
            tree( WORD_, "foo" ),
            tree( WORD_, "bar" )
        )
    ) ;

    final Set< Tag > tagset = TagMangler.findExplicitTags( tree ) ;
    final SyntacticTree meta = MetadataHelper.createMetadataDecoration( tree, tagset ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree( _META,
            tree( _WORD_COUNT, "2" ),
            tree(
                _TAGS,
                tree( _EXPLICIT_TAG, "t" )
            )
        ),
        meta
    ) ;

  }

  @Test
  public void findTags() {
    final SyntacticTree tree = tree(
        OPUS,

        tree(
            _LEVEL,
            tree( _EXPLICIT_TAG, "t1" ),
            tree( PARAGRAPH_REGULAR,
                tree( _EXPLICIT_TAG, "t2" ),
                tree( _EXPLICIT_TAG, "t3" ),
                tree( WORD_, "foo" ),
                tree( WORD_, "bar" )
            )
        )
    ) ;

    final Set< Tag > tags = TagMangler.findExplicitTags( tree ) ;
    Assert.assertEquals( 3, tags.size() ) ;
    Assert.assertTrue( tags.contains( new Tag( "t1" ) ) ) ;
    Assert.assertTrue( tags.contains( new Tag( "t2" ) ) ) ;
    Assert.assertTrue( tags.contains( new Tag( "t3" ) ) ) ;

  }
}
