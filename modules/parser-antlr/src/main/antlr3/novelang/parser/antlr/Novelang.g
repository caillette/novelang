/*
 * Copyright (C) 2008, 2009 Laurent Caillette
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


// Those tokens are turned into a NodeKind.java by TokenEnumerationGenerator.
// Beware of line comments, which are processed.
tokens {
  PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS ;        // tagbehavior=SCOPE
  COMPOSIUM ;                                     // tagbehavior=TRAVERSABLE 
  LEVEL_INTRODUCER_ ;
  LEVEL_INTRODUCER_INDENT_ ;
  LEVEL_TITLE ;
  EXTENDED_WORD_ ;
  BLOCK_INSIDE_PARENTHESIS ;
  BLOCK_INSIDE_SQUARE_BRACKETS ;
  BLOCK_INSIDE_DOUBLE_QUOTES ;
  BLOCK_INSIDE_SOLIDUS_PAIRS ;
  BLOCK_INSIDE_HYPHEN_PAIRS ;
  BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE ;  
  BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS ;
  BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS ;
  BLOCK_AFTER_TILDE ;
  SUBBLOCK ;
  LINES_OF_LITERAL ;                             
  NOVELLA ;                                      // tagbehavior=TRAVERSABLE
  PARAGRAPH_REGULAR ;                            // tagbehavior=TERMINAL
  PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ ;   // tagbehavior=TERMINAL
  WORD_AFTER_CIRCUMFLEX_ACCENT ;
  URL_LITERAL ;  
  RASTER_IMAGE ;
  VECTOR_IMAGE ;
  RESOURCE_LOCATION ;
  EMBEDDED_LIST_ITEM_WITH_HYPHEN_ ;
  CELL ;
  CELL_ROW ;
  CELL_ROWS_WITH_VERTICAL_LINE ;                 // tagbehavior=TERMINAL
  WORD_ ;
  WHITESPACE_ ;
  LINE_BREAK_ ;
  TAG ;
  ABSOLUTE_IDENTIFIER ;
  RELATIVE_IDENTIFIER ;
  COMPOSITE_IDENTIFIER ;

  PUNCTUATION_SIGN ;
  APOSTROPHE_WORDMATE ;
  SIGN_COMMA ;           // punctuationsign=true
  SIGN_FULLSTOP ;        // punctuationsign=true
  SIGN_ELLIPSIS ;        // punctuationsign=true
  SIGN_QUESTIONMARK ;    // punctuationsign=true
  SIGN_EXCLAMATIONMARK ; // punctuationsign=true
  SIGN_SEMICOLON ;       // punctuationsign=true
  SIGN_COLON ;           // punctuationsign=true

  // Composium stuff
  
  COMMAND_INSERT_ ;
  COMMAND_INSERT_CREATELEVEL_ ;
  COMMAND_INSERT_NOHEAD_ ;
  COMMAND_INSERT_LEVELABOVE_ ;
  COMMAND_INSERT_RECURSE_ ;
  COMMAND_INSERT_SORT_ ;
  COMMAND_INSERT_STYLE_ ;
  
  COMMAND_MAPSTYLESHEET_ ;
  COMMAND_MAPSTYLESHEET_ASSIGNMENT_ ;

}

@header {
package novelang.parser.antlr ;
import novelang.parser.antlr.ProblemDelegate ; // Keep first, used as a marker by code generator.
import novelang.parser.antlr.ParserDelegate ;
import novelang.parser.antlr.delimited.BlockDelimiter ;
import novelang.common.Location ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
}

@lexer::header {
package novelang.parser.antlr ;
import novelang.parser.antlr.ProblemDelegate ; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
}

@lexer::members {
 
  private ProblemDelegate delegate = new ProblemDelegate() ;
 
  public void setProblemDelegate( ProblemDelegate delegate ) {
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
 
  private static final Logger LOGGER = LoggerFactory.getLogger( NovelangLexer.class ) ;

  @Override
  public void traceIn( String s, int i ) {
    // Do nothing, parser's logging is enough.
  }

  @Override
  public void traceOut( String s, int i ) {
    // Do nothing, parser's logging is enough.
  }
}


@parser::members {
  private ParserDelegate delegate ;//= new GrammarDelegate() ;

  public void setParserDelegate( ParserDelegate delegate ) {
    this.delegate = delegate ;
  }

  @Override
  public void traceIn( String s, int ruleIndex ) {
    delegate.traceIn( s, ruleIndex ) ;
  }

  @Override
  public void traceOut( String s, int ruleIndex ) {
    delegate.traceOut( s, ruleIndex ) ;
  }

  @Override
  public void reportError(org.antlr.runtime.RecognitionException e ) {
    if( null == delegate ) {
      super.reportError( e ) ;
    } else {
      delegate.report( e ) ;
    }
  }
/*
  @Override
  public java.lang.String getErrorMessage(
      org.antlr.runtime.RecognitionException recognitionException,
      java.lang.String[] strings
  ) {
    System.out.println( "getErrorMessage( " + recognitionException + "\n" + strings ) ;
    return super.getErrorMessage( recognitionException, strings ) ;
  }
*/  

/* 
  @Override
  public void emitErrorMessage( String string ) {
    if( null == delegate ) {
      super.emitErrorMessage( string ) ;
    } else {
      delegate.report( string ) ;
    }
  }
*/
}


// ============================
// Novella, chapter and section
// ============================

novella
  @init { final Location startLocation = delegate.createLocation( input.LT( 1 ) ) ; }
  : ( p += mediumbreak | p += largebreak )?
  
    (   p += levelIntroducer
      | p += paragraph 
      | p += embeddableResource
      | p += blockQuote 
      | p += literal
      | p += bigDashedListItem
      | p += cellRowSequence
    )
    ( p += largebreak (
        p += levelIntroducer 
      | p += paragraph 
      | p += embeddableResource
      | p += blockQuote 
      | p += literal
      | p += bigDashedListItem
      | p += cellRowSequence
    ) )*      
    ( mediumbreak | largebreak )? 
    EOF

    // Was:
    // -> ^( NOVELLA $p+ )
    -> {  delegate.createTree( NOVELLA, startLocation, $p ) }
  ;
  
levelIntroducer 
  @init { final Location startLocation = delegate.createLocation( input.LT( 1 ) ) ; }
  : ( ( tags mediumbreak )?
      ( ( relativeIdentifier | absoluteIdentifier ) mediumbreak )?
      levelIntroducerIndent
      ( whitespace? levelTitle )?
    )

    // Was:
    // -> ^( LEVEL_INTRODUCER_ levelIntroducerIndent levelTitle?
    //      tags? relativeIdentifier? absoluteIdentifier?
    //    )

    -> {  delegate.createTree(
              LEVEL_INTRODUCER_,
              startLocation,
              $levelIntroducerIndent.tree,
              $levelTitle.tree,
              $tags.tree,
              $relativeIdentifier.tree,
              $absoluteIdentifier.tree
       ) }

  ;


// ====
// Tags
// ====

tag
  : ( COMMERCIAL_AT s = symbolicName )
    -> { delegate.createTree( TAG, $s.text ) }  
  ;
  
  
tags
  : tag ( mediumbreak tag )*
  ;


// ===========
// Identifiers
// ===========


