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
package org.novelang.bootstrap;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore ;
import org.novelang.configuration.parse.GenericParametersConstants;
import org.novelang.daemon.HttpDaemon;
import org.novelang.testing.NoSystemExit;
import org.novelang.testing.junit.MethodSupport;

/**
 * Tests for {@link Main}.
 *
 * @author Laurent Caillette
 */
public class BootstrapMainTest {

  @Test( expected = NoSystemExit.ExitTrappedException.class )
  public void exitWithIncorrectParameters() throws Exception {
    new Main().doMainWithoutSystemSetup( new String[] { "unknowncommand" } ) ;
  }

  @Test( expected = NoSystemExit.ExitTrappedException.class )
  public void exitBecauseHelpRequested() throws Exception {
    new Main().doMainWithoutSystemSetup( new String[] {
        HttpDaemon.COMMAND_NAME,
        GenericParametersConstants.OPTIONPREFIX + GenericParametersConstants.HELP_OPTION_NAME
    } ) ;
  }



// =======
// Fixture
// =======

  /**
   * The {@link org.novelang.testing.junit.MethodSupport} guarantees that test methods
   * will NOT run in parallel because {@link org.novelang.testing.NoSystemExit} is a singleton.
   */
  @SuppressWarnings( { "UnusedDeclaration" } )
  @Rule
  public final MethodSupport methodSupport = new MethodSupport( NoSystemExit.INSTANCE ) {
    @Override
    protected void beforeStatementEvaluation() {
      NoSystemExit.INSTANCE.install() ;
    }

    @Override
    protected void afterStatementEvaluation() {
      NoSystemExit.INSTANCE.uninstall() ;
    }
  } ;

  
}
