/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.mojo.xml;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.io.location.ClasspathResourceLocatorStrategy;
import org.apache.maven.shared.io.location.FileLocatorStrategy;
import org.apache.maven.shared.io.location.Location;
import org.apache.maven.shared.io.location.Locator;
import org.apache.maven.shared.io.location.LocatorStrategy;
import org.apache.maven.shared.io.location.URLLocatorStrategy;
import org.codehaus.plexus.util.cli.CommandLineUtils;

/**
 * The read-project-properties goal reads property files and stores the
 * properties as project properties. It serves as an alternate to specifying
 * properties in pom.xml.
 * <p/>
 * This class adds support for file paths, that "escape" the project scope
 * and therefore breaks build reproductibility. The purpose is to keep some
 * sensitive data in safe files that shouldn't go on a public repository.
 *
 * @author <a href="mailto:zarars@gmail.com">Zarar Siddiqi</a>
 * @author <a href="mailto:Krystian.Nowak@gmail.com">Krystian Nowak</a>
 * @version $Id: ReadPropertiesMojo.java 8861 2009-01-21 15:35:38Z pgier $
 * @goal read-project-properties
 */
public class ReadPropertiesMojo extends AbstractMojo {
  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * The properties files that will be used when reading properties.
   * RS: made optional to avoid issue for inherited plugins
   *
   * @parameter
   */
  private File[] files;

  //Begin: RS addition
  /**
   * Optional paths to properties files to be used.
   *
   * @parameter
   */
  private String[] filePaths;
  //End: RS addition

  /**
   * If the plugin should be quiet if any of the files was not found
   *
   * @parameter default-value="false"
   */
  private boolean quiet;

  public void execute() throws MojoExecutionException {
    //Begin: RS addition
    readPropertyFiles();
    //End: RS addition

    Properties projectProperties = new Properties();
    for( final File file : files ) {
      if( file.exists() ) {
        try {
          getLog().debug( "Loading properties file: " + file );

          projectProperties = project.getProperties();
          final FileInputStream stream = new FileInputStream( file );
          try {
            projectProperties.load( stream );
          } finally {
            stream.close();
          }
        } catch( IOException e ) {
          throw new MojoExecutionException( "Error reading properties file "
                  + file.getAbsolutePath(), e );
        }
      } else {
        if( quiet ) {
          getLog().warn( "Ignoring missing properties file: "
                  + file.getAbsolutePath() );
        } else {
          throw new MojoExecutionException( "Properties file not found: "
                  + file.getAbsolutePath() );
        }
      }
    }

    boolean useEnvVariables = false;
    for( Enumeration n = projectProperties.propertyNames() ; n.hasMoreElements() ; ) {
      final String k = ( String ) n.nextElement();
      final String p = ( String ) projectProperties.get( k );
      if( p.contains( "${env." ) ) {
        useEnvVariables = true;
        break;
      }
    }
    Properties environment = null;
    if( useEnvVariables ) {
      try {
        environment = CommandLineUtils.getSystemEnvVars();
      } catch( IOException e ) {
        throw new MojoExecutionException(
            "Error getting system envorinment variables: ", e );
      }
    }
    for( Enumeration n = projectProperties.propertyNames() ; n.hasMoreElements() ; ) {
      final String k = ( String ) n.nextElement();
      final String v = getPropertyValue( k, projectProperties, environment ) ;
      projectProperties.setProperty( k, v ) ;
      getLog().info( "Setting project property: '" + k + "' -> '" + v + "'" ) ;
    }
  }

  //Begin: RS addition

  /**
   * Obtain the file from the local project or the classpath
   *
   * @throws MojoExecutionException
   */
  private void readPropertyFiles() throws MojoExecutionException {
    if( filePaths != null && filePaths.length > 0 ) {

      getLog().debug( "Found " + filePaths.length + " file paths." );

      final File[] allFiles;

      int offset = 0;
      if( files != null && files.length != 0 ) {
        allFiles = new File[files.length + filePaths.length];
        System.arraycopy( files, 0, allFiles, 0, files.length );
        offset = files.length;
      } else {
        allFiles = new File[filePaths.length];
      }

      for( int i = 0 ; i < filePaths.length ; i++ ) {
        final Location location = getLocation( filePaths[ i ] );
        getLog().debug( "Using location " + location );
        try {
          allFiles[ offset + i ] = location.getFile();
        } catch( IOException e ) {
          throw new MojoExecutionException(
              "unable to open properties file", e );
        }
      }

      // replace the original array with the merged results
      files = allFiles;
    } else if( files == null || files.length == 0 ) {
      throw new MojoExecutionException(
          "no files or filePaths defined, one or both must be specified" );
    }
  }
  //End: RS addition

  /**
   * Retrieves a property value, replacing values like ${token} using the
   * Properties to look them up. Shamelessly adapted from:
   * http://maven.apache.
   * org/plugins/maven-war-plugin/xref/org/apache/maven/plugin
   * /war/PropertyUtils.html
   * <p/>
   * It will leave unresolved properties alone, trying for System properties,
   * and environment variables and implements reparsing (in the case that the
   * value of a property contains a key), and will not loop endlessly on a
   * pair like test = ${test}
   *
   * @param k           property key
   * @param p           project properties
   * @param environment environment variables
   * @return resolved property value
   */
  private static String getPropertyValue(
      final String k,
      final Properties p,
      final Properties environment
  ) {
    String v = p.getProperty( k );
    String ret = "" ;
    int idx, idx2;

    while( ( idx = v.indexOf( "${" ) ) >= 0 ) {
      // append prefix to result
      ret += v.substring( 0, idx );

      // strip prefix from original
      v = v.substring( idx + 2 );

      idx2 = v.indexOf( '}' );

      // if no matching } then bail
      if( idx2 < 0 ) {
        break;
      }

      // strip out the key and resolve it
      // resolve the key/value for the ${statement}
      final String nk = v.substring( 0, idx2 );
      v = v.substring( idx2 + 1 );
      String nv = p.getProperty( nk );

      // try global environment
      if( nv == null ) {
        nv = System.getProperty( nk );
      }

      // try environment variable
      if( nv == null && nk.startsWith( "env." ) && environment != null ) {
        nv = environment.getProperty( nk.substring( 4 ) );
      }

      // if the key cannot be resolved,
      // leave it alone ( and don't parse again )
      // else prefix the original string with the
      // resolved property ( so it can be parsed further )
      // taking recursion into account.
      if( nv == null || nv.equals( nk ) ) {
        ret += "${" + nk + "}";
      } else {
        v = nv + v;
      }
    }
    return ret + v;
  }

  //Begin: RS addition

  /**
   * Use various strategies to discover the file.
   */
  public Location getLocation( final String path ) {
    final LocatorStrategy classpathStrategy = new ClasspathResourceLocatorStrategy();

    final List strategies = new ArrayList();
    strategies.add( classpathStrategy );
    strategies.add( new FileLocatorStrategy() );
    strategies.add( new URLLocatorStrategy() );

    List refStrategies = new ArrayList();
    refStrategies.add( classpathStrategy );

    final Locator locator = new Locator();

    locator.setStrategies( strategies );
    final Location location = locator.resolve( path );
    getLog().debug( "Resolving '" + path + "' to " + location );
    return location;
  }
  //End: RS addition
}