compositeIdentifier
  : REVERSE_SOLIDUS ( REVERSE_SOLIDUS identifierSegment )+
    -> ^( COMPOSITE_IDENTIFIER  identifierSegment+ )
  ;
  
relativeIdentifier
  : ( REVERSE_SOLIDUS identifierSegment )
    -> ^( RELATIVE_IDENTIFIER  identifierSegment  )
  ;

absoluteIdentifier
  : REVERSE_SOLIDUS ( REVERSE_SOLIDUS identifierSegment )
    -> ^( ABSOLUTE_IDENTIFIER  identifierSegment )
  ;
  
identifierSegment
  : symbolicName
    -> { delegate.createTree( $symbolicName.text ) }  
  ;

// =====================
// Paragraph and related
// =====================

/** Title is like a paragraph but it can't start by a URL as URL always start
 *  on the first column so it would clash with section / chapter introducer.
 *  It may contain a URL, however.
 */
levelTitle
  : (
      { delegate.enterBlockDelimiterBoundary( input.LT( 1 ) ) ; }
      (   t += smallDashedListItem
	      | ( t += mixedDelimitedSpreadBlock 
	          ( t += whitespace t += mixedDelimitedSpreadBlock )* 
	        )
	    )
	    ( t += whitespace? t += softbreak 
	      (   ( url ) => t += url
	        | ( smallDashedListItem ) => t += smallDashedListItem
	        | ( t += whitespace? t += mixedDelimitedSpreadBlock 
	            ( t += whitespace t += mixedDelimitedSpreadBlock )* 
	          )        
	      )
	    )*    
	    { delegate.leaveBlockDelimiterBoundary() ; }
	  ) -> ^( LEVEL_TITLE $t+ )
  ;  

headerIdentifier : ; // TODO

paragraph 
  @init { final Location startLocation = delegate.createLocation( input.LT( 1 ) ) ; }
	: ( ( p += tags mediumbreak )?
      { delegate.enterBlockDelimiterBoundary( input.LT( 1 ) ) ; }
	    (   ( url ) => p += url
	      | ( smallDashedListItem ) => p += smallDashedListItem
	      | ( p += mixedDelimitedSpreadBlock 
	          ( p += whitespace p+= mixedDelimitedSpreadBlock )* 
	        )
	    )
	    ( p += whitespace? p += softbreak 
	      (   ( url ) => p += url
	        | ( whitespace? smallDashedListItem ) => p += whitespace? p += smallDashedListItem
	        | ( p += whitespace? p += mixedDelimitedSpreadBlock 
	            ( p += whitespace p+= mixedDelimitedSpreadBlock )* 
	          )
	      )
	    )*
	    { delegate.leaveBlockDelimiterBoundary() ; }

	  // Was:
	  // ) -> ^( PARAGRAPH_REGULAR $p+ )

      ) -> {  delegate.createTree(
                  PARAGRAPH_REGULAR,
                  startLocation,
                  $p
              )
        }
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

delimitedMonoblockNoSeparator
  : parenthesizedMonoblockNoSeparator
  | emphasizedMonoblockNoSeparator
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
  | blockAfterTilde
  ;
                
/** Everything in this rule implies a syntactic predicate, 
 *  so it will fool ANTLRWorks' interpreter.
 */
spreadBlockBody  // Relies on mixedDelimitedSpreadBlock
  : 
    (  // Beginning by URL or smallDashedListItem
      (
          ( ( softbreak url ) => ( softbreak url ) 
          ) 
        | ( ( softbreak whitespace? smallDashedListItem ) => 
                  ( softbreak whitespace? smallDashedListItem ) 
          )
	    )
	    
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlock 
		              ( whitespace mixedDelimitedSpreadBlock )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlock 
				              ( whitespace mixedDelimitedSpreadBlock )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlock 
				  ( whitespace mixedDelimitedSpreadBlock )*
				)?   	    
  	  )   	  
    )
    
  | ( // Other kind of beginning: just text 

      mixedDelimitedSpreadBlock 
      ( whitespace mixedDelimitedSpreadBlock )*
      
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlock 
		              ( whitespace mixedDelimitedSpreadBlock )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlock 
				              ( whitespace mixedDelimitedSpreadBlock )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlock 
				  ( whitespace mixedDelimitedSpreadBlock )*
				)?
   	    
   	  )?

    )
  ;  


monoblockBody
  : mixedDelimitedMonoblock
    ( whitespace mixedDelimitedMonoblock )*
  ;  

monoblockBodyNoSeparator
  : 
    ( word 
      ( (   punctuationSign 
          | delimitedMonoblockNoSeparator 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | 
  
    ( (   punctuationSign 
        | delimitedMonoblockNoSeparator
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedMonoblockNoSeparator
          | softInlineLiteral 
          | hardInlineLiteral           
        ) 
      )*   
      word?
    )  
  ;  

  
mixedDelimitedMonoblock  
  : 
    ( word 
      ( (   punctuationSign 
          | delimitedMonoblock 
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | 
  
    ( (   punctuationSign 
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
  | embeddableResource
  | blockAfterTilde
  ;
                

// ===========
// Parenthesis
// ===========
  
parenthesizedSpreadblock	  
  : ( 
      { delegate.startDelimitedText( BlockDelimiter.PARENTHESIS, input.LT( 1 ) ) ; }
      LEFT_PARENTHESIS whitespace?
      ( spreadBlockBody 
        whitespace? 
      )
      { delegate.reachEndDelimiter( BlockDelimiter.PARENTHESIS ) ; }
      RIGHT_PARENTHESIS
      { delegate.endDelimitedText( BlockDelimiter.PARENTHESIS ) ; }
    ) -> ^( BLOCK_INSIDE_PARENTHESIS spreadBlockBody )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.PARENTHESIS, mte ) ; }

parenthesizedMonoblock
  : ( 
      { delegate.startDelimitedText( BlockDelimiter.PARENTHESIS, input.LT( 1 ) ) ; }
      LEFT_PARENTHESIS whitespace?
      ( monoblockBody 
        whitespace? 
      )
      { delegate.reachEndDelimiter( BlockDelimiter.PARENTHESIS ) ; }
      RIGHT_PARENTHESIS
      { delegate.endDelimitedText( BlockDelimiter.PARENTHESIS ) ; }
    ) -> ^( BLOCK_INSIDE_PARENTHESIS monoblockBody )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.PARENTHESIS, mte ) ; }

parenthesizedMonoblockNoSeparator
  : ( 
      { delegate.startDelimitedText( BlockDelimiter.PARENTHESIS, input.LT( 1 ) ) ; }
      LEFT_PARENTHESIS monoblockBodyNoSeparator
      { delegate.reachEndDelimiter( BlockDelimiter.PARENTHESIS ) ; }
      RIGHT_PARENTHESIS
      { delegate.endDelimitedText( BlockDelimiter.PARENTHESIS ) ; }
    ) -> ^( BLOCK_INSIDE_PARENTHESIS monoblockBodyNoSeparator )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.PARENTHESIS, mte ) ; }

// ===============
// Square brackets
// ===============
  
squarebracketsSpreadblock
  : ( 
      { delegate.startDelimitedText( BlockDelimiter.SQUARE_BRACKETS, input.LT( 1 ) ) ; }
      LEFT_SQUARE_BRACKET whitespace?
      ( spreadBlockBody 
        whitespace? 
      )
      { delegate.reachEndDelimiter( BlockDelimiter.SQUARE_BRACKETS ) ; }
      RIGHT_SQUARE_BRACKET
      { delegate.endDelimitedText( BlockDelimiter.SQUARE_BRACKETS ) ; }
    ) -> ^( BLOCK_INSIDE_SQUARE_BRACKETS spreadBlockBody )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.SQUARE_BRACKETS, mte ) ; }

