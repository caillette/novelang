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

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import novelang.parser.antlr.AntlrBookLexer;
import novelang.parser.antlr.AntlrBookParser;
import novelang.parser.antlr.BookGrammarDelegate;
import novelang.parser.BookParser;
import novelang.parser.BookParserFactory;
import novelang.model.structural.StructuralBook;
import novelang.model.common.Tree;
import novelang.model.common.Problem;

/**
 * @author Laurent Caillette
 */
public class DefaultBookParserFactory implements BookParserFactory {

  public BookParser createParser( final StructuralBook book, final String text ) {

    return new BookParser() {

      private final CharStream stream = new ANTLRStringStream( text ) ;
      private final AntlrBookLexer lexer = new AntlrBookLexer( stream ) ;
      private final CommonTokenStream tokens = new CommonTokenStream( lexer ) ;
      private final AntlrBookParser parser = new AntlrBookParser( tokens ) ;
      private final BookGrammarDelegate delegate = new BookGrammarDelegate( book ) ;

      {
        parser.setDelegate( delegate ) ;
        parser.setTreeAdaptor( new CustomTreeAdaptor( book ) ) ;
      }
      
      public boolean hasProblem() {
        return delegate.getProblems().iterator().hasNext();
      }

      public Iterable< Problem > getProblems() {
        return delegate.getProblems();
      }

      public Tree parse() throws RecognitionException {
        return ( Tree ) parser.structure().getTree() ;
      }

    };
  }
}
