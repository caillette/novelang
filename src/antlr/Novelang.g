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
grammar Novelang ;

options { output = AST ; }

//import AllTokens, Url ;

tokens {
  PART ;
  URL ;
}




@parser::members {
private novelang.parser.antlr.GrammarDelegate delegate =
    new novelang.parser.antlr.GrammarDelegate() ;

public void setGrammarDelegate( novelang.parser.antlr.GrammarDelegate delegate ) {
  this.delegate = delegate ;
}

@Override
public void emitErrorMessage( String string ) {
  if( null == delegate ) {
    super.emitErrorMessage( string ) ;
  } else {
    delegate.report( string ) ;
  }
}
}


// =========================
// Part, chapter and section
// =========================

section 
  : EQUALS_SIGN EQUALS_SIGN EQUALS_SIGN 
    WHITESPACE?
    title?
  ;

// =====================
// Paragraph and related
// =====================

/** Title is like a paragraph but it can't start by a URL as URL always start
 *  on the first column so it would clash with section / chapter introducer.
 */
title
  : (   smallListItemWithHyphenBullet
      | ( mixedDelimitedSpreadBlock 
          ( WHITESPACE mixedDelimitedSpreadBlock )* 
        )
    )
    ( WHITESPACE? SOFTBREAK 
      ( ( url ) => url
        | smallListItemWithHyphenBullet
        | ( WHITESPACE? mixedDelimitedSpreadBlock 
            ( WHITESPACE mixedDelimitedSpreadBlock )* 
          )        
      )
    )*    
  ;  


paragraph 
  : (   ( url ) => url
      | smallListItemWithHyphenBullet
      | ( mixedDelimitedSpreadBlock 
          ( WHITESPACE mixedDelimitedSpreadBlock )* 
        )
    )
    ( WHITESPACE? SOFTBREAK 
      ( ( url ) => url
        | smallListItemWithHyphenBullet
        | ( WHITESPACE? mixedDelimitedSpreadBlock 
            ( WHITESPACE mixedDelimitedSpreadBlock )* 
          )        
      )
    )*
    
  ;  
  
delimitedSpreadblock
  : parenthesizedSpreadblock
  | doubleQuotedSpreadBlock
  ;

delimitedSpreadblockNoDoubleQuotes
  : parenthesizedSpreadblock
  ;


// ===================
// Parenthesized stuff
// ===================
  
parenthesizedSpreadblock
  : LEFT_PARENTHESIS WHITESPACE?
    ( spreadBlockBody 
      WHITESPACE? 
    )
    RIGHT_PARENTHESIS
  ;

/** Everything in this rule implies a syntactic predicate, 
 *  so it will foll ANTLRWorks' interpreter.
 */
spreadBlockBody
  : (   
        ( ( SOFTBREAK url WHITESPACE? SOFTBREAK ) => 
              ( SOFTBREAK url SOFTBREAK ) 
        ) 
      | ( ( SOFTBREAK WHITESPACE? smallListItemWithHyphenBullet WHITESPACE? SOFTBREAK ) => 
                ( SOFTBREAK smallListItemWithHyphenBullet SOFTBREAK ) 
        )
      | ( ( SOFTBREAK WHITESPACE? )? 
          mixedDelimitedSpreadBlock
          ( WHITESPACE mixedDelimitedSpreadBlock )*
        )
    )
    (   
        ( ( SOFTBREAK url WHITESPACE? SOFTBREAK ) => 
              ( SOFTBREAK url SOFTBREAK ) 
        ) 
      | ( ( SOFTBREAK WHITESPACE? smallListItemWithHyphenBullet WHITESPACE? SOFTBREAK ) => 
                ( SOFTBREAK smallListItemWithHyphenBullet SOFTBREAK ) 
          )
      | ( ( WHITESPACE? SOFTBREAK WHITESPACE? mixedDelimitedSpreadBlock ) =>
                ( SOFTBREAK mixedDelimitedSpreadBlock )
          ( WHITESPACE 
            mixedDelimitedSpreadBlock
          )*
        )             
    )* 
    // Missing: SOFTBREAK after last mixedDelimitedSpreadBlock
  ;  

  
mixedDelimitedSpreadBlock  
  : ( word ( ( punctuationSign | delimitedSpreadblock )+ word? )? ) 
  | ( ( punctuationSign | delimitedSpreadblock )+ word? ) 
  ;
                