squarebracketsMonoblock
  : ( 
      { delegate.startDelimitedText( BlockDelimiter.SQUARE_BRACKETS, input.LT( 1 ) ) ; }
      LEFT_SQUARE_BRACKET whitespace?
      ( monoblockBody 
        whitespace? 
      )
      { delegate.reachEndDelimiter( BlockDelimiter.SQUARE_BRACKETS ) ; }
      RIGHT_SQUARE_BRACKET
      { delegate.endDelimitedText( BlockDelimiter.SQUARE_BRACKETS ) ; }
    ) -> ^( BLOCK_INSIDE_SQUARE_BRACKETS monoblockBody )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.SQUARE_BRACKETS, mte ) ; }


// =============
// Double quotes
// =============

doubleQuotedSpreadBlock
	: ( 
      { delegate.startDelimitedText( BlockDelimiter.DOUBLE_QUOTES, input.LT( 1 ) ) ; }
      DOUBLE_QUOTE whitespace?
	    ( b += spreadBlockBodyNoDoubleQuotes 
	      whitespace? 
	    )?
	    { delegate.reachEndDelimiter( BlockDelimiter.DOUBLE_QUOTES ) ; }
	    DOUBLE_QUOTE
      { delegate.endDelimitedText( BlockDelimiter.DOUBLE_QUOTES ) ; }
	  ) -> ^( BLOCK_INSIDE_DOUBLE_QUOTES $b+ ) 
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.DOUBLE_QUOTES, mte ) ; }

delimitedSpreadblockNoDoubleQuotes
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock
  | emphasizedSpreadBlock
  | hyphenPairSpreadBlock
  ;


spreadBlockBodyNoDoubleQuotes  // Relies on: mixedDelimitedSpreadBlockNoDoubleQuotes
  : 
    (  // Beginning by URL or smallDashedListItem
      (
          ( ( softbreak url ) => ( softbreak url ) 
          ) 
        | ( ( softbreak whitespace? smallDashedListItem ) => 
                  ( softbreak whitespace? smallDashedListItem ) 
          )
	    )
	    
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoDoubleQuotes 
		              ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoDoubleQuotes 
				              ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlockNoDoubleQuotes 
				  ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )*
				)?   	    
  	  )   	  
    )
    
  | ( // Other kind of beginning: just text 

      mixedDelimitedSpreadBlockNoDoubleQuotes 
      ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )*
      
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoDoubleQuotes 
		              ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoDoubleQuotes 
				              ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlockNoDoubleQuotes 
				  ( whitespace mixedDelimitedSpreadBlockNoDoubleQuotes )*
				)?
   	    
   	  )?

    )
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
  | blockAfterTilde    
  ;

doubleQuotedMonoblock
 : ( 
      { delegate.startDelimitedText( BlockDelimiter.DOUBLE_QUOTES, input.LT( 1 ) ) ; }
      DOUBLE_QUOTE whitespace?
	    ( b += monoblockBodyNoDoubleQuotes 
	      whitespace? 
	    )?
	    { delegate.reachEndDelimiter( BlockDelimiter.DOUBLE_QUOTES ) ; }
	    DOUBLE_QUOTE
      { delegate.endDelimitedText( BlockDelimiter.DOUBLE_QUOTES ) ; }
	  ) -> ^( BLOCK_INSIDE_DOUBLE_QUOTES $b+ )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.DOUBLE_QUOTES, mte ) ; }
  

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
	: ( 
      { delegate.startDelimitedText( BlockDelimiter.SOLIDUS_PAIRS, input.LT( 1 ) ) ; }
	    SOLIDUS SOLIDUS whitespace?
	    ( b += spreadBlockBodyNoEmphasis 
	      whitespace? 
	    )?
	    { delegate.reachEndDelimiter( BlockDelimiter.SOLIDUS_PAIRS ) ; }
	    SOLIDUS SOLIDUS
      { delegate.endDelimitedText( BlockDelimiter.SOLIDUS_PAIRS ) ; }
	  ) -> ^( BLOCK_INSIDE_SOLIDUS_PAIRS $b+ )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.SOLIDUS_PAIRS, mte ) ; }

delimitedSpreadblockNoEmphasis
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock
  | doubleQuotedSpreadBlock
  | hyphenPairSpreadBlock
  ;

spreadBlockBodyNoEmphasis      // Relies on: mixedDelimitedSpreadBlockNoEmphasis
  : 
    (  // Beginning by URL or smallDashedListItem
      (
          ( ( softbreak url ) => ( softbreak url ) 
          ) 
        | ( ( softbreak whitespace? smallDashedListItem ) => 
                  ( softbreak whitespace? smallDashedListItem ) 
          )
	    )
	    
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoEmphasis 
		              ( whitespace mixedDelimitedSpreadBlockNoEmphasis )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoEmphasis 
				              ( whitespace mixedDelimitedSpreadBlockNoEmphasis )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlockNoEmphasis 
				  ( whitespace mixedDelimitedSpreadBlockNoEmphasis )*
				)?   	    
  	  )   	  
    )
    
  | ( // Other kind of beginning: just text 

      mixedDelimitedSpreadBlockNoEmphasis 
      ( whitespace mixedDelimitedSpreadBlockNoEmphasis )*
      
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoEmphasis 
		              ( whitespace mixedDelimitedSpreadBlockNoEmphasis )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoEmphasis 
				              ( whitespace mixedDelimitedSpreadBlockNoEmphasis )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlockNoEmphasis 
				  ( whitespace mixedDelimitedSpreadBlockNoEmphasis )*
				)?
   	    
   	  )?

    )
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
  | blockAfterTilde  whitespace
  ;

  

emphasizedMonoblock
	: ( 
      { delegate.startDelimitedText( BlockDelimiter.SOLIDUS_PAIRS, input.LT( 1 ) ) ; }
	    SOLIDUS SOLIDUS whitespace?
	    ( b += monoblockBodyNoEmphasis 
	      whitespace? 
	    )?
	    { delegate.reachEndDelimiter( BlockDelimiter.SOLIDUS_PAIRS ) ; }
	    SOLIDUS SOLIDUS
      { delegate.endDelimitedText( BlockDelimiter.SOLIDUS_PAIRS ) ; }
	  ) -> ^( BLOCK_INSIDE_SOLIDUS_PAIRS $b+ )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.SOLIDUS_PAIRS, mte ) ; }
  

emphasizedMonoblockNoSeparator
	: ( 
      { delegate.startDelimitedText( BlockDelimiter.SOLIDUS_PAIRS, input.LT( 1 ) ) ; }
	    SOLIDUS SOLIDUS 
	    ( b += monoblockBodyNoEmphasisNoSeparator )?
	    { delegate.reachEndDelimiter( BlockDelimiter.SOLIDUS_PAIRS ) ; }
	    SOLIDUS SOLIDUS
      { delegate.endDelimitedText( BlockDelimiter.SOLIDUS_PAIRS ) ; }
	  ) -> ^( BLOCK_INSIDE_SOLIDUS_PAIRS $b+ )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.SOLIDUS_PAIRS, mte ) ; }
  

