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
package novelang.logger;

/**
 * Implementation based on {@link org.apache.maven.plugin.logging.Log}.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class ConcreteLoggerFactory extends novelang.logger.LoggerFactory {

  @SuppressWarnings( { "StaticNonFinalField" } )
  private static org.apache.maven.plugin.logging.Log mojoLog = null ;

  public static void setMojoLog( final org.apache.maven.plugin.logging.Log  mojoLog ) {
    // Allowing multiple assignments because Maven does this.
    ConcreteLoggerFactory.mojoLog = mojoLog ;
  }

  @Override
  protected novelang.logger.Logger doGetLogger( final String name ) {
    if( mojoLog == null ) {
      throw new IllegalStateException( "Not set: mojo log " ) ;
    }
    return new MojoLoggerWrapper( name, mojoLog ) ;
  }
}
