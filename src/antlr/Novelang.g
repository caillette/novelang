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
  BLOCKQUOTE ;
  BOOK ;
  CHAPTER ;
  EMPHASIS ;                       // Should become BLOCK_INSIDE_SOLIDUS_PAIRS
  EXTENDED_WORD ;
  HARD_INLINE_LITERAL ;
  IDENTIFIER ;
  INTERPOLATEDCLAUSE ;            // Should become BLOCK_INSIDE_HYPHEN_PAIRS
  INTERPOLATEDCLAUSE_SILENTEND ;  // Should become BLOCK_INSIDE_HYPHEN_PAIR_WITH_LOWERED_END
  LITERAL ;
  PART ;
  PARENTHESIS ;
  PARAGRAPH_PLAIN ;  // Should become PARAGRAPH
  PARAGRAPH_SPEECH ; // Should become BIG_DASHED_LIST_ITEM
  QUOTE ;            // Should become BLOCK_INSIDE_DOUBLE_QUOTES
  SECTION ;
  SOFT_INLINE_LITERAL ;
  SQUARE_BRACKETS ;  // Should become BLOCK_INSIDE_SQUARE_BRACKETS
  SUPERSCRIPT ;
  TITLE ;
  URL ;  
  WORD ;
  
  PUNCTUATION_SIGN ;
  APOSTROPHE_WORDMATE ;
  SIGN_COMMA ;
  SIGN_FULLSTOP ;
  SIGN_ELLIPSIS ;
  SIGN_QUESTIONMARK ;
  SIGN_EXCLAMATIONMARK ;
  SIGN_SEMICOLON ;
  SIGN_COLON ;  
  
  FUNCTION_CALL ;
  FUNCTION_NAME ;
  VALUED_ARGUMENT_MODIFIER ;
  VALUED_ARGUMENT_PRIMARY ;
  VALUED_ARGUMENT_FLAG ;
  VALUED_ARGUMENT_ANCILLARY ;
  VALUED_ARGUMENT_ASSIGNMENT ;
}



