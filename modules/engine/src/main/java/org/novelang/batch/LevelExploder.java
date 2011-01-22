/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.novelang.common.Problem;
import org.novelang.common.Renderable;
import org.novelang.common.SyntacticTree;
import org.novelang.configuration.ConfigurationTools;
import org.novelang.configuration.LevelExploderConfiguration;
import org.novelang.configuration.ProducerConfiguration;
import org.novelang.configuration.parse.ArgumentException;
import org.novelang.configuration.parse.LevelExploderParameters;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.NodeKind;
import org.novelang.produce.DocumentProducer;
import org.novelang.rendering.GenericRenderer;
import org.novelang.rendering.NovellaWriter;
import org.novelang.rendering.RenderingTools;
import org.novelang.rendering.RenditionMimeType;

/**
 * Writes top levels into separate files.
 *
 * @author Laurent Caillette
 */
public class LevelExploder extends AbstractDocumentGenerator<LevelExploderParameters> {

  private static final Logger LOGGER = LoggerFactory.getLogger( LevelExploder.class ) ;

  @Override
  public void main(
      final LevelExploderParameters parameters
  ) throws Exception {


    final LevelExploderConfiguration configuration =
        ConfigurationTools.createExplodeLevelsConfiguration( parameters ) ;
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
        title + "." + RenditionMimeType.NOVELLA.getFileExtension()
    ) ;
    LOGGER.debug( "Outputting to file '", destinationFile.getAbsolutePath(), "'" ) ;
    final FileOutputStream fileOutputStream = new FileOutputStream( destinationFile ) ;
    try {
      new GenericRenderer( new NovellaWriter(
          producerConfiguration.getRenderingConfiguration(),
          null,
          charset
      ) ).render(
          new RenderingTools.RenderableTree( level, charset ),
          fileOutputStream,
          null,
          producerConfiguration.getContentConfiguration().getContentRoot()
      ) ;
    } finally {
      fileOutputStream.close() ;
    }
  }

  private int titleGenerator = 0 ;

  private String createLevelTitle(
      final SyntacticTree level,
      final Charset charset
  ) throws Exception {
    for( final SyntacticTree child : level.getChildren() ) {
      if( child.isOneOf( NodeKind.LEVEL_TITLE ) ) {
        final String title = RenderingTools.textualize( child, charset ) ;
        LOGGER.debug( "Found title: '", title, "'" ) ;
        return title.replace( ' ', '_' ) ; // TODO replace more filename-wrecking chars.
      }
    }
    final String generatedTitle = String.format( "_%d3", titleGenerator++ );
    LOGGER.debug( "Generated title: '", generatedTitle, "'" ) ;
    return generatedTitle ;
  }

  public static LevelExploderParameters createParameters(
      final String[] arguments,
      final File baseDirectory
  ) throws ArgumentException {
    return new LevelExploderParameters(
        baseDirectory,
        arguments
    );
  }

  public static String getSpecificCommandLineParametersDescriptor() {
    return " [OPTIONS] document-to-split.novella";
  }

}
