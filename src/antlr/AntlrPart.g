grammar AntlrPart ;


// Backtracking causes predicates to fail silently, 
// as it disables nesting depth limitation.
options { output = AST ; } //backtrack = true ; memoize = true ; } 

tokens {
  PART ;
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

part 
  : section
    ( HARDBREAK section )* 
      // Looks strange but helps supporting arbitrary 
      // volume of white garbage at the end of the file:
    ( ( SOFTBREAK | HARDBREAK ) WHITESPACE? )* 
    EOF 
    -> ^( PART section* )
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

sectionTitle
  :	 APOSTROPHE w1 = word 
	  ( WHITESPACE ( w2 += word | w3 += wordTrail ) )*
	  -> word* wordTrail*
  ;
  
 sectionIdentifier
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

  | '\u00e0' // LATIN SMALL LETTER A WITH GRAVE  
  | '\u00c0' // LATIN CAPITAL LETTER A WITH GRAVE  

  | '\u00e2' // LATIN SMALL LETTER A WITH CIRCUMFLEX (&acirc;)
  | '\u00c2' // LATIN CAPITAL LETTER A WITH CIRCUMFLEX (&Acirc;)

  | '\u00e6' // LATIN SMALL LETTER AE
  | '\u00c6' // LATIN CAPITAL LETTER AE
  
  | '\u00e7' // LATIN SMALL LETTER C WITH CEDILLA (&ccedil;)
  | '\u00c7' // LATIN CAPITAL LETTER C WITH CEDILLA (&Ccedil;)  

  | '\u00e8' // LATIN SMALL LETTER E WITH GRAVE (&egrave;)
  | '\u00c8' // LATIN CAPITAL LETTER E WITH GRAVE (&Egrave;)
  
  | '\u00e9' // LATIN SMALL LETTER E WITH ACUTE 
  | '\u00c9' // LATIN CAPITAL LETTER E WITH ACUTE

  | '\u00eA' // LATIN SMALL LETTER E WITH CIRCUMFLEX (&ecirc;)
  | '\u00cA' // LATIN CAPITAL LETTER E WITH CIRCUMFLEX (&Ecirc;)

  | '\u00ee' // LATIN SMALL LETTER I WITH CIRCUMFLEX (&icirc;)
  | '\u00ce' // LATIN SMALL LETTER I WITH CIRCUMFLEX (&icirc;)  
  
  | '\u00eb' // LATIN SMALL LETTER E WITH DIAERESIS (&euml;)
  | '\u00cb' // LATIN CAPITAL LETTER E WITH DIAERESIS (&Euml;)  

  | '\u00ef' // LATIN SMALL LETTER I WITH DIAERESIS (&iuml;)
  | '\u00cf' // LATIN CAPITAL LETTER I WITH DIAERESIS (&Iuml;)
  
  | '\u00f4' // LATIN SMALL LETTER O WITH CIRCUMFLEX (&ocirc;)
  | '\u00d4' // LATIN CAPITAL LETTER O WITH CIRCUMFLEX (&Ocirc;)
  
  | '\u00f6' // LATIN SMALL LETTER O WITH DIAERESIS (&ouml;)
  | '\u00d6' // LATIN CAPITAL LETTER O WITH DIAERESIS (&Ouml;)
  
  | '\u00f9' // LATIN SMALL LETTER U WITH GRAVE (&ugrave;)
  | '\u00d9' // LATIN CAPITAL LETTER U WITH GRAVE (&Ugrave;)

  | '\u00fb' // LATIN SMALL LETTER U WITH CIRCUMFLEX (&ucirc;)
  | '\u00db' // LATIN CAPITAL LETTER U WITH CIRCUMFLEX (&Ucirc;)
  
  | '\u00fc' // LATIN SMALL LETTER U WITH DIAERESIS (&uuml;)
  | '\u00dc' // LATIN CAPITAL LETTER U WITH DIAERESIS (&Uuml;)
    
  | '\u0153' // LATIN SMALL LIGATURE OE
  | '\u0152' // LATIN CAPITAL LIGATURE OE

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
