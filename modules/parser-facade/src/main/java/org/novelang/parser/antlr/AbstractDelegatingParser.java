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

package org.novelang.parser.antlr;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonErrorNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.novelang.common.Problem;
import org.novelang.common.SyntacticTree;
import org.antlr.runtime.tree.Tree;
//import NovelangLexer;
//import AllTokens;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractDelegatingParser {

  protected final NovelangParser antlrParser;
  private final GrammarDelegate delegate;

  public AbstractDelegatingParser( final String text, final GrammarDelegate delegate ) {
    this.delegate = delegate ;
    final CharStream stream = new ANTLRStringStream( text );
    final NovelangLexer lexer = new NovelangLexer( stream );
    lexer.setProblemDelegate( delegate ) ;
    final CommonTokenStream tokens = new CommonTokenStream( lexer );
    antlrParser = new NovelangParser( tokens ) ;
    final CustomTreeAdaptor treeAdaptor = new CustomTreeAdaptor( delegate.getLocationFactory() );
    delegate.setAdaptor( treeAdaptor ) ;
    delegate.setTokenNames( antlrParser.tokenNames ) ;
    antlrParser.setTreeAdaptor( treeAdaptor ) ;
    antlrParser.setParserDelegate( delegate ) ;
  }

  public boolean hasProblem() {
    return delegate.getProblems().iterator().hasNext() ;
  }

  public Iterable< Problem > getProblems() {
    final List< Problem > problems = Lists.newArrayList() ;
    Iterables.addAll( problems, delegate.getProblems() ) ;
    return ImmutableList.copyOf( problems ) ;
  }

  /**
   * Calls specific parsing method and returns the raw result.
   *
   * @return a non-null instance of the parsing result object (the one containing the
   *     {@link org.antlr.runtime.tree.Tree} object that is a {@link CommonErrorNode}
   *     or a {@link SyntacticTree}.
   */
  protected abstract Object callParserMethod() throws RecognitionException ;

  public final SyntacticTree parse() {
    final Object tree;
    try {
      tree = callParserMethod();
    } catch( RecognitionException e ) {
      getDelegate().report( e ) ;
      return null ;
    }
    

    final SyntacticTree result ;
    if( tree instanceof CommonErrorNode ) {
      getDelegate().report( ( ( CommonErrorNode ) tree ).trappedException ) ;
      result = null ;
    } else {
      result = TreeConverter.convert( ( Tree ) tree, getDelegate() ) ;
//      Statistics.logStatistics( result ) ;
    }
    return result ;
  }


  public GrammarDelegate getDelegate() {
    return delegate ;
  }
}
