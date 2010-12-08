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
package org.novelang.rendering;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;
import org.novelang.common.Problem;
import org.novelang.common.Renderable;
import org.novelang.common.StylesheetMap;
import org.novelang.common.SyntacticTree;
import org.novelang.outfit.DefaultCharset;
import org.novelang.parser.NodeKind;
import org.novelang.parser.antlr.TreeFixture;

/**
 * Tests for {@link XmlWriter}.
 *
 * @author Laurent Caillette
 */
public class XmlWriterTest {

  @Test
  public void   oeLigatured() throws Exception {
    final FragmentWriter xmlWriter = new XmlWriter() ;
    final GenericRenderer renderer = new GenericRenderer( xmlWriter ) ;


    final Renderable rendered = new MyRenderable(
        TreeFixture.tree( NodeKind.WORD_, "w" ) ) ;
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;

    renderer.render( rendered, outputStream, null ) ;

    final String rendition = new String( outputStream.toByteArray() ) ;

    Assert.assertEquals(
        "<?xml version=\"1.0\" encoding=\"" + DefaultCharset.RENDERING.name() + "\"?>\n" +
        "w",
        rendition
    ) ;
  }

  private static class MyRenderable implements Renderable {

    private final SyntacticTree syntacticTree ;

    private MyRenderable( final SyntacticTree syntacticTree ) {
      this.syntacticTree = syntacticTree ;
    }

    @Override
    public Iterable< Problem > getProblems() {
      return null;
    }

    @Override
    public Charset getRenderingCharset() {
      return DefaultCharset.RENDERING;
    }

    @Override
    public boolean hasProblem() {
      return false ;
    }

    @Override
    public SyntacticTree getDocumentTree() {
      return syntacticTree ;
    }

    @Override
    public StylesheetMap getCustomStylesheetMap() {
      return null ;
    }
  }
}
