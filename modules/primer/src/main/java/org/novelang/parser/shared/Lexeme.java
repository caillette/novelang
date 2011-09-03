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
package org.novelang.parser.shared;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import com.google.common.base.Preconditions;

/**
 * Holds lexeme declaration as found in Novelang grammar.
 *
 * @author Laurent Caillette
 */
public class Lexeme {
  private final String unicodeName ;
  private final Character character ;
  private final String htmlEntityName ;
  private final String ascii62;

  public Lexeme( 
      final String unicodeName, 
      final Character character, 
      final String htmlEntityName,
      final String ascii62
  ) {
    Preconditions.checkArgument( ! StringUtils.isBlank( unicodeName ) ) ;
    Preconditions.checkNotNull( character ) ;
    this.unicodeName = unicodeName;
    this.character = character ;
    this.htmlEntityName = htmlEntityName ;
    this.ascii62 = ascii62;
  }

  /**
   * Returns the same name as in ANTLR grammar.
   * @return a non-null, non-empty String.
   */
  public String getUnicodeName() {
    return unicodeName;
  }

  /**
   * Returns the character.
   * @return a non-null object.
   */
  public Character getCharacter() {
    return character;
  }

  /**
   * Returns the HTML entity name if it was declared in ANTLR grammar as comment.
   * @return a possibly null String.
   */
  public String getHtmlEntityName() {
    return htmlEntityName;
  }

  public boolean hasHtmlEntityName() {
    return ! StringUtils.isBlank( htmlEntityName ) ;
  }

  /**
   * Returns the ASCII representation limited to a set of 62 (a-z, A-Z, 0-9) characters
   * if it was declared in ANTLR grammar as comment.
   * @return a possibly null String.
   */
  public String getAscii62() {
    return ascii62;
  }
  
  public boolean hasDiacriticlessRepresentation() {
    return ! StringUtils.isBlank( ascii62 ) ;
  }

  /**
   * Returns a human-readable representation to appear in generated Java source code.
   * @return a possibly null String.
   */
  public String getAsJavaComment() {
    if( character < 128
     && character != '\t'
     && character != '\b'
     && character != '\n'
     && character != '\\'
    ) {
      return "" + character ;
    }
    return null ;
  }

  /**
   * Returns a Unicode-escaped representation to appear in generated Java source code.
   * @return a non-null, non-empty String like {@code "\\u00ff"}.
   */
  public String getAsJavaCharacter() {
    switch( character ) {
      case '\\' :
        return "\\\\" ;
      case '"' :
        return "\\\"" ;
      case '\'' :
        return "\\'" ;
      default :
        return CharUtils.unicodeEscaped( character ) ;
    }
  }


  @Override
  public boolean equals( final Object o ) {
    if( this == o ) {
      return true ;
    }
    if( o == null || getClass() != o.getClass() ) {
      return false ;
    }

    final Lexeme that = ( Lexeme ) o ;

    if(   htmlEntityName != null
        ? ! htmlEntityName.equals( that.htmlEntityName )
        : that.htmlEntityName != null
    ) {
      return false ;
    }
    if(   character != null ?
        ! character.equals( that.character )
        : that.character != null
    ) {
      return false ;
    }
    if(   unicodeName != null ?
        ! unicodeName.equals( that.unicodeName )
        : that.unicodeName != null
    ) {
      return false ;
    }

    return true ;
  }

  @Override
  public int hashCode() {
    int result = unicodeName != null ? unicodeName.hashCode() : 0 ;
    result = 31 * result + ( character != null ? character.hashCode() : 0 ) ;
    result = 31 * result + ( htmlEntityName != null ? htmlEntityName.hashCode() : 0 ) ;
    return result;
  }

  @Override
  public String toString() {
    return "LexemeDeclaration[" +
        "tokenName='" + unicodeName + '\'' +
        ", character=" + character +
        ", htmlEntityName='" + htmlEntityName + '\'' +
        ", ascii62='" + ascii62 + '\'' +
        ']';
  }
}
