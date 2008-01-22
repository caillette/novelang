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

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import novelang.parser.antlr.AntlrStructureLexer;
import novelang.parser.antlr.AntlrStructureParser;
import novelang.parser.StructureParser;
import novelang.parser.StructureParserFactory;

/**
 * @author Laurent Caillette
 */
public class DefaultStructureParserFactory implements StructureParserFactory {

  public StructureParser createParser( final String text ) {

    return new StructureParser() {

      private final CharStream stream = new ANTLRStringStream( text ) ;
      private final AntlrStructureLexer lexer = new AntlrStructureLexer( stream ) ;
      private final CommonTokenStream tokens = new CommonTokenStream( lexer ) ;
      private final AntlrStructureParser parser = new AntlrStructureParser( tokens ) ;

      public boolean hasProblem() {
        return ! parser.getSupport().getExceptions().iterator().hasNext() ;
      }

      public Iterable< RecognitionException > getProblems() {
        return parser.getSupport().getExceptions() ;
      }

      public void parse() throws RecognitionException {
        parser.structure() ;
      }

    } ;
  }
}
