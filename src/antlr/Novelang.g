
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

/** This grammar contains logic for both Part and Book as they have 
 * much in common, like breaks, titles and identifiers.
 * This is because ANTLR v3.0.1 doesn't support something like grammar inclusion.
 */
grammar Novelang ;

options { output = AST ; } 

tokens {
  PART ;
  CHAPTER ;
  SECTION  ;
  TITLE ;
  IDENTIFIER ;
  STYLE ;
  LOCUTOR ;
  PARAGRAPH_PLAIN ;
  PARAGRAPH_SPEECH ;
  PARAGRAPH_SPEECH_CONTINUED ;
  PARAGRAPH_SPEECH_ESCAPED ;
  BLOCKQUOTE ;
  LITERAL ;
  QUOTE ;
  EMPHASIS ;
  PARENTHESIS ;
  SQUARE_BRACKETS ;
  INTERPOLATEDCLAUSE ;
  INTERPOLATEDCLAUSE_SILENTEND ;
  SOFT_INLINE_LITERAL ;
  HARD_INLINE_LITERAL ;
  WORD ;
  URL ;
  
  BOOK ;
  FUNCTION_CALL ;
  FUNCTION_NAME ;
  VALUED_ARGUMENT_PRIMARY ;
  VALUED_ARGUMENT_ANCILLARY ;
  VALUED_ARGUMENT_MODIFIER ;
  VALUED_ARGUMENT_FLAG ;
  VALUED_ARGUMENT_ASSIGNMENT ;
  EXTENDED_WORD ;
  
  ELLIPSIS_OPENING ;
  APOSTROPHE_WORDMATE ;

  PUNCTUATION_SIGN ;

  SIGN_COMMA ;
  SIGN_FULLSTOP ;
  SIGN_ELLIPSIS ;
  SIGN_QUESTIONMARK ;
  SIGN_EXCLAMATIONMARK ;
  SIGN_SEMICOLON ;
  SIGN_COLON ;
  
}


@lexer::header { 
package novelang.parser.antlr ;
} 

@parser::header { 
package novelang.parser.antlr ;
import novelang.parser.antlr.GrammarDelegate;
} 


