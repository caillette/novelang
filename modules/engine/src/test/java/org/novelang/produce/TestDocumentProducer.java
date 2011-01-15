package org.novelang.produce;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.fop.apps.FopFactory;
import org.junit.Rule;
import org.junit.Test;
import org.novelang.ResourcesForTests;
import org.novelang.common.Renderable;
import org.novelang.common.SyntacticTree;
import org.novelang.common.filefixture.Resource;
import org.novelang.common.filefixture.ResourceInstaller;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.ContentConfiguration;
import org.novelang.configuration.FopFontStatus;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.RenderingConfiguration;
import org.novelang.configuration.RenditionKinematic;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.outfit.DefaultCharset;
import org.novelang.outfit.loader.ResourceLoader;
import org.novelang.parser.NodeKind;
import org.novelang.parser.antlr.TreeFixture;
import org.novelang.testing.junit.MethodSupport;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.novelang.parser.NodeKind.*;
import static org.novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for
 * {@link org.novelang.produce.DocumentProducer#createRenderable(DocumentRequest)}.
 * 
 * @author Laurent Caillette
 */
public class TestDocumentProducer {

  @Test
  public void tagFilteringOnImplicitTagForBook() throws IOException, MalformedRequestException {
    resourceInstaller.copyWithPath( ResourcesForTests.TaggedPart.IMPLICIT_TAGS_PART ) ;
    final Resource bookResource = ResourcesForTests.TaggedPart.IMPLICIT_TAGS_BOOK ;
    verifyFooTagApplies( OPUS, bookResource ) ;
  }

  @Test
  public void tagFilteringOnImplicitTagForPart() throws IOException, MalformedRequestException {
    final Resource bookResource = ResourcesForTests.TaggedPart.IMPLICIT_TAGS_PART ;
    verifyFooTagApplies( NOVELLA, bookResource ) ;
  }


// =======  
// Fixture
// =======
  
  private void verifyFooTagApplies( final NodeKind root, final Resource bookResource )
      throws IOException, MalformedRequestException {
    final File bookFile = resourceInstaller.copyWithPath( bookResource ) ;
    final SyntacticTree tree = produceTree( 
        bookFile.getParentFile(), 
        "/" + bookResource.getBaseName() + ".html?tags=Foo" 
    ) ;
    LOGGER.info( "Got:\n", tree.toStringTree() ) ;

    TreeFixture.assertEqualsNoSeparators(
        tree( root,
            tree( _META, tree( _WORD_COUNT, "2" ) ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "Foo" ),
                tree( LEVEL_TITLE, tree( WORD_, "Foo" ) )
            ),
            tree(
                _LEVEL,
                tree( _IMPLICIT_TAG, "Foo" ),
                tree( LEVEL_TITLE, tree( WORD_, "Foo" ) )
            )
        ),
        tree
    ) ;
  }


  private static SyntacticTree produceTree(
      final File contentRoot,
      final String requestAsString 
  ) throws IOException, MalformedRequestException {
    final DocumentRequest request = ( DocumentRequest ) GenericRequest.parse( requestAsString ) ;
    assertNotNull( request ) ;
    final Renderable renderable = 
        new DocumentProducer( createConfiguration( contentRoot ) ).createRenderable( request ) ;
    assertFalse( "" + renderable.getProblems(), renderable.hasProblem() ) ;
    return renderable.getDocumentTree() ;    
  }
  
  private static ProducerConfiguration createConfiguration( final File contentRoot ) {
    return new ProducerConfiguration() {
      @Override
      public RenderingConfiguration getRenderingConfiguration() {
        return new RenderingConfiguration() {
          @Override
          public ResourceLoader getResourceLoader() {
            throw new UnsupportedOperationException( "Should not be called for this test" ) ; 
          }

          @Override
          public FopFactory getFopFactory() {
            throw new UnsupportedOperationException( "Should not be called for this test" ) ;
          }

          @Override
          public FopFontStatus getCurrentFopFontStatus() {
            throw new UnsupportedOperationException( "Should not be called for this test" ) ;
          }

          @Override
          public Charset getDefaultCharset() {
            return DefaultCharset.RENDERING ;
          }

          @Override
          public RenditionKinematic getRenderingKinematic() {
            return RenditionKinematic.BATCH ;
          }
        } ;
      }

      @Override
      public ContentConfiguration getContentConfiguration() {
        return new ContentConfiguration() {
          @Override
          public File getContentRoot() {
            return contentRoot ;
          }

          @Override
          public Charset getSourceCharset() {
            return DefaultCharset.SOURCE ;
          }
        } ;
      }

      @Override
      public ExecutorService getExecutorService() {
        return Executors.newSingleThreadExecutor( ConfigurationTools.getExecutorThreadFactory() ) ;
      }
    } ;
  }
  
  static {
    ResourcesForTests.initialize() ;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger( TestDocumentProducer.class ) ;

  @Rule
  public final MethodSupport methodSupport = new MethodSupport() ;

  private final ResourceInstaller resourceInstaller = new ResourceInstaller( methodSupport ) ;

}
