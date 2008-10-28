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

import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenSource;

/**
 */
public class DebuggableGrammarLauncher {

  public static void main( String[] args ) throws IOException {
/*    // ANTLRWorks 1.2.1 generates this for Novelang-ante-ANTLR-3.1.1:
    NovelangLexer lex = new NovelangLexer(new ANTLRFileStream("/Users/Laurent/Novelang/antlrworks/__Test___input.txt"));
    CommonTokenStream tokens = new CommonTokenStream(lex);

    NovelangParser g = new NovelangParser(tokens, 49100, null);
    try {
        g.part();
    } catch (RecognitionException e) {
        e.printStackTrace();
    }
*/

    NovelangLexer lex = new NovelangLexer( 
        new ANTLRFileStream( "/Users/Laurent/Novelang/antlrworks/__Test___input.txt" ) );
    CommonTokenStream tokens = new CommonTokenStream( lex );

    NovelangParser g = new NovelangParser( tokens, 49100, null );
    try {
      g.part();
    } catch( RecognitionException e ) {
      e.printStackTrace();
    }

  }


}