delimitedMonoblockNoEmphasis
  : parenthesizedMonoblock
  | squarebracketsMonoblock
  | doubleQuotedMonoblock
  | hyphenPairMonoblock
  ;

delimitedMonoblockNoEmphasisNoSeparator
  : parenthesizedMonoblockNoSeparator
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

monoblockBodyNoEmphasisNoSeparator
  : mixedDelimitedMonoblockNoEmphasisNoSeparator
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


mixedDelimitedMonoblockNoEmphasisNoSeparator
  : ( word 
      ( (   punctuationSign 
          | delimitedMonoblockNoEmphasisNoSeparator  
          | softInlineLiteral 
          | hardInlineLiteral 
      ) word? )*
	  ) 
  | ( (   punctuationSign 
        | delimitedMonoblockNoEmphasisNoSeparator
        | softInlineLiteral 
        | hardInlineLiteral 
      )
      ( word? 
        (   punctuationSign 
          | delimitedMonoblockNoEmphasisNoSeparator 
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
	: 
    { delegate.startDelimitedText( BlockDelimiter.TWO_HYPHENS, input.LT( 1 ) ) ; }
	  HYPHEN_MINUS HYPHEN_MINUS whitespace?
    ( b += spreadBlockBodyNoHyphenPair
      whitespace? 
    )?
    { delegate.reachEndDelimiter( BlockDelimiter.TWO_HYPHENS ) ; }
    HYPHEN_MINUS 
    (   HYPHEN_MINUS -> ^( BLOCK_INSIDE_HYPHEN_PAIRS $b+ ) 
      | LOW_LINE -> ^( BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE $b+ ) 
    ) 	  
    { delegate.endDelimitedText( BlockDelimiter.TWO_HYPHENS ) ; }
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.TWO_HYPHENS, mte ) ; }

delimitedSpreadblockNoHyphenPair
  : parenthesizedSpreadblock
  | squarebracketsSpreadblock 
  | emphasizedSpreadBlock
  | doubleQuotedSpreadBlock
  ;

spreadBlockBodyNoHyphenPair  // Relies on: mixedDelimitedSpreadBlockNoHyphenPair
  : 
    (  // Beginning by URL or smallDashedListItem
      (
          ( ( softbreak url ) => ( softbreak url ) 
          ) 
        | ( ( softbreak whitespace? smallDashedListItem ) => 
                  ( softbreak whitespace? smallDashedListItem ) 
          )
	    )
	    
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoHyphenPair 
		              ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoHyphenPair 
				              ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlockNoHyphenPair 
				  ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )*
				)?   	    
  	  )   	  
    )
    
  | ( // Other kind of beginning: just text 

      mixedDelimitedSpreadBlockNoHyphenPair 
      ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )*
      
      ( ( (
		          ( ( whitespace? softbreak url ) => ( whitespace? softbreak url ) 
		          ) 
		        | ( ( whitespace? softbreak whitespace? smallDashedListItem ) 
		             => ( whitespace? softbreak whitespace? smallDashedListItem ) 
		          )
		        | ( 
		            ( 
		              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoHyphenPair 
		              ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )* 
		              whitespace? softbreak // lookahead: don't consume this block if last.
		            )
		            =>  ( 
				              whitespace? softbreak whitespace? mixedDelimitedSpreadBlockNoHyphenPair 
				              ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )* 
		                )            
		          )
		      )*	      
	   	    whitespace? softbreak 
   	    )
   	    
				( whitespace? mixedDelimitedSpreadBlockNoHyphenPair 
				  ( whitespace mixedDelimitedSpreadBlockNoHyphenPair )*
				)?
   	    
   	  )?

    )
  ;  

mixedDelimitedSpreadBlockNoHyphenPair
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
    | blockAfterTilde
  ;
  

hyphenPairMonoblock
 : ( 
      { delegate.startDelimitedText( BlockDelimiter.TWO_HYPHENS, input.LT( 1 ) ) ; }
      HYPHEN_MINUS HYPHEN_MINUS whitespace?
	    ( b += monoblockBodyNoHyphenPair
	      whitespace? 
	    )?
	    { delegate.reachEndDelimiter( BlockDelimiter.TWO_HYPHENS ) ; }
	    HYPHEN_MINUS HYPHEN_MINUS
      { delegate.endDelimitedText( BlockDelimiter.TWO_HYPHENS ) ; }
	  ) -> ^( BLOCK_INSIDE_HYPHEN_PAIRS $b+ )
  ;
  catch[ MismatchedTokenException mte ] {
      delegate.reportMissingDelimiter( BlockDelimiter.TWO_HYPHENS, mte ) ; }
  

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
  : 
    ( i += tags mediumbreak )?	
    HYPHEN_MINUS HYPHEN_MINUS HYPHEN_MINUS 
    { delegate.enterBlockDelimiterBoundary( input.LT( 1 ) ) ; }
    ( i += whitespace i += mixedDelimitedSpreadBlock )*
    ( ( i += whitespace? i += softbreak 
        ( 
            ( url ) => i += url
//          | ( smallDashedListItem ) => i += smallDashedListItem
          | ( whitespace? smallDashedListItem ) => i += whitespace? i += smallDashedListItem
          | ( i += whitespace? i += mixedDelimitedSpreadBlock 
              ( i += whitespace i += mixedDelimitedSpreadBlock )* 
            )
        )
      )* -> ^( PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_ $i* )
    )
    { delegate.leaveBlockDelimiterBoundary() ; }
  ;

smallDashedListItem
  : HYPHEN_MINUS 
    { delegate.enterBlockDelimiterBoundary( input.LT( 1 ) ) ; }
    ( b += whitespace b += mixedDelimitedMonoblock )+
    { delegate.leaveBlockDelimiterBoundary() ; }
    -> ^( EMBEDDED_LIST_ITEM_WITH_HYPHEN_ $b+ )
  ;


// =====
// Cells
// =====

cellRowSequence
  : ( c += tags mediumbreak )?	
    c += cellRow 
    ( ( WHITESPACE? SOFTBREAK WHITESPACE? ) c += cellRow )*
    -> ^( CELL_ROWS_WITH_VERTICAL_LINE $c+ )
  ;

cellRow
  : VERTICAL_LINE ( whitespace? cell )+ 
    -> ^( CELL_ROW cell+ )
  ;
  
cell
  : { delegate.enterBlockDelimiterBoundary( input.LT( 1 ) ) ; }
    ( mixedDelimitedMonoblock ( whitespace mixedDelimitedMonoblock )* whitespace? )? 
    { delegate.leaveBlockDelimiterBoundary() ; }
    VERTICAL_LINE
    -> ^( CELL mixedDelimitedMonoblock* )
  ;  

// =======  
// Literal
// =======


