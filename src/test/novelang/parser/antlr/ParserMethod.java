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
package novelang.parser.antlr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.RecognitionException;
import org.junit.Assert;
import novelang.common.LanguageTools;
import novelang.common.ReflectionTools;
import novelang.common.SyntacticTree;
import novelang.treemangling.SeparatorsMangler;


/**
 * Enumeration-based approach for calling parser methods.
 * 
 * @author Laurent Caillette
 */
public class ParserMethod {

  protected ParserMethod( final String methodName ) {
    this.method = ReflectionTools.getMethod( NovelangParser.class, methodName ) ;
  }

  private final Method method ;

  /**
   * Returns the tree object contained by parser's result object.
   */
  public SyntacticTree getTree( final CustomDelegatingParser parser ) {
    final Object node;
    try {
      node = parser.callParserMethod();
    } catch( RecognitionException e ) {
      parser.getDelegate().report( e ) ;
      return null ;
    }
    if( node instanceof CommonErrorNode ) {
      final CommonErrorNode errorNode = ( CommonErrorNode ) node ;
      parser.getDelegate().report( errorNode.trappedException ) ;
      return null ;
    }
    
    return ( SyntacticTree ) node  ;
  }


  public SyntacticTree createTree( final String text ) {
    final CustomDelegatingParser parser = new CustomDelegatingParser( method, text ) ;
    final SyntacticTree tree = getTree( parser ) ;
    AntlrTestHelper.checkSanity( parser ) ;
    return tree ;    
  }
  
  public void checkTreeAfterSeparatorRemoval( 
      final String text, 
      final SyntacticTree expectedTree 
  ) {
//    final SyntacticTree actualTree = createTree( text ) ;
    final SyntacticTree actualTree = SeparatorsMangler.removeSeparators( createTree( text ) ) ;
    TreeFixture.assertEqualsNoSeparators( expectedTree, actualTree ) ;
    
  }
  
  public void checkTree( final String text, final SyntacticTree expectedTree ) {
    final SyntacticTree actualTree = createTree( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
    
  }
  
  public void checkFails( final String text ) {
    final CustomDelegatingParser parser = new CustomDelegatingParser( method, text ) ;
    getTree( parser ) ;
    final String readableProblemList = 
        AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem() ;
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
    
  }

}