// ===================
// Double quotes stuff
// ===================

doubleQuotedSpreadBlock
  : DOUBLE_QUOTE WHITESPACE?
    ( spreadBlockBodyNoDoubleQuotes 
      WHITESPACE? 
    )?
    DOUBLE_QUOTE
  ;

/** Don't allow URLs inside double quotes because of weird errors.
 *  Not wishable anyways because in a near future URLs may be preceded by double-quoted title.
 */
spreadBlockBodyNoDoubleQuotes
  : (   ( SOFTBREAK WHITESPACE? smallListItemWithHyphenBullet WHITESPACE? SOFTBREAK )
      | ( ( SOFTBREAK WHITESPACE? )? 
          mixedDelimitedSpreadBlockNoDoubleQuotes
          ( WHITESPACE mixedDelimitedSpreadBlockNoDoubleQuotes )*
        )
    )
    (   ( SOFTBREAK WHITESPACE? smallListItemWithHyphenBullet WHITESPACE? SOFTBREAK )
      | ( WHITESPACE? SOFTBREAK WHITESPACE? 
          mixedDelimitedSpreadBlockNoDoubleQuotes
          ( WHITESPACE 
            mixedDelimitedSpreadBlockNoDoubleQuotes
          )*
        )             
    )* 
    // Missing: SOFTBREAK after last mixedDelimitedSpreadBlockNoDoubleQuotes
  ;  

mixedDelimitedSpreadBlockNoDoubleQuotes
  : ( word ( ( punctuationSign | delimitedSpreadblockNoDoubleQuotes )+ word? )? ) 
  | ( ( punctuationSign | delimitedSpreadblockNoDoubleQuotes )+ word? ) 
  ;
  





// =====
// Lists
// =====

bigListItemWithTripleHyphenBullet
  : HYPHEN_MINUS HYPHEN_MINUS HYPHEN_MINUS 
    ( WHITESPACE mixedDelimitedSpreadBlock )*
    ( WHITESPACE? SOFTBREAK 
      (   ( WHITESPACE? mixedDelimitedSpreadBlock 
            ( WHITESPACE mixedDelimitedSpreadBlock )* 
          )
        | ( url ) => url
        | smallListItemWithHyphenBullet
      )
    )*

  ;

smallListItemWithHyphenBullet
  : HYPHEN_MINUS ( WHITESPACE word )+
  ;
  
  
// ===========  
// Punctuation
// ===========  

leadingPunctuationSign
  : ELLIPSIS
  ;

punctuationSign
  : COMMA 
  | FULL_STOP
  | ELLIPSIS 
  | QUESTION_MARK 
  | EXCLAMATION_MARK 
  | SEMICOLON 
  | COLON
  | APOSTROPHE 
  ;

  
  
// ===================================
// Part-related URL rules
// http://www.ietf.org/rfc/rfc1738.txt
// ===================================

url
  : ( http = httpUrl )//-> ^( URL { delegate.createTree( URL, $http.text ) } ) )
  | ( file = fileUrl )//-> ^( URL { delegate.createTree( URL, $file.text ) } ) ) 
  ;
  
fileUrl                                   
//  : ( 'f' 'i' 'l' 'e' COLON ) //=> 'f' 'i' 'l' 'e' COLON // Grammatical ambiguity in the spec
    // following removed from the spec!
//    SOLIDUS SOLIDUS urlHost SOLIDUS 
//    urlFilePath
  :	LATIN_SMALL_LETTER_F LATIN_SMALL_LETTER_I LATIN_SMALL_LETTER_L LATIN_SMALL_LETTER_E 
    COLON SOLIDUS? urlFilePath 
  ;
  
httpUrl                                       // Grammatical ambiguity in the spec
  : ( LATIN_SMALL_LETTER_H 
      LATIN_SMALL_LETTER_T 
      LATIN_SMALL_LETTER_T 
      LATIN_SMALL_LETTER_P 
      COLON 
      SOLIDUS 
      SOLIDUS 
    ) => 
        LATIN_SMALL_LETTER_H 
        LATIN_SMALL_LETTER_T 
        LATIN_SMALL_LETTER_T 
        LATIN_SMALL_LETTER_P 
        COLON 
        SOLIDUS 
        SOLIDUS 
    urlHostPort 
    ( SOLIDUS httpUrlPath 
      ( QUESTION_MARK httpUrlSearch )? 
    )?
  ;