@parser::members {

// Tell paragraphBody accepts everything.
public static final int RESTRICT_NONE = -1 ;
public static final int RESTRICT_EMPHASIS = 1 ;
public static final int RESTRICT_QUOTE = 2 ;
public static final int RESTRICT_INTERPOLATEDCLAUSE = 3 ;

private GrammarDelegate delegate = new GrammarDelegate() ;

public void setGrammarDelegate( GrammarDelegate delegate ) {
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



// ==================
// Part-related rules
// ==================


part 
  : ( mediumBreak | largeBreak )?
    (   p += chapter 
      | p += section 
      | p += paragraph 
      | p += blockQuote 
      | p += literal
    )
    ( largeBreak (
        p += chapter 
      | p += section 
      | p += paragraph 
      | p += blockQuote 
      | p += literal
    ) )*      
    ( mediumBreak | largeBreak )?
    EOF 
    -> ^( PART $p+ )
  ;
  
chapter 
  : chapterIntroducer 
    ( smallBreak? title )?
    ( mediumBreak? headerIdentifier )?
    -> ^( CHAPTER title? headerIdentifier? )
  ;	  
	
section 
  : sectionIntroducer 
    ( smallBreak? ( title ) )?
    -> ^( SECTION title? )
  ;
    
paragraph
  : ( ( blockIdentifier mediumBreak )? 
       speechOpener smallBreak? paragraphBody[ RESTRICT_NONE ]  
    ) => ( blockIdentifier mediumBreak )? speechOpener smallBreak? paragraphBody[ RESTRICT_NONE ]  
    -> ^( PARAGRAPH_SPEECH blockIdentifier? paragraphBody  )
  |  
    ( ( blockIdentifier mediumBreak )? 
       speechEscape smallBreak? paragraphBody[ RESTRICT_NONE ]    
    ) => ( blockIdentifier mediumBreak )? speechEscape smallBreak? paragraphBody[ RESTRICT_NONE ]
    -> ^( PARAGRAPH_SPEECH_ESCAPED blockIdentifier? paragraphBody )
  | 
    ( ( blockIdentifier mediumBreak )? 
       speechContinuator smallBreak? paragraphBody [ RESTRICT_NONE ] 
    ) => ( blockIdentifier mediumBreak )? speechContinuator smallBreak? paragraphBody[ RESTRICT_NONE ]
    -> ^( PARAGRAPH_SPEECH_CONTINUED blockIdentifier? paragraphBody )
  | 
    ( ( blockIdentifier mediumBreak)? paragraphBody[ RESTRICT_NONE ] )
    -> ^( PARAGRAPH_PLAIN blockIdentifier? paragraphBody )
  |  
    ( httpUrl ) => ( http = httpUrl -> ^( URL { delegate.createTree( URL, $http.text ) } ) )
  ;

blockQuote
  : ( blockIdentifier mediumBreak)? 
    LESS_THAN_SIGN LESS_THAN_SIGN 
    ( mediumBreak | largeBreak )?
    paragraphBody[ RESTRICT_QUOTE ] 
    ( largeBreak paragraphBody[ RESTRICT_QUOTE ] )* 
    ( mediumBreak | largeBreak )?
    GREATER_THAN_SIGN GREATER_THAN_SIGN
    -> ^( BLOCKQUOTE blockIdentifier? ^( PARAGRAPH_PLAIN paragraphBody )* )
  ;  

literal
  : LESS_THAN_SIGN LESS_THAN_SIGN LESS_THAN_SIGN 
    WHITESPACE? SOFTBREAK
    l = literalLines
    SOFTBREAK GREATER_THAN_SIGN GREATER_THAN_SIGN GREATER_THAN_SIGN 
    -> ^( LITERAL { delegate.createTree( LITERAL, $l.unescaped ) } )
  ;  

literalLines returns [ String unescaped ]
@init {
  final StringBuffer buffer = new StringBuffer() ;
}
  : s1 = literalLine { buffer.append( $s1.unescaped ) ; }
    ( s2 = SOFTBREAK { buffer.append( $s2.text ) ; }
      s3 = literalLine { buffer.append( $s3.unescaped ) ; }
    )*
    { $unescaped = buffer.toString() ; }        
  ;

    
/**
 * This rule looks weird as negation doesn't work as expected.
 * It's just about avoiding '>>>' at the start of the line.
 * This doesn't work:
   ~( GREATER_THAN_SIGN GREATER_THAN_SIGN GREATER_THAN_SIGN )
   ( anySymbol | WHITESPACE )*
 * In addition escaped characters must be added as unescaped.
 * So we need to add every character "by hand". 
 */
literalLine returns [ String unescaped ]
@init {
  final StringBuffer buffer = new StringBuffer() ;
}
  : (   ( (   s1 = anySymbolExceptGreaterthansign { buffer.append( $s1.text ) ; } 
            | s2 = escapedCharacter { buffer.append( $s2.unescaped ) ; } 
            | s3 = WHITESPACE { buffer.append( $s3.text ) ; } 
          )
          (   s4 = anySymbol  { buffer.append( $s4.text ) ; } 
            | s5 = escapedCharacter  { buffer.append( $s5.unescaped ) ; } 
            | s6 = WHITESPACE  { buffer.append( $s6.text ) ; } 
          )*
        )
      |
        ( s7 = GREATER_THAN_SIGN  { buffer.append( $s7.text ) ; } 
          (   ( (   s8 = anySymbolExceptGreaterthansign  { buffer.append( $s8.text ) ; } 
                  | s9 = escapedCharacter  { buffer.append( $s9.unescaped ) ; } 
                  | s10 = WHITESPACE  { buffer.append( $s10.text ) ; } 
                ) 
                (   s11 = anySymbol  { buffer.append( $s11.text ) ; } 
                  | s12 = escapedCharacter  { buffer.append( $s12.unescaped ) ; } 
                  | s13 = WHITESPACE { buffer.append( $s13.text ) ; }
                )* 
              )
            | ( s14 = GREATER_THAN_SIGN { buffer.append( $s14.text ) ; }
                ( (   s15 = anySymbolExceptGreaterthansign { buffer.append( $s15.text ) ; }
                    | s16 = escapedCharacter  { buffer.append( $s16.unescaped ) ; } 
                    | s17 = WHITESPACE { buffer.append( $s17.text ) ; }
                  ) 
                  (   s18 = anySymbol { buffer.append( $s18.text ) ; }
                    | s19 = escapedCharacter  { buffer.append( $s19.unescaped ) ; } 
                  | s20 = WHITESPACE { buffer.append( $s20.text ) ; }
                  )* 
                )? 
              )
          )?
        )
    )?   
    { $unescaped = buffer.toString() ; }    
  ;
  
  
softInlineLiteral
@init {
  final StringBuffer buffer = new StringBuffer() ;
}
  : GRAVE_ACCENT
    (   s1 = anySymbolExceptGraveAccent { buffer.append( $s1.text ) ; }
      | s2 = WHITESPACE { buffer.append( $s2.text ) ; }
      | s3 = escapedCharacter { buffer.append( $s3.unescaped ) ; }
    )+ 
    GRAVE_ACCENT
    -> ^( SOFT_INLINE_LITERAL { delegate.createTree( SOFT_INLINE_LITERAL, buffer.toString() ) } )
  ;
  
hardInlineLiteral
@init {
  final StringBuffer buffer = new StringBuffer() ;
}
  : GRAVE_ACCENT GRAVE_ACCENT
    (   s1 = anySymbolExceptGraveAccent { buffer.append( $s1.text ) ; }
      | s2 = WHITESPACE { buffer.append( $s2.text ) ; }
      | s3 = escapedCharacter { buffer.append( $s3.unescaped ) ; }
    )+ 
    GRAVE_ACCENT GRAVE_ACCENT
    -> ^( HARD_INLINE_LITERAL { delegate.createTree( HARD_INLINE_LITERAL, buffer.toString() ) } )
  ;
  

anySymbol
  : anySymbolExceptGreaterthansign
  | GREATER_THAN_SIGN
  ;
    
anySymbolExceptGreaterthansign
  : anySymbolExceptGreaterthansignAndGraveAccent
  | GRAVE_ACCENT
  ;
  
anySymbolExceptGraveAccent
  : anySymbolExceptGreaterthansignAndGraveAccent
  | GREATER_THAN_SIGN
  ;
  
anySymbolExceptGreaterthansignAndGraveAccent
  :     digit
      | hexLetter
      | nonHexLetter 
      | AMPERSAND 
      | APOSTROPHE   
      | ASTERISK
      | COLON
      | COMMA 
      | COMMERCIAL_AT
      | DOLLAR_SIGN
      | DOUBLE_QUOTE
      | ELLIPSIS
      | EQUALS_SIGN
      | EXCLAMATION_MARK
      | FULL_STOP
//      | GRAVE_ACCENT
//      | GREATER_THAN_SIGN
      | HYPHEN_MINUS
      | LEFT_CURLY_BRACKET
      | LEFT_PARENTHESIS
      | LEFT_SQUARE_BRACKET
      | LESS_THAN_SIGN
      | LOW_LINE
      | NUMBER_SIGN
      | PLUS_SIGN
      | PERCENT_SIGN
      | QUESTION_MARK
      | RIGHT_CURLY_BRACKET
      | RIGHT_PARENTHESIS
      | RIGHT_SQUARE_BRACKET
      | SEMICOLON
      | SOLIDUS 
      | TILDE  
      | VERTICAL_LINE 
  ;


// ===================================
// Part-related URL rules
// http://www.ietf.org/rfc/rfc1738.txt
// ===================================

url
  : ( http = httpUrl -> ^( URL { delegate.createTree( URL, $http.text ) } )	)
  | ( file = fileUrl -> ^( URL { delegate.createTree( URL, $file.text ) } )	) 
  ;
  
fileUrl                                   
//  : ( 'f' 'i' 'l' 'e' COLON ) //=> 'f' 'i' 'l' 'e' COLON // Grammatical ambiguity in the spec
    // following removed from the spec!
//    SOLIDUS SOLIDUS urlHost SOLIDUS 
//    urlFilePath
  :	'f' 'i' 'l' 'e' COLON SOLIDUS? urlFilePath 
  ;
  
httpUrl                                       // Grammatical ambiguity in the spec
  : ( 'h' 't' 't' 'p' COLON SOLIDUS SOLIDUS ) => 'h' 't' 't' 'p' COLON SOLIDUS SOLIDUS 
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



               
               
               


// =================================
// Shared rules (both Part and Book)
// =================================

title
  : paragraphBody[ -1 ]
	  -> ^( TITLE paragraphBody )
  ;

headerIdentifier
  : REVERSE_SOLIDUS REVERSE_SOLIDUS  w = word
	  -> ^( IDENTIFIER { delegate.createTree( IDENTIFIER, $w.text ) } )
  ;
  
blockIdentifier
  :	REVERSE_SOLIDUS w = word 
	  -> ^( IDENTIFIER { delegate.createTree( IDENTIFIER, $w.text ) } )
  ;
  
/** This rule repeats through paragraphBodyNoXxx rules in order to avoid left-recursion
 *  with sub-paragraphs which don't have symmetrical delimiters.
 *  It doesn't seem possible to factor this with using backtracking or whatever.
 */  
paragraphBody[ int restriction ]
  :   openingEllipsis
    | ( // Start with plain words or inline literal.
        ( openingEllipsis smallBreak? )?
        nestedWordSequence
        ( ( mediumBreak? 
            delimitedBlock[ $restriction ] 
            ( smallBreak? punctuationSign )* )
          ( mediumBreak? nestedWordSequence )?
        )*
      )

    | ( // Start with delimited block.
        delimitedBlock[ $restriction ]
        ( smallBreak? punctuationSign )*
        ( mediumBreak? nestedWordSequence  )?
        
        (          
            ( mediumBreak? delimitedBlock[ $restriction ] ( smallBreak? punctuationSign )* )
            ( mediumBreak? nestedWordSequence )?          
        )*        
      )      
  ;
  
nestedWordSequence
  : ( word 
      ( (   ( mediumBreak word ) 
          | ( mediumBreak? ( softInlineLiteral | hardInlineLiteral ) word? )
          | ( smallBreak? punctuationSign word? )
        )
      )*
    )
  | ( ( softInlineLiteral | hardInlineLiteral ) word?
      ( (   ( mediumBreak word ) 
          | ( mediumBreak? ( softInlineLiteral | hardInlineLiteral ) word? )
          | ( smallBreak? punctuationSign word? )
        )
      )*
    )
  ;
  
  

    
delimitedBlock[ int restriction ]
  : parenthesizingText  
  | bracketingText 
  | { NO_QUOTE != $restriction}? => quotingText 
/*
  | { NO_EMPHASIS != $restriction}? => emphasizingText
  | { NO_INTERPOLATEDCLAUSE != $restriction}? => interpolatedClause
*/  
  ;  
  
parenthesizingText
  : LEFT_PARENTHESIS 
    mediumBreak?
    paragraphBody[ RESTRICT_NONE ]
    mediumBreak?
    RIGHT_PARENTHESIS
    -> ^( PARENTHESIS paragraphBody )
  ;

quotingText
  : ( DOUBLE_QUOTE      
      mediumBreak?
      paragraphBody[ RESTRICT_QUOTE ]
      mediumBreak?
      DOUBLE_QUOTE
    )
    -> ^( QUOTE paragraphBody )
  ;  
bracketingText
  : LEFT_SQUARE_BRACKET
    mediumBreak?
    paragraphBody[ RESTRICT_NONE ]
    mediumBreak?
    RIGHT_SQUARE_BRACKET
    -> ^( SQUARE_BRACKETS paragraphBody )
  ;

emphasizingText
  : SOLIDUS SOLIDUS
    mediumBreak?
    paragraphBody[ RESTRICT_EMPHASIS ]
    mediumBreak?
    SOLIDUS SOLIDUS
    -> ^( EMPHASIS paragraphBody )
  ;
  
interpolatedClause
  :	interpolatedClauseDelimiter 
    smallBreak?
    paragraphBody[ RESTRICT_INTERPOLATEDCLAUSE ]
    (   ( ( smallBreak? interpolatedClauseDelimiter )
            -> ^( INTERPOLATEDCLAUSE paragraphBody )
        )
      | ( ( smallBreak? interpolatedClauseSilentEnd ) 
          -> ^( INTERPOLATEDCLAUSE_SILENTEND paragraphBody )
        )
    )
  ;	  

  
  
/** Use between words, when everything is kept on the same line.
 */
smallBreak
  : WHITESPACE
    ->
  ;

/** Use inside a paragraph, when lines are together with no blank line in the middle.
 */
mediumBreak
  : ( WHITESPACE
      | ( WHITESPACE? SOFTBREAK WHITESPACE? )
    ) // Parenthesis needed to make the rewrite rule apply for the whole.
    ->
  ;

/** Use inside a paragraph, when lines are together with no blank line in the middle.
 */
lineBreak
  : ( WHITESPACE? SOFTBREAK WHITESPACE? ) // Parenthesis needed to make the rewrite rule apply for the whole.
    ->
  ;

/** One blank line in the middle, white spaces everywhere.
 */
largeBreak
  : ( ( WHITESPACE? SOFTBREAK ) ( WHITESPACE? SOFTBREAK )+ WHITESPACE? )
    ->
  ;
    
word
  : w = rawWord 
    -> ^( WORD { delegate.createTree( WORD, $w.text ) } )	
  ;  

/** This intermediary rule is useful as I didn't find how to
 * concatenate Tokens from inside the rewrite rule.
 */
rawWord returns [ String text ]
@init {
  final StringBuffer buffer = new StringBuffer() ;
}
  : (   s1 = hexLetter { buffer.append( $s1.text ) ; }
      | s2 = nonHexLetter { buffer.append( $s2.text ) ; }
      | s3 = digit { buffer.append( $s3.text ) ; }
      | s4 = escapedCharacter  { buffer.append( $s4.unescaped ) ; }
    )+
    ( s5 = HYPHEN_MINUS { buffer.append( $s5.text ) ; }
      (  s6 = hexLetter { buffer.append( $s6.text ) ; }
       | s7 = nonHexLetter { buffer.append( $s7.text ) ; }
       | s8 = digit { buffer.append( $s8.text ) ; }
       | s9 = escapedCharacter  { buffer.append( $s9.unescaped ) ; } 
      )+ 
    )*
    { $text = buffer.toString() ; }
  ;  

escapedCharacter returns [ String unescaped ]
  : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK letters RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    { $unescaped = delegate.unescapeCharacter( 
          $letters.text, 
          0, // getLine(), TODO fix this.
          0 // getCharPositionInLine() 
      ) ;
    }    
  ;

  
openingEllipsis
  : ELLIPSIS -> ^( ELLIPSIS_OPENING )
  ;

punctuationSign
  : COMMA -> ^( PUNCTUATION_SIGN SIGN_COMMA )
  | FULL_STOP  -> ^( PUNCTUATION_SIGN SIGN_FULLSTOP )
  | ELLIPSIS -> ^( PUNCTUATION_SIGN SIGN_ELLIPSIS )
  | QUESTION_MARK -> ^( PUNCTUATION_SIGN SIGN_QUESTIONMARK )
  | EXCLAMATION_MARK -> ^( PUNCTUATION_SIGN SIGN_EXCLAMATIONMARK )
  | SEMICOLON -> ^( PUNCTUATION_SIGN SIGN_SEMICOLON )
  | COLON -> ^( PUNCTUATION_SIGN SIGN_COLON )
  | APOSTROPHE -> ^( APOSTROPHE_WORDMATE )
  ;

  
  
    
  
  
  
// ==================
// Book-related rules
// ==================

book
  : ( mediumBreak | largeBreak )?
    functionCall
    ( largeBreak functionCall )*      
    ( mediumBreak | largeBreak )?
    EOF 
    -> ^( BOOK functionCall* )
  ;
  

functionCall
  : name = word 
     (   smallBreak url 
       | smallBreak headerIdentifier 
       | WHITESPACE? SOFTBREAK WHITESPACE? paragraphBody[ RESTRICT_NONE ]  
     )?
    ( mediumBreak ( ancillaryArgument | flagArgument | assignmentArgument ) )*
    ->  ^( FUNCTION_CALL 
            ^( FUNCTION_NAME { delegate.createTree( FUNCTION_NAME, $name.text ) } )  
            ^( VALUED_ARGUMENT_PRIMARY 
                ^( PARAGRAPH_PLAIN paragraphBody )? 
                url? 
                headerIdentifier? 
            )? 
            ancillaryArgument*
            flagArgument*
            assignmentArgument*
        )
  ; 
  
ancillaryArgument
  :	( PLUS_SIGN? blockIdentifier )
      -> ^( VALUED_ARGUMENT_ANCILLARY ^( VALUED_ARGUMENT_MODIFIER PLUS_SIGN )? blockIdentifier )   
  ;

flagArgument    
  : ( DOLLAR_SIGN flag = extendedWord )
      -> ^( VALUED_ARGUMENT_FLAG { delegate.createTree( VALUED_ARGUMENT_FLAG, $flag.text ) } )     
  ;

assignmentArgument    
  : ( DOLLAR_SIGN key = extendedWord EQUALS_SIGN value = extendedWord )
      -> ^( VALUED_ARGUMENT_ASSIGNMENT 
              { delegate.createTree( VALUED_ARGUMENT_ASSIGNMENT, $key.text ) } 
              { delegate.createTree( VALUED_ARGUMENT_ASSIGNMENT, $value.text ) } 
          )     
  ;

extendedWord
  : w = rawExtendedWord 
    -> ^( EXTENDED_WORD { delegate.createTree( EXTENDED_WORD, $w.text ) } )	
  ;  

/** This intermediary rule is useful as I didn't find how to
 * concatenate Tokens from inside the rewrite rule.
 */
rawExtendedWord returns [ String text ]
@init {
  final StringBuffer buffer = new StringBuffer() ;
}
  : (   s1 = hexLetter { buffer.append( $s1.text ) ; }
      | s2 = nonHexLetter { buffer.append( $s2.text ) ; }
      | s3 = digit { buffer.append( $s3.text ) ; }
    )+
    ( (   s5 = HYPHEN_MINUS { buffer.append( $s5.text ) ; }
        | s5 = SOLIDUS { buffer.append( $s5.text ) ; }
        | s5 = FULL_STOP { buffer.append( $s5.text ) ; }
      )
      (  s6 = hexLetter { buffer.append( $s6.text ) ; }
       | s7 = nonHexLetter { buffer.append( $s7.text ) ; }
       | s8 = digit { buffer.append( $s8.text ) ; }
      )+ 
    )*
    { $text = buffer.toString() ; }
  ;  

// ==============
// Common symbols
// ==============

/** Notation '0'..'9' giving weird things with Antlr 3.0.1 + AntlrWorks 1.1.7.
 */
digit : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' ;

letters : ( hexLetter | nonHexLetter )+ ;

hexLetter
  : 'a' | 'b' | 'c' | 'd' | 'e' | 'f'  
  | 'A' | 'B' | 'C' | 'D' | 'E' | 'F' 
  ;

nonHexLetter 
  : 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' 
  | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z'   
  | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P' 
  | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z'   

  | '\u00e0' // LATIN SMALL LETTER A WITH GRAVE  
  | '\u00c0' // LATIN CAPITAL LETTER A WITH GRAVE  

  | '\u00e2' // LATIN SMALL LETTER A WITH CIRCUMFLEX (&acirc;)
  | '\u00c2' // LATIN CAPITAL LETTER A WITH CIRCUMFLEX (&Acirc;)

  | '\u00e4' // LATIN SMALL LETTER A WITH DIAERESIS (&auml;)
  | '\u00c4' // LATIN CAPITAL LETTER A WITH DIAERESIS (&Auml;)

  | '\u00e6' // LATIN SMALL LETTER AE
  | '\u00c6' // LATIN CAPITAL LETTER AE
  
  | '\u00e7' // LATIN SMALL LETTER C WITH CEDILLA (&ccedil;)
  | '\u00c7' // LATIN CAPITAL LETTER C WITH CEDILLA (&Ccedil;)  

  | '\u00e8' // LATIN SMALL LETTER E WITH GRAVE (&egrave;)
  | '\u00c8' // LATIN CAPITAL LETTER E WITH GRAVE (&Egrave;)
  
  | '\u00e9' // LATIN SMALL LETTER E WITH ACUTE 
  | '\u00c9' // LATIN CAPITAL LETTER E WITH ACUTE

  | '\u00ea' // LATIN SMALL LETTER E WITH CIRCUMFLEX (&ecirc;)
  | '\u00ca' // LATIN CAPITAL LETTER E WITH CIRCUMFLEX (&Ecirc;)

  | '\u00eb' // LATIN SMALL LETTER E WITH DIAERESIS (&euml;)
  | '\u00cb' // LATIN CAPITAL LETTER E WITH DIAERESIS (&Euml;)  

  | '\u00ee' // LATIN SMALL LETTER I WITH CIRCUMFLEX (&icirc;)
  | '\u00ce' // LATIN SMALL LETTER I WITH CIRCUMFLEX (&icirc;)  
  
  | '\u00ef' // LATIN SMALL LETTER I WITH DIAERESIS (&iuml;)
  | '\u00cf' // LATIN CAPITAL LETTER I WITH DIAERESIS (&Iuml;)
  
  | '\u00f4' // LATIN SMALL LETTER O WITH CIRCUMFLEX (&ocirc;)
  | '\u00d4' // LATIN CAPITAL LETTER O WITH CIRCUMFLEX (&Ocirc;)
  
  | '\u00f6' // LATIN SMALL LETTER O WITH DIAERESIS (&ouml;)
  | '\u00d6' // LATIN CAPITAL LETTER O WITH DIAERESIS (&Ouml;)
  
  | '\u00f9' // LATIN SMALL LETTER U WITH GRAVE (&ugrave;)
  | '\u00d9' // LATIN CAPITAL LETTER U WITH GRAVE (&Ugrave;)

  | '\u00fb' // LATIN SMALL LETTER U WITH CIRCUMFLEX (&ucirc;)
  | '\u00db' // LATIN CAPITAL LETTER U WITH CIRCUMFLEX (&Ucirc;)
  
  | '\u00fc' // LATIN SMALL LETTER U WITH DIAERESIS (&uuml;)
  | '\u00dc' // LATIN CAPITAL LETTER U WITH DIAERESIS (&Uuml;)
    
  | '\u0153' // LATIN SMALL LIGATURE OE
  | '\u0152' // LATIN CAPITAL LIGATURE OE

  ;

chapterIntroducer : ASTERISK ASTERISK ASTERISK ;
sectionIntroducer : EQUALS_SIGN EQUALS_SIGN EQUALS_SIGN ;
speechOpener : HYPHEN_MINUS HYPHEN_MINUS HYPHEN_MINUS ;
interpolatedClauseDelimiter : HYPHEN_MINUS HYPHEN_MINUS  ;
interpolatedClauseSilentEnd : HYPHEN_MINUS LOW_LINE ;
speechContinuator : HYPHEN_MINUS HYPHEN_MINUS PLUS_SIGN ;
speechEscape : HYPHEN_MINUS HYPHEN_MINUS VERTICAL_LINE ;


SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;

// All namings respect Unicode standard.
// http://www.fileformat.info/info/unicode

AMPERSAND : '&' ;
APOSTROPHE : '\'' ;
ASTERISK : '*' ;
COLON : ':' ;
COMMA : ',' ;
COMMERCIAL_AT : '@' ;
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
SEMICOLON : ';' ;
SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK : '\u2039' ;
SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK : '\u203a' ;
SOLIDUS : '/' ;
TILDE : '~' ;
VERTICAL_LINE : '|' ;


  
//TRIPLE_LESSTHANSIGN : '<<<' ;
//TRIPLE_GREATERTHANSIGN : '>>>' ;


// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

/** We can't use '/*' because it gets confused with wildcards in file names.
 */
BLOCK_COMMENT
  : '{{' ( options { greedy = false ; } : . )* '}}' { $channel = HIDDEN ; }
  ;

/** As we don't support '/*' we avoid confusion by not supporting
 * usually-associated '//', also used for italics.
 */
LINE_COMMENT
  : '%%' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
