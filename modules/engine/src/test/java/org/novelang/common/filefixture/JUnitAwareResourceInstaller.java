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
package org.novelang.common.filefixture;

import java.io.File;
import java.io.IOException;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.testing.DirectoryFixture;
import org.novelang.testing.junit.NameAwareTestClassRunner;

/**
 * Like {@link ResourceInstaller} but directory name changes along with test's name
 * as given by {@link org.novelang.testing.junit.NameAwareTestClassRunner}.
 *
 * @author Laurent Caillette
 */
public class JUnitAwareResourceInstaller extends AbstractResourceInstaller {

  private static final Logger LOGGER = LoggerFactory.getLogger( JUnitAwareResourceInstaller.class );

  private String lastTestName = null ;
  private File lastScratchDirectory = null ;

  @Override
  public File getTargetDirectory() {

    final String testName = NameAwareTestClassRunner.getTestName() ;
    if( testName == null ) {
      throw new AssertionError(
          "No test name set. Maybe this test is not running with " +
          NameAwareTestClassRunner.class.getSimpleName() + "?"
      ) ;
    }

    if( lastTestName == null || ! lastTestName.equals( testName ) ) {
      lastTestName = testName ;
      final DirectoryFixture directoryFixture ;
      try {
        directoryFixture = new DirectoryFixture( testName ) ;
        lastScratchDirectory = directoryFixture.getDirectory() ;
      } catch( IOException e ) {
        // Nullifying those references will probably induce other unsuccessful attempts
        // to get a scratch directory but at least this will avoid to mess the tests up
        // for an unclear reason.
        lastTestName = null ;
        lastScratchDirectory = null ;
        LOGGER.error( e, "Could not get directory fixture" ) ;
        throw new AssertionError( "Could not get directory fixture" );
      }
    }

    return lastScratchDirectory ;

  }
}