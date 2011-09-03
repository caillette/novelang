/*
 * Copyright (C) 2011 Laurent Caillette
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

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.novelang.common.Problem;

/**
 * Useful factory methods for tests using {@link Novella}.
 * 
 * @author Laurent Caillette
 */
public class NovellaFixture {

  private NovellaFixture() { }

  public static Novella createStandaloneNovella( final String content ) {
    return new Novella( content ).makeStandalone() ;
  }
  
  public static List< Problem > extractProblems( final Novella novella )
  {
    final List< Problem > problems = Lists.newArrayList() ;
    Assert.assertTrue( novella.hasProblem() ) ;
    Iterables.addAll( problems, novella.getProblems() ) ;
    return problems ;
  }

  public static Novella create( final String content ) {
    return new Novella( content ) ;
  }
}
