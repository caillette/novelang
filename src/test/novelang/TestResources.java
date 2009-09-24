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
import java.nio.charset.Charset;
import java.util.Map;
import java.net.MalformedURLException;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.fonts.EmbedFontInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Preconditions;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.ContentConfiguration;
import novelang.configuration.DaemonConfiguration;
import novelang.configuration.FopFontStatus;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.RenderingConfiguration;
import novelang.loader.ClasspathResourceLoader;
import novelang.loader.ResourceLoader;
import novelang.loader.ResourceLoaderTools;
import novelang.loader.ResourceName;
import novelang.loader.UrlResourceLoader;
import novelang.system.DefaultCharset;

/**
 * The only place where constants referencing test-dedicated resources can be defined.
 *
 * @author Laurent Caillette
 */
public class TestResources {

  private TestResources() { }

  private static final String ONE_WORD_RELATIVEFILENAME = "one-word.nlp";
  public static final ResourceName ONE_WORD_RESOURCENAME = new ResourceName( ONE_WORD_RELATIVEFILENAME ) ;

  public static final String PARTS_DIR = "/parts" ;
  public static final String JUST_SECTIONS = PARTS_DIR + "/just-sections.nlp";
  public static final String MESSY_IDENTIFIERS = PARTS_DIR + "/messy-identifiers.nlp";
  public static final String SIMPLE_STRUCTURE = PARTS_DIR + "/simple-structure.nlp";
  public static final String MISSING_IMAGES = PARTS_DIR + "/missing-images.nlp";


  public static final String STYLE_RESOURCE_DIR = "/style" ;

  public static final ResourceName XSL_BAD_XPATH_1 = new ResourceName( "bad-xpath-1.xsl" ) ;
  public static final ResourceName XSL_BAD_XPATH_2 = new ResourceName( "bad-xpath-2.xsl" ) ;

  public static final String NODESET_DIR = "/format-in-xsl" ;
  public static final ResourceName NODESET_XSL = new ResourceName( "format.xsl" ) ;
  public static final String NODESET_SOMECHAPTERS_DOCUMENTNAME = "some-chapters" ;
  public static final String NODESET_SOMECHAPTERS =
      NODESET_DIR + "/" + NODESET_SOMECHAPTERS_DOCUMENTNAME + ".nlp" ;




  public static ProducerConfiguration createProducerConfiguration(
      final File contentDirectory,
      final ResourceLoader resourceLoader,
      final Charset renderingCharset
  ) {
    return new ProducerConfiguration() {

      public RenderingConfiguration getRenderingConfiguration() {
        return new RenderingConfiguration() {
          public ResourceLoader getResourceLoader() {
            return resourceLoader ;
          }
          public FopFactory getFopFactory() {
            return FopFactory.newInstance() ;
          }

          public FopFontStatus getCurrentFopFontStatus() {
            final Iterable< EmbedFontInfo > fontInfo = ImmutableList.of() ;
            final Map< String, EmbedFontInfo > failedFonts = ImmutableMap.of() ;
            return new FopFontStatus(
                fontInfo,
                failedFonts
            ) ;
          }
          public Charset getDefaultCharset() {
            return renderingCharset ;
          }
        } ;
      }

      public ContentConfiguration getContentConfiguration() {
        return new ContentConfiguration() {
          public File getContentRoot() {
            return contentDirectory;
          }
          public Charset getSourceCharset() {
            return DefaultCharset.SOURCE ;
          }
        } ;
      }
    } ;

  }

  public static ProducerConfiguration createProducerConfiguration(
      final File contentDirectory,
      final File styleDirectory,
      final boolean shouldAddClasspathResourceLoader,
      final Charset renderingCharset
  ) {
    Preconditions.checkNotNull( styleDirectory ) ;
    return doCreateProducerConfiguration(
        contentDirectory,
        styleDirectory,
        shouldAddClasspathResourceLoader,
        renderingCharset
    ) ;
  }


  public static ProducerConfiguration createProducerConfiguration(
      final File contentDirectory,
      final Charset renderingCharset
  ) {
    return doCreateProducerConfiguration(
        contentDirectory,
        null,
        true,
        renderingCharset
    ) ;
  }


  private static ProducerConfiguration doCreateProducerConfiguration(
      final File contentDirectory,
      final File styleDirectory,
      final boolean shouldAddClasspathResourceLoader,
      final Charset renderingCharset
  ) {
    final ResourceLoader resourceLoader ;
    final ResourceLoader customResourceLoader ;
    if( styleDirectory == null ) {
      resourceLoader = new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR ) ;
    } else {
      try {
        customResourceLoader = new UrlResourceLoader( styleDirectory.toURI().toURL() ) ;
      } catch( MalformedURLException e ) {
        throw new Error( e ) ;
      }
      if( shouldAddClasspathResourceLoader ) {
        resourceLoader = ResourceLoaderTools.compose(
            customResourceLoader,
            new ClasspathResourceLoader( ConfigurationTools.BUNDLED_STYLE_DIR )
        ) ;
      } else {
        resourceLoader = customResourceLoader ;
      }
    }
    return createProducerConfiguration( contentDirectory, resourceLoader, renderingCharset ) ;

  }


  public static DaemonConfiguration createDaemonConfiguration(
      final int httpDaemonPort,
      final File contentDirectory,
      final File styleDirectory,
      final Charset renderingCharset
  ) {
    final ProducerConfiguration producerConfiguration = createProducerConfiguration(
        contentDirectory,
        styleDirectory,
        false,
        renderingCharset
    ) ;

    return new DaemonConfiguration() {
      public int getPort() {
        return httpDaemonPort ;
      }
      public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration ;
      }

      public boolean getServeRemotes() {
        return true ;
      }
    } ;

  }

  public static DaemonConfiguration createDaemonConfiguration(
      final int httpDaemonPort,
      final File contentDirectory,
      final Charset renderingCharset
  ) {
    final ProducerConfiguration producerConfiguration = createProducerConfiguration(
        contentDirectory,
        renderingCharset
    ) ;

    return new DaemonConfiguration() {
      public int getPort() {
        return httpDaemonPort ;
      }
      public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration ;
      }

      public boolean getServeRemotes() {
        return true ;
      }
    } ;

  }

  public static DaemonConfiguration createDaemonConfiguration(
      final int httpDaemonPort,
      final File contentDirectory,
      final ResourceLoader resourceLoader
  ) {
    final ProducerConfiguration producerConfiguration = createProducerConfiguration(
        contentDirectory,
        resourceLoader,
        DefaultCharset.RENDERING
    ) ;

    return new DaemonConfiguration() {
      public int getPort() {
        return httpDaemonPort ;
      }
      public ProducerConfiguration getProducerConfiguration() {
        return producerConfiguration ;
      }

      public boolean getServeRemotes() {
        return true ;
      }
    } ;

  }


}
