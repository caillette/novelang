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

package org.novelang.novella;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import org.novelang.common.*;
import org.novelang.common.tree.Treepath;
import org.novelang.designator.Tag;
import org.novelang.treemangling.DesignatorInterpreter;
import org.novelang.treemangling.LevelMangler;
import org.novelang.treemangling.TagMangler;
import org.novelang.treemangling.UrlMangler;
import org.novelang.treemangling.SeparatorsMangler;
import org.novelang.treemangling.EmbeddedListMangler;
import org.novelang.treemangling.TagFilter;
import org.novelang.treemangling.ListMangler;
import org.novelang.parser.antlr.DelegatingPartParser;
import org.novelang.parser.GenericParser;
import org.novelang.outfit.DefaultCharset;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Preconditions;

/**
 * A Novella loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Novella extends AbstractSourceReader {

  private final SyntacticTree tree ;
  private final File partFileDirectory ;

  private Novella( final Novella other, final SyntacticTree newTree ) {
    super( other ) ;
    this.partFileDirectory = other.partFileDirectory ;
    this.tree = newTree ;
  }

  /**
   * Only for tests.
   */
  /*package*/ Novella( final String content ) {
    tree = createTree( content ) ;
    partFileDirectory = null ;
  }

  /**
   * Only for tests.
   */
  /*package*/ Novella( final File novellaFile ) throws IOException {
    this( novellaFile, DefaultCharset.SOURCE, DefaultCharset.RENDERING ) ;
  }

  public Novella(
      final File file,
      final Charset sourceCharset,
      final Charset suggestedRenderingCharset
  ) throws IOException {
    super(
        file.getCanonicalPath(),
        sourceCharset,
        suggestedRenderingCharset,
        "novella[" + file.getName() + "]"
    ) ;
    final File canonicalFile = file.getCanonicalFile() ;
    Preconditions.checkArgument( 
        ! canonicalFile.isDirectory(),
        "Novella file cannot be a directory: %s", canonicalFile
    ) ;
    this.partFileDirectory = canonicalFile.getParentFile() ;
    tree = createTree( readContent( canonicalFile ) ) ;
  }

  @Override
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

  @Override
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
  @Override
  public SyntacticTree getDocumentTree() {
    return tree ;
  }


// ===============  
// Pseudo-mutators
// ===============  
  
  /**
   * This is just for not messing the constructor up with some marginal argument.
   */
  public Novella relocateResourcePaths( final File contentRoot ) {
    
    if( null == getDocumentTree() || null == partFileDirectory ) {
      LOGGER.warn( "Resource paths not relocated. This may be normal when running tests" ) ;
      return this ;
    }    
    
    final List< Problem > relocationProblems = Lists.newArrayList() ;
    final ProblemCollector problemCollector = new ProblemCollector() {
      @Override
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
    Novella.this.collect( relocationProblems ); ;
//    Iterators.addAll( relocationProblems, Novella.this.getProblems().iterator() ) ;

    return new Novella( this, fixedTree ) ;

  }

  public Novella makeStandalone( final Set< Tag > restrictingTags ) {
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
      return new Novella( this, withMetadata.getTreeAtStart() ) ;
    }
  }

  public Novella makeStandalone() {
    return makeStandalone( ImmutableSet.< Tag >of() ) ;
  }
}
