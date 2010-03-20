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
package novelang.novelist;

import java.util.Locale;

import com.google.common.collect.ImmutableMap;

/**
 * Represents punctuation signs, and generates corresponding {@link TextElement}s
 * depending on a {@code Locale}. The locale is for supporting French typographic
 * rule that imposes a space preceding some punctuation signs.
 *
 * @author Laurent Caillette
 */
@SuppressWarnings( { "NonSerializableFieldInSerializableClass" } )
public enum Punctuation {

  FULL_STOP( "." ),
  EXCLAMATION_MARK( "!", true ),
  QUESTION_MARK( "?", true ),

  COMMA( "," ),
  SEMICOLON( ";", true ),
  COLON( ":", true )
  ;

  private final TextElement textElement ;
  private final TextElement textElementFrench ;

  private Punctuation( final String text ) {
    this( text, false ) ;
  }

  private Punctuation( final String text, final boolean frenchTreatment ) {
    this.textElement = new Element( text ) ;
    this.textElementFrench = frenchTreatment ? new Element( " " + text ) : this.textElement ;
  }

  private static class Element implements TextElement {
    private final String text ;
    public Element( final String text ) {
      this.text = text ;
    }
    public String getLiteral() {
      return this.text ;
    }
  }
  
  private TextElement get( final Locale locale ) {
    if( locale.equals( Locale.FRENCH ) ) {
      return textElementFrench ;
    } else {
      return textElement ;
    }
  }

  public static TextElement getMiddle( final Locale locale, final float percentage ) {
    return MIDDLES.get( percentage ).get( locale ) ;
  }


  public static TextElement getEnding( final Locale locale, final float percentage ) {
    return ENDINGS.get( percentage ).get( locale ) ;
  }


  private static final Frequency< Punctuation > ENDINGS = new Frequency< Punctuation >(
      "Punctuation endings",
      new ImmutableMap.Builder< Punctuation, Float >()
          .put( FULL_STOP, 70.0f )
          .put( EXCLAMATION_MARK, 15.0f )
          .put( QUESTION_MARK, 15.0f )
          .build()
  ) { } ;

  private static final Frequency< Punctuation > MIDDLES = new Frequency< Punctuation >(
      "Punctuation middles",
      new ImmutableMap.Builder< Punctuation, Float >()
          .put( COMMA, 80.0f )
          .put( SEMICOLON, 10.0f )
          .put( COLON, 10.0f )
          .build()
  ) { } ;

}
