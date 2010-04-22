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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import novelang.common.*;
import novelang.common.tree.Treepath;
import novelang.designator.Tag;
import novelang.treemangling.DesignatorInterpreter;
import novelang.treemangling.LevelMangler;
import novelang.treemangling.TagMangler;
import novelang.treemangling.UrlMangler;
import novelang.treemangling.SeparatorsMangler;
import novelang.treemangling.EmbeddedListMangler;
import novelang.treemangling.TagFilter;
import novelang.treemangling.ListMangler;
import novelang.parser.antlr.DelegatingPartParser;
import novelang.parser.GenericParser;
import novelang.system.DefaultCharset;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;

/**
 * A Part loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Part extends AbstractSourceReader {

  private final SyntacticTree tree ;
  private final File partFileDirectory ;

  private Part( final Part other, final SyntacticTree newTree ) {
    super( other ) ;
    this.partFileDirectory = other.partFileDirectory ;
    this.tree = newTree ;
  }

  /**
   * Only for tests.
   */
  /*package*/ Part( final String content ) {
    tree = createTree( content ) ;
    partFileDirectory = null ;
  }

  /**
   * Only for tests.
   */
  /*package*/ Part( final File partFile ) throws IOException {
    this( partFile, DefaultCharset.SOURCE, DefaultCharset.RENDERING ) ;
  }

  public Part(
      final File partFile,
      final Charset sourceCharset,
      final Charset suggestedRenderingCharset
  ) throws IOException {
    super(
        partFile.getAbsolutePath(),
        sourceCharset,
        suggestedRenderingCharset,
        "part[" + partFile.getName() + "]"
    ) ;
    Preconditions.checkArgument( 
        ! partFile.isDirectory(), 
        "Part file cannot be a directory: %s", partFile
    ) ;
    this.partFileDirectory = partFile.getParentFile() ;
    tree = createTree( readContent( partFile ) ) ;
  }

  protected GenericParser createParser( final String content ) {
    return new DelegatingPartParser( content, this ) ;

  }

  private SyntacticTree createTree( final String content ) {
    final SyntacticTree rawTree = parse( content ) ;
    if( null == rawTree || hasProblem() ) {
      return null ;
    } else {
      Treepath< SyntacticTree > rehierarchized = Treepath.create( rawTree ) ;
      rehierarchized = UrlMangler.fixNamedUrls( rehierarchized ) ;
      rehierarchized = SeparatorsMangler.insertMandatoryWhitespaceNearApostrophe( rehierarchized ) ;
      rehierarchized = EmbeddedListMangler.rehierarchizeEmbeddedLists( rehierarchized ) ;
      rehierarchized = SeparatorsMangler.removeSeparators( rehierarchized ) ;      
      rehierarchized = LevelMangler.rehierarchizeLevels( rehierarchized ) ;
      rehierarchized = TagMangler.enhance( rehierarchized ) ;

      return rehierarchized.getTreeAtEnd() ;
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


// ===============  
// Pseudo-mutators
// ===============  
  
  /**
   * This is just for not messing the constructor up with some marginal argument.
   */
  public Part relocateResourcePaths( final File contentRoot ) {
    
    if( null == getDocumentTree() || null == partFileDirectory ) {
      LOG.warn( "Resource paths not relocated. This may be normal when running tests" ) ;
      return this ;
    }    
    
    final List< Problem > relocationProblems = Lists.newArrayList() ;
    final ProblemCollector problemCollector = new ProblemCollector() {
      public void collect( final Problem problem ) {
        relocationProblems.add( problem ) ;
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
    Part.this.collect( relocationProblems ); ;
//    Iterators.addAll( relocationProblems, Part.this.getProblems().iterator() ) ;

    return new Part( this, fixedTree ) ;

  }

  public Part makeStandalone( final Set< Tag > restrictingTags ) {
    Preconditions.checkNotNull( restrictingTags ) ;
    if ( null == getDocumentTree() ) {
      return this ;
    } else {
      final Treepath< SyntacticTree > unhierarchized = Treepath.create( tree ) ;
      final Treepath< SyntacticTree > rehierarchized =
          ListMangler.rehierarchizeLists( unhierarchized ) ;
      final Treepath< SyntacticTree > enrichedWithDesignators =
          new DesignatorInterpreter( rehierarchized ).getEnrichedTreepath() ;
      final Set< Tag > tagset = TagMangler.findExplicitTags( tree ) ;
      final Treepath< SyntacticTree > tagsFiltered = 
          TagFilter.filter( enrichedWithDesignators, restrictingTags ) ;
      final Treepath< SyntacticTree > tagsPromoted = 
          TagMangler.promote( tagsFiltered, tagset ) ;
      final Treepath< SyntacticTree > withMetadata = Treepath.create(
          addMetadata( tagsPromoted.getTreeAtEnd(), tagset ) ) ;
      return new Part( this, withMetadata.getTreeAtStart() ) ;
    }
  }

  public Part makeStandalone() {
    return makeStandalone( ImmutableSet.< Tag >of() ) ;
  }
}
