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

    private TestResourceTree() { }

    public static void initialize() {
    ResourceSchema.initialize( Images.class ) ;
    ResourceSchema.initialize( FontStructure.class ) ;
    ResourceSchema.initialize( TaggedPart.class ) ;
    ResourceSchema.initialize( Scanned.class ) ;
    ResourceSchema.initialize( Served.class ) ;
    ResourceSchema.initialize( Parts.class ) ;
    ResourceSchema.initialize( Identifiers.class ) ;
    ResourceSchema.initialize( XslFormatting.class ) ;
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
    String TAGS_FORM_NAME = "tag-list" ;
    String UPDATING_TAG_VISIBILITY_STATUS_MESSAGE = "Updating tag visibility..." ;
    String UPDATING_TAG_VISIBILITY_ALERT_MESSAGE = "Done updating tag visibility" ;
    Directory dir = directory( "tagged" ) ;
    Resource TAGGED = resource( "tags-combinations.nlp" ) ;
  }

  public interface Scanned {
    Directory dir = directory( "scanned" ) ;


    Resource FILE_1 = resource( "file1.nlp" ) ;
    Resource FILE_2 = resource( "file2.nlp" ) ;

    Resource BOOK = resource( "book.nlb" ) ;
    Resource BOOK_NORECURSE = resource( "book-norecurse.nlb" ) ;
    Resource BOOK_WITHSTYLE = resource( "book-withstyle.nlb" ) ;

    public interface Subdirectory {
      Directory dir = directory( "sub" ) ;
      Resource FILE_3 = resource( "file3.nlp" ) ;
    }

  }


  public interface Served {
    Directory dir = directory( "served" ) ;

    Resource GOOD_PART = resource( "good.nlp" ) ;
    Resource GOOD_BOOK = resource( "good.nlb" ) ;
    Resource BROKEN_PART = resource( "broken.nlp" ) ;
    Resource BROKEN_BOOK_BAD_REFERENCED_PART = resource( "book-broken-1.nlb" ) ;
    Resource BROKEN_BOOK_BAD_SCANNED_PART = resource( "book-bad-scanned-part.nlb" ) ;
    Resource BOOK_ALTERNATE_XSL = resource( "book-alternatexsl.nlb" ) ;

      public interface Style {
      Directory dir = directory( "style" ) ;

      Resource VOID_XSL = resource( "void.xsl" ) ;
    }

  }
  
  public interface Parts {
    Directory dir = directory( "parts" ) ;

    Resource BROKEN_CANNOTPARSE = resource( "broken-cannotparse.nlp" ) ;
    Resource JUST_SECTIONS = resource( "just-sections.nlp" ) ;
    Resource MESSY_IDENTIFIERS = resource( "messy-identifiers.nlp" ) ;
    Resource MISSING_IMAGES = resource( "missing-images.nlp" ) ;
    Resource NO_CHAPTER = resource( "no-chapter.nlp" ) ;
    Resource ONE_WORD = resource( "one-word.nlp" ) ;
    Resource SIMPLE_STRUCTURE = resource( "simple-structure.nlp" ) ;

  }

  public interface Identifiers {
    Directory dir = directory( "identifiers" ) ;

    Resource BOOK = resource( "identifiers.nlb" ) ;
    Resource PART = resource( "identifiers.nlp" ) ;
  }

  
  public interface XslFormatting {
    Directory dir = directory( "format-in-xsl" ) ;

    Resource PART_SOMECHAPTERS = resource( "some-chapters.nlp" ) ;
    Resource XSL_NUMBERING = resource( "format-numbering.xsl" ) ;
  }

}
