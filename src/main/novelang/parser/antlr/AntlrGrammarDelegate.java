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

import novelang.model.common.LocationFactory;
import novelang.model.common.Problem;
import novelang.parser.implementation.GrammarDelegate;
import com.google.common.collect.Lists;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public class AntlrGrammarDelegate implements GrammarDelegate {

  private final LocationFactory locationFactory ;
  private final List< Problem > problems = Lists.newArrayList() ;

  public AntlrGrammarDelegate( LocationFactory locationFactory ) {
    this.locationFactory = locationFactory;
  }

  public void report( String antlrMessage ) {
    problems.add( Problem.createProblem( locationFactory, antlrMessage ) ) ;
  }

  public Iterable< Problem > getProblems() {
    return Lists.immutableList( problems ) ;
  }



}
