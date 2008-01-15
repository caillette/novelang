grammar Content5;

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
}
	

document 
  : section 
    ( HARDBREAK section )* 
    SOFTBREAK*
    EOF 
  ;
	
section 
  : SECTION_DELIMITER textLine? 
    ( HARDBREAK paragraph )+
  ;
    
/** A single line of text with no break inside.
 */    
textLine
	:	WHITESPACE? word 
	  ( WHITESPACE ( word | wordTrail ) )*
	  WHITESPACE?
	;
	
paragraph
  : ( ( SPEECH_OPENER WHITESPACE locutor ) |
      SPEECH_ESCAPE |
      SPEECH_CONTINUATOR
    )?
    WHITESPACE?
    paragraphBody
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
  ;   
  
quotingText
  : DOUBLE_QUOTE wideBreak?
    quotingTextItem
    ( ( wideBreak quotingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? DOUBLE_QUOTE
  ;

quotingTextItem
  :	word 
  | parenthesizingTextNoQ 
  | emphasizingTextNoQ
  ;

parenthesizingText
  : OPENING_PARENTHESIS wideBreak?
    parenthesizingTextItem
    ( ( wideBreak parenthesizingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? CLOSING_PARENTHESIS
  ;

parenthesizingTextItem
	:	word 
	| quotingTextNoP 
	| emphasizingTextNoP
	;

emphasizingText
  : EMPHASIS_DELIMITER wideBreak?
    emphasizingTextItem
    ( ( wideBreak emphasizingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? EMPHASIS_DELIMITER
  ;

emphasizingTextItem
	:	word 
	| quotingTextNoE 
	| parenthesizingTextNoE
	;

quotingTextNoP
  : DOUBLE_QUOTE wideBreak?
    quotingTextNoPItem
    ( ( wideBreak quotingTextNoPItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? DOUBLE_QUOTE
  ;

quotingTextNoPItem
	:	word
	| emphasizingTextNoQ
	;
  
quotingTextNoE
  : DOUBLE_QUOTE wideBreak?
    quotingTextNoEItem
    ( ( wideBreak quotingTextNoEItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? DOUBLE_QUOTE
  ;

quotingTextNoEItem
	:	word 
	| parenthesizingTextNoQ
	;

emphasizingTextNoP
  : EMPHASIS_DELIMITER wideBreak?
    emphasingTextNoQItem 
    ( ( wideBreak emphasingTextNoQItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? EMPHASIS_DELIMITER
  ;

emphasingTextNoQItem
	:	word 
	| quotingTextNoE
	;

emphasizingTextNoQ
  : EMPHASIS_DELIMITER wideBreak?
    emphasizingTextNoQItem
    ( ( wideBreak emphasizingTextNoQItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? EMPHASIS_DELIMITER
  ;

emphasizingTextNoQItem
	:	word | parenthesizingTextNoE
	;
  
parenthesizingTextNoQ
  : OPENING_PARENTHESIS wideBreak?
    parenthesizingTextNoQItem
    ( ( wideBreak parenthesizingTextNoQItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? CLOSING_PARENTHESIS
  ;

parenthesizingTextNoQItem
	:	word 
	| emphasizingTextNoQ
	;

parenthesizingTextNoE
  : OPENING_PARENTHESIS wideBreak?
    parenthesizingTextNoEItem
    ( ( wideBreak parenthesizingTextNoEItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? CLOSING_PARENTHESIS
  ;

parenthesizingTextNoEItem
	:	word
	| quotingTextNoE
	;

	  


/** Is PUNCTUATION_SIGN always enough?
  */
wordTrail
	:	PUNCTUATION_SIGN
	;

	
word 
  : CHARACTER+
  ( ( SINGLE_QUOTE | DASH ) CHARACTER+ )*
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

EMPHASIS_DELIMITER : '/' ;

DASH : '-' ;

SINGLE_QUOTE : '\'' ;
DOUBLE_QUOTE : '\"' ;

SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '--+' ;
SPEECH_ESCAPE : '--|' ;

LOCUTOR_DELIMITER : '::' ;

CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER : '===' ;


CHARACTER 
  : 'a'..'z' 
  | 'A'..'Z' 
  | '0'..'9' 
  | '&oelig;' 
  | '&OElig' 
  | '&aelig;' 
  | '&AElig' 
  ;


// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

BLOCK_COMMENT
  : '/*' ( options { greedy = false ; } : . )* '*/' { $channel = HIDDEN ; }
  ;

LINE_COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
