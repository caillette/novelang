/*
 * Copyright (C) 2009 Laurent Caillette
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
package novelang;

import novelang.common.filefixture.Directory;
import novelang.common.filefixture.Resource;
import novelang.common.filefixture.ResourceSchema;
import static novelang.common.filefixture.ResourceSchema.directory;
import static novelang.common.filefixture.ResourceSchema.resource;

/**
 * Schema of all resources used by tests.
 * 
 * @author Laurent Caillette
 */
public final class TestResourceTree {
  
  public static void initialize() {
    ResourceSchema.initialize( Images.class ) ;
    ResourceSchema.initialize( FontStructure.class ) ;
    ResourceSchema.initialize( TaggedPart.class ) ;
  }
    
  public interface Images {
    Directory dir = directory( "images" ) ;

    String RASTER_IMAGE_WIDTH = "128px" ;
    String RASTER_IMAGE_HEIGHT = "64px" ;
    String VECTOR_IMAGE_WIDTH = "128mm" ;
    String VECTOR_IMAGE_HEIGHT = "64mm" ;

    Resource RED_PNG = resource( "Red-128x64.png" ) ;
    Resource GREEN_JPG = resource( "Green-128x64.jpg" ) ;
    Resource BOOK_EXPLICIT = resource( "images-book-explicit.nlb" ) ;
    Resource BOOK_RECURSIVE = resource( "images-book-recursive.nlb" ) ;
    Resource PART_1 = resource( "images1.nlp" ) ;

    interface Child {

      Directory dir = directory( "child" ) ;
      Resource PART_2 = resource( "images2.nlp" ) ;
      Resource BLUE_GIF = resource( "Blue-128x64.gif" ) ;

      interface Grandchild {

        Directory dir = directory( "grandchild" ) ;
        Resource YELLOW_SVG = resource( "Yellow-128x64.svg" ) ;

      }
    }
    
  }

  public interface FontStructure {
    Directory dir = directory( "fonts-structure" ) ;

    public interface Alternate {
      Directory dir = directory( "alternate" ) ;

      Resource MONO_BOLD_OBLIQUE = resource( "Bitstream-Vera-Sans-Mono-Bold-Oblique.ttf" ) ;
    }

    public interface Fonts {
      Directory dir = directory( "fonts" ) ;

      Resource MONO_BOLD = resource( "Bitstream-Vera-Sans-Mono-Bold.ttf" ) ;
      Resource MONO = resource( "Bitstream-Vera-Sans-Mono.ttf" ) ;
    }

    public interface Parent {
      Directory dir = directory( "parent" ) ;

      public interface Child {
        Directory dir = directory( "child" ) ;

        Resource BAD = resource( "Bad.ttf" ) ;
        Resource MONO_OBLIQUE = resource( "Bitstream-Vera-Sans-Mono-Oblique.ttf" ) ;
      }

    }
  }
  
  public interface TaggedPart {
    String TAG1 = "T1" ;
    String TAG2 = "T2" ;
    Directory dir = directory( "tagged" ) ;
    Resource TAGGED = resource( "tags-combinations.nlp" ) ;
  }
  
}
