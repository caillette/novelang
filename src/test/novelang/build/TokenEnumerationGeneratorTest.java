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

package novelang.build;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.ImmutableList;
import junit.framework.Assert;


/**
 *
 * @author Laurent Caillette
 */
public class TokenEnumerationGeneratorTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger( TokenEnumerationGeneratorTest.class ) ;

  @Test
  public void testLoadTemplate() {
    final String generated = TokenEnumerationGenerator.generateJavaEnumeration(
        "novelang.parser",
        "NodeKind",
        ImmutableList.of(
            new TokenEnumerationGenerator.Item( "FOO" ),
            new TokenEnumerationGenerator.Item( "BAR" ),
            new TokenEnumerationGenerator.Item( "SIGN_BAZ" )
        )
    ) ;
    LOGGER.debug( "Generated: \n{}", generated ) ;

  }

  @Test
  public void testFindTokens() {
    final String tokensDeclaration =
        "SOME_UNUSUED_STUFF ; \n" +
        "tokens { \n" +
        " STUFF ; \n" +
        " OTHER_STUFF;\n" +
        "}"
    ;
    final Iterable<TokenEnumerationGenerator.Item> tokens =
        TokenEnumerationGenerator.findAntlrTokens( tokensDeclaration ) ;
    final List< TokenEnumerationGenerator.Item > tokenList = ImmutableList.copyOf( tokens ) ;
    Assert.assertEquals( 2, tokenList.size() ) ;
    Assert.assertTrue(
        "Got: " + tokenList,
        tokenList.contains( new TokenEnumerationGenerator.Item( "STUFF" ) ) 
    ) ;
    Assert.assertTrue(
        "Got: " + tokenList,
        tokenList.contains( new TokenEnumerationGenerator.Item( "OTHER_STUFF" ) )
    ) ;
  }

}