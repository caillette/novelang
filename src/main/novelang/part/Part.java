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

package novelang.part;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import novelang.common.AbstractSourceReader;
import novelang.common.StylesheetMap;
import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;
import novelang.hierarchy.Hierarchizer;
import novelang.system.DefaultCharset;
import novelang.parser.antlr.DefaultPartParserFactory;

/**
 * A Part loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Part extends AbstractSourceReader {

  private final SyntacticTree tree ;
  private final boolean standalone;


  public Part( String content ) {
    this( content, false ) ; // Would be logical to make it true but would pollute the tests.
  }

  public Part( String content, boolean standalone ) {
    this.standalone = standalone ; 
    tree = createTree( content ) ;
  }

  private SyntacticTree createTree( String content ) {
    final SyntacticTree rawTree = parse( new DefaultPartParserFactory(), content ) ;
    if( null == rawTree || hasProblem() ) {
      return null ;
    } else {
      final Treepath< SyntacticTree > rehierarchized1 =
          Hierarchizer.rehierarchizeLevels( Treepath.create( rawTree ) ) ;
      if( standalone ) {
        return addMetadata( Hierarchizer.rehierarchizeLists( rehierarchized1 ).getTreeAtEnd() ) ;
      } else {
        return rehierarchized1.getTreeAtEnd() ;
      }
    }
  }

  public Part( final File partFile ) throws MalformedURLException {
    this( partFile, false ) ;
  }

  public Part( final File partFile, boolean standalone ) throws MalformedURLException {
    this(
        partFile.toURI().toURL(),
        DefaultCharset.SOURCE,
        "part[" + partFile.getName() + "]",
        standalone
    ) ;

  }

  protected Part(
      URL partUrl,
      Charset charset,
      String thisToString,
      boolean standalone
  ) {
    super( partUrl, charset, thisToString ) ;
    this.standalone = standalone ;
    tree = createTree( readContent( partUrl, charset ) ) ;
  }

  public StylesheetMap getCustomStylesheetMap() {
    return StylesheetMap.EMPTY_MAP ;
  }

  // ==============
// Content access
// ==============

  /**
   * Returns result of parsing, may be null if it failed.
   * @return a possibly null object.
   */
  public SyntacticTree getDocumentTree() {
    return tree ;
  }




}
