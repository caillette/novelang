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
@SuppressWarnings( { "InnerClassFieldHidesOuterClassField" } )
public final class TestResourceTree {

    private TestResourceTree() { }

    public static void initialize() {
    ResourceSchema.initialize( Images.class ) ;
    ResourceSchema.initialize( MissingImages.class ) ;
    ResourceSchema.initialize( FontStructure.class ) ;
    ResourceSchema.initialize( TaggedPart.class ) ;
    ResourceSchema.initialize( Scanned.class ) ;
    ResourceSchema.initialize( BookWithEmptyPart.class ) ;
    ResourceSchema.initialize( Served.class ) ;
    ResourceSchema.initialize( Parts.class ) ;
    ResourceSchema.initialize( Identifiers.class ) ;
    ResourceSchema.initialize( XslFormatting.class ) ;
    ResourceSchema.initialize( MainResources.Style.class ) ;
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
      Resource NOVELLA_2 = resource( "images2.nlp" ) ;
      Resource BLUE_GIF = resource( "Blue-128x64.gif" ) ;

      interface Grandchild {

        Directory dir = directory( "grandchild" ) ;
        Resource YELLOW_SVG = resource( "Yellow-128x64.svg" ) ;

      }
    }
    
  }

  public interface MissingImages {
    Directory dir = directory( "missing-image" ) ;

    Resource MISSING_IMAGE_BOOK = resource( "missing-image-book.nlb" ) ;
    Resource MISSING_IMAGE_PART = resource( "missing-image.nlp" ) ;
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
    Directory dir = directory( "tagged" ) ;
    String TAG1 = "T1" ;
    String TAG2 = "T2" ;
    String TAGS_FORM_NAME = "tag-list" ;
    Resource TAGGED = resource( "tags-combinations.nlp" ) ;
    Resource IMPLICIT_TAGS_PART = resource( "implicit-tags.nlp" ) ;
    Resource IMPLICIT_TAGS_BOOK = resource( "implicit-tags-book.nlb" ) ;

    Resource PROMOTED_TAGS_BOOK = resource( "promoted-tags-book.nlb" ) ;
    Resource PROMOTED_TAGS_PART_1 = resource( "promoted-tags-1.nlp" ) ;

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


  public interface BookWithEmptyPart {
    Directory dir = directory( "book-with-empty-part" ) ;

    Resource BOOK = resource( "book.nlb" ) ;
    Resource EMPTY_NOVELLA = resource( "empty-part.nlp" ) ;

  }


  public interface Served {
    Directory dir = directory( "served" ) ;

    Resource GOOD_PART = resource( "good.nlp" ) ;
    Resource GOOD_BOOK = resource( "good.nlb" ) ;
    Resource BROKEN_NOVELLA = resource( "broken.nlp" ) ;
    Resource BROKEN_BOOK_BAD_REFERENCED_NOVELLA = resource( "book-broken-1.nlb" ) ;
    Resource BROKEN_BOOK_BAD_SCANNED_NOVELLA = resource( "book-bad-scanned-part.nlb" ) ;
    Resource BOOK_ALTERNATE_XSL = resource( "book-alternatexsl.nlb" ) ;

    public interface Style {
      Directory dir = directory( "style" ) ;

      Resource VOID_XSL = resource( "void.xsl" ) ;
    }

  }
  
  public interface Parts {
    Directory dir = directory( "parts" ) ;

    Resource NOVELLA_BROKEN_CANNOTPARSE = resource( "broken-cannotparse.nlp" ) ;
    Resource NOVELLA_JUST_SECTIONS = resource( "just-sections.nlp" ) ;
    Resource NOVELLA_MANY_IDENTIFIERS = resource( "many-identifiers.nlp" ) ;
    Resource NOVELLA_SOME_IDENTIFIERS_1 = resource( "some-identifiers-1.nlp" ) ;
    Resource NOVELLA_SOME_IDENTIFIERS_2 = resource( "some-identifiers-2.nlp" ) ;
    Resource NOVELLA_MISSING_IMAGES = resource( "missing-images.nlp" ) ;
    Resource NOVELLA_NO_CHAPTER = resource( "no-chapter.nlp" ) ;
    Resource NOVELLA_ONE_WORD = resource( "one-word.nlp" ) ;
    Resource NOVELLA_SIMPLE_STRUCTURE = resource( "simple-structure.nlp" ) ;
    Resource NOVELLA_UTF8_BOM = resource( "utf8-with-bom.nlp" ) ;

  }

  public interface Identifiers {
    Directory dir = directory( "identifiers" ) ;

    Resource BOOK_1 = resource( "identifiers-book1.nlb" ) ;
    Resource NOVELLA_1 = resource( "identifiers-1.nlp" ) ;
    Resource BOOK_2 = resource( "identifiers-book2.nlb" ) ;
    Resource NOVELLA_2 = resource( "identifiers-2.nlp" ) ;
    Resource BOOK_3_STRAIGHT = resource( "identifiers-book3-straight.nlb" ) ;
    Resource BOOK_3_RECURSE = resource( "identifiers-book3-recurse.nlb" ) ;
    Resource BOOK_4 = resource( "identifiers-book4.nlb" ) ;

    interface Subdirectory3 {
      Directory dir = directory( "sub3" ) ;
      Resource PART_3 = resource( "identifiers-3.nlp" ) ;
    }

    interface Subdirectory4 {
      Directory dir = directory( "sub4" ) ;
      Resource NOVELLA_4_0 = resource( "identifiers-4-0.nlp" ) ;
      Resource NOVELLA_4_1 = resource( "identifiers-4-1.nlp" ) ;
    }
  }

  
  public interface XslFormatting {
    Directory dir = directory( "styles" ) ;

    Resource PART_SOMECHAPTERS = resource( "some-chapters.nlp" ) ;
    Resource XSL_NUMBERING = resource( "format-numbering.xsl" ) ;
    Resource XSL_BADXPATH_1 = resource( "bad-xpath-1.xsl" ) ;
    Resource XSL_BADXPATH_2 = resource( "bad-xpath-2.xsl" ) ;
  }

  /**
   * Placeholder for deployable resources referenced by tests.
   */
  public interface MainResources {
    
    public interface Style {
      Directory dir = directory( "style" ) ;
      
      Resource DEFAULT_PDF_XSL = resource( "default-pdf.xsl" ) ;
    }
  }
}
