/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package novelang.model.implementation;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ClassUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import novelang.ResourceTools;
import novelang.ScratchDirectoryFixture;
import novelang.model.common.Location;
import novelang.model.common.Tree;
import static novelang.model.common.NodeKind.*;
import novelang.parser.antlr.TreeHelper;
import static novelang.parser.antlr.TreeHelper.tree;

/**
 * @author Laurent Caillette
 */
public class PartTest {
  private static final String STRUCTURE_1 = TestResources.STRUCTURE_1 ;
  private static final String SECTIONS_1 = TestResources.SECTIONS_1 ;
  private File book1Directory;
  private String testName;
  private Book book1;
  private Location location;

  @Test
  public void loadPartOk() throws IOException {
    final Part part = book1.createPart( SECTIONS_1, location ) ;
    part.load() ;
    final Tree partTree = part.getTree();
    Assert.assertNotNull( partTree ) ;
    final Tree expected = tree( PART,  
        tree( SECTION,
            tree( IDENTIFIER, tree( WORD, "Section1nlp" ) ),
            tree( PARAGRAPH_PLAIN, tree( WORD, "p00" ), tree( WORD, "w001" ) )
        ),
        tree( SECTION,
            tree( TITLE, tree( WORD, "section1" ), tree( WORD, "w11" ) ),
            tree( PARAGRAPH_PLAIN,
                tree( WORD, "p10" ),
                tree( WORD, "w101" ),
                tree( WORD, "w102" )
            )
        )
    ) ;
    TreeHelper.assertEquals( expected, partTree ) ;

//=== Section1nlp
//
//p00 w001
//
//=== 'section1 w11
//
//p10 w101
//w102
    Assert.assertFalse( part.getProblems().iterator().hasNext() ) ;
  }

  @Test
  public void findIdentifiersOk() throws IOException {
    final Part part = book1.createPart( SECTIONS_1, location ) ;
    part.load() ;
    part.getIdentifiers() ;
  }

  @Before
  public void setUp() throws IOException {
    testName = ClassUtils.getShortClassName( getClass() );
    final ScratchDirectoryFixture scratchDirectoryFixture =
        new ScratchDirectoryFixture( testName ) ;
    book1Directory = scratchDirectoryFixture.getBook1Directory();
    ResourceTools.copyResourceToFile( getClass(), STRUCTURE_1, book1Directory ) ;
    ResourceTools.copyResourceToFile( getClass(), SECTIONS_1, book1Directory ) ;
    book1 = new Book( testName, new File( book1Directory, STRUCTURE_1 ) );
    location = book1.createLocation( 0, 0 );
  }

}
