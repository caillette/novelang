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
package org.novelang.nhovestone;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import org.novelang.Version;
import org.novelang.VersionFormatException;

/**
 * Takes some clutter out from {@link Nhovestone}.
 *
 * @author Laurent Caillette
 */
public class NhovestoneTools {

  private static final Pattern COMMASEPARATEDVERSIONSSPLIT_PATTERN = Pattern.compile( "," );

  public static Iterable<Version> parseVersions( final String commaSeparatedVersions )
      throws VersionFormatException
  {
    final String[] versionsAsStrings =
        COMMASEPARATEDVERSIONSSPLIT_PATTERN.split( commaSeparatedVersions ) ;
    final ImmutableList.Builder< Version > versionsBuilder =
        new ImmutableList.Builder< Version >() ;
    for( final String versionsAsString : versionsAsStrings ) {
      versionsBuilder.add( Version.parse( versionsAsString ) ) ;
    }
    return versionsBuilder.build() ;
  }
}
