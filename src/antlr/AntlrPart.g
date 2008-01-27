grammar AntlrPart ;


// Backtracking causes predicates to fail silently, 
// as it disables nesting depth limitation.
options { output = AST ; } //backtrack = true ; memoize = true ; } 

tokens {
  DOCUMENT ;
  SECTION  ;
  SECTION_TITLE ;
  SECTION_IDENTIFIER ;
  LOCUTOR ;
  PARAGRAPH_PLAIN ;
  PARAGRAPH_SPEECH ;
  PARAGRAPH_SPEECH_CONTINUED ;
  PARAGRAPH_SPEECH_ESCAPED ;
  BLOCKQUOTE ;
  QUOTE ;
  EMPHASIS ;
  PARENTHESIS ;
  INTERPOLATEDCLAUSE ;
  INTERPOLATEDCLAUSE_SILENTEND ;
  WORD ;
  WORDTRAIL ;
}
	
//scope WordScope { StringBuffer buffer } 

@parser::header { 
  package novelang.parser.antlr ;
} 

@lexer::header { 
  package novelang.parser.antlr ;
} 

@parser::members {
  final List< Exception > exceptions = 
      new ArrayList< Exception>() ;

	public List< Exception > getExceptions() {
	  return new ArrayList< Exception >( exceptions ) ;
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
      // Looks strange but helps supporting arbitrary 
      // volume of white garbage at the end of the file:
    ( ( SOFTBREAK | HARDBREAK ) WHITESPACE? )* 
    EOF 
    -> ^( DOCUMENT section* )
  ;
	
section 
  : SECTION_DELIMITER WHITESPACE? 
    ( ( sectionTitle | sectionIdentifier ) WHITESPACE? )?
    ( HARDBREAK WHITESPACE ? ( paragraph | blockQuote ) )+ 
    WHITESPACE?
    -> ^( SECTION 
           ^( SECTION_TITLE sectionTitle )?
           ^( SECTION_IDENTIFIER sectionIdentifier )?
           paragraph* blockQuote* 
       )
  ;

sectionIdentifier
  :	 APOSTROPHE textLine
    -> textLine
  ;
  
sectionTitle 
  :	textLine
  ;  
    
/** A single line of text with no break inside.
 */    
textLine
	:	word 
	  ( WHITESPACE ( word | wordTrail ) )*
	  -> word* wordTrail*
	;

paragraph
  : 
    ( SPEECH_OPENER ( WHITESPACE locutor )? WHITESPACE? paragraphBody )
    -> ^( PARAGRAPH_SPEECH locutor? paragraphBody )
  | ( SPEECH_ESCAPE WHITESPACE? paragraphBody )
    -> ^( PARAGRAPH_SPEECH_ESCAPED paragraphBody ) 
  | ( SPEECH_CONTINUATOR WHITESPACE? paragraphBody )
    -> ^( PARAGRAPH_SPEECH_CONTINUED paragraphBody )
  | paragraphBody
    -> ^( PARAGRAPH_PLAIN paragraphBody )
  ;

blockQuote
  : OPENING_BLOCKQUOTE 
    wideBreak? 
    paragraphBody ( HARDBREAK paragraphBody )* 
    HARDBREAK? 
    CLOSING_BLOCKQUOTE
    -> ^( BLOCKQUOTE ^( PARAGRAPH_PLAIN paragraphBody )* )
  ;  

locutor
 	: word ( WHITESPACE word )* WHITESPACE? LOCUTOR_DELIMITER
 	  -> ^( LOCUTOR word* )
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
    -> ^( QUOTE quotingTextItem* wordTrail* )
  ;

quotingTextItem
  : word
  | parenthesizingText
  | emphasizingText
  | interpolatedClause
  ;   
  
parenthesizingText
  : LEFT_PARENTHESIS 
    { ++ parenthesisDepth < 3 }?
    wideBreak?
    parenthesizingTextItem
    ( ( wideBreak parenthesizingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak?
    RIGHT_PARENTHESIS
    { -- parenthesisDepth ; }
    -> ^( PARENTHESIS parenthesizingTextItem* wordTrail* )
  ;

parenthesizingTextItem
  : word
  | quotingText
  | emphasizingText
  | interpolatedClause
  ;   

emphasizingText
  : SOLIDUS 
    { ++ emphasisDepth < 2 }?
    wideBreak?
    emphasizingTextItem
    ( ( wideBreak emphasizingTextItem ) | ( wideBreak? wordTrail ) )*
    wideBreak? 
    SOLIDUS
    { -- emphasisDepth ; }
    -> ^( EMPHASIS emphasizingTextItem* wordTrail* )
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
    (   INTERPOLATED_CLAUSE_DELIMITER 
        -> ^( INTERPOLATEDCLAUSE interpolatedClauseItem* wordTrail* )
      | INTERPOLATED_CLAUSE_SILENT_END 
        -> ^( INTERPOLATEDCLAUSE_SILENTEND interpolatedClauseItem* wordTrail* )
    )
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
    -> ^( WORDTRAIL PUNCTUATION_SIGN )
  ;

wideBreak 
	:	( WHITESPACE | ( WHITESPACE? SOFTBREAK WHITESPACE? ) ) ->
  ;
  
word : SYMBOL -> ^( WORD SYMBOL ) ;  

/** Use a token (uppercase name) to aggregate other tokens.
 */
SYMBOL 
  : ( LETTER | DIGIT )+
    ( ( APOSTROPHE | HYPHEN_MINUS ) ( LETTER | DIGIT )+ )*
  ; 

PUNCTUATION_SIGN 
  : COMMA 
  | FULL_STOP 
  | ELLIPSIS 
  | QUESTION_MARK 
  | EXCLAMATION_MARK 
  | SEMICOLON 
  ;

HARDBREAK : SOFTBREAK SOFTBREAK+ ; 
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;


fragment COMMA : ',' ;
fragment FULL_STOP : '.' ;
fragment ELLIPSIS : '...' ;
fragment QUESTION_MARK : '?' ;
fragment EXCLAMATION_MARK : '!' ;
fragment SEMICOLON : ';' ;

fragment LETTER 
  : 'a'..'z' 
  | 'A'..'Z' 
  | '&oelig;' 
  | '&OElig' 
  | '&aelig;' 
  | '&AElig' 
  ;

fragment DIGIT : '0'..'9';
  

LEFT_PARENTHESIS : '(' ;
RIGHT_PARENTHESIS : ')' ;

OPENING_BLOCKQUOTE : '<<<' ;
CLOSING_BLOCKQUOTE : '>>>' ;

INTERPOLATED_CLAUSE_DELIMITER : '--' ;
INTERPOLATED_CLAUSE_SILENT_END : '-_' ;

SOLIDUS : '/' ;

HYPHEN_MINUS : '-' ;

APOSTROPHE : '\'' ;
DOUBLE_QUOTE : '\"' ;
GRAVE_ACCENT : '`' ;


SPEECH_OPENER : '---' ;
SPEECH_CONTINUATOR : '--+' ;
SPEECH_ESCAPE : '--|' ;

LOCUTOR_DELIMITER : '::' ;

CHAPTER_DELIMITER : '***' ;
SECTION_DELIMITER : '===' ;


// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

BLOCK_COMMENT
  : '/*' ( options { greedy = false ; } : . )* '*/' { $channel = HIDDEN ; }
  ;

LINE_COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN ; }
  ;
