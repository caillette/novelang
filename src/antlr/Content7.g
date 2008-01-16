grammar Content7 ;

// Backtracking causes predicates to fail silently, 
// thus disabling nesting depth limitation.
//options { output = AST ; } //backtrack = true ; memoize = true ; } 

tokens {
  DOCUMENT ;
  SECTION  ;
  TITLE ;
}
	

@parser::header { 
  package novelang.parser ;
} 

@lexer::header { 
  package novelang.parser ;
} 
	
@parser::members {
  final List< RecognitionException > exceptions = 
      new ArrayList<RecognitionException>() ;

	public List< RecognitionException > getExceptions() {
	  return new ArrayList< RecognitionException >( exceptions ) ;
	}
	
	@Override
	public void reportError( RecognitionException e ) {
	  super.reportError( e ) ;
	  exceptions.add( e ) ;
	}
	
	private int quoteDepth = 0 ;
	private int parenthesisDepth = 0 ;
	private int emphasisDepth = 0 ;
	private int interpolatedClauseDepth = 0 ;
}

document 
  : section
    ( HARDBREAK section )* 
    SOFTBREAK*
    EOF 
//    -> ^( DOCUMENT section* )
  ;
	
section 
  : SECTION_DELIMITER 
    textLine?     
    ( HARDBREAK ( paragraph | blockQuote ) )+
//    -> ^( SECTION paragraph* blockQuote* )
  ;
    
/** A single line of text with no break inside.
 */    
textLine
	:	WHITESPACE? word 
	  ( WHITESPACE ( word | wordTrail ) )*
	  WHITESPACE?
	;
	
paragraph
  : (   speechOpening 
      | SPEECH_ESCAPE 
      | SPEECH_CONTINUATOR
    )?
    WHITESPACE?
    paragraphBody
  ;
  
blockQuote
  : OPENING_BLOCKQUOTE 
    wideBreak? 
    paragraphBody ( HARDBREAK paragraphBody )* 
    HARDBREAK? 
    CLOSING_BLOCKQUOTE
  ;  

speechOpening
	:	SPEECH_OPENER ( WHITESPACE locutor )?
	;
  
locutor
 	: word ( WHITESPACE word )* WHITESPACE? LOCUTOR_DELIMITER
 	;
 	  
paragraphBody 
  : paragraphItem 
    ( ( wideBreak paragraphItem ) | ( wideBreak? wordTrail ) )*
  ;

paragraphItem
  : word
  | parenthesizingText
  | quotingText
  | emphasizingText
  | interpolatedClause
  ;   
  
quotingText
  : DOUBLE_QUOTE 
    { ++ quoteDepth < 2 }?
    wideBreak?
    quotingTextItem
    ( ( wideBreak quotingTextItem ) | ( wideBreak? wordTrail ) )*    
    wideBreak? 
    DOUBLE_QUOTE
    { -- quoteDepth ; }
  ;

quotingTextItem
  : word
  | parenthesizingText
  | emphasizingText
  | interpolatedClause
  ;   
  
parenthesizingText
  : OPENING_PARENTHESIS 
    { ++ parenthesisDepth < 3 }?
    wideBreak?
    parenthesizingTextItem
    ( ( wideBreak parenthesizingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak?
    CLOSING_PARENTHESIS
    { -- parenthesisDepth ; }
  ;

parenthesizingTextItem
  : word
  | quotingText
  | emphasizingText
  | interpolatedClause
  ;   

emphasizingText
  : EMPHASIS_DELIMITER 
    { ++ emphasisDepth < 2 }?
    wideBreak?
    emphasizingTextItem
    ( ( wideBreak emphasizingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? 
    EMPHASIS_DELIMITER
    { -- emphasisDepth ; }
  ;
  
emphasizingTextItem  
  : word
  | quotingText
  | parenthesizingText
  | interpolatedClause
  ;   

interpolatedClause
  :	INTERPOLATED_CLAUSE_DELIMITER 
    { ++ interpolatedClauseDepth < 2 }?
    wideBreak?
    interpolatedClauseItem
    ( ( wideBreak interpolatedClauseItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? 
    ( INTERPOLATED_CLAUSE_DELIMITER | INTERPOLATED_CLAUSE_SILENT_END )
    { -- interpolatedClauseDepth ; }
  ;	  
  
interpolatedClauseItem  
  : word
  | quotingText
  | parenthesizingText
  | emphasizingText
  ;   


/** Is PUNCTUATION_SIGN enough?
  */
wordTrail
	:	PUNCTUATION_SIGN
	;

	
word 
  : ( LETTER | DIGIT )+
    ( ( SINGLE_QUOTE | DASH ) ( LETTER | DIGIT )+ )*
  ; 

wideBreak 
	:	( WHITESPACE | ( WHITESPACE? SOFTBREAK WHITESPACE? ) )
  ;

PUNCTUATION_SIGN 
  : COMMA 
  | PERIOD 
  | ELLIPSIS 
  | QUESTION_MARK 
  | EXCLAMATION_MARK 
  | SEMICOLON 
  ;

HARDBREAK : SOFTBREAK SOFTBREAK+ ; 
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;


fragment COMMA : ',' ;
fragment PERIOD : '.' ;
fragment ELLIPSIS : '...' ;
fragment QUESTION_MARK : '?' ;
fragment EXCLAMATION_MARK : '!' ;
fragment SEMICOLON : ';' ;

OPENING_PARENTHESIS : '(' ;
CLOSING_PARENTHESIS : ')' ;

OPENING_BLOCKQUOTE : '<<<' ;
CLOSING_BLOCKQUOTE : '>>>' ;

INTERPOLATED_CLAUSE_DELIMITER : '--' ;
INTERPOLATED_CLAUSE_SILENT_END : '-_' ;

EMPHASIS_DELIMITER : '/' ;

DASH : '-' ;

SINGLE_QUOTE : '\'' ;
DOUBLE_QUOTE : '\"' ;
BACKQUOTE : '`' ;


SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '--+' ;
SPEECH_ESCAPE : '--|' ;

LOCUTOR_DELIMITER : '::' ;

CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER : '===' ;


LETTER 
  : 'a'..'z' 
  | 'A'..'Z' 
  | '&oelig;' 
  | '&OElig' 
  | '&aelig;' 
  | '&AElig' 
  ;

DIGIT : '0'..'9';
  

// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

BLOCK_COMMENT
  : '/*' ( options { greedy = false ; } : . )* '*/' { $channel = HIDDEN ; }
  ;

LINE_COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