@lexer::members {
 
  private novelang.parser.antlr.ProblemDelegate delegate = 
      new novelang.parser.antlr.ProblemDelegate() ;
 
  public void setProblemDelegate( novelang.parser.antlr.ProblemDelegate delegate ) {
    this.delegate = delegate ;
  }
 
  @Override
  public void reportError(org.antlr.runtime.RecognitionException e ) {
    if( null == delegate ) {
      super.reportError( e ) ;
    } else {
      delegate.report( e ) ;
    }
  }
 
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

part 
  : ( mediumbreak | largebreak )? 
  
    (   p += chapter 
      | p += section 
      | p += paragraph 
      | p += blockQuote 
      | p += literal
      | p += bigDashedListItem
    )
    ( largebreak (
        p += chapter 
      | p += section 
      | p += paragraph 
      | p += blockQuote 
      | p += literal
      | p += bigDashedListItem
    ) )*      
    ( mediumbreak | largebreak )? 
    EOF 
    -> ^( PART $p+ )
  ; 
  
chapter 
  : ( ASTERISK ASTERISK ASTERISK
      ( whitespace? title )?
    )
    -> ^( CHAPTER title? )
  ;

section 
  : ( EQUALS_SIGN EQUALS_SIGN EQUALS_SIGN 
      ( whitespace? title )?
    )
    -> ^( SECTION title? ) 
  ;

// =====================
// Paragraph and related
// =====================

/** Title is like a paragraph but it can't start by a URL as URL always start
 *  on the first column so it would clash with section / chapter introducer.
 *  It may contain a URL, however.
 */
title
  : (
      (   t += smallDashedListItem
	      | ( t += mixedDelimitedSpreadBlock 
	          ( whitespace t += mixedDelimitedSpreadBlock )* 
	        )
	    )
	    ( whitespace? softbreak 
	      (   ( url ) => t += url
	        | ( smallDashedListItem ) => t += smallDashedListItem
	        | ( whitespace? t += mixedDelimitedSpreadBlock 
	            ( whitespace t += mixedDelimitedSpreadBlock )* 
	          )        
	      )
	    )*    
	  ) -> ^( TITLE $t+ )
  ;  

headerIdentifier : ; // TODO

paragraph 
	: ( (   ( url ) => p += url
	      | ( smallDashedListItem ) => p += smallDashedListItem
	      | ( p += mixedDelimitedSpreadBlock 
	          ( whitespace p+= mixedDelimitedSpreadBlock )* 
	        )
	    )
	    ( whitespace? softbreak 
	      ( ( url ) => p += url
	        | ( smallDashedListItem ) => p += smallDashedListItem
	        | ( whitespace? p += mixedDelimitedSpreadBlock 
	            ( whitespace p+= mixedDelimitedSpreadBlock )* 
	          )        
	      )
	    )*
	  ) -> ^( PARAGRAPH_PLAIN $p+ )
    
  ;  



// =======================
// Generic delimited stuff
// =======================
  
delimitedSpreadblock
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock
  | doubleQuotedSpreadBlock
  | emphasizedSpreadBlock
  | hyphenPairSpreadBlock
  ;

delimitedMonoblock
  : parenthesizedMonoblock
  | squarebracketsMonoblock
  | doubleQuotedMonoblock
  | emphasizedMonoblock
  | hyphenPairMonoblock
  ;

mixedDelimitedSpreadBlock  
  : ( word 
      ( (   punctuationSign 
          | delimitedSpreadblock 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedSpreadblock 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedSpreadblock 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;
                
/** Everything in this rule implies a syntactic predicate, 
 *  so it will fool ANTLRWorks' interpreter.
 */
spreadBlockBody
  : (   
        ( ( softbreak url whitespace? softbreak ) => 
              ( softbreak url softbreak ) 
        ) 
      | ( ( softbreak whitespace? smallDashedListItem whitespace? softbreak ) => 
                ( softbreak smallDashedListItem softbreak ) 
        )
      | ( ( softbreak whitespace? )? 
          mixedDelimitedSpreadBlock
          ( whitespace mixedDelimitedSpreadBlock )*
        )
    )
    (   
        ( ( softbreak url whitespace? softbreak ) => 
              ( softbreak url softbreak ) 
        ) 
      | ( ( softbreak whitespace? smallDashedListItem whitespace? softbreak ) => 
                ( softbreak whitespace? smallDashedListItem softbreak ) 
          )
      | ( ( whitespace? softbreak whitespace? mixedDelimitedSpreadBlock ) =>
                ( whitespace? softbreak whitespace? mixedDelimitedSpreadBlock )
          ( whitespace 
            mixedDelimitedSpreadBlock
          )*
        )             
    )* 
    // Missing: SOFTBREAK after last mixedDelimitedSpreadBlock
  ;  


monoblockBody
  : mixedDelimitedMonoblock
    ( whitespace mixedDelimitedMonoblock )*
  ;  

  
mixedDelimitedMonoblock  
  : ( word ( 
      (   punctuationSign 
        | delimitedMonoblock 
        | softInlineLiteral 
        | hardInlineLiteral 
      )+ word? )? 
    ) 
  | ( (   punctuationSign 
        | delimitedMonoblock 
        | softInlineLiteral 
        | hardInlineLiteral 
      )+ 
      word? 
    ) 
  ;
                

// ===========
// Parenthesis
// ===========
  
parenthesizedSpreadblock
  : ( LEFT_PARENTHESIS whitespace?
      ( spreadBlockBody 
        whitespace? 
      )
      RIGHT_PARENTHESIS
    ) -> ^( PARENTHESIS spreadBlockBody )
  ;

parenthesizedMonoblock
  : ( LEFT_PARENTHESIS whitespace?
      ( monoblockBody 
        whitespace? 
      )
      RIGHT_PARENTHESIS
    ) -> ^( PARENTHESIS monoblockBody )
  ;

// ===============
// Square brackets
// ===============
  
squarebracketsSpreadblock
  : ( LEFT_SQUARE_BRACKET whitespace?
      ( spreadBlockBody 
        whitespace? 
      )
      RIGHT_SQUARE_BRACKET
    ) -> ^( SQUARE_BRACKETS spreadBlockBody )
  ;

squarebracketsMonoblock
  : ( LEFT_SQUARE_BRACKET whitespace?
      ( monoblockBody 
        whitespace? 
      )
      RIGHT_SQUARE_BRACKET
    ) -> ^( SQUARE_BRACKETS monoblockBody )
  ;


// =============
// Double quotes
// =============

doubleQuotedSpreadBlock
	: ( DOUBLE_QUOTE whitespace?
	    ( b += spreadBlockBodyNoDoubleQuotes 
	      whitespace? 
	    )?
	    DOUBLE_QUOTE
	  ) -> ^( QUOTE $b+ ) 
  ;

delimitedSpreadblockNoDoubleQuotes
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock
  | emphasizedSpreadBlock
  | hyphenPairSpreadBlock
  ;

/** Don't allow URLs inside double quotes because of weird errors.
 *  Not wishable anyways because in a near future URLs may be preceded by double-quoted title.
 */
spreadBlockBodyNoDoubleQuotes
  : (   ( softbreak whitespace? smallDashedListItem whitespace? softbreak )
      | ( ( softbreak whitespace? )? 
          mixedDelimitedSpreadBlockNoDoubleQuotes
          ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )*
        )
    )
    (   ( softbreak whitespace? smallDashedListItem whitespace? softbreak )
      | ( whitespace? softbreak whitespace? 
          mixedDelimitedSpreadBlockNoDoubleQuotes
          ( whitespace 
            mixedDelimitedSpreadBlockNoDoubleQuotes
          )*
        )             
    )* 
    // Missing: SOFTBREAK after last mixedDelimitedSpreadBlockNoDoubleQuotes
  ;  

mixedDelimitedSpreadBlockNoDoubleQuotes
  : ( word 
      ( (   punctuationSign 
          | delimitedSpreadblockNoDoubleQuotes 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedSpreadblockNoDoubleQuotes 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedSpreadblockNoDoubleQuotes 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;

doubleQuotedMonoblock
 : ( DOUBLE_QUOTE whitespace?
	    ( b += monoblockBodyNoDoubleQuotes 
	      whitespace? 
	    )?
	    DOUBLE_QUOTE
	  ) -> ^( QUOTE $b+ )
  ;

delimitedMonoblockNoDoubleQuotes
  : parenthesizedMonoblock
  | squarebracketsMonoblock
  | emphasizedMonoblock
  ;

monoblockBodyNoDoubleQuotes
  : ( mixedDelimitedMonoblockNoDoubleQuotes
      ( whitespace mixedDelimitedMonoblockNoDoubleQuotes )*
    )
    ( whitespace? softbreak whitespace? 
      mixedDelimitedMonoblockNoDoubleQuotes
      ( whitespace 
        mixedDelimitedMonoblockNoDoubleQuotes
      )*                   
    )* 
  ;  

mixedDelimitedMonoblockNoDoubleQuotes
//  : ( word ( ( punctuationSign | delimitedMonoblockNoDoubleQuotes )+ word? )? ) 
//  | ( ( punctuationSign | delimitedMonoblockNoDoubleQuotes )+ word? ) 
//  ;
  : ( word 
      ( (   punctuationSign 
          | delimitedMonoblockNoDoubleQuotes 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedMonoblockNoDoubleQuotes 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedMonoblockNoDoubleQuotes 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;
  
  


// ========
// Emphasis
// ========

emphasizedSpreadBlock
	: ( SOLIDUS SOLIDUS whitespace?
	    ( b += spreadBlockBodyNoEmphasis 
	      whitespace? 
	    )?
	    SOLIDUS SOLIDUS
	  ) -> ^( EMPHASIS $b+ )
  ;

delimitedSpreadblockNoEmphasis
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock
  | doubleQuotedSpreadBlock
  | hyphenPairSpreadBlock
  ;

/** Don't allow URLs inside double quotes because of weird errors.
 *  Not wishable anyways because in a near future URLs may be preceded by double-quoted title.
 */
spreadBlockBodyNoEmphasis
  : (   ( softbreak whitespace? smallDashedListItem whitespace? softbreak )
      | ( ( softbreak whitespace? )? 
          mixedDelimitedSpreadBlockNoEmphasis
          ( whitespace mixedDelimitedSpreadBlockNoEmphasis )*
        )
    )
    (   ( softbreak whitespace? smallDashedListItem whitespace? softbreak )
      | ( whitespace? softbreak whitespace? 
          mixedDelimitedSpreadBlockNoEmphasis
          ( whitespace 
            mixedDelimitedSpreadBlockNoEmphasis
          )*
        )             
    )* 
    // Missing: SOFTBREAK after last mixedDelimitedSpreadBlockNoEmphasis
  ;  

mixedDelimitedSpreadBlockNoEmphasis
//  : ( word ( ( punctuationSign | delimitedSpreadblockNoEmphasis )+ word? )? ) 
//  | ( ( punctuationSign | delimitedSpreadblockNoEmphasis )+ word? ) 
//  ;
  : ( word 
      ( (   punctuationSign 
          | delimitedSpreadblockNoEmphasis 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedSpreadblockNoEmphasis 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedSpreadblockNoEmphasis 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;

  

emphasizedMonoblock
	: ( SOLIDUS SOLIDUS whitespace?
	    ( b += monoblockBodyNoEmphasis 
	      whitespace? 
	    )?
	    SOLIDUS SOLIDUS
	  ) -> ^( EMPHASIS $b+ )
  ;

delimitedMonoblockNoEmphasis
  : parenthesizedMonoblock
  | squarebracketsMonoblock
  | doubleQuotedMonoblock
  | hyphenPairMonoblock
  ;

monoblockBodyNoEmphasis
  : ( mixedDelimitedMonoblockNoEmphasis
      ( whitespace mixedDelimitedMonoblockNoEmphasis )*
    )
    ( whitespace? softbreak whitespace? 
      mixedDelimitedMonoblockNoEmphasis
      ( whitespace 
        mixedDelimitedMonoblockNoEmphasis
      )*                   
    )* 
  ;  

mixedDelimitedMonoblockNoEmphasis
//  : ( word ( ( punctuationSign | delimitedMonoblockNoEmphasis )+ word? )? ) 
//  | ( ( punctuationSign | delimitedMonoblockNoEmphasis )+ word? ) 
//  ;
  : ( word 
      ( (   punctuationSign 
          | delimitedMonoblockNoEmphasis 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedMonoblockNoEmphasis 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedMonoblockNoEmphasis 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;

  

// ===============================================
// Block inside hyphen pairs (interpolated clause)
// ===============================================

hyphenPairSpreadBlock
	: HYPHEN_MINUS HYPHEN_MINUS whitespace?
    ( b += spreadBlockBodyNoHyphenPair
      whitespace? 
    )?
    HYPHEN_MINUS 
    (   HYPHEN_MINUS -> ^( INTERPOLATEDCLAUSE $b+ ) 
      | LOW_LINE -> ^( INTERPOLATEDCLAUSE_SILENTEND $b+ ) 
    ) 	  
  ;

delimitedSpreadblockNoHyphenPair
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock
  | emphasizedSpreadBlock
  | doubleQuotedSpreadBlock
  ;

/** Don't allow URLs inside double quotes because of weird errors.
 *  Not wishable anyways because in a near future URLs may be preceded by double-quoted title.
 */
spreadBlockBodyNoHyphenPair
  : (   ( softbreak whitespace? smallDashedListItem whitespace? softbreak )
      | ( ( softbreak whitespace? )? 
          mixedDelimitedSpreadBlockNoHyphenPair
          ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )*
        )
    )
    (   ( softbreak whitespace? smallDashedListItem whitespace? softbreak )
      | ( whitespace? softbreak whitespace? 
          mixedDelimitedSpreadBlockNoHyphenPair
          ( whitespace 
            mixedDelimitedSpreadBlockNoHyphenPair
          )*
        )             
    )* 
    // Missing: SOFTBREAK after last mixedDelimitedSpreadBlockNoHyphenPair
  ;  

mixedDelimitedSpreadBlockNoHyphenPair
//  : ( word ( ( punctuationSign | delimitedSpreadblockNoHyphenPair )+ word? )? ) 
//  | ( ( punctuationSign | delimitedSpreadblockNoHyphenPair )+ word? ) 
//  ;
  : ( word 
      ( (   punctuationSign 
          | delimitedSpreadblockNoHyphenPair 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedSpreadblockNoHyphenPair 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedSpreadblockNoHyphenPair 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;
  

hyphenPairMonoblock
 : ( HYPHEN_MINUS HYPHEN_MINUS whitespace?
	    ( b += monoblockBodyNoHyphenPair
	      whitespace? 
	    )?
	    HYPHEN_MINUS HYPHEN_MINUS
	  ) -> ^( INTERPOLATEDCLAUSE $b+ )
  ;

delimitedMonoblockNoHyphenPair
  : parenthesizedMonoblock
  | squarebracketsMonoblock
  | emphasizedMonoblock
  | doubleQuotedMonoblock
  ;

monoblockBodyNoHyphenPair
  : ( mixedDelimitedMonoblockNoHyphenPair
      ( whitespace mixedDelimitedMonoblockNoHyphenPair )*
    )
    ( whitespace? softbreak whitespace? 
      mixedDelimitedMonoblockNoHyphenPair
      ( whitespace 
        mixedDelimitedMonoblockNoHyphenPair
      )*                   
    )* 
  ;  

mixedDelimitedMonoblockNoHyphenPair
//  : ( word ( ( punctuationSign | delimitedMonoblockNoHyphenPair )+ word? )? ) 
//  | ( ( punctuationSign | delimitedMonoblockNoHyphenPair )+ word? ) 
//  ;
  : ( word 
      ( (   punctuationSign 
          | delimitedMonoblockNoHyphenPair 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedMonoblockNoHyphenPair 
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedMonoblockNoHyphenPair 
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    ) 
  ;
  






// =====
// Lists
// =====

bigDashedListItem
  : HYPHEN_MINUS HYPHEN_MINUS HYPHEN_MINUS 
    ( whitespace i += mixedDelimitedSpreadBlock )*
    ( whitespace? softbreak 
      (   ( whitespace? i += mixedDelimitedSpreadBlock 
            ( whitespace i += mixedDelimitedSpreadBlock )* 
          )
        | ( url ) => i += url
        | ( smallDashedListItem ) => i += smallDashedListItem
      )
    )* -> ^( PARAGRAPH_SPEECH $i+ )

  ;

smallDashedListItem
  : HYPHEN_MINUS ( whitespace mixedDelimitedMonoblock )+
  ;



// =======  
// Literal
// =======


blockQuote
  : LESS_THAN_SIGN LESS_THAN_SIGN 
    ( mediumbreak | largebreak )?
    paragraph 
    ( largebreak paragraph )* 
    ( mediumbreak | largebreak )?
    GREATER_THAN_SIGN GREATER_THAN_SIGN
    -> ^( BLOCKQUOTE paragraph* )
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
  
  
  
// ===========  
// Punctuation
// ===========  

leadingPunctuationSign
  : ELLIPSIS
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




// ====
// Word
// ====

word
  : ( w1 = rawWord ( CIRCUMFLEX_ACCENT w2 = rawWord ) )
    -> ^( WORD 
            { delegate.createTree( WORD, $w1.text ) } 
            ^( SUPERSCRIPT { delegate.createTree( WORD, $w2.text ) } )
        )	
  | ( w = rawWord )
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



blockIdentifier
  :	REVERSE_SOLIDUS w = word 
	  -> ^( IDENTIFIER { delegate.createTree( IDENTIFIER, $w.text ) } )
  ;

  
// ==================
// Book-related rules
// ==================

book
  : ( mediumbreak | largebreak )?
    functionCall
    ( largebreak functionCall )*      
    ( mediumbreak | largebreak )?
    EOF 
    -> ^( BOOK functionCall* )
  ;
  

functionCall
  : name = word 
     (   whitespace url 
       | WHITESPACE? SOFTBREAK WHITESPACE? paragraph
     )?
    ( mediumbreak ( ancillaryArgument | flagArgument | assignmentArgument ) )*
    ->  ^( FUNCTION_CALL 
            ^( FUNCTION_NAME { delegate.createTree( FUNCTION_NAME, $name.text ) } )  
            ^( VALUED_ARGUMENT_PRIMARY 
                paragraph? 
                url? 
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





// ====================
// Low-level constructs
// ====================

softbreak : SOFTBREAK -> ; 

whitespace : WHITESPACE -> ;

mediumbreak
  : ( WHITESPACE
      | ( WHITESPACE? SOFTBREAK WHITESPACE? )
    ) 
    ->
  ;
  
/** One blank line in the middle, white spaces everywhere.
 */
largebreak
  : ( ( WHITESPACE? SOFTBREAK ) ( WHITESPACE? SOFTBREAK )+ WHITESPACE? )
    ->
  ;  

anySymbolExceptGreaterthansignAndGraveAccent
  :     digit
      | hexLetter
      | nonHexLetter 
      | AMPERSAND 
      | APOSTROPHE   
      | ASTERISK
      | CIRCUMFLEX_ACCENT
      | COLON
      | COMMA 
      | COMMERCIAL_AT
      | DEGREE_SIGN
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
      | SECTION_SIGN
      | SEMICOLON
      | SOLIDUS
      | TILDE  
      | VERTICAL_LINE 
  ;

letters : letter+ ;

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
