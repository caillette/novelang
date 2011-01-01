/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.parser.xpath;

import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;
import org.novelang.common.tree.TreepathTools;
import org.novelang.parser.NodeKind;

import static org.novelang.parser.NodeKindTools.tokenNameAsXmlElementName;

/**
 * Jaxen-specific logic to apply XPath expressions to a {@link Treepath} of
 * {@link SyntacticTree}s.
 * <p>
 * TODO: add a set of {@link NodeKind}s recognized as elements.
 * <p>
 * TODO (not urgent): suppport namespaces in an orthodox manner
 *     with {@link org.jaxen.XPath#addNamespace(java.lang.String, java.lang.String)}. 
 *
 * @author Laurent Caillette
 */
public class SyntacticTreeNavigator extends DefaultNavigator {

  private static final SyntacticTreeNavigator INSTANCE = new SyntacticTreeNavigator() ;

  public static SyntacticTreeNavigator getInstance() {
    return INSTANCE ;
  }




  @Override
  public boolean isDocument( final Object object ) {
    return isDocument( asTreepath( object ).getTreeAtEnd().getNodeKind() );
  }

  @Override
  public boolean isElement( final Object object ) {
    final NodeKind nodeKind = asTreepath( object ).getTreeAtEnd().getNodeKind() ;
    return nodeKind != null && ! isDocument( nodeKind ) ;
  }

  @Override
  public boolean isAttribute( final Object object ) {
    return false ;
  }

  @Override
  public boolean isNamespace( final Object object ) {
    return false ;
  }

  @Override
  public boolean isComment( final Object object ) {
    return false ;
  }

  @Override
  public boolean isText( final Object object ) {
    return asTreepath( object ).getTreeAtEnd().getNodeKind() == null ;
  }

  @Override
  public boolean isProcessingInstruction( final Object object ) {
    return false ;
  }



  @Override
  public String getNamespacePrefix( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }



  @Override
  public String getElementName( final Object object ) {
    return tokenNameAsXmlElementName( asTreepath( object ).getTreeAtEnd().getNodeKind().name() ) ;
  }

  @Override
  public String getElementNamespaceUri( final Object object ) {
//    return XmlNamespaces.TREE_NAMESPACE_URI ;
    return null ;
  }

  /**
   * Returns the text of this element, aggregated with the text of subelements.
   */
  @Override
  public String getElementStringValue( final Object object ) {
    final StringBuilder stringBuilder = new StringBuilder() ;
    final Treepath< SyntacticTree > treepath = asTreepath( object ) ;
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
      final SyntacticTree child = tree.getChildAt( i ) ;
      if( isElement( child ) ) {
        stringBuilder.append( getElementStringValue( Treepath.create( treepath, i ) ) ) ;
      } else if( isText( child ) ) {
        return getTextStringValue( Treepath.create( treepath, i ) ) ;
      } else {
        throw new IllegalArgumentException( "Unsupported: " + child ) ;
      }
    }
    return stringBuilder.toString() ;
  }



  @Override
  public String getElementQName( final Object object ) {
    return getElementName( object ) ;
  }

  @Override
  public String getAttributeNamespaceUri( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }

  @Override
  public String getAttributeName( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }



  @Override
  public String getAttributeQName( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }

  @Override
  public String getCommentStringValue( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }

  @Override
  public String getAttributeStringValue( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }

  @Override
  public String getNamespaceStringValue( final Object object ) {
    throw new UnsupportedOperationException( "Unexpected call" ) ;
  }

  @Override
  public String getTextStringValue( final Object object ) {
    return asTreepath( object ).getTreeAtEnd().getText() ;
  }

  @Override
  public Iterator getChildAxisIterator( final Object contextNode ) throws UnsupportedAxisException {
    // Testing both is more robust that checking #getNodeKind() != null if we add
    // a set of supported NodeKinds.
    if( isElement( contextNode ) || isDocument( contextNode ) ) {
      return TreepathTools.iteratorOnChildren( asTreepath( contextNode ) ) ;
    }
    return JaxenConstants.EMPTY_ITERATOR ;
  }

  @Override
  public Object getParentNode( final Object contextNode ) throws UnsupportedAxisException {
    if( isDocument( contextNode ) ) {
      return JaxenConstants.EMPTY_ITERATOR ;
    }
    final Treepath<SyntacticTree> treepath = asTreepath( contextNode );
    Treepath< SyntacticTree > parent = treepath.getPrevious() ;
    if( parent == null ) {
      parent = treepath.getStart() ;
      if( parent.getTreeAtEnd() == treepath.getTreeAtEnd() ) {
        return null ;
      }
    }
    return parent ;
  }

  @Override
  public Object getDocumentNode( final Object contextNode ) {
    return asTreepath( contextNode ).getStart() ;
  }

  @Override
  public Iterator getParentAxisIterator( final Object contextNode ) 
      throws UnsupportedAxisException
  {
    if( isDocument( contextNode ) ) {
      return JaxenConstants.EMPTY_ITERATOR ;
    }
    final Treepath<SyntacticTree> treepath = asTreepath( contextNode );
    Treepath< SyntacticTree > parent = treepath.getPrevious() ;
    if( parent == null ) {
      parent = treepath.getStart() ;
    }
    return new SingleObjectIterator( parent ) ;

  }

  @Override
  public Iterator getNamespaceAxisIterator( final Object contextNode )
      throws UnsupportedAxisException
  {
    throw new UnsupportedAxisException( "Namespaces not supported" ) ;
  }

  @Override
  public Iterator getAttributeAxisIterator( final Object contextNode ) throws UnsupportedAxisException {
    throw new UnsupportedAxisException( "Namespaces not supported" ) ;
  }

  @Override
  public XPath parseXPath( final String xpathExpression ) throws SAXPathException {
    return SyntacticTreeXpath.createUntypedXPath( xpathExpression ) ;
  }



// =======
// Helpers
// =======

  @SuppressWarnings( { "unchecked" } )
  private static Treepath<SyntacticTree> asTreepath( final Object object ) {
    final Treepath< SyntacticTree > treepath = ( Treepath< SyntacticTree > ) object ;
    return treepath;
  }

  private static boolean isDocument( final NodeKind nodeKind ) {
    return nodeKind == NodeKind.OPUS
        || nodeKind == NodeKind.NOVELLA
    ;
  }


}
