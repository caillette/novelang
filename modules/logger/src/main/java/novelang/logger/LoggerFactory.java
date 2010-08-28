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
 * The API for obtaining instances of {@link Logger} object.
 * This class delegates to a {@value #CONCRETE_LOGGER_FACTORY_CLASS_NAME} class, giving
 * pluggable implementation ability.
 *
 * @author Laurent Caillette
 */
public abstract class LoggerFactory {

  private static final LoggerFactory concreteLoggerFactory ;

  private static final String CONCRETE_LOGGER_FACTORY_CLASS_NAME =
      "novelang.logger.ConcreteLoggerFactory";

  static {
    try {
      concreteLoggerFactory = ( LoggerFactory ) Class.forName(
          CONCRETE_LOGGER_FACTORY_CLASS_NAME ).newInstance() ;
    } catch( ClassNotFoundException e ) {
      throw new RuntimeException( e ) ;
    } catch( InstantiationException e ) {
      throw new RuntimeException( e ) ;
    } catch( IllegalAccessException e ) {
      throw new RuntimeException( e ) ;
    }
  }

  public static Logger getLogger( final Class someClass ) {
    return getLogger( someClass.getName() ) ;
  }

  public static Logger getLogger( final String name ) {
    return concreteLoggerFactory.doGetLogger( name ) ;
  }

  protected abstract Logger doGetLogger( String name ) ;


}
