grammar AntlrPart ;

options { output = AST ; } 

tokens {
  PART ;
  CHAPTER ;
  SECTION  ;
  TITLE ;
  IDENTIFIER ;
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
  PUNCTUATION_SIGN ;
  SIGN_COMMA ;
  SIGN_FULLSTOP ;
  SIGN_ELLIPSIS ;
  SIGN_QUESTIONMARK ;
  SIGN_EXCLAMATIONMARK ;
  SIGN_SEMICOLON ;
  SIGN_COLON ;
}
	
//scope WordScope { StringBuffer buffer } 

@parser::header { 
package novelang.parser.antlr ;
import novelang.parser.antlr.AntlrGrammarDelegate;
} 

@lexer::header { 
package novelang.parser.antlr ;
} 

@lexer::members {
  // TODO delete this once sure we don't need to hack inside the lexer.
}

@parser::members {

private AntlrGrammarDelegate delegate ;

public void setGrammarDelegate( AntlrGrammarDelegate delegate ) {
  this.delegate = delegate ;
}
	
@Override
public void emitErrorMessage( String string ) {
  if( null == delegate ) {
    super.emitErrorMessage( string ) ;
  } else {
    delegate.report( string ) ;
  }
}
	
private int quoteDepth = 0 ;
private int parenthesisDepth = 0 ;
private int emphasisDepth = 0 ;
private int interpolatedClauseDepth = 0 ;
}

part 
  : ( mediumBreak | largeBreak )?
    (   ( section ( largeBreak section )* ) 
      | ( chapter ( largeBreak chapter )* )
    )
    ( mediumBreak | largeBreak )?
    EOF 
    -> ^( PART  section* chapter* )
  ;
  
chapter 
  : CHAPTER_DELIMITER 
    ( smallBreak? ( title | identifier ) )?
    ( largeBreak section )+ 
    -> ^( CHAPTER
           title?
           identifier?
           section*
        )
  ;	  
	
section 
  : SECTION_DELIMITER 
    ( smallBreak? ( title | identifier ) )?
    ( largeBreak ( paragraph | blockQuote ) )+ 
    -> ^( SECTION 
           title?
           identifier?
           paragraph* blockQuote* 
        )
  ;

title
  :	 APOSTROPHE paragraphBody
	  -> ^( TITLE paragraphBody )
  ;


 identifier
  :	 word 
	  ( smallBreak ( word | punctuationSign ) )*
	  -> ^( IDENTIFIER word* punctuationSign* )
  ;
    
paragraph
  : 
    ( SPEECH_OPENER ( smallBreak locutor )? smallBreak? paragraphBody )
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
    mediumBreak?
    paragraphBody ( largeBreak paragraphBody )* 
    mediumBreak?
    CLOSING_BLOCKQUOTE
    -> ^( BLOCKQUOTE ^( PARAGRAPH_PLAIN paragraphBody )* )
  ;  

locutor
 	: word ( smallBreak word )* smallBreak? LOCUTOR_DELIMITER
 	  -> ^( LOCUTOR word* )
 	;
 	  
paragraphBody 
  : (   ( word ( mediumBreak word )* )
      | parenthesizingText
      | quotingText
      | emphasizingText
      | interpolatedClause
    )
    ( mediumBreak?
      (   parenthesizingText
        | quotingText
        | emphasizingText
        | interpolatedClause    
        | punctuationSign
      )
      ( mediumBreak 
        word ( smallBreak word )* )?
    )*
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
    mediumBreak?
    quotingTextItem
    ( ( mediumBreak quotingTextItem ) | ( smallBreak? punctuationSign ) )*    
    mediumBreak? 
    DOUBLE_QUOTE
    { -- quoteDepth ; }
    -> ^( QUOTE quotingTextItem* punctuationSign* )
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
    mediumBreak?
    parenthesizingTextItem
    ( ( mediumBreak parenthesizingTextItem ) | ( smallBreak? punctuationSign ) )*
    mediumBreak?
    RIGHT_PARENTHESIS
    { -- parenthesisDepth ; }
    -> ^( PARENTHESIS parenthesizingTextItem* punctuationSign* )
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
    mediumBreak?
    emphasizingTextItem
    ( ( mediumBreak emphasizingTextItem ) | ( smallBreak? punctuationSign ) )*
    mediumBreak? 
    SOLIDUS
    { -- emphasisDepth ; }
    -> ^( EMPHASIS emphasizingTextItem* punctuationSign* )
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
    smallBreak?
    interpolatedClauseItem
    ( ( mediumBreak interpolatedClauseItem ) | ( smallBreak? punctuationSign ) )*
    smallBreak? 
    (   INTERPOLATED_CLAUSE_DELIMITER 
        -> ^( INTERPOLATEDCLAUSE interpolatedClauseItem* punctuationSign* )
      | INTERPOLATED_CLAUSE_SILENT_END 
        -> ^( INTERPOLATEDCLAUSE_SILENTEND interpolatedClauseItem* punctuationSign* )
    )
    { -- interpolatedClauseDepth ; }    
  ;	  
  
