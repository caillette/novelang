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
package org.novelang.outfit.shell;

import java.io.File;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.net.Socket;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import org.novelang.testing.DirectoryFixture;
import org.novelang.testing.RepeatedAssert;
import org.novelang.testing.StandalonePredicate;
import org.novelang.testing.junit.NameAwareTestClassRunner;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.collect.ImmutableList.of;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Laurent Caillette
 */
@RunWith( NameAwareTestClassRunner.class )
public class JavaShellTest {

  @Test
  public void getTheOfficialJar() throws IOException {
    if( isLikelyToWork() ) {
      final File jarFile = AgentFileInstaller.getInstance().getJarFile() ;
      assertThat( jarFile ).isNotNull() ;
    }
  }


  @Test( expected = ProcessInitializationException.class )
  public void cannotStart() throws Exception {

    if( isLikelyToWork() ) {
      final ShellFixture shellFixture = new ShellFixture() ;
      final JavaShell javaShell = new JavaShell( shellFixture.getParameters()
          .withJavaClasses( new JavaClasses.ClasspathAndMain(
              "ThisClassDoesNotExist",
              shellFixture.getJarFile()
          )
      ) ) ;
      javaShell.start() ;
    } else {
      throw new ProcessInitializationException( "Whatever", null ) ;
    }
  }


  @Test
  public void startTwice() throws Exception {

    if( isLikelyToWork() ) {
      startAndShutdown( new JavaShell( new ShellFixture().getParameters() ) ) ;
      startAndShutdown( new JavaShell( new ShellFixture().getParameters() ) ) ;
    }
  }

  @Test
  public void useAsJmxConnector() throws Exception {

    if( isLikelyToWork() ) {
      final JavaShell javaShell = new JavaShell( new ShellFixture().getParameters() ) ;
      javaShell.start() ;
      try {
        final RuntimeMXBean runtimeMXBean = javaShell.getManagedBean(
            RuntimeMXBean.class, JavaShellTools.RUNTIME_MX_BEAN_OBJECTNAME ) ;
        final String virtualMachineName = runtimeMXBean.getVmName() ;
        assertNotNull( virtualMachineName ) ;
        LOGGER.info( "Returned VM name: '", virtualMachineName, "'" ) ;
      } finally {
        javaShell.shutdown( ShutdownStyle.FORCED ) ;
      }
    }
  }


  @Test
  public void detectProgramExitedOnItsOwn() throws Exception {

    if( isLikelyToWork() ) {

      final ShellFixture shellFixture = new ShellFixture() ;
      final int heartbeatFatalDelay = 1000 ;
      final JavaShell javaShell = new JavaShell( shellFixture.getParameters()
              .withHeartbeatFatalDelayMilliseconds( heartbeatFatalDelay )
              .withHeartbeatPeriodMilliseconds( 100 )
      ) ;
      javaShell.start() ;
      shellFixture.askForSelfTermination() ;
      Thread.sleep( ( long ) heartbeatFatalDelay ) ;
      assertFalse( javaShell.isUp() ) ;
    }
  }



  @Test
  public void startForeignProgramAndMissHeartbeat() throws Exception {

    final int heartbeatPeriod = 10 * 1000 ;
    final int heartbeatFatalDelay = 1 * 1000 ; 
    final long maybeDownCheckPeriod = 200L ;
    final int maybeDownRetryCount = ( int ) ( ( long ) heartbeatPeriod / maybeDownCheckPeriod ) ;

    if( isLikelyToWork() ) {

      final ShellFixture shellFixture = new ShellFixture() ;

      final JavaShellParameters parameters = shellFixture.getParameters()
          .withHeartbeatPeriodMilliseconds( heartbeatPeriod )
          .withHeartbeatFatalDelayMilliseconds( heartbeatFatalDelay )
      ;

      final JavaShell javaShell = new JavaShell( parameters ) ;
      try {
        javaShell.start() ;
        RepeatedAssert.assertEventually(
            new MaybeDown( javaShell ),
            maybeDownCheckPeriod,
            TimeUnit.MILLISECONDS,
            maybeDownRetryCount
        ) ;
        final List< String > log = readLines( shellFixture.getLogFile() ) ;
        assertThat( log )
            .hasSize( 1 )
            .contains( "Starting up and listening..." )
        ;
      } finally {
        javaShell.shutdown( ShutdownStyle.FORCED ) ;
      }

    }

  }