blockQuote
  @init { final Location startLocation = delegate.createLocation( input.LT( 1 ) ) ; }
  : (
      { delegate.enterBlockDelimiterBoundary( input.LT( 1 ) ) ; }    
      ( p += tags mediumbreak )?
      LESS_THAN_SIGN LESS_THAN_SIGN
      ( mediumbreak | largebreak )?
      ( p += paragraph | p += literal )
      ( largebreak ( p += paragraph | p += literal ) )*
      ( mediumbreak | largebreak )?
      GREATER_THAN_SIGN GREATER_THAN_SIGN
      { delegate.leaveBlockDelimiterBoundary() ; }
    )

    // Was:
    // -> ^( PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS $p+ )

     -> {  delegate.createTree(
               PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS,
               startLocation,
               $p
           )
         }
  ;

literal
  : ( tags mediumbreak )?
    LESS_THAN_SIGN LESS_THAN_SIGN LESS_THAN_SIGN 
    WHITESPACE? SOFTBREAK
    l = literalLines
    SOFTBREAK GREATER_THAN_SIGN GREATER_THAN_SIGN GREATER_THAN_SIGN 
    -> { delegate.createTree( LINES_OF_LITERAL, $l.unescaped ) } 
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
    -> { delegate.createTree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS, buffer.toString() ) } 
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
    -> { delegate.createTree( BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS, buffer.toString() ) } 
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
  : FULL_STOP FULL_STOP FULL_STOP
  ;

punctuationSign
  : s1 = COMMA -> ^( PUNCTUATION_SIGN { delegate.createTree( SIGN_COMMA, $s1.text ) } )
      
  | ( FULL_STOP FULL_STOP FULL_STOP ) => FULL_STOP FULL_STOP FULL_STOP 
      -> ^( PUNCTUATION_SIGN { delegate.createTree( SIGN_ELLIPSIS, "..." ) } )
              
  | s3 = FULL_STOP  -> ^( PUNCTUATION_SIGN { delegate.createTree( SIGN_FULLSTOP, $s3.text ) } )
  | s4 = QUESTION_MARK -> ^( PUNCTUATION_SIGN
      { delegate.createTree( SIGN_QUESTIONMARK, $s4.text ) } )
  | s5 = EXCLAMATION_MARK -> ^( PUNCTUATION_SIGN
      { delegate.createTree( SIGN_EXCLAMATIONMARK, $s5.text ) } ) 
  | s6 = SEMICOLON -> ^( PUNCTUATION_SIGN { delegate.createTree( SIGN_SEMICOLON, $s6.text ) } )
  | s7 = COLON -> ^( PUNCTUATION_SIGN { delegate.createTree( SIGN_COLON, $s7.text ) } ) 
  | s8 = APOSTROPHE -> { delegate.createTree( APOSTROPHE_WORDMATE, $s8.text ) }  
  ;
  
  
// ===================================
// Novella-related URL rules
// http://www.ietf.org/rfc/rfc1738.txt
// ===================================

url
  : ( http = httpUrl -> { delegate.createTree( URL_LITERAL, $http.text ) } )	
  | ( file = fileUrl -> { delegate.createTree( URL_LITERAL, $file.text ) } )	 
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
      LATIN_SMALL_LETTER_S?
      COLON 
      SOLIDUS 
      SOLIDUS 
    ) => 
        LATIN_SMALL_LETTER_H 
        LATIN_SMALL_LETTER_T 
        LATIN_SMALL_LETTER_T 
        LATIN_SMALL_LETTER_P 
        LATIN_SMALL_LETTER_S?
        COLON 
        SOLIDUS 
        SOLIDUS 
    urlHostPort 
    ( SOLIDUS httpUrlPath 
      ( QUESTION_MARK httpUrlSearch )? 
    )?
    ( NUMBER_SIGN httpUrlSearch )? // Not in the spec
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
    )*
  ;  
  
httpUrlSearch
  : (   urlUChar
      | SEMICOLON
      | COLON
      | COMMERCIAL_AT
      | AMPERSAND
      | EQUALS_SIGN
      | SOLIDUS
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


// =====
// Image
// =====

embeddableResource
  @init { final Location startLocation = delegate.createLocation( input.LT( 1 ) ) ; }
  : externalResourcePath 
    (    ( rasterImageExtension
          -> ^( RASTER_IMAGE { delegate.createTree(
                      RESOURCE_LOCATION,
                      startLocation,
                      ( String ) $externalResourcePath.text + $rasterImageExtension.text
                  ) }
              )
         )
       | ( vectorImageExtension
          -> ^( VECTOR_IMAGE { delegate.createTree(
                      RESOURCE_LOCATION,
                      startLocation,
                      ( String ) $externalResourcePath.text + $vectorImageExtension.text
                  ) }
              ) 
         )
    )
  ;

externalResourcePath
  : //( FULL_STOP FULL_STOP? )? SOLIDUS ( FULL_STOP FULL_STOP SOLIDUS )*
    ( FULL_STOP ( FULL_STOP ( SOLIDUS FULL_STOP FULL_STOP )* )? )? SOLIDUS
    externalResourceSegment ( SOLIDUS externalResourceSegment )*
  ;
  
externalResourceSegment
  : (   externalResourceCharacter
        ( FULL_STOP FULL_STOP? )?
    )*
    externalResourceCharacter               
  ;
  
externalResourceCharacter 
  : letter
  | digit
  | HYPHEN_MINUS
  | EQUALS_SIGN
  | PLUS_SIGN
  | PERCENT_SIGN
  | COMMA
  | COMMERCIAL_AT
  | LOW_LINE
  | DOLLAR_SIGN
  ;

rasterImageExtension
  : FULL_STOP ( 
        ( LATIN_SMALL_LETTER_P LATIN_SMALL_LETTER_N LATIN_SMALL_LETTER_G )
      | ( LATIN_SMALL_LETTER_J LATIN_SMALL_LETTER_P LATIN_SMALL_LETTER_G )
      | ( LATIN_SMALL_LETTER_G LATIN_SMALL_LETTER_I LATIN_SMALL_LETTER_F )
      
    )
  ;
  
vectorImageExtension
  : FULL_STOP ( 
        ( LATIN_SMALL_LETTER_S LATIN_SMALL_LETTER_V LATIN_SMALL_LETTER_G )      
    )
  ;
  

// ====
// Word
// ====


blockAfterTilde
  : ( 
      TILDE 
      b += subblockAfterTilde
	  )+ -> ^( BLOCK_AFTER_TILDE $b+ ) 
  ;
  
subblockAfterTilde
  : (  
	    (  s += word 
	      ( (   s += punctuationSign 
	          | s += delimitedMonoblockNoSeparator
	          | s += softInlineLiteral 
	          | s += hardInlineLiteral 
	      ) s += word? )*
		  ) 
	  | ( 
	      (   s += punctuationSign 
	        | s += delimitedMonoblockNoSeparator
	        | s += softInlineLiteral 
	        | s += hardInlineLiteral 
	      )
	      ( s += word? 
	        (   s += punctuationSign 
	          | s += delimitedMonoblockNoSeparator
	          | s += softInlineLiteral 
	          | s += hardInlineLiteral           
	        ) 
	      )*   
	      s += word?
	    ) 
    ) -> ^( SUBBLOCK $s+ ) 
  ;


word
  : ( w1 = rawWord ( CIRCUMFLEX_ACCENT w2 = rawWord ) )
    -> ^(   WORD_ { delegate.createTree( $w1.text ) } 
            ^( WORD_AFTER_CIRCUMFLEX_ACCENT { delegate.createTree( $w2.text ) } )
        )	
  | ( w = rawWord )
    -> { delegate.createTree( WORD_, $w.text ) } 
  ;  
  
symbolicName
  : ( hexLetter | nonHexLetter | digit )
    ( ( HYPHEN_MINUS | LOW_LINE )? ( hexLetter | nonHexLetter | digit ) )*
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
      | s3 =letterWithDiacritics { buffer.append( $s3.text ) ; }
      | s4 = digit { buffer.append( $s4.text ) ; }
      | s5 = escapedCharacter  { buffer.append( $s5.unescaped ) ; }
    )+
    ( s6 = HYPHEN_MINUS { buffer.append( $s6.text ) ; }
      (  s7 = hexLetter { buffer.append( $s7.text ) ; }
       | s8 = nonHexLetter { buffer.append( $s8.text ) ; }
       | s9 = letterWithDiacritics { buffer.append( $s9.text ) ; }
       | s10 = digit { buffer.append( $s10.text ) ; }
       | s11 = escapedCharacter  { buffer.append( $s11.unescaped ) ; } 
      )+ 
    )*
    { $text = buffer.toString() ; }
  ;  

