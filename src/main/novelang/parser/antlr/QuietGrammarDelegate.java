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

package novelang.parser.antlr;

import novelang.model.structural.StructuralBook;
import novelang.model.structural.StructuralChapter;
import novelang.model.common.Problem;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import novelang.model.implementation.Part;

/**
 * A delegate to be instantiated by default inside the {@code NovelangParser} allowing it
 * to run from AntlrWorks.
 *
 * @author Laurent Caillette
 */
public class QuietGrammarDelegate extends BookGrammarDelegate {

  public QuietGrammarDelegate() {
    super( new NullStructuralBook() ) ;
  }

  private static class NullStructuralBook implements StructuralBook {

    public Iterable< Problem > getProblems() {
      return null ;
    }

    public Part createPart( String partFileName, Location location ) {
      return null ;
    }

    public StructuralChapter createChapter( Location location ) {
      return null;
    }

    public void setIdentifier( Tree identifier ) { }

    public Location createLocation( int line, int column ) {
      return new Location( "(No location)", line, column ) ;
    }
  }

}
