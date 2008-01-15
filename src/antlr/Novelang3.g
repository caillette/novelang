grammar Novelang3;

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
    ( ( SOFTBREAK | HARDBREAK ) section )* 
    SOFTBREAK*
    EOF 
  ;
	
section 
  : SECTION_DELIMITER textline? 
    ( SOFTBREAK | HARDBREAK ) paragraphs 
  ;
  
speech 
  : SPEECH_OPENER WHITESPACE textblock ;
	
speechescape 
  : SPEECH_ESCAPE WHITESPACE textblock ;
 
speechcontinued 
  : SPEECH_CONTINUATOR WHITESPACE textblock ;
  
speeches 
  : speech 
    ( ( SOFTBREAK | HARDBREAK )
      ( speech | speechescape | speechcontinued )
    )*
  ;
	
textblock 
  : textline ( SOFTBREAK textline )* ;

textline
	:	WHITESPACE? word 
	  ( wordtrail word )*
    wordtrail?
	;
	
/** Could be used to enforce there is a punctuation sign 
    at the end of each sentence. 
 */
wordtrail
	:	WHITESPACE 
	| ( WHITESPACE? PUNCTUATION_SIGN WHITESPACE? )
	;

	
paragraphs 
  : ( textblocks )
  | ( textblocks HARDBREAK speeches ) 
  | ( textblocks ( HARDBREAK speeches HARDBREAK textblocks )+ )   
  | ( speeches ) 
  | ( speeches HARDBREAK textblocks ) 
  | ( speeches ( HARDBREAK textblocks HARDBREAK speeches )+ ) 
  ;

textblocks	
  :	textblock ( HARDBREAK textblock )* ;

word 
  : CHARACTER+
  ( DASH CHARACTER+ )*
  ; 

CHARACTER 
  : 'a'..'z' 
  | 'A'..'Z' 
  | '0'..'9' 
  | '&oelig;' 
  | '&OElig' 
  ;

PUNCTUATION_SIGN 
  : COMMA 
  | PERIOD 
  | ELLIPSIS 
  | QUESTION_MARK 
  | EXCLAMATION_MARK 
  | SEMICOLON 
  ;


fragment COMMA : ',' ;
fragment PERIOD : '.' ;
fragment ELLIPSIS : '...' ;
fragment QUESTION_MARK : '?' ;
fragment EXCLAMATION_MARK : '!' ;
fragment SEMICOLON : ';' ;

fragment DASH : '-' ;


HARDBREAK : SOFTBREAK SOFTBREAK+ ; 
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;

SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '+++' ;
SPEECH_ESCAPE : '(((' ;

CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER : '===' ;


