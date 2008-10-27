
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

/** 
 * This grammar contains every token used by Novelang parsers. 
 * Because ANTLRWorks (at least in version 1.2.1) doesn't support import of composite grammars 
 * then all tokens must be defined here.
 *
 * All tokens must respect Unicode naming. 
 * http://www.fileformat.info/info/unicode
 */
lexer grammar AllTokens ;

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
