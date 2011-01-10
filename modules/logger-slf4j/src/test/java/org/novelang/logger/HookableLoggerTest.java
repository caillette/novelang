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
package org.novelang.logger;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for {@link org.novelang.logger.HookableLogger} that can't take place in original
 * Novelang-logger project, because it has no default logging system.
 *
 * @author Laurent Caillette
 */
public class HookableLoggerTest {
  
  @Test
  public void hookableLoggerInstallation() {
    final StandaloneRecordingLogger recordingLogger = new StandaloneRecordingLogger() ;
    final HookableLogger hookableLogger = HookableLogger.get( LOGGER_NAME ) ;
    assertThat( hookableLogger ).isSameAs( LOGGER ) ;
    hookableLogger.installHook( recordingLogger ) ;

    assertThat( recordingLogger.getRecords() ).isEmpty() ;

    LOGGER.debug( "Hi, there" ) ;

    final ImmutableList< LogRecord > loggingRecords = recordingLogger.getRecords() ;
    assertThat( loggingRecords ).hasSize( 1 ) ;
    assertThat( loggingRecords.get( 0 ).getLevel() ).isSameAs( Level.DEBUG ) ;
    assertThat( loggingRecords.get( 0 ).getMessage() ).isEqualTo( "Hi, there" ) ;

    HookableLogger.uninstallAllHooks() ;
    LOGGER.debug( "Hi again" ) ;
    assertThat( loggingRecords ).hasSize( 1 ) ;
  }

// =======
// Fixture
// =======

  private static final String LOGGER_NAME = HookableLoggerTest.class.getName() ;
  private static final Logger LOGGER = LoggerFactory.getLogger( LOGGER_NAME ) ;

  @After
  public void tearDown() {
    // In case of other tests that forget to do their cleanup:
    HookableLogger.uninstallAllHooks() ;
  }
}
