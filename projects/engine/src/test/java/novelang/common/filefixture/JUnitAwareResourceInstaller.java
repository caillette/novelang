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
package novelang.common.filefixture;

import java.io.File;
import java.io.IOException;

import org.junit.runners.NameAwareTestClassRunner;

import com.google.common.base.Preconditions;
import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.DirectoryFixture;

/**
 * Like {@link ResourceInstaller} but directory name changes along with test's name
 * as given by {@link org.junit.runners.NameAwareTestClassRunner}.
 *
 * @author Laurent Caillette
 */
public class JUnitAwareResourceInstaller extends AbstractResourceInstaller {

  private static final Log LOG = LogFactory.getLog( JUnitAwareResourceInstaller.class ) ;

  private String lastTestName = null ;
  private File lastScratchDirectory = null ;

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
        LOG.error( "Could not get directory fixture", e ) ;
        throw new AssertionError( "Could not get directory fixture" );
      }
    }

    return lastScratchDirectory ;

  }
}