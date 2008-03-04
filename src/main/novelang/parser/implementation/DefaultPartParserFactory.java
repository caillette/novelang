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
package novelang.parser.implementation;

import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import novelang.parser.PartParserFactory;
import novelang.parser.PartParser;
import novelang.parser.antlr.AntlrPartLexer;
import novelang.parser.antlr.AntlrPartParser;
import novelang.model.common.Tree;
import novelang.model.common.LocationFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Laurent Caillette
 */
public class DefaultPartParserFactory implements PartParserFactory {

  public PartParser createParser( final LocationFactory locationFactory, final String text ) {

    return new PartParser() {

      private final CharStream stream = new ANTLRStringStream( text ) ;
      private final AntlrPartLexer lexer = new AntlrPartLexer( stream ) ;
      private final CommonTokenStream tokens = new CommonTokenStream( lexer ) ;
      private final AntlrPartParser parser = new AntlrPartParser( tokens ) ;

      {
        parser.setTreeAdaptor( new CustomTreeAdaptor( locationFactory ) ) ;
      }

      public boolean hasProblem() {
        return ! parser.getProblems().iterator().hasNext() ;
      }

      public Iterable< Exception > getProblems() {
        final List< Exception > problems = Lists.newArrayList() ;
        Iterables.addAll( problems, lexer.getProblems() ) ;
        Iterables.addAll( problems, parser.getProblems() ) ;
        return Lists.immutableList( problems ) ;
      }

      public Tree parse() throws RecognitionException {
        return ( Tree ) parser.part().getTree() ;
      }

    } ;
  }

}
