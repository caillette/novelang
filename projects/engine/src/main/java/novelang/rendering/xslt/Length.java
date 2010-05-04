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
package novelang.rendering.xslt;

import novelang.system.LogFactory;
import novelang.system.Log;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Laurent Caillette
 */
public class Length {

  private static final Log LOG = LogFactory.getLog( Length.class ) ;


  public static Number positionUnderCharacterRemainderThreshold(
      final NodeList nodeList,
      final int characterCountThreshold
  ) {
    LOG.debug(
        "Finding position of node with less than %s characters in list of %s nodes",
        characterCountThreshold,
        nodeList.getLength()
    ) ;
    int total = 0 ;
    int lastMatchingNodeIndex = -1 ;
    for( int i = nodeList.getLength() - 1 ; i >= 0 ; i-- ) {
      final Node node = nodeList.item( i ) ;
      LOG.debug( "  Node[ %s ] = %s", i, node.getNodeName() ) ;
      final int count = countCharacters( node ) ;
      if( characterCountThreshold < total + count ) {
        break ;
      } else {
        total += count ;
        lastMatchingNodeIndex = i ;
      }
    }
    LOG.debug( "  Returning position %s", lastMatchingNodeIndex ) ;
    return lastMatchingNodeIndex ;
  }

  /**
   * Counts all the character of given node and following siblings.
   * @param node a non-null object.
   * @return -1 if something got wrong, a zero-or-positive integer otherwise.
   */
  public static Number countCharactersOfSelfAndFollowingSiblings( Node node ) {
//    LOGGER.debug( "Counting characters until end for %s", node.getNodeName() ) ;
    final Node parent = node.getParentNode() ;
    int characters ;
    if( null == parent ) {
      characters = -1 ;
    } else {
      characters = 0 ;
      do {
        characters += countCharacters( node ) ;
        node = node.getNextSibling() ;
      } while( node != null ) ;
    }
//    LOGGER.debug( "Returning characterCount: %s", characters ) ;
    return characters ;
  }

  private static int countCharacters( final Node node ) {
    int count = 0 ;
    final String textContent = node.getTextContent() ;
//    LOGGER.debug( "Node[ %s ].textContent = '%s'", node.getNodeName(), textContent ) ;
    if( null !=  textContent ) {
      count += textContent.length() ;
    }
    count += countCharacters( node.getChildNodes() ) ;
    return count ;
  }

  private static int countCharacters( final NodeList nodeList ) {
    int count = 0 ;
    for( int i = 0 ; i < nodeList.getLength() ; i++ ) {
      final Node node = nodeList.item( i ) ;
      count += countCharacters( node ) ;
    }
    return count ;
  }



}