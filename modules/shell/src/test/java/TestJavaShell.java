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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import novelang.DirectoryFixture;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.system.TcpPortBooker;
import novelang.system.shell.AgentFileInstaller;
import novelang.system.shell.JavaClasses;
import novelang.system.shell.JavaShell;
import novelang.system.shell.ShutdownStyle;
import novelang.system.shell.StupidListener;
import org.apache.commons.io.FileUtils;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.NameAwareTestClassRunner;

import static com.google.common.collect.ImmutableList.of;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Laurent Caillette
 */
@RunWith( NameAwareTestClassRunner.class )
public class TestJavaShell {
  public static final ImmutableList< String > EMPTY_LIST = of();

  @Test
  public void getTheOfficialJar() throws IOException {
    if( isRunningAsMavenTest() ) {

      final File jarFile = AgentFileInstaller.getJarFile() ;
      assertThat( jarFile ).isNotNull() ;

    }
  }

  @Test
  public void startForeignProgram() throws Exception {
    if( isRunningAsMavenTest() ) {

      final int jmxPort = TcpPortBooker.THIS.find() ;
      final int dummyListenerPort = TcpPortBooker.THIS.find() ;
      final File scratchDirectory = new DirectoryFixture().getDirectory() ;
      final File logFile = new File( scratchDirectory, "dummy.txt" ) ;
      final File jarFile = installFixturePrograms( scratchDirectory ) ;
      
      final JavaShell javaShell = new JavaShell(
          "dummy",
          scratchDirectory,
          EMPTY_LIST,
          new JavaClasses.ClasspathAndMain( StupidListener.class.getName(), jarFile ),
          of( logFile.getAbsolutePath(), Integer.toString( dummyListenerPort ) ),
          STUPID_LISTENER_STARTED,
          jmxPort
      ) ;

      javaShell.start( 10L, TimeUnit.SECONDS ) ;
      javaShell.shutdown( ShutdownStyle.GENTLE ) ;

      final List< String > log = FileUtils.readLines( logFile ) ;
      assertThat( log )
          .hasSize( 2 )
          .contains( "Starting up and listening...", "Terminated." )
      ;
    }

  }


// =======
// Fixture
// =======

  private static final Log LOG = LogFactory.getLog( TestJavaShell.class ) ;

  private static final Predicate< String > STUPID_LISTENER_STARTED = new Predicate<String>() {
    @Override
    public boolean apply( final String input ) {
      return input.startsWith( "Started." ) ;
    }
  };

  @SuppressWarnings( { "ThrowableInstanceNeverThrown" } )
  private static boolean isRunningAsMavenTest() {
    for( final StackTraceElement element : new Exception().getStackTrace() ) {
      if( element.getClassName().contains( "org.apache.maven.surefire.Surefire" ) ) {
        return true ;
      }
    }
    LOG.warn( "Not running as Maven test. Skipping " + NameAwareTestClassRunner.getTestName() +
        " because needed resources may be missing." ) ;    
    return false ;
  }


  private static File installFixturePrograms( final File directory ) throws IOException {
    final File jarFile = new File( directory, "java-program.jar" ) ;
    AgentFileInstaller.copyResourceToFile( FIXTURE_PROGRAM_JAR_RESOURCE_NAME, jarFile ) ;
    return jarFile ;
  }

  /**
   * TODO: Make this work for non-SNAPSHOT versions.
   */
  @SuppressWarnings( { "HardcodedFileSeparator" } )
  private static final String FIXTURE_PROGRAM_JAR_RESOURCE_NAME =
      "/Novelang-shell-fixture-SNAPSHOT.jar" ;

}
