/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
  QUOTE ;
  EMPHASIS ;
  PARENTHESIS ;
  SQUARE_BRACKETS ;
  INTERPOLATEDCLAUSE ;
  INTERPOLATEDCLAUSE_SILENTEND ;
  WORD ;
  URL ;
  
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
  
  NO_PARAGRAPHBODY_RESTRICTION ; // Internal use only.
}


scope ParagraphScope { 
  boolean inQuotes ;
  boolean inInterpolatedClause ;
  boolean inEmphasis ;
}


// Book-reserved scopes.
	
scope ChapterScope { StructuralChapter chapter } 
scope SectionScope { StructuralSection section } 
scope InclusionScope { StructuralInclusion inclusion } 


@lexer::header { 
package novelang.parser.antlr ;
} 

@parser::header { 
package novelang.parser.antlr ;
import novelang.parser.antlr.GrammarDelegate;
import novelang.parser.antlr.BookGrammarDelegate;
import novelang.parser.antlr.QuietGrammarDelegate;
import novelang.model.structural.StructuralChapter;
import novelang.model.structural.StructuralSection;
import novelang.model.structural.StructuralInclusion;
} 

@lexer::members {

private GrammarDelegate delegate = new QuietGrammarDelegate() ;

public void setGrammarDelegate( GrammarDelegate delegate ) {
  this.delegate = delegate ;
}
}

@parser::members {

// Tell paragraphBody accepts everything.
public static final int ALLOW_ALL = -1 ;

private GrammarDelegate delegate = new QuietGrammarDelegate() ;

// Only used when parsing some Book file.
private BookGrammarDelegate bookDelegate ;

public void setGrammarDelegate( GrammarDelegate delegate ) {
  this.delegate = delegate ;
  if( delegate instanceof BookGrammarDelegate ) {
    bookDelegate = ( BookGrammarDelegate ) delegate ;
  }
}
		
private boolean areScopesEnabled() {
  return bookDelegate != null && bookDelegate.getScopesEnabled() ;
}
		
@Override
public void emitErrorMessage( String string ) {
  if( null == delegate ) {
    super.emitErrorMessage( string ) ;
  } else {
    delegate.report( string ) ;
  }
}
	
private int parenthesisDepth = 0 ;
private int squareBracketsDepth = 0 ;
private int quoteDepth = 0 ;
private int emphasisDepth = 0 ;
private int interpolatedClauseDepth = 0 ;
}



// ==================
// Part-related rules
// ==================


part 
  : ( mediumBreak | largeBreak )?
    (   ( section ( largeBreak section )* ) 
      | ( chapter ( largeBreak chapter )* )
    )
    ( mediumBreak | largeBreak )?
    EOF 
    -> ^( PART section* chapter* )
  ;
  
chapter 
  : CHAPTER_INTRODUCER 
    ( smallBreak? ( title | identifier ) )?
    ( largeBreak section )+ 
    -> ^( CHAPTER
           title?
           identifier?
           section*
        )
  ;	  
	
section 
  : SECTION_INTRODUCER 
    ( smallBreak? ( title | identifier ) )?
    ( largeBreak ( paragraph | blockQuote ) )+ 
    -> ^( SECTION 
           title?
           identifier?
           paragraph* blockQuote* 
        )
  ;
    
paragraph
scope ParagraphScope ;
  : 
    ( SPEECH_OPENER ( smallBreak locutor )? smallBreak? paragraphBody )
    -> ^( PARAGRAPH_SPEECH locutor? paragraphBody )
  | ( SPEECH_ESCAPE WHITESPACE? paragraphBody )
    -> ^( PARAGRAPH_SPEECH_ESCAPED paragraphBody ) 
  | ( SPEECH_CONTINUATOR WHITESPACE? paragraphBody )
    -> ^( PARAGRAPH_SPEECH_CONTINUED paragraphBody )
  | paragraphBody
    -> ^( PARAGRAPH_PLAIN paragraphBody )
  | ( url ) => url
  ;

blockQuote
  : OPENING_BLOCKQUOTE 
    ( mediumBreak | largeBreak )?
    paragraphBody 
    ( largeBreak paragraphBody )* 
    ( mediumBreak | largeBreak )?
    CLOSING_BLOCKQUOTE
    -> ^( BLOCKQUOTE ^( PARAGRAPH_PLAIN paragraphBody )* )
  ;  

locutor
 	: word 
 	  ( smallBreak word )* 
 	  smallBreak? LOCUTOR_INTRODUCER
 	  -> ^( LOCUTOR word* ) // TODO fix this, apostrophe is swallowed.
 	;


