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
import org.junit.Assert;
import novelang.common.LanguageTools;
import novelang.common.ReflectionTools;
import novelang.common.SyntacticTree;


/**
 * Enumeration-based approach for calling parser methods.
 * 
 * @author Laurent Caillette
 */
public class ParserMethod {

  protected ParserMethod( String methodName ) {
    this.method = ReflectionTools.getMethod( NovelangParser.class, methodName ) ;
  }

  private final Method method ;

  /**
   * Returns the tree object contained by parser's result object.
   */
  public SyntacticTree getTree( NovelangParser parser ) {
    final Object node = getNode( parser ) ;
    
    if( node instanceof CommonErrorNode ) {
      final CommonErrorNode errorNode = ( CommonErrorNode ) node ;
      LanguageTools.rethrowUnchecked( errorNode.trappedException ); 
    }
    
    return ( SyntacticTree ) node  ;
  }

  private Object getNode( NovelangParser parser ) {
    final Object antlrResult ;
    try {
      antlrResult = method.invoke( parser ) ;
    } catch ( IllegalAccessException e ) {
      throw new RuntimeException( e ) ;
    } catch ( InvocationTargetException e ) {
      throw new RuntimeException( e ) ;
    }

    final Method getTreeMethod = ReflectionTools.getMethod(
        antlrResult.getClass(),
        "getTree"
    ) ;


    final Object node = ReflectionTools.invoke( getTreeMethod, antlrResult ) ;
    return node;
  }

  public SyntacticTree createTree( String text ) {
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    final SyntacticTree tree = getTree( parser.getAntlrParser() ) ;
    AntlrTestHelper.checkSanity( parser ) ;
    return tree ;    
  }
  
  public void checkTreeAfterSeparatorRemoval( String text, SyntacticTree expectedTree ) {
//    final SyntacticTree actualTree = createTree( text ) ;
    final SyntacticTree actualTree = TreeFixture.removeSeparators( createTree( text ) ) ;
    TreeFixture.assertEqualsNoSeparators( expectedTree, actualTree ) ;
    
  }
  
  public void checkBareTree( String text, SyntacticTree expectedTree ) {
    final SyntacticTree actualTree = createTree( text ) ;
    TreeFixture.assertEquals( expectedTree, actualTree ) ;
    
  }
  
  public void checkFails( String text ) {
    final DelegatingPartParser parser = AntlrTestHelper.createPartParser( text ) ;
    getNode( parser.getAntlrParser() ) ;
    final String readableProblemList = 
        AntlrTestHelper.createProblemList( parser.getProblems() ) ;
    final boolean parserHasProblem = parser.hasProblem() ;
    Assert.assertTrue( readableProblemList, parserHasProblem ) ;
    
  }

}