urlIpSchemePart
  : SOLIDUS SOLIDUS urlLogin ( SOLIDUS urlPath ) 
  ;
  
urlLogin
  : ( urlUser ( COLON urlPassword )? COMMERCIAL_AT )? urlHostPort 
  ;
  
urlHostPort
  : urlHost ( ( COLON urlPort ) )?
  ;
  
urlHost
  : urlHostName
  | urlHostNumber
  ;
  
urlHostName
  : ( urlDomainLabel FULL_STOP )* urlTopLabel
  ;
  
urlDomainLabel
  : urlAlphaDigit 
  | ( urlAlphaDigit ( urlAlphaDigit | HYPHEN_MINUS )* urlAlphaDigit )
  ;
  
urlTopLabel
  : urlAlpha 
  | ( urlAlpha ( urlAlphaDigit | HYPHEN_MINUS )* urlAlphaDigit )
  ;
  
urlHostNumber
  : urlDigits FULL_STOP urlDigits FULL_STOP urlDigits FULL_STOP urlDigits 
  ;
  
urlPort
  : digit+ 
  ;
  
urlUser
  : (   urlUChar  
      | SEMICOLON
      | QUESTION_MARK
      | AMPERSAND
      | EQUALS_SIGN
    )*
  ;

urlPassword
  : (   urlUChar  
      | SEMICOLON
      | QUESTION_MARK
      | AMPERSAND
      | EQUALS_SIGN
    )*
  ;

urlPath
  : urlXChar*
  ;
  
urlFilePath
  : urlFileSegment ( SOLIDUS urlFileSegment )*
  ;
  
urlFileSegment
  : (   urlUChar     
      | QUESTION_MARK
      | COLON
      | SEMICOLON
      | COMMERCIAL_AT
      | AMPERSAND
      | EQUALS_SIGN
      | TILDE         // Not in the spec.
      | NUMBER_SIGN   // Not in the spec.
    )+                // + added from the spec.
  ;

  
httpUrlPath
  : httpUrlSegment 
    ( ( SOLIDUS httpUrlPath ) => SOLIDUS httpUrlPath )* // Grammatical ambiguity in the spec again!
  ;
  
httpUrlSegment
  : (   urlUChar
      | SEMICOLON
      | COLON
      | COMMERCIAL_AT
      | AMPERSAND
      | EQUALS_SIGN
      | TILDE          // Not in the spec.
      | NUMBER_SIGN    // Not in the spec.
    )*
  ;  
  
httpUrlSearch
  : (   urlUChar
      | SEMICOLON
      | COLON
      | COMMERCIAL_AT
      | AMPERSAND
      | EQUALS_SIGN
    )*
  ;  
               
urlAlpha
  : hexLetter 
  | nonHexLetter
  ;
  
urlAlphaDigit
  : hexLetter 
  | nonHexLetter
  | digit
  ;
  
urlDigits
  : digit+
  ;
  
urlSafe
  : DOLLAR_SIGN
  | HYPHEN_MINUS
  | LOW_LINE
  | FULL_STOP
  | PLUS_SIGN
  ;
  
urlExtra
  : EXCLAMATION_MARK
  | ASTERISK
  | APOSTROPHE
  | LEFT_PARENTHESIS
  | RIGHT_PARENTHESIS
  | COMMA
  ;
  
urlReserved
  : SEMICOLON
  | SOLIDUS
  | QUESTION_MARK
  | COLON
  | COMMERCIAL_AT
  | AMPERSAND
  | EQUALS_SIGN
  ;
  
urlHex
  : digit | hexLetter
  ;
  
urlEscape
  : PERCENT_SIGN urlHex urlHex
  ;
  
urlUnreserved
  : hexLetter 
  | nonHexLetter
  | digit 
  | urlSafe 
  | urlExtra
  ;
  
urlUChar
  : urlUnreserved
  | urlEscape
  ;
  
urlXChar
  : urlUnreserved
  | urlReserved
  | urlEscape
  ;











// ====================
// Low-level constructs
// ====================

word
  : ( letter | digit ) ( HYPHEN_MINUS? ( letter | digit ) )*
  ;
  
mediumSpace
  : ( WHITESPACE? SOFTBREAK WHITESPACE? ) | WHITESPACE ;  
  


// Symbols


letter : hexLetter | nonHexLetter ;

