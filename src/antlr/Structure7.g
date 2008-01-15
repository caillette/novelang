grammar Structure7;

// Backtracking causes predicates to fail silently, 
// thus disabling nesting depth limitation.
//options { backtrack = true ; memoize = true ; } 


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
	

// ======================
// Real stuff begins here
// ======================

structure
  : SOFTBREAK*
    inclusion
    ( SOFTBREAK* inclusion )*
    SOFTBREAK*
    EOF
  ;

inclusion
  : '%include' WHITESPACE
    (   identifier
      | ( fileName WHITESPACE title WHITESPACE paragraphReferences )
    )
    WHITESPACE?
  ;

/** Not the same as in Content grammar which has no end quote.
  */
identifier
  : BACKQUOTE word ( WHITESPACE word )* BACKQUOTE
  ;

fileName
  :	( LETTER | DIGIT | PERIOD | DASH )+
  ;

title
  : DOUBLE_QUOTE 
    WHITESPACE? 
    word 
    ( WHITESPACE word )*
    WHITESPACE? 
    DOUBLE_QUOTE
  ;

paragraphReferences
  : ( number | paragraphInterval ) 
    ( WHITESPACE ( number | paragraphInterval ) )+
  ;
  
paragraphInterval
  :	number '..' number
  ;
  

// ====================
// Real stuff ends here
// ====================


/** Is PUNCTUATION_SIGN enough?
  */
wordTrail
	:	PUNCTUATION_SIGN
	;

number
  : DIGIT+
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
