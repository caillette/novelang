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

import novelang.common.Renderable;
import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.common.StylesheetMap;
import com.google.common.collect.ImmutableList;

/**
 * @author Laurent Caillette
 */
public class RenderingTools {

  /**
   * Produces a text-only version of some {@code SyntacticTree}.
   */
  public static String textualize( final SyntacticTree tree, final Charset charset )
      throws Exception
  {

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
    new GenericRenderer( new PlainTextWriter( charset ) ).render(
        new RenderableTree( tree, charset ),
        byteArrayOutputStream
    ) ;

    return new String( byteArrayOutputStream.toByteArray(), charset.name() ) ;
  }

  public static class RenderableTree implements Renderable {
    private final SyntacticTree tree ;
    private final Charset charset ;

    public RenderableTree( SyntacticTree tree, Charset charset ) {
      this.tree = tree ;
      this.charset = charset ;
    }

    public Iterable<Problem> getProblems() {
      return ImmutableList.of() ;
    }

    public Charset getRenderingCharset() {
      return charset;
    }

    public boolean hasProblem() {
      return false ;
    }

    public SyntacticTree getDocumentTree() {
      return tree;
    }

    public StylesheetMap getCustomStylesheetMap() {
      return null ;
    }
  }
}