digit 
  : DIGIT_0 | DIGIT_1 | DIGIT_2 | DIGIT_3 | DIGIT_4 
  | DIGIT_5 | DIGIT_6 | DIGIT_7 | DIGIT_8 | DIGIT_9 
;

hexLetter
  : LATIN_SMALL_LETTER_A   | LATIN_SMALL_LETTER_B   | LATIN_SMALL_LETTER_C 
  | LATIN_SMALL_LETTER_D   | LATIN_SMALL_LETTER_E   | LATIN_SMALL_LETTER_F  
  | LATIN_CAPITAL_LETTER_A | LATIN_CAPITAL_LETTER_B | LATIN_CAPITAL_LETTER_C 
  | LATIN_CAPITAL_LETTER_D | LATIN_CAPITAL_LETTER_E | LATIN_CAPITAL_LETTER_F  
  ;

nonHexLetter 
  : LATIN_SMALL_LETTER_G   | LATIN_SMALL_LETTER_H   | LATIN_SMALL_LETTER_I 
  | LATIN_SMALL_LETTER_J   | LATIN_SMALL_LETTER_K   | LATIN_SMALL_LETTER_L  
  | LATIN_SMALL_LETTER_M   | LATIN_SMALL_LETTER_N   | LATIN_SMALL_LETTER_O  
  | LATIN_SMALL_LETTER_P   | LATIN_SMALL_LETTER_Q   | LATIN_SMALL_LETTER_R  
  | LATIN_SMALL_LETTER_S   | LATIN_SMALL_LETTER_T   | LATIN_SMALL_LETTER_U
  | LATIN_SMALL_LETTER_V   | LATIN_SMALL_LETTER_W   | LATIN_SMALL_LETTER_X  
  | LATIN_SMALL_LETTER_Y   | LATIN_SMALL_LETTER_Z

  | LATIN_CAPITAL_LETTER_G | LATIN_CAPITAL_LETTER_H | LATIN_CAPITAL_LETTER_I
  | LATIN_CAPITAL_LETTER_J | LATIN_CAPITAL_LETTER_K | LATIN_CAPITAL_LETTER_L  
  | LATIN_CAPITAL_LETTER_M | LATIN_CAPITAL_LETTER_N | LATIN_CAPITAL_LETTER_O  
  | LATIN_CAPITAL_LETTER_P | LATIN_CAPITAL_LETTER_Q | LATIN_CAPITAL_LETTER_R  
  | LATIN_CAPITAL_LETTER_S | LATIN_CAPITAL_LETTER_T | LATIN_CAPITAL_LETTER_U
  | LATIN_CAPITAL_LETTER_V | LATIN_CAPITAL_LETTER_W | LATIN_CAPITAL_LETTER_X  
  | LATIN_CAPITAL_LETTER_Y | LATIN_CAPITAL_LETTER_Z

  | LATIN_SMALL_LETTER_A_WITH_GRAVE
  | LATIN_CAPITAL_LETTER_A_WITH_GRAVE

  | LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX

  | LATIN_SMALL_LETTER_A_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS

  | LATIN_SMALL_LETTER_AE
  | LATIN_CAPITAL_LETTER_AE
  
  | LATIN_SMALL_LETTER_C_WITH_CEDILLA
  | LATIN_CAPITAL_LETTER_C_WITH_CEDILLA

  | LATIN_SMALL_LETTER_E_WITH_GRAVE
  | LATIN_CAPITAL_LETTER_E_WITH_GRAVE
  
  | LATIN_SMALL_LETTER_E_WITH_ACUTE
  | LATIN_CAPITAL_LETTER_E_WITH_ACUTE

  | LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX

  | LATIN_SMALL_LETTER_E_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS

  | LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX
  
  | LATIN_SMALL_LETTER_I_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS

  | LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX
  
  | LATIN_SMALL_LETTER_O_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS
  
  | LATIN_SMALL_LETTER_U_WITH_GRAVE
  | LATIN_CAPITAL_LETTER_U_WITH_GRAVE

  | LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX
  
  | LATIN_SMALL_LETTER_U_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS
    
  | LATIN_SMALL_LIGATURE_OE
  | LATIN_CAPITAL_LIGATURE_OE

  ;

speechOpener : HYPHEN_MINUS HYPHEN_MINUS HYPHEN_MINUS ;
interpolatedClauseDelimiter : HYPHEN_MINUS HYPHEN_MINUS  ;
interpolatedClauseSilentEnd : HYPHEN_MINUS LOW_LINE ;
speechContinuator : HYPHEN_MINUS HYPHEN_MINUS PLUS_SIGN ;
speechEscape : HYPHEN_MINUS HYPHEN_MINUS VERTICAL_LINE ;





