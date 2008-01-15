grammar novelang2;


document : 
  section ( ( SOFTBREAK | HARDBREAK ) section )* 
  ( SOFTBREAK | HARDBREAK )? EOF 
;
	
section : 
  SECTION_DELIMITER ( WHITESPACE titleline )? 
  ( SOFTBREAK | HARDBREAK ) paragraphsequence 
;

titleline : 
  word ( WHITESPACE word )* WHITESPACE? ;
  
speech : 
  SPEECH_OPENER WHITESPACE text ;
	
speechescape :
  SPEECH_ESCAPE WHITESPACE text ;
 
speechcontinued : 
  SPEECH_CONTINUATOR WHITESPACE text ;
  
speechsequence :
  speech 
  ( ( SOFTBREAK | HARDBREAK ) ( speech | speechescape | speechcontinued ) )* 
;
	
text : 
  word 
  ( ( ( WHITESPACE SOFTBREAK? ) | SOFTBREAK ) ( word ) )* 
  WHITESPACE? 
; 
	
paragraphsequence :   
  ( textonlysequence ) |
  ( textonlysequence HARDBREAK speechsequence ) |
  ( textonlysequence ( HARDBREAK speechsequence HARDBREAK textonlysequence )+ ) |
  
  ( speechsequence ) |
  ( speechsequence HARDBREAK textonlysequence ) |
  ( speechsequence ( HARDBREAK textonlysequence HARDBREAK speechsequence )+ ) 
;

textonlysequence	:	
  text ( HARDBREAK text )*
;

word : 
  CHARACTER+ ; 

CHARACTER : 
  ( 'a'..'z' | 'A'..'Z' | '0'..'9' /*| '&oelig;' | '&OElig' */) 
;

/*
PUNCTUATION_SIGN : ( 
      COMMA | 
      PERIOD | 
      ELLIPSIS | 
      QUESTION_MARK | 
      EXCLAMATION_MARK | 
      SEMICOLON | 
      DASH | 
      CHARACTER 
) ;


COMMA : ',' ;
PERIOD : '.' ;
ELLIPSIS : '...' ;
QUESTION_MARK : '?' ;
EXCLAMATION_MARK : '!' ;
SEMICOLON : ';' ;
DASH : '-' ;
*/


HARDBREAK :	SOFTBREAK SOFTBREAK+ ; 
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;

SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '+++' ;
SPEECH_ESCAPE : '(((' ;

CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER :	 '===' ;

