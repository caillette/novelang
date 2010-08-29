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
package org.novelang.parser.antlr;

import org.junit.Test;
import org.antlr.runtime.RecognitionException;
import static org.novelang.parser.antlr.TreeFixture.tree;
import static org.novelang.parser.NodeKind.RASTER_IMAGE;
import static org.novelang.parser.NodeKind.RESOURCE_LOCATION;

/**
 * @author Laurent Caillette
 */
public class EmbeddableResourceParsingTest {


  @Test
  public void rasterImageIsAbsoluteFile() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "/foo.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "/foo.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageInAbsoluteSubdirectory() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "/foo/bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "/foo/bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsRelativeFile() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "./bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "./bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsRelativeFileInSubdirectory() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "./foo/bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "./foo/bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsInSuperdirectory() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "../bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "../bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsInSuperdirectory2() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "../../bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "../../bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageIsInSuperdirectory3() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "../../../bar.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "../../../bar.jpg" ) )
        )
    ) ;
  }

  @Test
  public void rasterImageHasVariousCharactersInItsPath() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkTreeAfterSeparatorRemoval(
        "/f.o-o/b=a_r.jpg",
        tree(
            RASTER_IMAGE,
            tree( RESOURCE_LOCATION, tree( "/f.o-o/b=a_r.jpg" ) )
        )
    ) ;
  }

  @Test
  public void badlyFormedRasterImage1() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkFails(
        "/.foo.unknown"
    ) ;
  }

  @Test
  public void badlyFormedRasterImage2() throws RecognitionException {
    PARSERMETHOD_EMBEDDABLE_RESOURCE.checkFails(
        "/.foo.jpg"
    ) ;
  }
  
// =======
// Fixture
// =======

  private static final ParserMethod PARSERMETHOD_EMBEDDABLE_RESOURCE =
      new ParserMethod( "embeddableResource" ) ;

}
