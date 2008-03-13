/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.parser.antlr;

import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ANTLRStringStream;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractDelegatingParser< T extends GrammarDelegate > {

  private final AntlrPartParser parser ;
  private final T delegate;

  public AbstractDelegatingParser( String text, T delegate ) {
    this.delegate = delegate ;
    CharStream stream = new ANTLRStringStream( text );
    AntlrPartLexer lexer = new AntlrPartLexer( stream );
    CommonTokenStream tokens = new CommonTokenStream( lexer );
    parser = new AntlrPartParser( tokens ) ;
    parser.setTreeAdaptor( new CustomTreeAdaptor( delegate.getLocationFactory() ) ) ;
    parser.setGrammarDelegate( delegate ) ;
  }

  public boolean hasProblem() {
    return delegate.getProblems().iterator().hasNext() ;
  }

  public Iterable< Problem > getProblems() {
    final List< Problem > problems = Lists.newArrayList() ;
    Iterables.addAll( problems, delegate.getProblems() ) ;
    return Lists.immutableList( problems ) ;
  }

  public abstract Tree parse() throws RecognitionException ;

  protected AntlrPartParser getAntlrParser() {
    return parser ;
  }

  public T getDelegate() {
    return delegate ;
  }
}
