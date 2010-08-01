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
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import novelang.KnownVersions;
import novelang.Version;
import novelang.configuration.parse.GenericParameters;
import novelang.system.DefaultCharset;
import novelang.system.Husk;
import novelang.system.Log;
import novelang.system.LogFactory;
import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static novelang.configuration.parse.GenericParameters.OPTIONPREFIX;
import static novelang.nhovestone.driver.VirtualMachineTools.SYSTEMPROPERTYNAME_STICKER;

/**
 * Encapsulates a {@link ProcessDriver}, factoring common code for running either a
 * {@link novelang.daemon.HttpDaemon} or {@link novelang.batch.DocumentGenerator}. 
 *
 * @author Laurent Caillette
 */
public abstract class EngineDriver {

  private static final Log LOG = LogFactory.getLog( EngineDriver.class ) ;

  private final ProcessDriver processDriver ;
  private final Sticker sticker ;


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

    final String absoluteClasspath = configuration.getAbsoluteClasspath() ;
    final File installationDirectory = configuration.getInstallationsDirectory() ;
    if( absoluteClasspath == null ) {
      checkArgument( installationDirectory.exists(), installationDirectory ) ;
      checkNotNull( version ) ;
    } else {
      checkArgument( ! StringUtils.isBlank( absoluteClasspath ) ) ;
      checkArgument( installationDirectory == null, installationDirectory ) ;
    }

    final String applicationName = "Novelang"
        + ( version == null ? "" : "-" + version.getName() )
        + ":" + commandName
    ;

    final ImmutableList.Builder< String > optionsBuilder = new ImmutableList.Builder< String >() ;

    optionsBuilder.add( "java" ) ;

    this.sticker = Sticker.create() ;

    optionsBuilder.add( SYSTEMPROPERTYNAME_STICKER + "=" + sticker.asString() ) ;

    optionsBuilder.add( "-Xmx" + checkNotNull( configuration.getJvmHeapSizeMegabytes() + "M" ) ) ;

    optionsBuilder.add( "-Djava.awt.headless=true" ) ;

    optionsBuilder.add( "-server" ) ;

    if( "64".equals( System.getProperty( "sun.arch.data.model" ) ) ) {
      optionsBuilder.add( "-d64" ) ;
    }

    final Iterable< String > otherJvmOptions = configuration.getJvmOtherOptions() ;
    if( otherJvmOptions != null ) {
      for( final String processOption : otherJvmOptions ) {
        if( processOption.startsWith( "-Xmx" ) ) {
          throw new IllegalArgumentException(
              "Use method in " + Configuration.class.getName() + " to set -Xmx" ) ;
        } else {
          optionsBuilder.add( checkNotNull( processOption ) ) ;
        }
      }
    }

    if( installationDirectory != null ) {
      optionsBuilder.add( "-jar" ) ;
      optionsBuilder.add( KnownVersions.getAbsoluteJarPath( installationDirectory, version ) ) ;
    }

    if( absoluteClasspath != null ) {
      optionsBuilder.add( "-cp" ) ;
      optionsBuilder.add( absoluteClasspath ) ;
    }

    optionsBuilder.add( "novelang.bootstrap.Main" ) ;

    optionsBuilder.add( commandName ) ;

    final Iterable< String > otherProgramOptions = configuration.getProgramOtherOptions() ;
    if( otherProgramOptions != null ) {
      for( final String programOption : otherProgramOptions ) {
        optionsBuilder.add( checkNotNull( programOption ) ) ;
      }
    }

    if( configuration.getLogDirectory() != null ) {
      optionsBuilder.add( OPTIONPREFIX + GenericParameters.LOG_DIRECTORY_OPTION_NAME ) ;
      optionsBuilder.add( configuration.getLogDirectory().getAbsolutePath() ) ;
    }

    optionsBuilder.add( OPTIONPREFIX + GenericParameters.OPTIONNAME_CONTENT_ROOT ) ;
    optionsBuilder.add( checkNotNull(
        configuration.getContentRootDirectory() ).getAbsolutePath() ) ;

    optionsBuilder.add( OPTIONPREFIX + GenericParameters.OPTIONNAME_DEFAULT_SOURCE_CHARSET ) ;
    optionsBuilder.add( DefaultCharset.SOURCE.name() ) ;

    final List< String > processOptions = optionsBuilder.build() ;

    processDriver = new ProcessDriver(
        checkNotNull( configuration.getWorkingDirectory() ),
        applicationName,
        processOptions,
        startupSensor
    ) ;

  }


  public void start( final long timeout, final TimeUnit timeUnit )
      throws
      IOException,
      ProcessDriver.ProcessCreationFailedException,
      InterruptedException
  {
    processDriver.start( timeout, timeUnit ) ;
  }


  public void shutdown( final boolean force ) throws InterruptedException {
    processDriver.shutdown( force ) ;
  }

  


  @Husk.Converter( converterClass = ConfigurationHelper.class )
  public interface Configuration< CONFIGURATION extends Configuration > {

    /**
     * Mutually exclusive with {@link #getAbsoluteClasspath()}.
     * @return null if {@link #getAbsoluteClasspath()} returns a non-null value, a directory
     *         containing an unzipped Novelang installation otherwise.
     */
    File getInstallationsDirectory() ;

    /**
     * Mutually exclusive with {@link #withAbsoluteClasspath(String)}.
     */
    CONFIGURATION withInstallationsDirectory( File directory ) ;

    /**
     * Mutually exclusive with {@link #getInstallationsDirectory()}.
     * @return null if {@link #getInstallationsDirectory()} returns a non-null value, a valid
     *         classpath otherwise.
     */
    String getAbsoluteClasspath() ;

    /**
     * Mutually exclusive with {@link #withInstallationsDirectory(java.io.File)}.
     */
    CONFIGURATION withAbsoluteClasspath( String absoluteClasspath ) ;


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

  }

  @SuppressWarnings( { "UnusedDeclaration" } )
  public static class ConfigurationHelper {

    public static Iterable< String > convert( final String... strings ) {
      return ImmutableList.of( strings ) ;
    }

  }
}
