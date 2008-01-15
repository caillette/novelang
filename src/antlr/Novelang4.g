grammar Novelang4;

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
	

/** 
    Just text and punctuation signs with possibly SOFTBREAKS in the middle. 
    No wordtrail expected at the start of a line.
  */  
rawtext  
  : word
    ( ( widebreak word ) | wordtrail )* 
  ;

widebreak
  :	( WHITESPACE | ( WHITESPACE? SOFTBREAK WHITESPACE? ) )
  ;

  
inlinequote
  : DOUBLE_QUOTE WHITESPACE? rawtext WHITESPACE? DOUBLE_QUOTE
  ;

/**
    A single line of text with no break inside.
 */    
textline
	:	WHITESPACE? word 
	  ( wordtrail | ( WHITESPACE word ) )*
	  WHITESPACE?
	;
	
/** Could be used to enforce there is a punctuation sign 
    at the end of each sentence. 
 */
wordtrail
	:	WHITESPACE? PUNCTUATION_SIGN
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

textblock 
  : rawtext ( /*widebreak?*/ inlinequotes rawtext )*
  | ( rawtext /*widebreak?*/ inlinequotes )+
  | inlinequotes ( rawtext /*widebreak?*/ inlinequotes )*
  | ( inlinequotes /*widebreak?*/ rawtext )+
  ;
  
inlinequotes
  :	 inlinequote wordtrail? ( /*widebreak?*/ inlinequote wordtrail? )*
  ;
  
/*  
  :	( rawtext ( widebreak? inlinequote wordtrail? widebreak? rawtext )* )
  
  | ( rawtexttheninlinequote
      ( widebreak? rawtexttheninlinequote )* 
    )
    
  | ( inlinequote wordtrail? 
      ( widebreak? rawtext widebreak? inlinequote wordtrail? )*
    )                   //   ^ This widebreak causes trouble.
    
  | ( inlinequote wordtrail? widebreak? rawtext 
      ( widebreak? inlinequote wordtrail? widebreak? rawtext )*
    )  // ^ This one, too.
  ;

rawtexttheninlinequote
	:	rawtext widebreak? inlinequote wordtrail?
	;
*/    

word 
  : CHARACTER+
  ( ( SINGLE_QUOTE | DASH ) CHARACTER+ )*
  ; 

CHARACTER 
  : 'a'..'z' 
  | 'A'..'Z' 
  | '0'..'9' 
  | '&oelig;' 
  | '&OElig' 
  | '&aelig;' 
  | '&AElig' 
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

DASH : '-' ;

SINGLE_QUOTE : '\'' ;
DOUBLE_QUOTE : '"' ;


HARDBREAK : SOFTBREAK SOFTBREAK+ ; 
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ /*{ $channel = HIDDEN ; }*/ ;

SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '--+' ;
SPEECH_ESCAPE : '--|' ;

CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER : '===' ;


// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

BLOCK_COMMENT
  : '/*' ( options { greedy = false ; } : . )* '*/' { $channel = HIDDEN ; }
  ;

LINE_COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
