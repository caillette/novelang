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
package novelang.nhovestone.driver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import novelang.Version;
import novelang.configuration.parse.GenericParameters;
import novelang.system.DefaultCharset;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;
import novelang.system.TcpPortBooker;
import novelang.system.shell.JavaClasses;
import novelang.system.shell.JavaShell;
import novelang.system.shell.ProcessShell;
import novelang.system.shell.ShutdownStyle;
import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static novelang.configuration.parse.GenericParameters.OPTIONPREFIX;

/**
 * Encapsulates a {@link JavaShell}, factoring common code for running either a
 * {@link novelang.daemon.HttpDaemon} or {@link novelang.batch.DocumentGenerator}. 
 *
 * @author Laurent Caillette
 */
public abstract class EngineDriver {

  private static final Log LOG = LogFactory.getLog( EngineDriver.class ) ;

  private final JavaShell javaShell;
  
  public static final String NOVELANG_BOOTSTRAP_MAIN_CLASS_NAME = "novelang.bootstrap.Main";


  /**
   * Constructor.
   * 
   * @param configuration a non-null object.
   * @param commandName a command name like
   *         {@value novelang.daemon.HttpDaemon#COMMAND_NAME} or
   *         {@value novelang.batch.DocumentGenerator#COMMAND_NAME}.
   * @param startupSensor a non-null object.
   */
  protected EngineDriver(
      final Configuration< ? > configuration,
      final String commandName,
      final Predicate< String > startupSensor
  ) {

    checkArgument( ! StringUtils.isBlank( commandName ) ) ;

    final Version version = configuration.getVersion() ;

    final String applicationName = "Novelang"
        + ( version == null ? "" : "-" + version.getName() )
        + ":" + commandName
    ;

    final ImmutableList.Builder< String > jvmOptionsBuilder = ImmutableList.builder() ;

    final Iterable< String > otherJvmOptions = configuration.getJvmOtherOptions() ;
    if( otherJvmOptions != null ) {
      for( final String processOption : otherJvmOptions ) {
        if( processOption.startsWith( "-Xmx" ) ) {
          throw new IllegalArgumentException(
              "Use method in " + Configuration.class.getName() + " to set -Xmx" ) ;
        } else {
          jvmOptionsBuilder.add( checkNotNull( processOption ) ) ;
        }
      }
    }
    jvmOptionsBuilder.add( "-Xmx" + checkNotNull(
        configuration.getJvmHeapSizeMegabytes() + "M" ) ) ;
    jvmOptionsBuilder.add( "-Djava.awt.headless=true" ) ;
    jvmOptionsBuilder.add( "-server" ) ;
    if( "64".equals( System.getProperty( "sun.arch.data.model" ) ) ) {
      jvmOptionsBuilder.add( "-d64" ) ;
    }


    final ImmutableList.Builder< String > programOptionsBuilder = ImmutableList.builder() ;
    programOptionsBuilder.add( commandName ) ;

    final Iterable< String > otherProgramOptions = configuration.getProgramOtherOptions() ;
    if( otherProgramOptions != null ) {
      for( final String programOption : otherProgramOptions ) {
        programOptionsBuilder.add( checkNotNull( programOption ) ) ;
      }
    }

    if( configuration.getLogDirectory() != null ) {
      programOptionsBuilder.add( OPTIONPREFIX + GenericParameters.LOG_DIRECTORY_OPTION_NAME ) ;
      programOptionsBuilder.add( configuration.getLogDirectory().getAbsolutePath() ) ;
    }

    programOptionsBuilder.add( OPTIONPREFIX + GenericParameters.OPTIONNAME_CONTENT_ROOT ) ;
    programOptionsBuilder.add( checkNotNull(
        configuration.getContentRootDirectory() ).getAbsolutePath() ) ;

    programOptionsBuilder.add(
        OPTIONPREFIX + GenericParameters.OPTIONNAME_DEFAULT_SOURCE_CHARSET ) ;
    programOptionsBuilder.add( DefaultCharset.SOURCE.name() ) ;

    final Iterable< String > programArguments = configuration.getProgramArguments() ;
    if( programArguments != null ) {
      for( final String programArgument : programArguments ) {
        programOptionsBuilder.add( checkNotNull( programArgument ) ) ;
      }
    }


    JavaShell.Parameters parameters =
        Husk.create( JavaShell.Parameters.class );
    parameters = parameters
        .withWorkingDirectory( configuration.getWorkingDirectory() )
        .withNickname( "Novelang-" + version.getName() )
        .withJvmArguments( jvmOptionsBuilder.build() )
        .withJavaClasses( configuration.getJavaClasses() )
        .withProgramArguments( programOptionsBuilder.build() )
        .withStartupSensor( startupSensor )
        .withJmxPort( TcpPortBooker.THIS.find() )
    ;


    javaShell = new JavaShell( parameters ) ;

  }


  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException,
      InterruptedException, ProcessShell.ProcessCreationFailedException
  {
    javaShell.start( timeout, timeUnit ) ;
  }


  public Integer shutdown( final boolean force ) throws InterruptedException, IOException {
    return javaShell.shutdown( force ? ShutdownStyle.FORCED : ShutdownStyle.WAIT ) ;
  }

  


  @Husk.Converter( converterClass = ConfigurationHelper.class )
  public interface Configuration< CONFIGURATION extends Configuration > {

    JavaClasses getJavaClasses() ;
    CONFIGURATION withJavaClasses( JavaClasses javaClasses ) ;

    Version getVersion() ;
    CONFIGURATION withVersion( Version version ) ;

    File getWorkingDirectory() ;
    CONFIGURATION withWorkingDirectory( File directory ) ;

    File getLogDirectory() ;
    CONFIGURATION withLogDirectory( File directory ) ;

    File getContentRootDirectory() ;
    CONFIGURATION withContentRootDirectory( File directory ) ;

    Integer getJvmHeapSizeMegabytes() ;
    CONFIGURATION withJvmHeapSizeMegabytes( Integer sizeMegabytes) ;

    Iterable< String > getJvmOtherOptions() ;
    CONFIGURATION withJvmOtherOptions( String... options ) ;

    Iterable< String > getProgramOtherOptions() ;
    CONFIGURATION withProgramOtherOptions( String... options ) ;

    Iterable< String > getProgramArguments() ;
    CONFIGURATION withProgramArguments( String... options ) ;

  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public static class ConfigurationHelper {

    public static Iterable< String > convert( final String... strings ) {
      return ImmutableList.of( strings ) ;
    }

  }
}