escapedCharacter returns [ String unescaped ]
  : LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK 
    symbolicName 
    RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
    { $unescaped = delegate.unescapeCharacter( 
          $symbolicName.text, 
          $symbolicName.start.getLine(),
          $symbolicName.start.getCharPositionInLine() 
      ) ;
    }    
  ;


// =======================
// Composium-related rules
// =======================

composium
  : ( mediumbreak | largebreak )?
    functionCall
    ( largebreak functionCall )*      
    ( mediumbreak | largebreak )?
    EOF 
    -> ^( COMPOSIUM functionCall* )
  ;
  

functionCall
  : functionCallInsert 
  | functionCallMapstylesheet        
  ; 
  
functionCallInsert 
  : ( keywordInsert
      whitespace p += url 
      ( mediumbreak p += keywordRecurse )?
      ( mediumbreak p += keywordSort )?
      ( mediumbreak p += keywordCreateLevel | mediumbreak p += keywordNoHead )?
      ( mediumbreak p += parameterLevelAbove )?
      ( mediumbreak p += parameterInsertStyle ) ?        
      ( mediumbreak p += compositeIdentifier )*        
    )
    -> ^( COMMAND_INSERT_ $p+ )
  ;
  
keywordInsert
  : LATIN_SMALL_LETTER_I LATIN_SMALL_LETTER_N LATIN_SMALL_LETTER_S 
    LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_R LATIN_SMALL_LETTER_T  
  ;
  
keywordRecurse
  : ( LATIN_SMALL_LETTER_R LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_C 
      LATIN_SMALL_LETTER_U LATIN_SMALL_LETTER_R LATIN_SMALL_LETTER_S LATIN_SMALL_LETTER_E 
    )
    -> ^( COMMAND_INSERT_RECURSE_ )    
  ;
  
keywordSort
  : ( LATIN_SMALL_LETTER_S LATIN_SMALL_LETTER_O LATIN_SMALL_LETTER_R LATIN_SMALL_LETTER_T 
      EQUALS_SIGN s = sortOrder 
    )
    -> { delegate.createTree( COMMAND_INSERT_SORT_, $s.text ) }
           
  ;
  
sortOrder
  : ( letter | digit )+ ( PLUS_SIGN | HYPHEN_MINUS ) 
  ;
  
keywordCreateLevel
  : ( LATIN_SMALL_LETTER_C LATIN_SMALL_LETTER_R LATIN_SMALL_LETTER_E 
      LATIN_SMALL_LETTER_A LATIN_SMALL_LETTER_T LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_L 
      LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_V LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_L 
    )
    -> ^( COMMAND_INSERT_CREATELEVEL_ )
  ;
  
keywordNoHead
  : ( LATIN_SMALL_LETTER_N LATIN_SMALL_LETTER_O LATIN_SMALL_LETTER_H
      LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_A LATIN_SMALL_LETTER_D
    )
    -> ^( COMMAND_INSERT_NOHEAD_ )
  ;

parameterLevelAbove
  : ( LATIN_SMALL_LETTER_L LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_V LATIN_SMALL_LETTER_E 
      LATIN_SMALL_LETTER_L LATIN_SMALL_LETTER_A LATIN_SMALL_LETTER_B LATIN_SMALL_LETTER_O 
      LATIN_SMALL_LETTER_V LATIN_SMALL_LETTER_E EQUALS_SIGN s = digit+
    )
    -> { delegate.createTree( COMMAND_INSERT_LEVELABOVE_, $s.text ) }         
       
  ;
  
parameterInsertStyle
  : ( LATIN_SMALL_LETTER_S LATIN_SMALL_LETTER_T LATIN_SMALL_LETTER_Y 
      LATIN_SMALL_LETTER_L LATIN_SMALL_LETTER_E EQUALS_SIGN
      s = rawExtendedWord
    )
    -> { delegate.createTree( COMMAND_INSERT_STYLE_, $s.text ) } 
  ;

  
functionCallMapstylesheet  
  : ( keywordMapstylesheet ( mediumbreak assignmentArgument )+ )
    -> ^( COMMAND_MAPSTYLESHEET_ assignmentArgument+ )
  ;
  
keywordMapstylesheet
  : LATIN_SMALL_LETTER_M  LATIN_SMALL_LETTER_A LATIN_SMALL_LETTER_P
    LATIN_SMALL_LETTER_S LATIN_SMALL_LETTER_T LATIN_SMALL_LETTER_Y 
    LATIN_SMALL_LETTER_L LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_S
    LATIN_SMALL_LETTER_H LATIN_SMALL_LETTER_E LATIN_SMALL_LETTER_E
    LATIN_SMALL_LETTER_T 
  ;
  

assignmentArgument    
  : ( key = rawExtendedWord EQUALS_SIGN value = rawExtendedWord )
      -> ^( COMMAND_MAPSTYLESHEET_ASSIGNMENT_
              { delegate.createTree( $key.text ) } 
              { delegate.createTree( $value.text ) } 
          )     
  ;

extendedWord
  : w = rawExtendedWord 
    -> { delegate.createTree( EXTENDED_WORD_, $w.text ) } 	
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

softbreak : SOFTBREAK -> ^( LINE_BREAK_ ) ; 

whitespace : 
  WHITESPACE 
  -> { delegate.createTree( WHITESPACE_, $whitespace.text ) } 
;

levelIntroducerIndent 
  : EQUALS_SIGN EQUALS_SIGN+
    -> { delegate.createTree( LEVEL_INTRODUCER_INDENT_, $levelIntroducerIndent.text ) } 
  ;

mediumbreak
  : ( whitespace
      | ( whitespace? softbreak whitespace? )
    ) 
  ;
  
/** One blank line in the middle, white spaces everywhere.
 */
largebreak
  : ( ( whitespace? softbreak ) ( whitespace? softbreak )+ whitespace? )
  ;  

anySymbolExceptGreaterthansignAndGraveAccent
  :     digit
      | hexLetter
      | nonHexLetter 
      | letterWithDiacritics
      | AMPERSAND 
      | APOSTROPHE   
      | ASTERISK
      | CIRCUMFLEX_ACCENT
      | COLON
      | COPYRIGHT_SIGN
      | COMMA 
      | COMMERCIAL_AT
      | DEGREE_SIGN
      | DOLLAR_SIGN
      | DOUBLE_QUOTE
