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

/**
 * A <a href="http://java.sun.com/javase/6/docs/api/java/lang/instrument/package-summary.html?is-external=true" >Java Agent</a>
 * that performs shutdown after loaded.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class ImmediateShutdownAgent {

  /**
   * Called by the JVM as asked to load the agent.
   * This method performs an asynchronous shutdown because
   * {@link com.sun.tools.attach.VirtualMachine#loadAgent(String)} returns only after the
   * {@code agentmain} completes. So we don't want to mess its execution.
   *
   */
  public static void agentmain( final String agentArguments ) {
    new Thread(
        new Runnable() {
          public void run() {
            System.exit( 0 ) ;
          }
        },
        ImmediateShutdownAgent.class.getSimpleName()
    ).start() ;
  }

}
