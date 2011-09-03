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
package org.novelang.outfit;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.google.common.collect.ImmutableList;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Carries multiple {@code Exception}s in a single one.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "CallToPrintStackTrace" } )
public class CompositeException extends Exception {

  private final ImmutableList< Exception > exceptions ;

  public CompositeException( final String message, final ImmutableList< Exception > exceptions ) {
    super( message ) ;
    this.exceptions = checkNotNull( exceptions ) ;
  }

  @Override
  public void printStackTrace() {
    for( final Exception exception : exceptions ) {
      exception.printStackTrace() ;
    }
  }

  @Override
  public void printStackTrace( final PrintWriter printWriter ) {
    for( final Exception exception : exceptions ) {
      exception.printStackTrace( printWriter ) ;
    }
  }

  @Override
  public void printStackTrace( final PrintStream printStream ) {
    for( final Exception exception : exceptions ) {
      exception.printStackTrace( printStream ) ;
    }
  }
}
