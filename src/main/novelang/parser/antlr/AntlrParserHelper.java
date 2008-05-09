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

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.structural.StructuralChapter;
import novelang.model.structural.StructuralSection;
import novelang.model.structural.StructuralInclusion;
import com.google.common.base.Objects;

/**
 * @author Laurent Caillette
 */
public final class AntlrParserHelper {

  private AntlrParserHelper() { }

  public static Location createLocation( LocationFactory factory, TokenStream input ) {
    return factory.createLocation(
        ( ( Token ) input.LT( 1 ) ).getLine(),
        ( ( Token ) input.LT( 1 ) ).getCharPositionInLine()
    ) ;
  }

  public static StructuralSection createSection( StructuralChapter chapter, TokenStream input ) {
    final Location location = AntlrParserHelper.createLocation( chapter, input ) ;
      return chapter.createSection( location ) ;
  }

  public static StructuralInclusion createInclusion(
      StructuralSection section,
      TokenStream input,
      String identifier
  ) {
    final Location location = AntlrParserHelper.createLocation( section, input ) ;
      return section.createInclusion( location, identifier ) ;    
  }

  public static int parseReversibleNumber( String number ) {
    number = Objects.nonNull( number ) ;
    final int minusSignAdjustment ;
    if( number.endsWith( "-") ) {
      number = number.substring( 0, number.length() - 1 ) ;
      minusSignAdjustment = -1 ;
    } else {
      minusSignAdjustment = 1 ;
    }
    return Integer.parseInt( number ) * minusSignAdjustment ;

  }

  public static void addParagraph(
      StructuralInclusion inclusion,
      TokenStream input,
      String reversibleNumber
  ) {
    final Location location = AntlrParserHelper.createLocation( inclusion, input ) ;
    inclusion.addParagraph(
        location,
        AntlrParserHelper.parseReversibleNumber( reversibleNumber )
    ) ;    
  }

  public static void addParagraphRange(
      StructuralInclusion inclusion,
      TokenStream input,
      String bound1,
      String bound2
  ) {
    final Location location = AntlrParserHelper.createLocation( inclusion, input ) ;
    inclusion.addParagraphRange(
        location,
        AntlrParserHelper.parseReversibleNumber( bound1 ),
        AntlrParserHelper.parseReversibleNumber( bound2 )
    ) ;
  }


}
