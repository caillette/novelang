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
package novelang.novelist;

import java.util.Random;

import com.google.common.base.Preconditions;

/**
 * Generates specialized values from a {@link Random} object.
 *
 * @author Laurent Caillette
 */
public class RandomizationTools {

  public static int boundInteger(
      final Random random,
      final int minimumInclusive,
      final int maximumInclusive
  ) {
    return minimumInclusive +
        random.nextInt( maximumInclusive - minimumInclusive );
  }

  public static float percentage( final Random random ) {
    return ( float ) ( 100.0 * Math.abs( random.nextDouble() ) ) ;
  }

  public static boolean percentChances( final Random random, final float percentage ) {
    Preconditions.checkArgument( percentage >= 0.0F ) ;
    Preconditions.checkArgument( percentage < 100.0F ) ;
    return percentage( random ) < percentage ;
  }
}
