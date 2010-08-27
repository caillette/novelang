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
package novelang.system.shell;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

/**
 * Parameters for creating a {@link JavaShell}.
 *
 * @author Laurent Caillette
 */
public interface JavaShellParameters {

  String getNickname() ;
  JavaShellParameters withNickname( String nickname ) ;

  File getWorkingDirectory() ;
  JavaShellParameters withWorkingDirectory( File workingDirectory ) ;

  ImmutableList< String > getJvmArguments() ;
  JavaShellParameters withJvmArguments( ImmutableList< String > jvmArguments ) ;

  JavaClasses getJavaClasses() ;
  JavaShellParameters withJavaClasses( JavaClasses javaClasses ) ;

  ImmutableList< String > getProgramArguments() ;
  JavaShellParameters withProgramArguments( ImmutableList< String > programArguments ) ;

  Predicate< String > getStartupSensor() ;
  JavaShellParameters withStartupSensor( Predicate< String > startupSensor ) ;

  int getJmxPort() ;
  JavaShellParameters withJmxPort( int jmxPort ) ;

  Integer getHeartbeatFatalDelayMilliseconds() ;
  JavaShellParameters withHeartbeatFatalDelayMilliseconds( Integer maximum ) ;

  Integer getHeartbeatPeriodMilliseconds() ;
  JavaShellParameters withHeartbeatPeriodMilliseconds( Integer maximum ) ;

  Long getStartupTimeoutDuration() ;
  JavaShellParameters withStartupTimeoutDuration( Long duration ) ;

  TimeUnit getStartupTimeoutTimeUnit() ;
  JavaShellParameters withStartupTimeoutTimeUnit( TimeUnit timeUnit ) ;

}