//      | ELLIPSIS
      | EURO_SIGN
      | EQUALS_SIGN
      | EXCLAMATION_MARK
      | FULL_STOP
//      | GRAVE_ACCENT
//      | GREATER_THAN_SIGN
      | HYPHEN_MINUS
      | LEFT_CURLY_BRACKET
      | LEFT_PARENTHESIS
      | LEFT_SINGLE_QUOTATION_MARK
      | LEFT_SQUARE_BRACKET
      | LESS_THAN_SIGN
      | LOW_LINE
      | MULTIPLICATION_SIGN
      | NUMBER_SIGN
      | POUND_SIGN
      | PLUS_SIGN
      | PERCENT_SIGN
      | QUESTION_MARK
      | REGISTERED_SIGN
      | REVERSE_SOLIDUS
      | RIGHT_CURLY_BRACKET
      | RIGHT_PARENTHESIS
      | RIGHT_SINGLE_QUOTATION_MARK
      | RIGHT_SQUARE_BRACKET
      | SECTION_SIGN
      | SEMICOLON
      | SOLIDUS
      | TILDE  
      | VERTICAL_LINE 
  ;

letters : letter+ ;

letter : hexLetter | nonHexLetter | letterWithDiacritics ;

asciiLetter : hexLetter | nonHexLetter ;

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
  ;

letterWithDiacritics
  : LATIN_SMALL_LETTER_A_WITH_GRAVE
  | LATIN_CAPITAL_LETTER_A_WITH_GRAVE

  | LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX

  | LATIN_SMALL_LETTER_A_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS
  
  | LATIN_SMALL_LETTER_A_WITH_ACUTE
  | LATIN_CAPITAL_LETTER_A_WITH_ACUTE

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
  
  | LATIN_SMALL_LETTER_I_WITH_ACUTE
  | LATIN_CAPITAL_LETTER_I_WITH_ACUTE

  | LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX
  
  | LATIN_SMALL_LETTER_O_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS
  
  | LATIN_SMALL_LETTER_O_WITH_ACUTE
  | LATIN_CAPITAL_LETTER_O_WITH_ACUTE
  
  | LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE
  | LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE
  
  | LATIN_SMALL_LETTER_U_WITH_GRAVE
  | LATIN_CAPITAL_LETTER_U_WITH_GRAVE

  | LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX
  | LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX
  
  | LATIN_SMALL_LETTER_U_WITH_DIAERESIS
  | LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS
  
  | LATIN_SMALL_LETTER_U_WITH_ACUTE
  | LATIN_CAPITAL_LETTER_U_WITH_ACUTE
  
  | LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE
  | LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE
    
  | LATIN_SMALL_LIGATURE_OE
  | LATIN_CAPITAL_LIGATURE_OE

  ;





// ======
// Tokens
// ======
//
// They become GeneratedLexemes.java with the magic of the LexemeGenerator.
// The commented HTML named entity is used, for convenient aliasing.


LATIN_SMALL_LETTER_A : 'a' ;                          //           "a"
LATIN_SMALL_LETTER_B : 'b' ;                          //           "b"
LATIN_SMALL_LETTER_C : 'c' ;                          //           "c"
LATIN_SMALL_LETTER_D : 'd' ;                          //           "d"
LATIN_SMALL_LETTER_E : 'e' ;                          //           "e"
LATIN_SMALL_LETTER_F : 'f' ;                          //           "f"
LATIN_SMALL_LETTER_G : 'g' ;                          //           "g"
LATIN_SMALL_LETTER_H : 'h' ;                          //           "h"
LATIN_SMALL_LETTER_I : 'i' ;                          //           "i"
LATIN_SMALL_LETTER_J : 'j' ;                          //           "j"
LATIN_SMALL_LETTER_K : 'k' ;                          //           "k"
LATIN_SMALL_LETTER_L : 'l' ;                          //           "l"
LATIN_SMALL_LETTER_M : 'm' ;                          //           "m"
LATIN_SMALL_LETTER_N : 'n' ;                          //           "n"
LATIN_SMALL_LETTER_O : 'o' ;                          //           "o"
LATIN_SMALL_LETTER_P : 'p' ;                          //           "p"
LATIN_SMALL_LETTER_Q : 'q' ;                          //           "q"
LATIN_SMALL_LETTER_R : 'r' ;                          //           "r"
LATIN_SMALL_LETTER_S : 's' ;                          //           "s"
LATIN_SMALL_LETTER_T : 't' ;                          //           "t"
LATIN_SMALL_LETTER_U : 'u' ;                          //           "u"
LATIN_SMALL_LETTER_V : 'v' ;                          //           "v"
LATIN_SMALL_LETTER_W : 'w' ;                          //           "w"
LATIN_SMALL_LETTER_X : 'x' ;                          //           "x"
LATIN_SMALL_LETTER_Y : 'y' ;                          //           "y"
LATIN_SMALL_LETTER_Z : 'z' ;                          //           "z"

LATIN_CAPITAL_LETTER_A : 'A' ;                        //           "A"
LATIN_CAPITAL_LETTER_B : 'B' ;                        //           "B"
LATIN_CAPITAL_LETTER_C : 'C' ;                        //           "C"
LATIN_CAPITAL_LETTER_D : 'D' ;                        //           "D"
LATIN_CAPITAL_LETTER_E : 'E' ;                        //           "E"
LATIN_CAPITAL_LETTER_F : 'F' ;                        //           "F"
LATIN_CAPITAL_LETTER_G : 'G' ;                        //           "G"
LATIN_CAPITAL_LETTER_H : 'H' ;                        //           "H"
LATIN_CAPITAL_LETTER_I : 'I' ;                        //           "I"
LATIN_CAPITAL_LETTER_J : 'J' ;                        //           "J"
LATIN_CAPITAL_LETTER_K : 'K' ;                        //           "K"
LATIN_CAPITAL_LETTER_L : 'L' ;                        //           "L"
LATIN_CAPITAL_LETTER_M : 'M' ;                        //           "M"
LATIN_CAPITAL_LETTER_N : 'N' ;                        //           "N"
LATIN_CAPITAL_LETTER_O : 'O' ;                        //           "O"
LATIN_CAPITAL_LETTER_P : 'P' ;                        //           "P"
LATIN_CAPITAL_LETTER_Q : 'Q' ;                        //           "Q"
LATIN_CAPITAL_LETTER_R : 'R' ;                        //           "R"
LATIN_CAPITAL_LETTER_S : 'S' ;                        //           "S"
LATIN_CAPITAL_LETTER_T : 'T' ;                        //           "T"
LATIN_CAPITAL_LETTER_U : 'U' ;                        //           "U"
LATIN_CAPITAL_LETTER_V : 'V' ;                        //           "V"
LATIN_CAPITAL_LETTER_W : 'W' ;                        //           "W"
LATIN_CAPITAL_LETTER_X : 'X' ;                        //           "X"
LATIN_CAPITAL_LETTER_Y : 'Y' ;                        //           "Y"
LATIN_CAPITAL_LETTER_Z : 'Z' ;                        //           "Z"

