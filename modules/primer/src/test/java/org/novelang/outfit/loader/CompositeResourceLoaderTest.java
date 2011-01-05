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
package org.novelang.outfit.loader;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for {@link CompositeResourceLoader}.
 *
 * @author Laurent Caillette
 */
public class CompositeResourceLoaderTest {


  @Test
  public void compose1() {
    final DummyResourceLoader resourceLoader1 = new DummyResourceLoader() ;
    final DummyResourceLoader resourceLoader2 = new DummyResourceLoader() ;
    final CompositeResourceLoader compositeResourceLoader =
        new CompositeResourceLoader( resourceLoader1, resourceLoader2 ) ;
    assertThat( compositeResourceLoader.getInputStream( new ResourceName( "r.x" ) ) )
        .isSameAs( resourceLoader1.inputStream ) ;
  }


  @Test
  public void compose2() {
    final AbstractResourceLoader resourceLoader1 = new DummyResourceLoader() ;
    final AbstractResourceLoader resourceLoader2 = new ClasspathResourceLoader() ;
    final AbstractResourceLoader resourceLoader3 = new DummyResourceLoader() ;
    final CompositeResourceLoader compositeResourceLoader =
        new CompositeResourceLoader( resourceLoader1, resourceLoader2, resourceLoader3 ) ;
    assertThat( compositeResourceLoader.getAll() )
        .containsExactly( resourceLoader1, resourceLoader3, resourceLoader2 ) ;
  }

}
