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
package org.novelang.daemon;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;


/**
 * A Jetty {@link org.eclipse.jetty.util.thread.ThreadPool} with custom naming
 * for threads.
 *
 * @author Laurent Caillette
 */
/*package*/ class JettyThreadPool extends ExecutorThreadPool {

  public JettyThreadPool() {
    this( new ThreadFactoryBuilder()
        .setDaemon( false )
        .setNameFormat( "Jetty-%02d" )
        .build()
    ) ;
  }
  public JettyThreadPool( final ThreadFactory threadFactory ) {
   super( Executors.newCachedThreadPool( threadFactory ) ) ;
  }
}
