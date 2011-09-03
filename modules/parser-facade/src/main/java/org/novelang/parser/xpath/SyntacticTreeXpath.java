/*
 * Copyright (C) 2011 Laurent Caillette
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

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.Treepath;

/**
 * Type-safe XPath implementation.
 * There is some cheating with namespaces: because it will always be "n:" its made
 * optional and pre-processed by a regex for removal before passing it to the
 * {@link SyntacticTreeNavigator} which doesn't support it.
 *
 * @author Laurent Caillette
 */
public class SyntacticTreeXpath {

  private final XPath xpath ;

  private static final class UntypedXpath extends BaseXPath {
    public UntypedXpath( final String xpathExpression ) throws JaxenException {
      super( xpathExpression, SyntacticTreeNavigator.getInstance() ) ;
    }
  }

  /*package*/ static XPath createUntypedXPath( final String xpathExpression )
      throws JaxenException
  {
    return new UntypedXpath( xpathExpression ) ;
  }
  

  public SyntacticTreeXpath( final String xpathExpression ) throws JaxenException {
    xpath = createUntypedXPath( removeNamespaces( xpathExpression ) ) ;
  }

  @SuppressWarnings( { "unchecked" } )
  public ImmutableList< Treepath< SyntacticTree > > selectNodes( final SyntacticTree tree )
      throws JaxenException
  {
    return ImmutableList.< Treepath< SyntacticTree > >copyOf(
        xpath.selectNodes( Treepath.create( tree ) ) ) ;
  }

  /**
   * We're lucky: axis selector like {@code child} don't end by a "n" so we shouldn't get
   * confused by them.
   * The regex below selects "n:" prefix if there is no preceding letter and if there
   * is a letter right after it. 
   */
  private static final Pattern NAMESPACE_REMOVER =
      Pattern.compile( "((?<![0-9a-zA-Z_\\-])n:(?=\\w))" ) ;

  private static String removeNamespaces( final String xpathExpression ) {
    return NAMESPACE_REMOVER.matcher( xpathExpression ).replaceAll( "" );
  }


}