// ======
// Tokens
// ======


LATIN_SMALL_LETTER_A : 'a' ;
LATIN_SMALL_LETTER_B : 'b' ;
LATIN_SMALL_LETTER_C : 'c' ;
LATIN_SMALL_LETTER_D : 'd' ;
LATIN_SMALL_LETTER_E : 'e' ;
LATIN_SMALL_LETTER_F : 'f' ;
LATIN_SMALL_LETTER_G : 'g' ;
LATIN_SMALL_LETTER_H : 'h' ;
LATIN_SMALL_LETTER_I : 'i' ;
LATIN_SMALL_LETTER_J : 'j' ;
LATIN_SMALL_LETTER_K : 'k' ;
LATIN_SMALL_LETTER_L : 'l' ;
LATIN_SMALL_LETTER_M : 'm' ;
LATIN_SMALL_LETTER_N : 'n' ;
LATIN_SMALL_LETTER_O : 'o' ;
LATIN_SMALL_LETTER_P : 'p' ;
LATIN_SMALL_LETTER_Q : 'q' ;
LATIN_SMALL_LETTER_R : 'r' ;
LATIN_SMALL_LETTER_S : 's' ;
LATIN_SMALL_LETTER_T : 't' ;
LATIN_SMALL_LETTER_U : 'u' ;
LATIN_SMALL_LETTER_V : 'v' ;
LATIN_SMALL_LETTER_W : 'w' ;
LATIN_SMALL_LETTER_X : 'x' ;
LATIN_SMALL_LETTER_Y : 'y' ;
LATIN_SMALL_LETTER_Z : 'z' ;

LATIN_CAPITAL_LETTER_A : 'A' ;
LATIN_CAPITAL_LETTER_B : 'B' ;
LATIN_CAPITAL_LETTER_C : 'C' ;
LATIN_CAPITAL_LETTER_D : 'D' ;
LATIN_CAPITAL_LETTER_E : 'E' ;
LATIN_CAPITAL_LETTER_F : 'F' ;
LATIN_CAPITAL_LETTER_G : 'G' ;
LATIN_CAPITAL_LETTER_H : 'H' ;
LATIN_CAPITAL_LETTER_I : 'I' ;
LATIN_CAPITAL_LETTER_J : 'J' ;
LATIN_CAPITAL_LETTER_K : 'K' ;
LATIN_CAPITAL_LETTER_L : 'L' ;
LATIN_CAPITAL_LETTER_M : 'M' ;
LATIN_CAPITAL_LETTER_N : 'N' ;
LATIN_CAPITAL_LETTER_O : 'O' ;
LATIN_CAPITAL_LETTER_P : 'P' ;
LATIN_CAPITAL_LETTER_Q : 'Q' ;
LATIN_CAPITAL_LETTER_R : 'R' ;
LATIN_CAPITAL_LETTER_S : 'S' ;
LATIN_CAPITAL_LETTER_T : 'T' ;
LATIN_CAPITAL_LETTER_U : 'U' ;
LATIN_CAPITAL_LETTER_V : 'V' ;
LATIN_CAPITAL_LETTER_W : 'W' ;
LATIN_CAPITAL_LETTER_X : 'X' ;
LATIN_CAPITAL_LETTER_Y : 'Y' ;
LATIN_CAPITAL_LETTER_Z : 'Z' ;

DIGIT_0 : '0' ;
DIGIT_1 : '1' ;
DIGIT_2 : '2' ;
DIGIT_3 : '3' ;
DIGIT_4 : '4' ;
DIGIT_5 : '5' ;
DIGIT_6 : '6' ;
DIGIT_7 : '7' ;
DIGIT_8 : '8' ;
DIGIT_9 : '9' ;

LATIN_SMALL_LETTER_A_WITH_GRAVE : '\u00e0' ;
LATIN_CAPITAL_LETTER_A_WITH_GRAVE : '\u00c0' ;

LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX : '\u00e2' ;   // &acirc;
LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX : '\u00c2' ; // &Acirc;

LATIN_SMALL_LETTER_A_WITH_DIAERESIS : '\u00e4' ;    // &auml;
LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS : '\u00c4' ;  // &Auml;

