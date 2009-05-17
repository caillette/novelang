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
package novelang.parser.antlr.delimited;

import java.util.List;

import novelang.common.Problem;
import novelang.common.LocationFactory;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;

/**
 * @author Laurent Caillette
 */
public class BlockDelimiterTools {

  private BlockDelimiterTools() {
    throw new Error() ;
  }

  public static Iterable< Problem > createProblems(
      LocationFactory locationFactory,
      DefaultBlockDelimitersBoundary blockDelimitersBoundary
  ) {
    return createProblems(
        locationFactory,
        blockDelimitersBoundary.getFaultyDelimitedBlocks()
    ) ;
  }


  public static Iterable< Problem > createProblems(
      LocationFactory locationFactory,
      Iterable< DelimitedBlockStatus > statuses
  ) {
    final List< Problem > problems = Lists.newArrayList() ;
    for( DelimitedBlockStatus status : statuses ) {
      final Problem problem = Problem.createProblem(
          "Missing delimiter. " +
              "For '" + status.getBlockDelimiter().getStart() + "' there should be a matching " +
              createDelimiterEndMessage( status.getBlockDelimiter().getEnd() )
          ,
          locationFactory,
          status.getLine(),
          status.getColumn()
      ) ;
      problems.add( problem ) ;
    }

    return ImmutableList.copyOf( problems ) ;
  }

  private static String createDelimiterEndMessage( String[] ends) {
    return "'" + Joiner.on( "' or '" ).join( ends ) + "'" ;
  }

}
