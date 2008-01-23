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

scope ChapterScope { StructureParserSupport.Chapter chapter } 
scope SectionScope { StructureParserSupport.Section section } 


@parser::header { 
  package novelang.parser.antlr ;
  import novelang.parser.StructureParserSupport ;
} 

@lexer::header { 
  package novelang.parser.antlr ;
  
} 
	
@parser::members {

  private final StructureParserSupport support = new StructureParserSupport() ;

	public StructureParserSupport getSupport() {
	  return support ;
	}
	
	@Override
	public void reportError( RecognitionException e ) {
	  super.reportError( e ) ;
	  support.addException( e ) ;
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
//    -> ^( STRUCTURE parts chapter* )
  ;
  
	parts
  : part ( LINEBREAK part )*
    -> part*
  ;
 
part 
  : OCTOTHORPE WHITESPACE genericFileName WHITESPACE?
    { 
      final String text = $genericFileName.text ;
      support.addPart( text ) ;
    }    
    -> ^( PART genericFileName )  
/*    -> { new CommonTree( new CommonToken( 
             PART, 
             $genericFileName.text 
         ) ) 
       }
*/       
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
    { StructureParserSupport.Chapter chapter = new StructureParserSupport.Chapter() ;
      support.addChapter( chapter ) ;      
      { $ChapterScope::chapter = chapter ; }
    }
    WHITESPACE? ( title WHITESPACE? 
      { $ChapterScope::chapter.setTitle( $title.text ) ; }
    )?
    LINEBREAK
    ( style WHITESPACE? LINEBREAK )?
    section ( LINEBREAK section )* 
    -> ^( CHAPTER ^( TITLE title )? style? section* )
  ;
  
section
  scope SectionScope ;
  : SECTION_INTRODUCER 
    { StructureParserSupport.Section section = new StructureParserSupport.Section() ;
      $ChapterScope::chapter.addSection( section ) ;      
      $SectionScope::section = section ; 
    }  
    WHITESPACE? 
    ( title WHITESPACE? 
      { $SectionScope::section.setTitle( $title.text ) ; }
    )?
    LINEBREAK
    ( style WHITESPACE? LINEBREAK )?
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
