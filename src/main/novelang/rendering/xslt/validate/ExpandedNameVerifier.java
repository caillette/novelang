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
package novelang.rendering.xslt.validate;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.common.Location;

/**
 * Given an XPath expression, this class checks that all expanded names with a given prefix
 * have known element names.
 * <p>
 * In XPath specification, an
 * <a href="http://www.w3.org/TR/xpath#dt-expanded-name">expanded name</a>
 * is a pair "consisting of a local part" (XML node name) "and a possibly null namespace URI".
 * <p>
 * This is useful to ensure an XSLT keeps synchronized with the tokens of a Novelang grammar.
 *
 * @author Laurent Caillette
 */
public class ExpandedNameVerifier {

  private static final Logger LOGGER = LoggerFactory.getLogger( ExpandedNameVerifier.class ) ;

  private final Set< String > nodeNames ;

  public ExpandedNameVerifier( Set< String > nodeNames ) {
    Preconditions.checkContentsNotNull( nodeNames ) ;
    this.nodeNames = nodeNames ;
  }

  private static final String XPATH_PATTERN_FRAGMENT = ":([0-9a-zA-Z\\-_]+)" ;

  private String xmlPrefix = null ;
  private Pattern xpathPattern = null ;

  public void setXmlPrefix( String prefix ) {
    Preconditions.checkNotNull( prefix ) ;
    Preconditions.checkState( xpathPattern == null ) ;
    xmlPrefix = prefix ;
    xpathPattern = Pattern.compile( prefix + XPATH_PATTERN_FRAGMENT ) ;
    LOGGER.debug( "Crafted regex: " + xpathPattern.toString() ) ;
  }

  public void unsetXmlPrefix() {
    Preconditions.checkNotNull( xpathPattern ) ;
    xpathPattern = null ;
    xmlPrefix = null ;
  }

  public String getXmlPrefix() {
    return xmlPrefix ;
  }

  private final List< BadExpandedName > badExpandedNames = Lists.newLinkedList() ;

  public void verify( Location location, String xpath ) {
    if ( null != xpathPattern ) {
      final Matcher matcher = xpathPattern.matcher( xpath ) ;
      while( matcher.find() && matcher.groupCount() == 1 ) {
        final String elementName = matcher.group( 1 ) ;
        if( ! nodeNames.contains( elementName ) ) {
          badExpandedNames.add( new BadExpandedName( location, xpath, xmlPrefix, elementName ) ) ;
        }
      }
    }
  }

  public Iterable< BadExpandedName > getBadExpandedNames() {
    return ImmutableList.copyOf( badExpandedNames ) ;
  }

  public void checkNoBadExpandedNames() throws BadExpandedNamesException {
    if( ! badExpandedNames.isEmpty() ) {
      throw new BadExpandedNamesException( getBadExpandedNames() ) ;
    }
  }


}
