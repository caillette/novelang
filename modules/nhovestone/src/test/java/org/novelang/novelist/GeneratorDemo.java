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
package org.novelang.novelist;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.junit.Test;

/**
 * Demo for {@link WordGenerator}.
 *
 * @author Laurent Caillette
 */
public class GeneratorDemo {
  private static final int LINE_LENGTH = 80;

  @Test
  public void createSomeWords() {

    final Generator< Word > wordGenerator = new WordGenerator(
        GenerationDefaults.FOR_WORDS ) ;

    generateAndPrint( wordGenerator, 1000, true ) ;
  }


  @Test
  public void createSomeSentences() {

    final Generator< Sentence > sentenceGenerator =
        new SentenceGenerator( GenerationDefaults.FOR_SENTENCES ) ;

    generateAndPrint( sentenceGenerator, 100, true );
  }


  @Test
  public void createSomeLevels() {

    final Generator< Level > levelGenerator =
        new LevelGenerator( GenerationDefaults.FOR_LEVELS ) ;

    generateAndPrint( levelGenerator, 30, false ) ;
  }


// =======
// Fixture
// =======

  private static final Logger LOGGER = LoggerFactory.getLogger( GeneratorDemo.class ) ;


  private static void generateAndPrint(
      final Generator< ? extends TextElement > generator,
      final int iterationCount,
      final boolean addBlanks
  ) {
    final StringBuilder textBuilder = new StringBuilder() ;
    int lineLength = 0 ;
    
    for( int i = 0 ; i < iterationCount ; i ++ ) {

      final TextElement textElement = generator.generate() ;

      final String string = textElement.getLiteral() ;
      textBuilder.append( string ) ;

      if( addBlanks ) {
        textBuilder.append( " " ) ;
        lineLength += string.length() ;
        if( lineLength > 80  ) {
          textBuilder.append( "\n" ) ;
          lineLength = 0 ;
        }
      }
    }

    LOGGER.info( "Generated:\n\n", textBuilder ) ;
  }


}