DIGIT_0 : '0' ;                                       //           "0"
DIGIT_1 : '1' ;                                       //           "1"
DIGIT_2 : '2' ;                                       //           "2"
DIGIT_3 : '3' ;                                       //           "3"
DIGIT_4 : '4' ;                                       //           "4"
DIGIT_5 : '5' ;                                       //           "5"
DIGIT_6 : '6' ;                                       //           "6"
DIGIT_7 : '7' ;                                       //           "7"
DIGIT_8 : '8' ;                                       //           "8"
DIGIT_9 : '9' ;                                       //           "9"

LATIN_SMALL_LETTER_A_WITH_GRAVE : '\u00e0' ;          // &agrave;  "a"
LATIN_CAPITAL_LETTER_A_WITH_GRAVE : '\u00c0' ;        // &Agrave;  "A"

LATIN_SMALL_LETTER_A_WITH_ACUTE : '\u00e1' ;          // &acute;   "a"
LATIN_CAPITAL_LETTER_A_WITH_ACUTE : '\u00c1' ;        // &Acute;   "A"

LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX : '\u00e2' ;     // &acirc;   "a"
LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX : '\u00c2' ;   // &Acirc;   "A"

LATIN_SMALL_LETTER_A_WITH_DIAERESIS : '\u00e4' ;      // &auml;    "a"
LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS : '\u00c4' ;    // &Auml;    "A"

LATIN_SMALL_LETTER_AE : '\u00e6' ;                    // &aelig;   "ae"
LATIN_CAPITAL_LETTER_AE : '\u00c6' ;                  // &AElig;   "AE"
  
LATIN_SMALL_LETTER_C_WITH_CEDILLA : '\u00e7' ;        // &ccedil;  "c"
LATIN_CAPITAL_LETTER_C_WITH_CEDILLA : '\u00c7' ;      // &Ccedil;  "C"

LATIN_SMALL_LETTER_E_WITH_GRAVE : '\u00e8' ;          // &egrave;  "e"
LATIN_CAPITAL_LETTER_E_WITH_GRAVE : '\u00c8' ;        // &Egrave;  "E"
  
LATIN_SMALL_LETTER_E_WITH_ACUTE : '\u00e9' ;          // &ecute;   "e"
LATIN_CAPITAL_LETTER_E_WITH_ACUTE : '\u00c9' ;        // &Ecute;   "E"

LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX : '\u00ea' ;     // &ecirc;   "e"
LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX : '\u00ca' ;   // &Ecirc;   "E"

LATIN_SMALL_LETTER_E_WITH_DIAERESIS : '\u00eb' ;      // &euml;    "e"
LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS : '\u00cb' ;    // &Euml;    "E"

LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX : '\u00ee' ;     // &icirc;   "i"
LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX : '\u00ce' ;   // &Icirc;   "I"
  
LATIN_SMALL_LETTER_I_WITH_DIAERESIS : '\u00ef' ;      // &iuml;    "i"
LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS : '\u00cf' ;    // &Iuml;    "I"

LATIN_SMALL_LETTER_I_WITH_ACUTE : '\u00ed' ;          //           "i"
LATIN_CAPITAL_LETTER_I_WITH_ACUTE : '\u00cd' ;        //           "I"

LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX : '\u00f4' ;     // &ocirc;   "o"
LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX : '\u00d4' ;   // &Ocirc;   "O"
  
LATIN_SMALL_LETTER_O_WITH_DIAERESIS : '\u00f6' ;      // &ouml;    "o"
LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS : '\u00d6' ;    // &Ouml;    "O"

LATIN_SMALL_LETTER_O_WITH_ACUTE : '\u00f3' ;          // &ocute;   "o"
LATIN_CAPITAL_LETTER_O_WITH_ACUTE : '\u00d3' ;        // &Ocute;   "O"
  
LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE : '\u0151' ;   //           "o"
LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE : '\u0150' ; //           "O"
  
LATIN_SMALL_LETTER_U_WITH_GRAVE : '\u00f9' ;          // &ugrave;  "u"
LATIN_CAPITAL_LETTER_U_WITH_GRAVE : '\u00d9' ;        // &Ugrave;  "U"

LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX : '\u00fb' ;     // &ucirc;   "u"
LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX : '\u00db' ;   // &Ucirc;   "U"
  
LATIN_SMALL_LETTER_U_WITH_DIAERESIS : '\u00fc' ;      // &uuml;    "u"
LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS : '\u00dc' ;    // &Uuml;    "U"
    
LATIN_SMALL_LETTER_U_WITH_ACUTE : '\u00fa' ;          // &ucute;   "u"
LATIN_CAPITAL_LETTER_U_WITH_ACUTE : '\u00da' ;        // &Ucute;   "U"
    
LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE : '\u0171' ;   //           "u"
LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE : '\u0170' ; //           "U"
    
LATIN_SMALL_LIGATURE_OE : '\u0153' ;                  // &oelig;   "oe"
LATIN_CAPITAL_LIGATURE_OE : '\u0152' ;                // &OElig;   "OE"



SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' )+ ;


AMPERSAND : '&' ; // &amp;
APOSTROPHE : '\'' ;
ASTERISK : '*' ;
CIRCUMFLEX_ACCENT : '^' ;
COLON : ':' ;
COPYRIGHT_SIGN : '\u00a9' ; // &copy;
COMMA : ',' ;
COMMERCIAL_AT : '@' ;
DEGREE_SIGN : '\u00b0' ; // &deg;
DOLLAR_SIGN : '$' ;
DOUBLE_QUOTE : '\"' ;
//ELLIPSIS : '...' ;
EURO_SIGN : '\u20ac' ; // &euro;
EQUALS_SIGN : '=' ;
EXCLAMATION_MARK : '!' ;
FULL_STOP : '.' ;
GRAVE_ACCENT : '`' ;
GREATER_THAN_SIGN : '>' ; // &gt;
HYPHEN_MINUS : '-' ;
LEFT_CURLY_BRACKET : '{' ;
LEFT_PARENTHESIS : '(' ;
LEFT_SQUARE_BRACKET : '[' ;
LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK : '\u00ab' ; // &laquo;
LESS_THAN_SIGN : '<' ;   // &lt;
LOW_LINE : '_' ;
MULTIPLICATION_SIGN : '\u00d7' ; // &times;
NUMBER_SIGN : '#' ;
PLUS_SIGN : '+' ;
PERCENT_SIGN : '%' ;
POUND_SIGN : '£' ;                                      // &pound;
QUESTION_MARK : '?' ;
REGISTERED_SIGN : '\u00ae' ;                            // &reg; 
REVERSE_SOLIDUS : '\\' ;
RIGHT_CURLY_BRACKET : '}' ;
RIGHT_PARENTHESIS : ')' ;
RIGHT_SQUARE_BRACKET : ']' ;
RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK : '\u00bb' ; // &raquo;
SECTION_SIGN : '\u00a7' ;                               // &sect;
SEMICOLON : ';' ;
SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK : '\u2039' ;  // &lsaquo;
LEFT_SINGLE_QUOTATION_MARK : '\u2019' ;                 // &rsquo;
RIGHT_SINGLE_QUOTATION_MARK : '\u2018' ;                // &lsquo;
SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK : '\u203a' ; // &rsaquo;
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
