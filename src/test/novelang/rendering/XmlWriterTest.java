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
package novelang.rendering;

import java.nio.charset.Charset;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.junit.Assert;
import novelang.common.Renderable;
import novelang.common.Problem;
import novelang.common.SyntacticTree;
import novelang.common.StylesheetMap;
import novelang.parser.NodeKind;
import novelang.parser.antlr.TreeFixture;
import novelang.parser.Encoding;

/**
 * Tests for {@link XmlWriter}.
 *
 * @author Laurent Caillette
 */
public class XmlWriterTest {

  @Test
  public void oeLigatured() {
    final FragmentWriter xmlWriter = new XmlWriter() ;
    final GenericRenderer renderer = new GenericRenderer( xmlWriter ) ;


    final Renderable rendered = new MyRenderable(
        TreeFixture.tree( NodeKind.WORD_, "w" ) ) ;
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;

    renderer.render( rendered, outputStream ) ;

    final String rendition = new String( outputStream.toByteArray() ) ;

    Assert.assertEquals(
        "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
        "w",
        rendition
    ) ;
  }

  private static class MyRenderable implements Renderable {

    private final SyntacticTree syntacticTree ;

    private MyRenderable( SyntacticTree syntacticTree ) {
      this.syntacticTree = syntacticTree ;
    }

    public Iterable< Problem > getProblems() {
      return null;
    }

    public Charset getEncoding() {
      return Encoding.DEFAULT ;
    }

    public boolean hasProblem() {
      return false ;
    }

    public SyntacticTree getDocumentTree() {
      return syntacticTree ;
    }

    public StylesheetMap getCustomStylesheetMap() {
      return null ;
    }
  }
}