// ===================================
// Part-related URL rules
// http://www.ietf.org/rfc/rfc1738.txt
// ===================================

url
  : ( http = fileUrl -> ^( URL { delegate.createTree( URL, $http.text ) } )	)
  | ( file = httpUrl -> ^( URL { delegate.createTree( URL, $file.text ) } )	) 
  ;
  
fileUrl
  : ( 'f' 'i' 'l' 'e' COLON SOLIDUS SOLIDUS ) => 'f' 'i' 'l' 'e' COLON SOLIDUS SOLIDUS 
   ( urlHost ) // Change from spec: don't handle 'localhost' in this rule.
   SOLIDUS 
   urlFilePath
  ;
  
httpUrl
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
    )*
  ;

  
httpUrlPath
  : httpUrlSegment 
    ( ( SOLIDUS httpUrlPath ) => SOLIDUS httpUrlPath )*
  ;
  
httpUrlSegment
  : (   urlUChar
      | SEMICOLON
      | COLON
      | COMMERCIAL_AT
      | AMPERSAND
      | EQUALS_SIGN
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
  :	 APOSTROPHE paragraphBody
	  -> ^( TITLE paragraphBody )
  ;

identifier
  :	paragraphBody
	  -> ^( IDENTIFIER paragraphBody )
  ;
  
/** This rule repeats through paragraphBodyNoXxx rules in order to avoid left-recursion
 *  with sub-paragraphs which don't have symmetrical delimiters.
 *  It doesn't seem possible to factor this with using backtracking or whatever.
 */  
paragraphBody 
  :   openingEllipsis
    | ( openingEllipsis?
        nestedWordSequence 
        ( mediumBreak?
          ( nestedParagraph ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedWordSequence )?
        )*
      )
    | ( nestedParagraph ( smallBreak? punctuationSign )? 
        ( mediumBreak? nestedParagraph ( smallBreak? punctuationSign )? )*
        ( mediumBreak?
          nestedWordSequence
          ( mediumBreak? nestedParagraph ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedParagraph ( smallBreak? punctuationSign )? )* // TODO factor with previous line
        )*    
        ( mediumBreak?
          nestedWordSequence
        )?
      )  
  ;

  
paragraphBodyNoQuote
  :   openingEllipsis
    | ( openingEllipsis?
        nestedWordSequence 
        ( mediumBreak?
          ( nestedParagraphNoQuote ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedWordSequence )?
        )*
      )
    | ( nestedParagraphNoQuote ( smallBreak? punctuationSign )? 
        ( mediumBreak? nestedParagraphNoQuote ( smallBreak? punctuationSign )? )*
        ( mediumBreak?
          nestedWordSequence
          ( mediumBreak? nestedParagraphNoQuote ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedParagraphNoQuote ( smallBreak? punctuationSign )? )*
        )*    
        ( mediumBreak?
          nestedWordSequence
        )?
      )  
  ;
  
paragraphBodyNoEmphasis
  :   openingEllipsis
    | ( openingEllipsis?
        nestedWordSequence 
        ( mediumBreak?
          ( nestedParagraphNoEmphasis ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedWordSequence )?
        )*
      )
    | ( nestedParagraphNoEmphasis ( smallBreak? punctuationSign )? 
        ( mediumBreak? nestedParagraphNoEmphasis ( smallBreak? punctuationSign )? )*
        ( mediumBreak?
          nestedWordSequence
          ( mediumBreak? nestedParagraphNoEmphasis ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedParagraphNoEmphasis ( smallBreak? punctuationSign )? )*
        )*    
        ( mediumBreak?
          nestedWordSequence
        )?
      )  
  ;
  
paragraphBodyNoInterpolatedClause
  :   openingEllipsis
    | ( openingEllipsis?
        nestedWordSequence 
        ( mediumBreak?
          ( nestedParagraphNoInterpolatedClause ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedWordSequence )?
        )*
      )
    | ( nestedParagraphNoInterpolatedClause ( smallBreak? punctuationSign )? 
        ( mediumBreak? nestedParagraphNoInterpolatedClause ( smallBreak? punctuationSign )? )*
        ( mediumBreak?
          nestedWordSequence
          ( mediumBreak? nestedParagraphNoInterpolatedClause ( smallBreak? punctuationSign )? )
          ( mediumBreak? nestedParagraphNoInterpolatedClause ( smallBreak? punctuationSign )? )*
        )*    
        ( mediumBreak?
          nestedWordSequence
        )?
      )  
  ;

nestedWordSequence
  : ( word ) 
    (   ( mediumBreak word ) 
      | ( smallBreak? punctuationSign ) 
      | ( smallBreak? punctuationSign word ) 
    )*
  ;
  
nestedParagraph
  : parenthesizingText  
  | bracketingText 
  | quotingText 
  | emphasizingText
  | interpolatedClause
  ;  
  
nestedParagraphNoQuote
  : parenthesizingText  
  | bracketingText 
  | emphasizingText
  | interpolatedClause
  ;  
  
nestedParagraphNoEmphasis
  : parenthesizingText  
  | bracketingText 
  | quotingText 
  | interpolatedClause
  ;  
  
nestedParagraphNoInterpolatedClause
  : parenthesizingText  
  | bracketingText 
  | quotingText 
  | emphasizingText
  ;  

parenthesizingText
  : LEFT_PARENTHESIS 
    mediumBreak?
    paragraphBody 
    mediumBreak?
    RIGHT_PARENTHESIS
    -> ^( PARENTHESIS paragraphBody )
  ;

quotingText
  : ( DOUBLE_QUOTE      
      mediumBreak?
      paragraphBodyNoQuote
      mediumBreak?
      DOUBLE_QUOTE
    )
    -> ^( QUOTE paragraphBodyNoQuote )
  ;  
bracketingText
  : LEFT_SQUARE_BRACKET
    mediumBreak?
    paragraphBody
    mediumBreak?
    RIGHT_SQUARE_BRACKET
    -> ^( SQUARE_BRACKETS paragraphBody )
  ;

emphasizingText
  : SOLIDUS SOLIDUS
    mediumBreak?
    paragraphBodyNoEmphasis
    mediumBreak?
    SOLIDUS SOLIDUS
    -> ^( EMPHASIS paragraphBodyNoEmphasis )
  ;
  
interpolatedClause
  :	INTERPOLATED_CLAUSE_DELIMITER 
    smallBreak?
    paragraphBodyNoInterpolatedClause 
    (   ( ( smallBreak? INTERPOLATED_CLAUSE_DELIMITER )
            -> ^( INTERPOLATEDCLAUSE paragraphBodyNoInterpolatedClause )
        )
      | ( ( smallBreak? INTERPOLATED_CLAUSE_SILENT_END ) 
          -> ^( INTERPOLATEDCLAUSE_SILENTEND paragraphBodyNoInterpolatedClause )
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

/** One blank line in the middle, white spaces everywhere.
 */
largeBreak
  : ( ( WHITESPACE? SOFTBREAK ) ( WHITESPACE? SOFTBREAK )+ WHITESPACE? )
    ->
  ;
    
word
  : w = rawWord 
    // QuietGrammarDelegate helps running this from AntlrWorks debugger:
    -> ^( WORD { delegate.createTree( WORD, $w.text ) } )	
//    -> ^( WORD $word.text )	
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
    (   ( bookParts
          largeBreak
          bookChapter ( largeBreak bookChapter )*
        )
      | ( AUTOGENERATE_DIRECTIVE ( mediumBreak | largeBreak ) bookParts )
    ) 
    ( mediumBreak | largeBreak )?
    EOF
  ;

bookParts
  : bookPart ( ( mediumBreak | largeBreak ) bookPart )*
  ;
 

bookPart 
  : NUMBER_SIGN smallBreak? genericFileName 
    { if( null != bookDelegate ) {
        final String fileName = $genericFileName.text ;
        bookDelegate.createPart( fileName, input ) ;
      }
    }    
  ;

genericFileName
  :	SOLIDUS? genericFileNameItem 
    ( SOLIDUS genericFileNameItem )*
  ;

/** '..' is forbidden for security reasons.
 */    
genericFileNameItem 
  : (   hexLetter 
      | nonHexLetter 
      | digit 
      | HYPHEN_MINUS 
      | ASTERISK 
      | LOW_LINE 
      | ( FULL_STOP ~FULL_STOP ) 
    )+
  ;


bookChapter
  scope ChapterScope ;
  : CHAPTER_INTRODUCER 
    { if( areScopesEnabled() ) { 
        final StructuralChapter chapter = bookDelegate.createChapter( input ) ;
        $ChapterScope::chapter = chapter ; 
      }
    }    
    ( smallBreak? title
      { if( areScopesEnabled() ) {
        $ChapterScope::chapter.setTitle( 
            ( novelang.model.common.Tree ) $title.tree ) ; }
      }       
    )?
    ( mediumBreak | largeBreak )
    ( style 
      { if( areScopesEnabled() ) {
        $ChapterScope::chapter.setStyle( 
            ( novelang.model.common.Tree ) $style.tree ) ; 
        }
      }
      ( mediumBreak | largeBreak )
    )?
    bookSection ( largeBreak bookSection )* 
  ;
  

bookSection
  scope SectionScope ;
  : SECTION_INTRODUCER 
    { if( areScopesEnabled() ) {
      $SectionScope::section = AntlrParserHelper.createSection( 
          $ChapterScope::chapter, input ) ; 
      }  
    }    
    ( smallBreak? title 
      { if( areScopesEnabled() ) {
          $SectionScope::section.setTitle( 
              ( novelang.model.common.Tree ) $title.tree ) ; }
        }
    )?
    
    ( ( mediumBreak | largeBreak )
      style 
      { if( areScopesEnabled() ) {
        $SectionScope::section.setStyle( 
            ( novelang.model.common.Tree ) $style.tree ) ; 
        }
      }
      
    )?
    largeBreak
    inclusion
    ( ( mediumBreak | largeBreak ) inclusion )*
  ;

style 
	:	STYLE_DIRECTIVE smallBreak word
	  -> ^( STYLE word )
	;

inclusion
  scope InclusionScope ;
  // TODO find how to collate or not depending on the first symbol.
  : ( ( PLUS_SIGN | VERTICAL_LINE ) 
      smallBreak? identifier 
      { if( areScopesEnabled() ) {
          $InclusionScope::inclusion = AntlrParserHelper.createInclusion(
              $SectionScope::section,
              input,
              $identifier.text
          ) ; 
          $InclusionScope::inclusion.setCollateWithPrevious( false ) ;
        }
      }  
      ( ( mediumBreak | largeBreak ) paragraphReferences )? 
    )
  ;   

paragraphReferences
  : PARAGRAPH_REFERENCES_INTRODUCER
    ( ( mediumBreak | largeBreak ) 
      ( includedParagraphIndex | includedParagraphRange ) 
    )+
  ;

includedParagraphIndex  
  : postsignedInteger
    { if( areScopesEnabled() ) {
        AntlrParserHelper.addParagraph( 
            $InclusionScope::inclusion, input, $postsignedInteger.text ) ; 
      }
    }        
  ;
  
includedParagraphRange
  :	n1 = postsignedInteger '..' n2 = postsignedInteger
    { if( areScopesEnabled() ) {
        AntlrParserHelper.addParagraphRange( 
            $InclusionScope::inclusion, input, $n1.text, $n2.text ) ; 
      }
    }
  ;

positiveInteger : digit+ ;

postsignedInteger : digit+ HYPHEN_MINUS? ;




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

escapedCharacter returns [ String unescaped ]
  : AMPERSAND letters SEMICOLON
    { $unescaped = delegate.escapeSymbol( 
          $letters.text, 
          getLine(),
          getCharPositionInLine() 
      ) ;
    }    
  ;



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
HYPHEN_MINUS : '-' ;
LEFT_PARENTHESIS : '(' ;
LEFT_SQUARE_BRACKET : '[' ;
LOW_LINE : '_' ;
NUMBER_SIGN : '#' ;
PLUS_SIGN : '+' ;
PERCENT_SIGN : '%' ;
QUESTION_MARK : '?' ;
RIGHT_PARENTHESIS : ')' ;
RIGHT_SQUARE_BRACKET : ']' ;
SEMICOLON : ';' ;
SOLIDUS : '/' ;
TILDE : '~' ;
VERTICAL_LINE : '|' ;


  
OPENING_BLOCKQUOTE : '<<<' ;
CLOSING_BLOCKQUOTE : '>>>' ;
INTERPOLATED_CLAUSE_DELIMITER : '--' ;
INTERPOLATED_CLAUSE_SILENT_END : '-_' ;
SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '--+' ;
SPEECH_ESCAPE : '--|' ;
LOCUTOR_INTRODUCER : '::' ;
CHAPTER_INTRODUCER : '***' ;
SECTION_INTRODUCER : '===' ;
PARAGRAPH_REFERENCES_INTRODUCER : '<=' ;

AUTOGENERATE_DIRECTIVE : ':autogenerate' ;
STYLE_DIRECTIVE : ':style' ;

//URL_HTTP_SCHEME : 'http://' ;
//URL_FILE_SCHEME : 'file://' ;
//URL_LOCALHOST : 'localhost' ;

// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

/** We can't use '/*' because it gets confused with wildcards in file names.
 */
BLOCK_COMMENT
  : '{{' ( options { greedy = false ; } : . )* '}}' { $channel = HIDDEN ; }
  ;

/** As we don't support '/*' we avoid confusion by not supporting
 * usually-associated '//'.
 */
LINE_COMMENT
  : '%%' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
