grammar AntlrStructure;

options { output = AST ; } 

tokens {
  STRUCTURE ;
  PART ;
  CHAPTER ;
  SECTION ;
  STYLE ;
  TITLE ;
  IDENTIFIER ;
  INCLUSION ;
  INCLUSION_APPENDING ;
  PARAGRAPH ;
  POSITION ;
  INTERVAL ;
}

scope ChapterScope { StructuralChapter chapter } 
scope SectionScope { StructuralSection section } 


@parser::header { 
  package novelang.parser.antlr ;
  import novelang.model.common.Locator;
  import novelang.model.structural.StructuralBook ;
  import novelang.model.structural.StructuralChapter ;
  import novelang.model.structural.StructuralSection ;
} 

@lexer::header { 
  package novelang.parser.antlr ;
  
} 
	
@parser::members {

  /**
   * This needs to be set here because ANTLWorks doesn't know about it.
   */
  private StructuralBook book = 
      new novelang.model.implementation.Book( "Debug only" ) ;

	@Override
	public void reportError( RecognitionException e ) {
	  super.reportError( e ) ;
	  book.addStructureParsingException( e ) ;
	}
	
  public void setBook( StructuralBook book ) {
    this.book = book ; 
	}
}
	

structure
  : LINEBREAK?
    (   ( ':autogenerate' LINEBREAK parts )
      | ( parts
          LINEBREAK
          chapter ( LINEBREAK chapter )*
        )
    ) 
    LINEBREAK?
    EOF
  ;
  
	parts
  : part ( LINEBREAK part )*
    -> part*
  ;
 
part 
  : OCTOTHORPE WHITESPACE genericFileName WHITESPACE?
    { 
      final String text = $genericFileName.text ;
      book.addPartReference( text ) ;
    }    
    -> ^( PART genericFileName )  
  ;
 
genericFileName
  :	n += pathDelimiter? n += genericFileNameItem 
    ( n += pathDelimiter n += genericFileNameItem )+
    -> $n* 
  ;
    
genericFileNameItem
  : ( LETTER | DIGIT | HYPHEN_MINUS | ASTERISK | FULL_STOP )+
  ;

pathDelimiter
  : SOLIDUS 
  ;



chapter
  scope ChapterScope ;
  : CHAPTER_INTRODUCER 
    { StructuralChapter chapter = book.createChapter() ;
      { $ChapterScope::chapter = chapter ; }
    }
    WHITESPACE? 
    ( title WHITESPACE? 
      { $ChapterScope::chapter.setTitle( $title.text ) ; }
    )?
    LINEBREAK
    ( style WHITESPACE? LINEBREAK 
      { $ChapterScope::chapter.setStyle( $style.text ) ; }
    )?
    section ( LINEBREAK section )* 
    -> ^( CHAPTER ^( TITLE title )? style? section* )
  ;
  
section
  scope SectionScope ;
  : SECTION_INTRODUCER 
    { final Locator locator = $ChapterScope::chapter.createLocator(
          ( ( Token )input.LT( 1 ) ).getLine(),
          ( ( Token )input.LT( 1 ) ).getCharPositionInLine()
      ) ;
      StructuralSection section = $ChapterScope::chapter.createSection( locator ) ;
      $SectionScope::section = section ; 
    }  
    WHITESPACE? 
    ( title WHITESPACE? 
      { $SectionScope::section.setTitle( $title.text ) ; }
    )?
    LINEBREAK
    ( style WHITESPACE? LINEBREAK 
      { $SectionScope::section.setStyle( $style.text ) ; }
    )?
    inclusion WHITESPACE?
    ( LINEBREAK inclusion WHITESPACE? )*
    -> ^( SECTION ^( TITLE title )? style? inclusion* )
  ;

style
	:	':style' WHITESPACE word
	  -> ^( STYLE word )
	;


title
  : word ( WHITESPACE word )*
  ;

inclusion
/*  : ( '+' | '\\' ) WHITESPACE
    identifier
    ( WHITESPACE paragraphReferences )?     
*/
  : ( ( PLUS_SIGN WHITESPACE identifier ( WHITESPACE paragraphReferences )? )
      -> ^( INCLUSION ^( IDENTIFIER identifier ) paragraphReferences* )
    )
  | ( ( VERTICAL_LINE WHITESPACE identifier ( WHITESPACE paragraphReferences )? )
      -> ^( INCLUSION_APPENDING ^( IDENTIFIER identifier ) paragraphReferences* )
    )
  ;   

paragraphReferences
  : PARAGRAPH_REFERENCES_INTRODUCER
    ( WHITESPACE ( reversibleNumber | paragraphInterval ) )+
    -> ^( PARAGRAPH reversibleNumber* paragraphInterval* )
  ;
  
paragraphInterval
  :	n1 = positiveNumber '..' n2 = positiveNumber
   -> ^( INTERVAL $n1 $n2 ) 
  ;

identifier
  : word( WHITESPACE word )*	
  ;



positiveNumber
  : DIGIT+
    -> ^( POSITION DIGIT+ )
  ;
	
reversibleNumber
  : DIGIT+ HYPHEN_MINUS?
  ;
	
word 
  : ( LETTER | DIGIT )+
    ( ( APOSTROPHE | HYPHEN_MINUS ) ( LETTER | DIGIT )+ )*
  ; 



LINEBREAK : ( '\r' | '\n' )+ ; 
WHITESPACE : ( ' ' | '\t' )+ ;


COMMA : ',' ;
FULL_STOP : '.' ;

SECTION_SIGN : '¤' ; // It's Unicode name but it's used for paragraphs!

SOLIDUS : '/' ; 
REVERSE_SOLIDUS : '\\' ; 
APOSTROPHE : '\'' ;
HYPHEN_MINUS : '-' ;
ASTERISK : '*' ;
OCTOTHORPE : '#' ;
PLUS_SIGN : '+' ;
VERTICAL_LINE : '|' ;

CHAPTER_INTRODUCER : '***' ;
SECTION_INTRODUCER : '===' ;
PARAGRAPH_REFERENCES_INTRODUCER : '<<' ;

LETTER 
  : 'a'..'z' 
  | 'A'..'Z' 
  ;

DIGIT : '0'..'9';
  
// From Java 5 grammar http://www.antlr.org/grammar/1152141644268/Java.g

BLOCK_COMMENT
  : '{' ( options { greedy = false ; } : . )* '}' { $channel = HIDDEN ; }
  ;

LINE_COMMENT
  : '//' ~('\n'|'\r')* '\r'? '\n' 
    { $channel=HIDDEN ; /*this.setText( "\n" ) ;*/ }
  ;
