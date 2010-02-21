package novelang.produce;

import novelang.TestResourceTree;
import novelang.book.BookTest;
import novelang.common.Renderable;
import novelang.common.SyntacticTree;
import novelang.common.filefixture.JUnitAwareResourceInstaller;
import novelang.common.filefixture.Resource;
import novelang.configuration.ContentConfiguration;
import novelang.configuration.FopFontStatus;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.RenderingConfiguration;
import novelang.loader.ResourceLoader;
import novelang.parser.NodeKind;
import novelang.parser.antlr.TreeFixture;
import novelang.system.DefaultCharset;
import novelang.system.Log;
import novelang.system.LogFactory;

import junit.framework.Assert;
import org.apache.fop.apps.FopFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static novelang.parser.NodeKind.*;
import static novelang.parser.NodeKind.WORD_;
import static novelang.parser.antlr.TreeFixture.tree;

/**
 * Tests for {@link novelang.produce.DocumentProducer#createRenderable(AbstractRequest)}.
 * 
 * @author Laurent Caillette
 */
@RunWith( NameAwareTestClassRunner.class )
public class TestDocumentProducer {

  @Test
  public void tagFilteringOnImplicitTagForBook() throws IOException {
    resourceInstaller.copyWithPath( TestResourceTree.TaggedPart.IMPLICIT_TAGS_PART ) ;
    final Resource bookResource = TestResourceTree.TaggedPart.IMPLICIT_TAGS_BOOK ;
    verifyFooTagApplies( BOOK, bookResource ) ;
  }

  @Test
  public void tagFilteringOnImplicitTagForPart() throws IOException {
    final Resource bookResource = TestResourceTree.TaggedPart.IMPLICIT_TAGS_PART ;
    verifyFooTagApplies( PART, bookResource ) ;
  }


// =======  
// Fixture
// =======
  
  private void verifyFooTagApplies( final NodeKind root, final Resource bookResource ) 
      throws IOException 
  {
    final File bookFile = resourceInstaller.copyWithPath( bookResource ) ;
    final SyntacticTree tree = produceTree( 
        bookFile.getParentFile(), 
        "/" + bookResource.getBaseName() + ".html?tags=Foo" 
    ) ;
    LOG.info( "Got:\n" + tree.toStringTree() ) ;

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
  ) throws IOException {
    final AbstractRequest request = RequestTools.createDocumentRequest( requestAsString ) ;
    assertNotNull( request ) ;
    final Renderable renderable = 
        new DocumentProducer( createConfiguration( contentRoot ) ).createRenderable( request ) ;
    assertFalse( "" + renderable.getProblems(), renderable.hasProblem() ) ;
    return renderable.getDocumentTree() ;    
  }
  
  private static ProducerConfiguration createConfiguration( final File contentRoot ) {
    return new ProducerConfiguration() {
      public RenderingConfiguration getRenderingConfiguration() {
        return new RenderingConfiguration() {
          public ResourceLoader getResourceLoader() {
            throw new UnsupportedOperationException( "Should not be called for this test" ) ; 
          }

          public FopFactory getFopFactory() {
            throw new UnsupportedOperationException( "Should not be called for this test" ) ;
          }

          public FopFontStatus getCurrentFopFontStatus() {
            throw new UnsupportedOperationException( "Should not be called for this test" ) ;
          }

          public Charset getDefaultCharset() {
            return DefaultCharset.RENDERING ;
          }
        } ;
      }

      public ContentConfiguration getContentConfiguration() {
        return new ContentConfiguration() {
          public File getContentRoot() {
            return contentRoot ;
          }

          public Charset getSourceCharset() {
            return DefaultCharset.SOURCE ;
          }
        } ;
      }
    } ;
  }
  
  static {
    TestResourceTree.initialize() ;
  }

  private static final Log LOG = LogFactory.getLog( BookTest.class ) ;
  private final JUnitAwareResourceInstaller resourceInstaller = new JUnitAwareResourceInstaller() ;

}
