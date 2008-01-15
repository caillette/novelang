grammar novelang1;


document 
  : section ( ( SOFTBREAK | HARDBREAK ) section )* 
        ( SOFTBREAK | HARDBREAK )? EOF ;
	
section
  : SECTION_DELIMITER ( WHITESPACE titleline )? 
	      ( SOFTBREAK | HARDBREAK ) paragraphsequence? ; // Louise found '?' weird.

titleline 
  : word ( WHITESPACE word )* WHITESPACE? ;
  
paragraph 
	: word ( ( ( WHITESPACE SOFTBREAK? ) | SOFTBREAK ) word )* WHITESPACE? ; 
	
paragraphsequence 
	: ( paragraph ) ( HARDBREAK paragraph )* ;
	
word: CHARACTER+ ; 

CHARACTER : ( 'a'..'z' | 'A'..'Z' | '0'..'9' )+ ;
HARDBREAK :	SOFTBREAK SOFTBREAK+ ; 
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;
MDASH : '---' ;
LOCUTION_CONTINUATOR : '+--' ;
CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER :	 '===' ;

