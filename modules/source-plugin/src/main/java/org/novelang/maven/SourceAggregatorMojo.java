/*
 * Copyright (C) 2011 Laurent Caillette
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
package org.novelang.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.novelang.logger.ConcreteLoggerFactory;
import org.novelang.logger.LoggerFactory;

/**
 * Aggregates the sources of the dependencies.
 *
 * @goal aggregate
 * @requiresDependencyResolution runtime
 * @threadSafe
 * 
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class SourceAggregatorMojo extends AbstractMojo {

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project ;

  /**
   * Where to copy the sources to.
   *
   * @parameter expression="${project.build.directory}/aggregated-sources"
   */
  private File outputDirectory = null ;




  /**
   * The regular expression to grab directory name from dependency project name.
   * Must have exactly one group, like: {@code Novelang-(\w+(?:-\w+)*)
   */
  private static final Pattern DIRECTORY_NAME_GRABBER_PATTERN =
      Pattern.compile( "Novelang-(\\w+(?:-\\w+)*)" ) ;

  private static final String MAIN_JAVA_SOURCE_DIRECTORY = "/src/main/java/" ;

  private static final String MODULES_RELATIVE_PATH_FROM_ROOT_PROJECT = "/modules/" ;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    ConcreteLoggerFactory.setMojoLog( getLog() ) ;
    LoggerFactory.configurationComplete() ;

    final List< File > sourcePathList = buildSourcePathList( project ) ;

    outputDirectory.mkdirs() ;

    for( final File sourcePath : sourcePathList ) {
      try {
        FileUtils.copyDirectory( sourcePath, outputDirectory, true ) ;
      } catch( IOException e ) {
        throw new MojoExecutionException(
            "Could not copy '" + sourcePath.getAbsolutePath() + "' " +
            "to '" + outputDirectory.getAbsolutePath() + "'"
            , e
        ) ;
      }

    }
  }


  private static List< File > buildSourcePathList( final MavenProject project )
      throws MojoExecutionException
  {

    @SuppressWarnings( { "unchecked" } )
    final List<Dependency> dependencies = project.getRuntimeDependencies() ;
    final ImmutableList.Builder< File > sourcePathListBuilder = ImmutableList.builder() ;

    final File parentRoot = findParentRoot( project ) ;

    for( final Dependency dependency : dependencies ) {
      final Matcher matcher = DIRECTORY_NAME_GRABBER_PATTERN.matcher( dependency.getArtifactId() ) ;
      final String moduleDirectory ;
      if( matcher.find() ) {
        if( matcher.groupCount() == 1 ) {
          moduleDirectory = matcher.group( 1 ) ;
          final String moduleFileName = parentRoot.getAbsolutePath() +
              MODULES_RELATIVE_PATH_FROM_ROOT_PROJECT + moduleDirectory + MAIN_JAVA_SOURCE_DIRECTORY ;

          sourcePathListBuilder.add( new File( moduleFileName ) ) ;
        } else {
          throw new MojoExecutionException( "Couldn't apply pattern '" +
              DIRECTORY_NAME_GRABBER_PATTERN.pattern() + " to '" + dependency.getArtifactId() + "'" ) ;
        }
      }
    }
    return sourcePathListBuilder.build() ;
  }

  private static File findParentRoot( final MavenProject project ) {
    MavenProject maybeRoot = project ;
    while( true ) {
      final MavenProject parent = maybeRoot.getParent() ;
      if( parent == null ) {
        break ;
      } else {
        maybeRoot = parent ;
      }
    }
    return maybeRoot.getBasedir() ;
  }

}