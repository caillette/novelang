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
import java.util.List;
import java.nio.charset.Charset;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableList;
import novelang.common.Problem;
import novelang.common.Renderable;
import novelang.common.SyntacticTree;
import novelang.configuration.ConfigurationTools;
import novelang.configuration.LevelExploderConfiguration;
import novelang.configuration.ProducerConfiguration;
import novelang.configuration.parse.ArgumentException;
import novelang.configuration.parse.LevelExploderParameters;
import novelang.produce.DocumentProducer;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.parser.NodeKind;
import novelang.rendering.RenditionMimeType;
import novelang.rendering.GenericRenderer;
import novelang.rendering.NlpWriter;
import novelang.rendering.RenderingTools;

/**
 * Writes top levels into separate files.
 *
 * @author Laurent Caillette
 */
public class LevelExploder extends AbstractDocumentGenerator<LevelExploderParameters> {

  private static final Log LOG = LogFactory.getLog( LevelExploder.class ) ;

  public void main(
      final String commandName,
      final boolean mayTerminateJvm, 
      final String[] arguments,
      final File baseDirectory
  ) throws Exception {

    final LevelExploderParameters parameters ;

    parameters = createParametersOrExit( commandName, true, arguments, baseDirectory ) ;

    try {
      LOG.info(
          "Starting %s with arguments %s",
          getClass().getSimpleName(),
          asString( arguments )
      ) ;

      final LevelExploderConfiguration configuration =
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
      final LevelExploderConfiguration configuration,
      final File outputDirectory,
      final DocumentProducer documentProducer
  ) {
    try {

      final Renderable document = documentProducer.createRenderable(
          configuration.getDocumentRequest() );
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
    ) ).render( new RenderingTools.RenderableTree( level, charset ), fileOutputStream ) ;
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
        final String title = RenderingTools.textualize( child, charset ) ;
        LOG.debug( "Found title: '" + title + "'" ) ;
        return title.replace( ' ', '_' ) ; // TODO replace more filename-wrecking chars.
      }
    }
    final String generatedTitle = String.format( "_%d3", titleGenerator++ );
    LOG.debug( "Generated title: '" + generatedTitle + "'" ) ;
    return generatedTitle ;
  }

  protected LevelExploderParameters createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException {
    return new LevelExploderParameters(
        baseDirectory,
        arguments
    );
  }

  protected String getSpecificCommandLineParametersDescriptor() {
    return " [OPTIONS] document-to-split.novella";
  }

}