interpolatedClauseItem  
  : word
  | quotingText
  | parenthesizingText
  | emphasizingText
  ;   

/** Use between words, when everything is kept on the same line.
 */
smallBreak
  : WHITESPACE
    ->
  ;

/** Use inside a paragraph, when lines are together with no blank line in the middle.
 */
mediumBreak
  : ( WHITESPACE
      | ( WHITESPACE? SOFTBREAK WHITESPACE? )
    ) // Parenthesis seems useful to make the rewrite rule apply for the whole.
    ->
  ;

/** One blank line in the middle, white spaces everywhere.
 */
largeBreak
  : ( ( WHITESPACE? SOFTBREAK ) ( WHITESPACE? SOFTBREAK )+ WHITESPACE? )
    ->
  ;

  
word : SYMBOL -> ^( WORD SYMBOL ) ;  

/** Use a token (uppercase name) to aggregate other tokens.
 */
SYMBOL 
  : ( LETTER | DIGIT )+
    ( ( HYPHEN_MINUS | APOSTROPHE ) ( LETTER | DIGIT )+ )*
    APOSTROPHE?
  ; 

punctuationSign
  : COMMA -> ^( PUNCTUATION_SIGN SIGN_COMMA )
  | FULL_STOP  -> ^( PUNCTUATION_SIGN SIGN_FULLSTOP )
  | ELLIPSIS -> ^( PUNCTUATION_SIGN SIGN_ELLIPSIS )
  | QUESTION_MARK -> ^( PUNCTUATION_SIGN SIGN_QUESTIONMARK )
  | EXCLAMATION_MARK -> ^( PUNCTUATION_SIGN SIGN_EXCLAMATIONMARK )
  | SEMICOLON -> ^( PUNCTUATION_SIGN SIGN_SEMICOLON )
  | COLON -> ^( PUNCTUATION_SIGN SIGN_COLON )
  ;

  
SOFTBREAK : ( '\r' '\n' ? ) | '\n' ; 
WHITESPACE : ( ' ' | '\t' )+ ;


COMMA : ',' ;
FULL_STOP : '.' ;
ELLIPSIS : '...' ;
QUESTION_MARK : '?' ;
EXCLAMATION_MARK : '!' ;
SEMICOLON : ';' ;
COLON : ':' ;

fragment LETTER 
  : 'a'..'z' 
  | 'A'..'Z' 

  // http://www.fileformat.info/info/unicode

  | '\u00e0' // LATIN SMALL LETTER A WITH GRAVE  
  | '\u00c0' // LATIN CAPITAL LETTER A WITH GRAVE  

  | '\u00e2' // LATIN SMALL LETTER A WITH CIRCUMFLEX (&acirc;)
  | '\u00c2' // LATIN CAPITAL LETTER A WITH CIRCUMFLEX (&Acirc;)

  | '\u00e4' // LATIN SMALL LETTER A WITH DIAERESIS (&auml;)
  | '\u00c4' // LATIN CAPITAL LETTER A WITH DIAERESIS (&Auml;)

  | '\u00e6' // LATIN SMALL LETTER AE
  | '\u00c6' // LATIN CAPITAL LETTER AE
  
  | '\u00e7' // LATIN SMALL LETTER C WITH CEDILLA (&ccedil;)
  | '\u00c7' // LATIN CAPITAL LETTER C WITH CEDILLA (&Ccedil;)  

  | '\u00e8' // LATIN SMALL LETTER E WITH GRAVE (&egrave;)
  | '\u00c8' // LATIN CAPITAL LETTER E WITH GRAVE (&Egrave;)
  
  | '\u00e9' // LATIN SMALL LETTER E WITH ACUTE 
  | '\u00c9' // LATIN CAPITAL LETTER E WITH ACUTE

  | '\u00ea' // LATIN SMALL LETTER E WITH CIRCUMFLEX (&ecirc;)
  | '\u00ca' // LATIN CAPITAL LETTER E WITH CIRCUMFLEX (&Ecirc;)

  | '\u00eb' // LATIN SMALL LETTER E WITH DIAERESIS (&euml;)
  | '\u00cb' // LATIN CAPITAL LETTER E WITH DIAERESIS (&Euml;)  

  | '\u00ee' // LATIN SMALL LETTER I WITH CIRCUMFLEX (&icirc;)
  | '\u00ce' // LATIN SMALL LETTER I WITH CIRCUMFLEX (&icirc;)  
  
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
