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
package novelang;

import java.io.File;

import com.google.common.base.Preconditions;
import novelang.outfit.shell.JavaClasses;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Laurent Caillette
 */
public class KnownVersions {

//  private static final Version VERSION_0_44_MAX = parse( "0.44.999" ) ;

  /**
   * Directory layout changing to "Novelang-distribution-0.44+/lib/Novelang-bootstrap-0.44+.jar".
   */
  public static final Version VERSION_0_44_0 = parse( "0.44.0" ) ;
  
  public static final Version VERSION_0_41_0 = parse( "0.41.0" ) ;
  public static final Version VERSION_0_38_1 = parse( "0.38.1" ) ;

  /**
   * First version supporting --content-root option.
   */
  public static final Version VERSION_0_35_0 = parse( "0.35.0" ) ;

  private static Version parse( final String versionAsString ) {
    try {
      return Version.parse( versionAsString ) ;
    } catch( VersionFormatException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  /**
   * Returns the path to the bootstrapping jar, relative to application installation directory.
   * @param installationsDirectory a non-null object representing a directory.
   * @param version a non-null object.
   * @return a valid path, without the leading file separator.
   *
   * @deprecated use {@link #asJavaClasses(java.io.File, Version)}.
   */
  public static String getAbsoluteJarPath(
      final File installationsDirectory,
      final Version version
  ) {
    Preconditions.checkNotNull( version ) ;
    if( Version.COMPARATOR.compare( VERSION_0_44_0, version ) <= 0 ) {
//      if( Version.COMPARATOR.compare( VERSION_0_44_MAX, version ) <= 0 ) {
        return
            FilenameUtils.normalizeNoEndSeparator( installationsDirectory.getAbsolutePath() ) +
            File.separator +
            "Novelang-distribution-" + version.getName() + File.separator +
            "lib" + File.separator +
            "Novelang-bootstrap-" + version.getName() + ".jar"
        ;
//      } else {
//        return a path with "launcher" instead of "bootstrap"
//      }
    } else {
      return
          FilenameUtils.normalizeNoEndSeparator( installationsDirectory.getAbsolutePath() ) +
          File.separator +
          "Novelang-" + version.getName() + File.separator +
          "Novelang-" + version.getName() + ".jar"
      ;
    }
  }

  /**
   * Returns the path to the bootstrapping jar, relative to application installation directory.
   * @param installationsDirectory a non-null object representing a directory.
   * @param version a non-null object.
   * @return a valid path, without the leading file separator.
   */
  public static JavaClasses asJavaClasses(
      final File installationsDirectory,
      final Version version
  ) {
    Preconditions.checkNotNull( version ) ;
    if( Version.COMPARATOR.compare( VERSION_0_44_0, version ) <= 0 ) {
        return new JavaClasses.SingleJar( new File(
            FilenameUtils.normalizeNoEndSeparator( installationsDirectory.getAbsolutePath() ) +
            File.separator +
            "Novelang-distribution-" + version.getName() + File.separator +
            "lib" + File.separator +
            "Novelang-bootstrap-" + version.getName() + ".jar"
        ) ) ;
    } else {
      return new JavaClasses.SingleJar( new File(
          FilenameUtils.normalizeNoEndSeparator( installationsDirectory.getAbsolutePath() ) +
          File.separator +
          "Novelang-" + version.getName() + File.separator +
          "Novelang-" + version.getName() + ".jar"
      ) ) ;
    }
  }

}
