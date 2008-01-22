/*
 * Copyright (C) 2006 Laurent Caillette
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
package novelang.parser.implementation;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import novelang.parser.PartParserFactory;
import novelang.parser.PartParser;
import novelang.parser.antlr.AntlrPartLexer;
import novelang.parser.antlr.AntlrPartParser;

/**
 * @author Laurent Caillette
 */
public class DefaultPartParserFactory implements PartParserFactory {

  public PartParser createParser( final String text ) {

    return new PartParser() {

      private final CharStream stream = new ANTLRStringStream( text ) ;
      private final AntlrPartLexer lexer = new AntlrPartLexer( stream ) ;
      private final CommonTokenStream tokens = new CommonTokenStream( lexer ) ;
      private final AntlrPartParser parser = new AntlrPartParser( tokens ) ;

      public boolean hasProblem() {
        return ! parser.getExceptions().isEmpty() ;
      }

      public Iterable<RecognitionException> getProblems() {
        return parser.getExceptions() ;
      }

      public void parse() throws RecognitionException {
        parser.document() ;
      }

    } ;
  }
}
