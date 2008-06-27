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

package novelang.batch;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Laurent Caillette
 */
public class BatchParametersTest {

  @Test( expected = ParametersException.class )
  public void noArgumentsThrowsException() throws ParametersException {
    buildFrom() ;
  }

  @Test( expected = ParametersException.class )
  public void noDocumentThrowsException() throws ParametersException {
    buildFrom( "-t", "foo" ) ;
  }

  @Test
  public void printHelp() throws IOException {
    System.out.println( BatchParameters.HELP ) ;
  }

  @Test
  public void helpShort() throws ParametersException {
    Assert.assertTrue( parse( "-h" ).isHelp() ) ;
  }

  @Test
  public void helpLong() throws ParametersException {
    Assert.assertTrue( parse( "--help" ).isHelp() ) ;
  }

  @Test
  public void helpSymbol() throws ParametersException {
    Assert.assertTrue( parse( "-?" ).isHelp() ) ;
  }

  @Test
  public void helpNotFooledByOtherCommand() throws ParametersException {
    Assert.assertTrue( parse( "-targetDirectory", "foo", "-?" ).isHelp() ) ;
  }

  @Test
  public void targetDirectoryShort() throws ParametersException {
    Assert.assertEquals(
        "my/directory",
        parse( "-t", "my/directory" ).getTargetDirectory().getPath()
    ) ;
  }

  @Test
  public void targetDirectoryLong() throws ParametersException {
    Assert.assertEquals(
        "my/directory",
        parse( "--targetDirectory", "my/directory" ).getTargetDirectory().getPath()
    ) ;
  }

  @Test
  public void documents() throws ParametersException {
    Assert.assertEquals(
        Arrays.asList( "document1", "document2" ),
        parse( "document1", "document2" ).getDocuments() 
    ) ;
  }




  private static final BatchParameters parse( String... args ) throws ParametersException {
    return BatchParameters.parse( args ) ;
  }

  private static final BatchParameters buildFrom( String... args ) throws ParametersException {
    return BatchParameters.buildFrom( args ) ;
  }

}
