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

/**
 * Implementation based on <a href="http://www.slf4j.org/">SLF4J</a>.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "UnusedDeclaration" } )
public class ConcreteLoggerFactory extends org.novelang.logger.LoggerFactory {

  @Override
  protected org.novelang.logger.Logger doGetLogger( final String name ) {
    return new Slf4jLoggerWrapper( org.slf4j.LoggerFactory.getLogger( name ) ) ;
  }
}
