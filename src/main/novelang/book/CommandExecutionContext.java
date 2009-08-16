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
package novelang.book;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import novelang.common.Problem;
import novelang.common.StylesheetMap;
import novelang.common.SyntacticTree;
import novelang.loader.ResourceName;
import novelang.rendering.RenditionMimeType;
import novelang.system.DefaultCharset;

/**
 * Contains all input and output for {@link novelang.book.function.Command} evaluation.
 * If some functions need to communicate by changing some shared value (like the map of 
 * the stylesheets, or Book's tree) this happen through this class and this class only.
 * 
 *  
 * @author Laurent Caillette
 */
public final class CommandExecutionContext {

  private final File baseDirectory ;
  private final File bookDirectory ;
  private final Charset sourceCharset ;
  private final Charset renderingCharset ;
  private final Map< RenditionMimeType, ResourceName > mappedStylesheets ;

  /**
   * Kind of shortcut on {@link #mappedStylesheets}.
   */
  private final StylesheetMap stylesheetMap ;

  private final SyntacticTree bookTree ;
  private final Iterable< Problem > problems ;
  
  private static final Iterable< Problem > NO_PROBLEM = ImmutableList.of() ;
  

  private CommandExecutionContext( 
      final CommandExecutionContext other, 
      final SyntacticTree alternateBookTree,
      final Iterable< Problem > moreProblems,
      final Map< RenditionMimeType, ResourceName > moreStylesheetMappings
  ) {
    this.baseDirectory = other.baseDirectory ;
    this.bookDirectory = other.bookDirectory ;
    this.sourceCharset = other.sourceCharset ;
    this.renderingCharset = other.renderingCharset ;

    final Map< RenditionMimeType, ResourceName > newMap = Maps.newHashMap() ;
    newMap.putAll( other.mappedStylesheets ) ;
    newMap.putAll( moreStylesheetMappings ) ;
    this.mappedStylesheets = ImmutableMap.copyOf( newMap ) ;
    this.stylesheetMap = new StylesheetMap() {
      public ResourceName get( RenditionMimeType renditionMimeType ) {
        return mappedStylesheets.get( renditionMimeType ) ;
      }
    } ;

    this.bookTree = alternateBookTree ;
    if ( moreProblems == other.getProblems() ) {
      // We can do that because we know it's unmodifiable.
      this.problems = other.getProblems() ;
    } else {
      // The concat method itself proxies the iterators, making debugging hard.
      this.problems = ImmutableList.copyOf( 
          Iterables.concat( other.getProblems(), moreProblems ) ) ;
    }
  }


  public CommandExecutionContext( final File baseDirectory ) {
    this( baseDirectory, baseDirectory ) ;
  }


  public CommandExecutionContext( final File baseDirectory, final File bookDirectory ) {
    this.baseDirectory = Preconditions.checkNotNull( baseDirectory ) ;
    this.bookDirectory = Preconditions.checkNotNull( bookDirectory ) ;
    this.sourceCharset = DefaultCharset.SOURCE ;
    this.renderingCharset = DefaultCharset.RENDERING ;
    this.mappedStylesheets = Maps.newHashMap() ;
    this.stylesheetMap = StylesheetMap.EMPTY_MAP ;
    this.bookTree = null ;
    this.problems = NO_PROBLEM ;
  }


  public File getBaseDirectory() {
    return baseDirectory;
  }


  public File getBookDirectory() {
    return bookDirectory;
  }


  public Charset getSourceCharset() {
    return sourceCharset ;
  }


  public Charset getRenderingCharset() {
    return renderingCharset ;
  }


  public SyntacticTree getDocumentTree() {
    return bookTree;
  }


  public Iterable< Problem > getProblems() {
    return problems ;
  }


  public CommandExecutionContext addMappings(
      final Map< RenditionMimeType, ResourceName > moreStylesheetMappings
  ) throws DuplicateStylesheetMappingException {

    for( final RenditionMimeType key : moreStylesheetMappings.keySet() ) {
      final ResourceName resourceName = moreStylesheetMappings.get( key );
      if( mappedStylesheets.containsKey( key ) ) {
        throw new DuplicateStylesheetMappingException(
            key,
            mappedStylesheets.get( key ),
            resourceName
        ) ;
      }
    }

    return new CommandExecutionContext(
        this,
        this.getDocumentTree(),
        this.getProblems(),
        moreStylesheetMappings
    ) ;
  }

  
  public StylesheetMap getCustomStylesheets() {
    return stylesheetMap ;
  }


  public CommandExecutionContext update( final SyntacticTree bookTree ) {
    return new CommandExecutionContext( this, bookTree, getProblems(), mappedStylesheets ) ;
  }


  public CommandExecutionContext addProblems( final Iterable< Problem > problems ) {
    return new CommandExecutionContext( this, getDocumentTree(), problems, mappedStylesheets ) ;
  }


  public CommandExecutionContext addProblem( final Problem problem ) {
    return new CommandExecutionContext(
        this,
        getDocumentTree(),
        ImmutableList.of( problem ),
        mappedStylesheets
    ) ;
  }


  public static class DuplicateStylesheetMappingException extends Exception {
    public DuplicateStylesheetMappingException(
        final RenditionMimeType renditionMimeType,
        final ResourceName previousStylesheetpath,
        final ResourceName newStylesheetpath
    ) {
      super(
          "Already mapping " + renditionMimeType + " to " + previousStylesheetpath +
          "; won't replace by " + newStylesheetpath
      ) ;
    }
  }
}