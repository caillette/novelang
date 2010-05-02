package novelang.parser ;

import java.util.Collections ;
import java.util.Set ;
import com.google.common.collect.Sets ;
import novelang.common.SyntacticTree ;
import novelang.common.TagBehavior;
import static novelang.common.TagBehavior.* ;

/**
 * Don't modify this class manually nor check it in the VCS.
 * Instead, run code generation which create Java source code from ANTLR grammar.
 *
 * Generated on 
 * @author 
 */
public enum NodeKind {
  PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS( false, SCOPE ), 
  COMPOSIUM( false, TRAVERSABLE ), 
  LEVEL_INTRODUCER_( false, NON_TRAVERSABLE ), 
  LEVEL_INTRODUCER_INDENT_( false, NON_TRAVERSABLE ), 
  LEVEL_TITLE( false, NON_TRAVERSABLE ), 
  EXTENDED_WORD_( false, NON_TRAVERSABLE ), 
  BLOCK_INSIDE_PARENTHESIS( false, NON_TRAVERSABLE ), 
  BLOCK_INSIDE_SQUARE_BRACKETS( false, NON_TRAVERSABLE ), 
  BLOCK_INSIDE_DOUBLE_QUOTES( false, NON_TRAVERSABLE ), 
  BLOCK_INSIDE_SOLIDUS_PAIRS( false, NON_TRAVERSABLE ), 
  BLOCK_INSIDE_HYPHEN_PAIRS( false, NON_TRAVERSABLE ), 
  BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE( false, NON_TRAVERSABLE ), 
  BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS( false, NON_TRAVERSABLE ), 
  BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS( false, NON_TRAVERSABLE ), 
  BLOCK_AFTER_TILDE( false, NON_TRAVERSABLE ), 
  SUBBLOCK( false, NON_TRAVERSABLE ), 
  LINES_OF_LITERAL( false, NON_TRAVERSABLE ), 
  NOVELLA( false, TRAVERSABLE ), 
  PARAGRAPH_REGULAR( false, TERMINAL ), 
  PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_( false, TERMINAL ), 
  WORD_AFTER_CIRCUMFLEX_ACCENT( false, NON_TRAVERSABLE ), 
  URL_LITERAL( false, NON_TRAVERSABLE ), 
  RASTER_IMAGE( false, NON_TRAVERSABLE ), 
  VECTOR_IMAGE( false, NON_TRAVERSABLE ), 
  RESOURCE_LOCATION( false, NON_TRAVERSABLE ), 
  EMBEDDED_LIST_ITEM_WITH_HYPHEN_( false, NON_TRAVERSABLE ), 
  CELL( false, NON_TRAVERSABLE ), 
  CELL_ROW( false, NON_TRAVERSABLE ), 
  CELL_ROWS_WITH_VERTICAL_LINE( false, TERMINAL ), 
  WORD_( false, NON_TRAVERSABLE ), 
  WHITESPACE_( false, NON_TRAVERSABLE ), 
  LINE_BREAK_( false, NON_TRAVERSABLE ), 
  TAG( false, NON_TRAVERSABLE ), 
  ABSOLUTE_IDENTIFIER( false, NON_TRAVERSABLE ), 
  RELATIVE_IDENTIFIER( false, NON_TRAVERSABLE ), 
  COMPOSITE_IDENTIFIER( false, NON_TRAVERSABLE ), 
  PUNCTUATION_SIGN( false, NON_TRAVERSABLE ), 
  APOSTROPHE_WORDMATE( false, NON_TRAVERSABLE ), 
  SIGN_COMMA( true, NON_TRAVERSABLE ), 
  SIGN_FULLSTOP( true, NON_TRAVERSABLE ), 
  SIGN_ELLIPSIS( true, NON_TRAVERSABLE ), 
  SIGN_QUESTIONMARK( true, NON_TRAVERSABLE ), 
  SIGN_EXCLAMATIONMARK( true, NON_TRAVERSABLE ), 
  SIGN_SEMICOLON( true, NON_TRAVERSABLE ), 
  SIGN_COLON( true, NON_TRAVERSABLE ), 
  COMMAND_INSERT_( false, NON_TRAVERSABLE ), 
  COMMAND_INSERT_CREATELEVEL_( false, NON_TRAVERSABLE ), 
  COMMAND_INSERT_NOHEAD_( false, NON_TRAVERSABLE ), 
  COMMAND_INSERT_LEVELABOVE_( false, NON_TRAVERSABLE ), 
  COMMAND_INSERT_RECURSE_( false, NON_TRAVERSABLE ), 
  COMMAND_INSERT_SORT_( false, NON_TRAVERSABLE ), 
  COMMAND_INSERT_STYLE_( false, NON_TRAVERSABLE ), 
  COMMAND_MAPSTYLESHEET_( false, NON_TRAVERSABLE ), 
  COMMAND_MAPSTYLESHEET_ASSIGNMENT_( false, NON_TRAVERSABLE ), 
  _STYLE( false, NON_TRAVERSABLE ), 
  _LEVEL( false, SCOPE ), 
  _LIST_WITH_TRIPLE_HYPHEN( false, TRAVERSABLE ), 
  _PARAGRAPH_AS_LIST_ITEM( false, NON_TRAVERSABLE ), 
  _EMBEDDED_LIST_WITH_HYPHEN( false, NON_TRAVERSABLE ), 
  _EMBEDDED_LIST_ITEM( false, NON_TRAVERSABLE ), 
  _META_TIMESTAMP( false, NON_TRAVERSABLE ), 
  _META( false, NON_TRAVERSABLE ), 
  _LOCATION( false, NON_TRAVERSABLE ), 
  _WORD_COUNT( false, NON_TRAVERSABLE ), 
  _TAGS( false, NON_TRAVERSABLE ), 
  _IMAGE_WIDTH( false, NON_TRAVERSABLE ), 
  _IMAGE_HEIGHT( false, NON_TRAVERSABLE ), 
  _URL( false, NON_TRAVERSABLE ), 
  _PLACEHOLDER_( false, NON_TRAVERSABLE ), 
  _ZERO_WIDTH_SPACE( false, NON_TRAVERSABLE ), 
  _PRESERVED_WHITESPACE( false, NON_TRAVERSABLE ), 
  _IMPLICIT_IDENTIFIER( false, NON_TRAVERSABLE ), 
  _EXPLICIT_IDENTIFIER( false, NON_TRAVERSABLE ), 
  _IMPLICIT_TAG( false, NON_TRAVERSABLE ), 
  _PROMOTED_TAG( false, NON_TRAVERSABLE ), 
  _EXPLICIT_TAG( false, NON_TRAVERSABLE ), 
  _COLLIDING_EXPLICIT_IDENTIFIER( false, NON_TRAVERSABLE ) ;

  private final boolean punctuationSign ;
  private final TagBehavior tagBehavior ;

  NodeKind( final boolean punctuationSign, final TagBehavior tagBehavior ) {
    this.punctuationSign = punctuationSign ;
    this.tagBehavior = tagBehavior ;
  }

  public TagBehavior getTagBehavior() {
    return tagBehavior ;
  }

  private static final Set< String > NAMES ;
  static {
    final Set< String > names = Sets.newHashSet() ;
    for( final NodeKind nodeKind : NodeKind.values() ) {
      names.add( nodeKind.name() ) ;
    }
    NAMES = Collections.unmodifiableSet( names ) ;
  }

  public static Set< String > getNames() {
    return NAMES ;
  }

  /**
   * Returns if a given {@code Tree} is of expected kind.
   * @param tree may be null.
   */
  public boolean isRoot( SyntacticTree tree ) {
    if( null == tree ) {
      return false ;
    }
    final String text = tree.getText();
    return
        NAMES.contains( text ) &&
        name().equals( text )
    ;
  }

}