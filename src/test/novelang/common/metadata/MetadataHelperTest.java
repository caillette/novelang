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

import org.junit.Test;
import novelang.common.SyntacticTree;
import novelang.parser.NodeKind;
import static novelang.parser.NodeKind.*;
import novelang.parser.antlr.TreeFixture;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link MetadataHelper}.
 *
 * @author Laurent Caillette
 */
public class MetadataHelperTest {

  @Test
  public void generateDocumentMetadata() {

    final SyntacticTree tree = tree( BOOK,
        tree( NodeKind.PARAGRAPH_REGULAR,
            tree( WORD_, "foo" ),
            tree( WORD_, "bar" )
        )
    ) ;

    final SyntacticTree meta = MetadataHelper.createMetadataDecoration( tree ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree( _META,
            tree( _WORD_COUNT, "2" )
        ),
        meta
    ) ;

  }
}