  @Test
  public void startForeignProgram() throws Exception {
    if( isLikelyToWork() ) {
      final ShellFixture shellFixture = new ShellFixture() ;

      final JavaShell javaShell = new JavaShell( shellFixture.getParameters() ) ;
      try {
        javaShell.start() ;
        LOGGER.info( "Started process known as ", javaShell.getNickname(), "." ) ;
        javaShell.shutdown( ShutdownStyle.GENTLE ) ;
      } catch( Exception e ) {
        javaShell.shutdown( ShutdownStyle.FORCED ) ;
      }

      final List< String > log = readLines( shellFixture.getLogFile() ) ;
      assertThat( log )
          .hasSize( 2 )
          .contains( "Starting up and listening...", "Terminated." )
      ;
    }

  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( JavaShellTest.class ) ;

  public static final long SHELL_STARTUP_TIMEOUT_DURATION = 20L ;
  public static final TimeUnit SHELL_STARTUP_TIMEOUT_UNIT = TimeUnit.SECONDS ;


  private static final Predicate< String > STUPID_LISTENER_STARTED = new Predicate< String >() {
    @Override
    public boolean apply( final String input ) {
      return input.startsWith( "Started." ) ;
    }
  } ;

  @SuppressWarnings( { "ThrowableInstanceNeverThrown" } )
  private static boolean isLikelyToWork() {

    for( final StackTraceElement element : new Exception().getStackTrace() ) {
      if( element.getClassName().contains( "org.apache.maven.surefire.Surefire" ) ) {
        // Maven tests should work all time unless broken for good reason.
        return true ;
      }
    }

    final String warning = "Not running as Maven test, nor couldn't find agent jar file" +
            " (check system properties). Skipping " + NameAwareTestClassRunner.getTestName() +
            " because needed resources may be missing."
    ;
    String message = warning ;

    if( AgentFileInstaller.mayHaveValidInstance() ) {
      try {
        AgentFileInstaller.getInstance().getJarFile() ;
        message = null ;
      } catch( MissingResourceException ignore ) { }
    }

    if( message == null ) {
      return true ;
    } else {
      LOGGER.warn( message ) ;
      return false ;
    }


  }

  @SuppressWarnings( { "unchecked" } )
  private static List< String > readLines( final File logFile ) throws IOException {
    return FileUtils.readLines( logFile ) ;
  }


  private static final String FIXTUREJARFILE_PROPERTYNAME =
      "org.novelang.outfit.shell.fixturejarfile" ;


  private static File installFixturePrograms( final File directory ) throws IOException {
    final String fixtureJarFileAsString = System.getProperty( FIXTUREJARFILE_PROPERTYNAME ) ;
    if( fixtureJarFileAsString == null ) {
      final File jarFile = new File( directory, "java-program.jar" ) ;
      AgentFileInstaller.getInstance().copyVersionedJarToFile(
          FIXTURE_PROGRAM_JAR_RESOURCE_RADIX, jarFile ) ;
      return jarFile ;
    } else {
      final File existingJarFile = AgentFileInstaller.getInstance()
          .resolveWithVersion( fixtureJarFileAsString ) ;
      if( ! existingJarFile.isFile() ) {
        throw new IllegalArgumentException( "Not an existing file: '" + existingJarFile + "'" ) ;
      }
      return existingJarFile ;
    }

  }

  private static void startAndShutdown( final JavaShell javaShell ) throws Exception {
    javaShell.start() ;
    try {
      assertNotNull( javaShell.getManagedBean( RuntimeMXBean.class,
          JavaShellTools.RUNTIME_MX_BEAN_OBJECTNAME ).getVmName() ) ;
    } finally {
      javaShell.shutdown( ShutdownStyle.GENTLE ) ;
    }
  }



  /**
   * TODO: Make this work for non-SNAPSHOT versions.
   */
  @SuppressWarnings( { "HardcodedFileSeparator" } )
  private static final String FIXTURE_PROGRAM_JAR_RESOURCE_RADIX =
      "/Novelang-shell-fixture-" ;



  private static class ShellFixture {

    private final File logFile ;
    private final JavaShellParameters parameters ;
    private final int dummyListenerPort ;
    private final File jarFile ;

    public File getLogFile() {
      return logFile;
    }

    public JavaShellParameters getParameters() {
      return parameters;
    }

    public File getJarFile() {
      return jarFile ;
    }

    public ShellFixture() throws IOException {
      final int jmxPort = org.novelang.outfit.TcpPortBooker.THIS.find() ;
      dummyListenerPort = org.novelang.outfit.TcpPortBooker.THIS.find() ;
      final File scratchDirectory = new DirectoryFixture().getDirectory() ;
      logFile = new File( scratchDirectory, "dummy.txt" );
      jarFile = installFixturePrograms( scratchDirectory ) ;

      parameters = org.novelang.outfit.Husk.create( JavaShellParameters.class )
          .withNickname( "Stupid" )
          .withWorkingDirectory( scratchDirectory )
          .withJavaClasses( new JavaClasses.ClasspathAndMain(
              "org.novelang.outfit.shell.StupidListener",
              jarFile
          ) )
          .withStartupSensor( STUPID_LISTENER_STARTED )
          .withProgramArguments( of(
              logFile.getAbsolutePath(),
              Integer.toString( dummyListenerPort )
          ) )
          .withJmxPort( jmxPort )
      ;
    }

    public void askForSelfTermination() {
      try {
        final Socket clientSocket = new Socket( "localhost", dummyListenerPort ) ;
        clientSocket.close() ;
      } catch( IOException e ) {
        LOGGER.debug( "Couldn't open socket on port ", dummyListenerPort, ": ", e.getMessage() ) ;
      }
    }
  }


  private static class MaybeDown implements StandalonePredicate {

    private final JavaShell javaShell ;

    public MaybeDown( final JavaShell javaShell ) {
      this.javaShell = javaShell ;
    }

    @Override
    public boolean apply() {
      return ! javaShell.isUp() ;
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() ;
    }
  }


}
