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

import novelang.loader.ResourceName;

/**
 * @author Laurent Caillette
 */
public class TestResources {

  private TestResources() { }

  public static final String JUST_SECTIONS = "/just-sections.nlp";
  public static final String MESSY_IDENTIFIERS = "/messy-identifiers.nlp";
  public static final String SIMPLE_STRUCTURE = "/simple-structure.nlp";
  public static final String ONE_WORD = "/one-word.nlp";

  public static final String SCANNED_DIR = "/scanned" ;
  public static final String SCANNED_BOOK = SCANNED_DIR + "/book.nlb" ;
  public static final String SCANNED_FILE1 = SCANNED_DIR + "/file1.nlp" ;
  public static final String SCANNED_FILE2 = SCANNED_DIR + "/file2.nlp" ;
  public static final String SCANNED_SUBDIR = SCANNED_DIR + "/sub" ;
  public static final String SCANNED_FILE3 = SCANNED_SUBDIR + "/file3.nlp" ;

  public static final ResourceName SHOWCASE = new ResourceName( "showcase/showcase.nlp" ) ;

  public static final String SERVED_GOOD_NOEXTENSION = "/served/good";
  public static final String SERVED_GOOD = SERVED_GOOD_NOEXTENSION + ".nlp" ;

  public static final String SERVED_BROKEN_NOEXTENSION = "/served/broken";
  public static final String SERVED_BROKEN = SERVED_BROKEN_NOEXTENSION + ".nlp" ;


}
