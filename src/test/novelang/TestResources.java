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

import org.apache.fop.apps.FopFactory;
import com.google.common.collect.Iterables;
import novelang.configuration.ContentConfiguration;
import novelang.configuration.FontDescriptor;
import novelang.configuration.RenderingConfiguration;
import novelang.configuration.ServerConfiguration;
import novelang.configuration.ConfigurationTools;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceName;
import novelang.loader.ResourceLoaderTools;

/**
 * The only place where constants referencing test-dedicated resources can be defined.
 *
 * @author Laurent Caillette
 */
public class TestResources {

  private TestResources() { }

  public static final String JUST_SECTIONS = "/just-sections.nlp";
  public static final String MESSY_IDENTIFIERS = "/messy-identifiers.nlp";
  public static final String SIMPLE_STRUCTURE = "/simple-structure.nlp";
  public static final String ONE_WORD = "/one-word.nlp";
  public static final String BROKEN_CANNOTPARSE = "/broken/broken-cannotparse.nlp";


  public static final String SCANNED_DIR = "/scanned" ;
  public static final String SCANNED_BOOK = SCANNED_DIR + "/book.nlb" ;
  public static final String SCANNED_FILE1 = SCANNED_DIR + "/file1.nlp" ;
  public static final String SCANNED_FILE2 = SCANNED_DIR + "/file2.nlp" ;
  public static final String SCANNED_SUBDIR = SCANNED_DIR + "/sub" ;
  public static final String SCANNED_FILE3 = SCANNED_SUBDIR + "/file3.nlp" ;

  public static final String NODESET_DIR = "/nodeset" ;
  public static final ResourceName NODESET_XSL = new ResourceName( "nodeset.xsl" ) ;
  public static final String NODESET_SOMECHAPTERS_DOCUMENTNAME = "some-chapters" ;
  public static final String NODESET_SOMECHAPTERS =
      NODESET_DIR + "/" + NODESET_SOMECHAPTERS_DOCUMENTNAME + ".nlp" ;

  public static final ResourceName SHOWCASE = new ResourceName( "showcase/showcase.nlp" ) ;


  public static final String SERVED_DIRECTORY_NAME = "served" ;
  public static final String SERVED_PART_GOOD_NOEXTENSION = "/" + SERVED_DIRECTORY_NAME + "/good";
  public static final String SERVED_PARTSOURCE_GOOD = SERVED_PART_GOOD_NOEXTENSION + ".nlp" ;

  public static final ResourceName SERVED_VOIDSTYLESHEET =
      new ResourceName( SERVED_DIRECTORY_NAME + "void.xsl" ) ;

  public static final String SERVED_PART_BROKEN_NOEXTENSION =
      "/" + SERVED_DIRECTORY_NAME + "/broken" ;

  public static final String SERVED_PARTSOURCE_BROKEN = SERVED_PART_BROKEN_NOEXTENSION + ".nlp" ;

  public static final String SERVED_BOOK_ALTERNATESTYLESHEET_NOEXTENSION =
      "/" + SERVED_DIRECTORY_NAME + "/book-alternatexsl" ;

  public static final String SERVED_BOOK_ALTERNATESTYLESHEET =
      SERVED_BOOK_ALTERNATESTYLESHEET_NOEXTENSION + ".nlb" ;

  public static final String SERVED_BOOK_BADSCANNEDPART_NOEXTENSION =
      "/" + SERVED_DIRECTORY_NAME + "/book-bad-scanned-part" ;

  public static final String SERVED_BOOK_BADSCANNEDPART =
      SERVED_BOOK_BADSCANNEDPART_NOEXTENSION + ".nlb" ;


  public static void copyServedResources( File contentDirectory ) {
    TestResourceTools.copyResourceToFile(
        TestResources.class, SERVED_PARTSOURCE_GOOD, contentDirectory ) ;

    TestResourceTools.copyResourceToFile(
        TestResources.class, SERVED_PARTSOURCE_BROKEN, contentDirectory ) ;

    TestResourceTools.copyResourceToFile(
        TestResources.class, SERVED_BOOK_ALTERNATESTYLESHEET, contentDirectory ) ;

  }

  public static ServerConfiguration createServerConfiguration(
      final File contentDirectory,
      final String styleDirectoryName,
      final boolean shouldAddClasspathResourceLoader
  ) {
    final ResourceLoader resourceLoader ;
    final ClasspathResourceLoader customResourceLoader =
        new ClasspathResourceLoader( styleDirectoryName ) ;
    if( shouldAddClasspathResourceLoader ) {
      resourceLoader = ResourceLoaderTools.compose(
          customResourceLoader,
          new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR )
      ) ;
    } else {
      resourceLoader = customResourceLoader ;
    }

    return new ServerConfiguration() {

      public RenderingConfiguration getRenderingConfiguration() {
        return new RenderingConfiguration() {
          public ResourceLoader getResourceLoader() {
            return resourceLoader ;
          }
          public FopFactory getFopFactory() {
            return FopFactory.newInstance() ;
          }

          public Iterable< FontDescriptor > getFontDescriptors() {
            return Iterables.emptyIterable() ;
          }
        } ;
      }

      public ContentConfiguration getContentConfiguration() {
        return new ContentConfiguration() {
          public File getContentRoot() {
            return contentDirectory;
          }
        } ;
      }
    } ;

  }


  public static ServerConfiguration createServerConfiguration(
      final File contentDirectory,
      final String styleDirectoryName
  ) {
    return createServerConfiguration(
        contentDirectory,
        styleDirectoryName,
        false
    );
  }
}