LATIN_SMALL_LETTER_AE : '\u00e6' ;
LATIN_CAPITAL_LETTER_AE : '\u00c6' ;
  
LATIN_SMALL_LETTER_C_WITH_CEDILLA : '\u00e7' ;   // &ccedil;
LATIN_CAPITAL_LETTER_C_WITH_CEDILLA : '\u00c7' ; // &Ccedil;

LATIN_SMALL_LETTER_E_WITH_GRAVE : '\u00e8' ;     // &egrave;
LATIN_CAPITAL_LETTER_E_WITH_GRAVE : '\u00c8' ;   // &Egrave;
  
LATIN_SMALL_LETTER_E_WITH_ACUTE : '\u00e9' ;
LATIN_CAPITAL_LETTER_E_WITH_ACUTE : '\u00c9' ;

LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX : '\u00ea' ; // &ecirc;
LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX : '\u00ca' ; // &Ecirc;

LATIN_SMALL_LETTER_E_WITH_DIAERESIS : '\u00eb' ; // &euml;
LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS : '\u00cb' ; // &Euml;

LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX : '\u00ee' ; // &icirc;
LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX : '\u00ce' ; // &icirc;
  
LATIN_SMALL_LETTER_I_WITH_DIAERESIS : '\u00ef' ; // &iuml;
LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS : '\u00cf' ; // &Iuml;

LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX : '\u00f4' ; // &ocirc;
LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX : '\u00d4' ; // &Ocirc;
  
LATIN_SMALL_LETTER_O_WITH_DIAERESIS : '\u00f6' ; // &ouml;
LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS : '\u00d6' ; // &Ouml;
  
LATIN_SMALL_LETTER_U_WITH_GRAVE : '\u00f9' ; // &ugrave;
LATIN_CAPITAL_LETTER_U_WITH_GRAVE : '\u00d9' ; // &Ugrave;

LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX : '\u00fb' ; // &ucirc;
LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX : '\u00db' ; // &Ucirc;
  
LATIN_SMALL_LETTER_U_WITH_DIAERESIS : '\u00fc' ; // &uuml;
LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS : '\u00dc' ; // &Uuml;
    
LATIN_SMALL_LIGATURE_OE : '\u0153' ; // &oelig;
LATIN_CAPITAL_LIGATURE_OE : '\u0152' ; // &OElig;



SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;


AMPERSAND : '&' ;
APOSTROPHE : '\'' ;
ASTERISK : '*' ;
CIRCUMFLEX_ACCENT : '^' ;
COLON : ':' ;
COMMA : ',' ;
COMMERCIAL_AT : '@' ;
DEGREE_SIGN : '\u00b0' ;
DOLLAR_SIGN : '$' ;
DOUBLE_QUOTE : '\"' ;
ELLIPSIS : '...' ;
EQUALS_SIGN : '=' ;
EXCLAMATION_MARK : '!' ;
FULL_STOP : '.' ;
GRAVE_ACCENT : '`' ;
GREATER_THAN_SIGN : '>' ;
HYPHEN_MINUS : '-' ;
LEFT_CURLY_BRACKET : '{' ;
LEFT_PARENTHESIS : '(' ;
LEFT_SQUARE_BRACKET : '[' ;
LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK : '\u00ab' ;
LESS_THAN_SIGN : '<' ;
LOW_LINE : '_' ;
NUMBER_SIGN : '#' ;
PLUS_SIGN : '+' ;
PERCENT_SIGN : '%' ;
QUESTION_MARK : '?' ;
REVERSE_SOLIDUS : '\\' ;
RIGHT_CURLY_BRACKET : '}' ;
RIGHT_PARENTHESIS : ')' ;
RIGHT_SQUARE_BRACKET : ']' ;
RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK : '\u00bb' ;
SECTION_SIGN : '\u00a7' ;   
SEMICOLON : ';' ;
SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK : '\u2039' ;
SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK : '\u203a' ;
SOLIDUS : '/' ;
TILDE : '~' ;
VERTICAL_LINE : '|' ;



// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

/**
 * We can't use '/*' because it would fool wildcard detection in file names.
 */
BLOCK_COMMENT
  : '{{' ( options { greedy = false ; } : . )* '}}' { $channel = HIDDEN ; }
  ;

/**
 * As we don't support '/*' we avoid confusion in user's brain by not supporting
 * usually-associated '//', also used for italics.
 */
LINE_COMMENT
  : '%%' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
