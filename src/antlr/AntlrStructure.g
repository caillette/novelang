grammar AntlrStructure;

options { output = AST ; } 

tokens {
  STRUCTURE ;
  PART ;
  CHAPTER ;
  TITLE ;
  SECTION ;
  STYLE ;
  IDENTIFIER ;
  INCLUSION ;
  INCLUSION_APPENDING ;
  PARAGRAPH ;
  POSITION ;
  INTERVAL ;
  WORD ;
}

scope ChapterScope { StructuralChapter chapter } 
scope SectionScope { StructuralSection section } 
scope InclusionScope { StructuralInclusion inclusion } 


@parser::header { 
package novelang.parser.antlr ;
import novelang.model.common.Location;
import novelang.model.structural.StructuralBook ;
import novelang.model.structural.StructuralChapter ;
import novelang.model.structural.StructuralSection ;
import novelang.model.structural.StructuralInclusion ;
import novelang.parser.antlr.StructureGrammarDelegate;
} 

@lexer::header { 
  package novelang.parser.antlr ;  
} 
	
@parser::members {
private StructureGrammarDelegate delegate ;

public void setDelegate( StructureGrammarDelegate delegate ) {
  this.delegate = delegate ;
}

@Override
public void emitErrorMessage( String string ) {
  delegate.report( string ) ;
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
      final String fileName = $genericFileName.text ;
      delegate.createPart( fileName, input ) ;
    }    
    -> ^( PART genericFileName )  
  ;
 
genericFileName
  :	n += pathDelimiter? n += genericFileNameItem 
    ( n += pathDelimiter n += genericFileNameItem )*
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
    { final StructuralChapter chapter = delegate.createChapter( input ) ;
      { $ChapterScope::chapter = chapter ; }
    }
    WHITESPACE? 
    ( title WHITESPACE? 
      { $ChapterScope::chapter.setTitle( ( novelang.model.common.Tree ) $title.tree ) ; }
    )?
    LINEBREAK
    ( style WHITESPACE? LINEBREAK 
      { $ChapterScope::chapter.setStyle( $style.text ) ; }
    )?
    section ( LINEBREAK section )* 
    -> ^( CHAPTER title? style? section* )
  ;
  
section
  scope SectionScope ;
  : SECTION_INTRODUCER 
    { $SectionScope::section = AntlrParserHelper.createSection( $ChapterScope::chapter, input ) ; 
    }  
    WHITESPACE? 
    ( title WHITESPACE? 
      { $SectionScope::section.setTitle( ( novelang.model.common.Tree ) $title.tree ) ; }
    )?
    LINEBREAK
    ( style WHITESPACE? LINEBREAK 
      { $SectionScope::section.setStyle( $style.text ) ; }
    )?
    inclusion WHITESPACE?
    ( LINEBREAK inclusion WHITESPACE? )*
    -> ^( SECTION title? style? inclusion* )
  ;

style
	:	':style' WHITESPACE word
	  -> ^( STYLE word )
	;


title
  : word ( WHITESPACE word )*
    -> ^( TITLE word* )
  ;

inclusion
  scope InclusionScope ;
/*  : ( '+' | '\\' ) WHITESPACE
    identifier
    ( WHITESPACE paragraphReferences )?     
*/
  // TODO find in the book how to avoid this copy-paste.
  : ( ( PLUS_SIGN WHITESPACE identifier 
        { $InclusionScope::inclusion = AntlrParserHelper.createInclusion(
              $SectionScope::section,
              input,
              $identifier.text
          ) ; 
          $InclusionScope::inclusion.setCollateWithPrevious( false ) ;
        }  
        ( WHITESPACE paragraphReferences )? 
      )
      -> ^( INCLUSION ^( IDENTIFIER identifier ) paragraphReferences* )
    )
  | ( ( VERTICAL_LINE WHITESPACE identifier 
        { $InclusionScope::inclusion = AntlrParserHelper.createInclusion(
              $SectionScope::section,
              input,
              $identifier.text
          ) ; 
          $InclusionScope::inclusion.setCollateWithPrevious( true ) ;
        }  
        ( WHITESPACE paragraphReferences )? 
      )
      -> ^( INCLUSION_APPENDING ^( IDENTIFIER identifier ) paragraphReferences* )
    )
  ;   

paragraphReferences
  : PARAGRAPH_REFERENCES_INTRODUCER
    ( WHITESPACE ( 
        includedParagraphIndex 
      | includedParagraphRange 
    ) )+
    -> ^( PARAGRAPH includedParagraphIndex* includedParagraphRange* )
  ;
  
includedParagraphIndex  
  : reversibleNumber
    { AntlrParserHelper.addParagraph( $InclusionScope::inclusion, input, $reversibleNumber.text ) ; }
  ;
  
includedParagraphRange
  :	n1 = reversibleNumber '..' n2 = reversibleNumber
    { AntlrParserHelper.addParagraphRange( $InclusionScope::inclusion, input, $n1.text, $n2.text ) ; }
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
  : ( w += LETTER | w += DIGIT )
    ( w += APOSTROPHE | w += HYPHEN_MINUS | w += LETTER | w += DIGIT )*
    -> ^( WORD $w+ )
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
