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
import java.nio.charset.Charset;
import java.util.List;

import novelang.common.*;
import novelang.common.tree.Treepath;
import novelang.hierarchy.Hierarchizer;
import novelang.parser.antlr.DefaultPartParserFactory;
import novelang.system.DefaultCharset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * A Part loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Part extends AbstractSourceReader {

  private final SyntacticTree tree ;
  private final boolean standalone ;
  private final File partFileDirectory ;


  /**
   * Only for tests.
   */
  public Part( String content ) {
    this( content, false ) ; // Would be logical to make it true but would pollute the tests.
  }

  /**
   * Only for tests.
   */
  public Part( String content, boolean standalone ) {
    this.standalone = standalone ; 
    tree = createTree( content ) ;
    partFileDirectory = null ;
  }

  /**
   * Only for tests.
   */
  public Part( final File partFile ) throws MalformedURLException {
    this( partFile, DefaultCharset.SOURCE, DefaultCharset.RENDERING, false ) ;
  }

  /**
   * Only for tests.
   */
  public Part( final File partFile, boolean standalone ) throws MalformedURLException {
    this( partFile, DefaultCharset.SOURCE, DefaultCharset.RENDERING, standalone ) ;
  }

  public Part(
      final File partFile,
      Charset sourceCharset,
      Charset suggestedRenderingCharset,
      boolean standalone
  ) throws MalformedURLException {
    super(
        partFile.toURI().toURL(),
        sourceCharset,
        suggestedRenderingCharset,
        "part[" + partFile.getName() + "]"
    ) ;
    this.standalone = standalone ;
    this.partFileDirectory = partFile.getParentFile() ;
    tree = createTree( readContent( partFile.toURI().toURL() ) ) ;
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


  /**
   * This is just for 
   * @param contentRoot
   * @return
   */
  public Renderable relocateResourcePaths( File contentRoot ) {
    
    if( null == getDocumentTree() || null == partFileDirectory ) {
      LOGGER.warn( "Resource paths not relocated. This may be normal when running tests" ) ;
      return this ;
    }    
    
    final List< Problem > problems = Lists.newArrayList() ;
    final ProblemCollector problemCollector = new ProblemCollector() {
      public void collect( Problem problem ) {
        problems.add( problem ) ;
      }
    } ;   
    
    final SyntacticTree fixedTree ;
    if( null == getDocumentTree() ) {
      fixedTree = null ;
    } else {
      fixedTree = new ImageFixer(
          contentRoot, 
          partFileDirectory, 
          problemCollector 
      ).relocateResources( getDocumentTree() ) ;
    }
    Iterators.addAll( problems, Part.this.getProblems().iterator() ) ;
    
    return new Renderable() {
      
      public Iterable< Problem > getProblems() {
        return ImmutableList.copyOf( problems ) ;
      }

      public Charset getRenderingCharset() {
        return Part.this.getRenderingCharset() ;
      }

      public boolean hasProblem() {
        return problems.size() > 0 ;
      }

      public SyntacticTree getDocumentTree() {
        return fixedTree ;
      }

      public StylesheetMap getCustomStylesheetMap() {
        return Part.this.getCustomStylesheetMap() ;
      }
    };
  }

}
