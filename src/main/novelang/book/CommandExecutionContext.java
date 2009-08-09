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
import java.util.Map;
import java.util.ArrayList;
import java.nio.charset.Charset;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import novelang.common.StylesheetMap;
import novelang.common.SyntacticTree;
import novelang.common.Problem;
import novelang.loader.ResourceName;
import novelang.rendering.RenditionMimeType;
import novelang.system.DefaultCharset;

/**
 * Contains all input and output for {@link novelang.book.function.AbstractFunctionCall} evaluation.
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
  private final StylesheetMap stylesheetMap ;
  private final SyntacticTree bookTree ;
  private final Iterable< Problem > problems ;
  
  private static final Iterable< Problem > NO_PROBLEM = ImmutableList.of() ;
  

  private CommandExecutionContext( CommandExecutionContext other ) {
    this( other, other.bookTree, other.problems ) ;
  }
  
  private CommandExecutionContext( 
      final CommandExecutionContext other, 
      final SyntacticTree alternateBookTree,
      final Iterable< Problem > moreProblems
  ) {
    this.baseDirectory = other.baseDirectory ;
    this.bookDirectory = other.bookDirectory ;
    this.sourceCharset = other.sourceCharset ;
    this.renderingCharset = other.renderingCharset ;
    this.mappedStylesheets = Maps.newHashMap( other.mappedStylesheets ) ;
    this.stylesheetMap = new StylesheetMap() {
      public ResourceName get( RenditionMimeType renditionMimeType ) {
        return mappedStylesheets.get( renditionMimeType ) ;
      }
    } ;
    this.bookTree = alternateBookTree ;
    if ( moreProblems == other.getProblems() ) {
      // Sure it's unmodifiable.
      this.problems = other.getProblems() ;
    } else {
      this.problems = Iterables.concat( getProblems(), moreProblems ) ;
    }
  }

  public CommandExecutionContext( final File baseDirectory ) {
    this( baseDirectory, baseDirectory ) ;
  }

  public CommandExecutionContext( File baseDirectory, File bookDirectory ) {
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

  public SyntacticTree getBookTree() {
    return bookTree;
  }

  public Iterable< Problem > getProblems() {
    return problems ;
  }

  public CommandExecutionContext map( 
      final RenditionMimeType renditionMimeType, 
      final String stylesheetPath 
  ) {
    final CommandExecutionContext newEnvironment = new CommandExecutionContext( this ) ;
    newEnvironment.mappedStylesheets.put(
        Preconditions.checkNotNull( renditionMimeType ), new ResourceName( stylesheetPath ) ) ;
    return newEnvironment ;
  }
  
  public StylesheetMap getCustomStylesheets() {
    return stylesheetMap ;
  }

  public CommandExecutionContext update( final SyntacticTree bookTree ) {
    return new CommandExecutionContext( this, bookTree, getProblems() ) ;
  }

  public CommandExecutionContext update( final Iterable< Problem > problems ) {
    return new CommandExecutionContext( this, getBookTree(), problems ) ;
  }
}
