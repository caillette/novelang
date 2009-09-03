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
package novelang.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.nio.charset.Charset;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableList;
import novelang.common.Problem;
import novelang.common.Renderable;
import novelang.common.SyntacticTree;
import novelang.common.StylesheetMap;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.ExplodeLevelsConfiguration;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.ExplodeLevelsParameters;
import novelang.produce.DocumentProducer;
import novelang.produce.DocumentRequest;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.parser.NodeKind;
import novelang.rendering.RenditionMimeType;
import novelang.rendering.GenericRenderer;
import novelang.rendering.PlainTextWriter;
import novelang.rendering.NlpWriter;

/**
 * Writes top levels into separate files.
 *
 * @author Laurent Caillette
 */
public class ExplodeLevels extends AbstractDocumentGenerator< ExplodeLevelsParameters > {

  private static final Log LOG = LogFactory.getLog( ExplodeLevels.class ) ;

  public void main(
      String commandName,
      String[] arguments,
      File baseDirectory
  ) throws Exception {

    final ExplodeLevelsParameters parameters ;

    parameters = createParametersOrExit( commandName, arguments, baseDirectory ) ;

    try {
      LOG.info(
          "Starting %s with arguments %s",
          getClass().getSimpleName(),
          asString( arguments )
      ) ;

      final ExplodeLevelsConfiguration configuration =
          ConfigurationTools.createExplodeLevelsConfiguration( parameters ); ;
      final File outputDirectory = configuration.getOutputDirectory();
      resetTargetDirectory( outputDirectory ) ;
      final DocumentProducer documentProducer =
          new DocumentProducer( configuration.getProducerConfiguration() ) ;
      final List< Problem > allProblems = Lists.newArrayList() ;

      Iterables.addAll(
          allProblems,
          processDocumentRequest( configuration, outputDirectory, documentProducer )
      ) ;

      if( ! allProblems.isEmpty() ) {
        reportProblems( outputDirectory, allProblems ) ;
        System.err.println(
            "There were problems. See " + outputDirectory + "/" + PROBLEMS_FILENAME ) ;
      }

    } catch( Exception e ) {
      LOG.error( "Fatal", e ) ;
      throw e ;
    }
  }

  private Iterable< Problem > processDocumentRequest(
      final ExplodeLevelsConfiguration configuration,
      final File outputDirectory,
      final DocumentProducer documentProducer
  ) {
    try {

      final Renderable document = documentProducer.createRenderable(
          configuration.getDocumentRequest() ) ;
      for( final SyntacticTree child : document.getDocumentTree().getChildren() ) {
        if( child.isOneOf( NodeKind._LEVEL ) ) {
          processLevel(
              configuration.getProducerConfiguration(),
              child,
              outputDirectory
          ) ;
        }
      }
    } catch( Exception e ) {
      return ImmutableList.of( Problem.createProblem( e ) ) ;
    }
    return ImmutableList.of() ;
  }

  private void processLevel(
      final ProducerConfiguration producerConfiguration,
      final SyntacticTree level,
      final File outputDirectory
  ) throws Exception {
    final Charset charset = producerConfiguration.getRenderingConfiguration().getDefaultCharset() ;
    final String title = createLevelTitle(
        level,
        charset
    ) ;
    final File destinationFile = new File(
        outputDirectory,
        title + "." + RenditionMimeType.NLP.getFileExtension()
    ) ;
    LOG.debug( "Outputting to file '" + destinationFile.getAbsolutePath() + "'" ) ;
    final FileOutputStream fileOutputStream = new FileOutputStream( destinationFile ) ;
    new GenericRenderer( new NlpWriter(
        producerConfiguration.getRenderingConfiguration(),
        null,
        charset
    ) ).render( new MyRenderable( level, charset ), fileOutputStream ) ;
    fileOutputStream.flush() ;
    fileOutputStream.close() ;
  }

  private int titleGenerator = 0 ;

  private String createLevelTitle(
      final SyntacticTree level,
      final Charset charset
  ) throws Exception {
    for( final SyntacticTree child : level.getChildren() ) {
      if( child.isOneOf( NodeKind.LEVEL_TITLE ) ) {
        final String title = textualize( child.getChildAt( 0 ), charset ) ;
        LOG.debug( "Found title: '" + title + "'" ) ;
        return title.replace( ' ', '_' ) ; // TODO replace more filename-wrecking chars.
      }
    }
    final String generatedTitle = String.format( "_%d3", titleGenerator++ );
    LOG.debug( "Generated title: '" + generatedTitle + "'" ) ;
    return generatedTitle ;
  }

  /**
   * TODO: move this into some shared library, we need this elsewhere.
   */
  private String textualize( final SyntacticTree tree, final Charset charset ) throws Exception {

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() ;
    new GenericRenderer( new PlainTextWriter( charset ) ).render(
        new MyRenderable( tree, charset ),
        byteArrayOutputStream
    ) ;

    return new String( byteArrayOutputStream.toByteArray(), charset.name() ) ;
  }

  protected ExplodeLevelsParameters createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException {
    return new ExplodeLevelsParameters(
        baseDirectory,
        arguments
    );
  }

  protected String getSpecificCommandLineParametersDescriptor() {
    return " [OPTIONS] document-to-split.nlp";
  }

  private static class MyRenderable implements Renderable {
    private final SyntacticTree tree;
    private final Charset charset;

    public MyRenderable( SyntacticTree tree, Charset charset ) {
      this.tree = tree;
      this.charset = charset;
    }

    public Iterable<Problem> getProblems() {
      return ImmutableList.of() ;
    }

    public Charset getRenderingCharset() {
      return charset;
    }

    public boolean hasProblem() {
      return false ;
    }

    public SyntacticTree getDocumentTree() {
      return tree;
    }

    public StylesheetMap getCustomStylesheetMap() {
      return null ;
    }
  }
}
