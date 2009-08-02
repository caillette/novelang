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
package novelang.rendering.xslt;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.ReadableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.Version;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Contains functions to be called from XSLT.
 * 
 * @author Laurent Caillette
 */
public class Versioning {

  private static final Log LOG = LogFactory.getLog( Versioning.class ) ;

  public static String versionName() {
    LOG.debug( "Called versionName()" ) ;
    return Version.name() ;
  }


}