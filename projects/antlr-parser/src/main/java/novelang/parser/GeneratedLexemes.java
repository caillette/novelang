package novelang.parser ;

import java.util.Set ;
import java.util.Map ;
import com.google.common.collect.ImmutableMap ;
import com.google.common.collect.Maps ;
import novelang.parser.shared.Lexeme ;

/**
 * Don't modify this class manually nor check it in the VCS.
 * Instead, run code generation which create Java source code from ANTLR grammar.
 *
 * Generated on 
 * @author 
 */
public class GeneratedLexemes {

  private static final Map< Character, Lexeme > LEXEMES ;

  static {
    final Map< Character, Lexeme > map = Maps.newHashMap() ;
    
    add( map, '\u0039', "DIGIT_9", null, "9" ) ; // 9
    add( map, '\u006c', "LATIN_SMALL_LETTER_L", null, "l" ) ; // l
    add( map, '\u003d', "EQUALS_SIGN", null, null ) ; // =
    add( map, '\u002a', "ASTERISK", null, null ) ; // *
    add( map, '\u00cf', "LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS", "Iuml", "I" ) ; 
    add( map, '\u00a9', "COPYRIGHT_SIGN", "copy", null ) ; 
    add( map, '\u004e', "LATIN_CAPITAL_LETTER_N", null, "N" ) ; // N
    add( map, '\u0170', "LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE", null, "U" ) ; 
    add( map, '\u0065', "LATIN_SMALL_LETTER_E", null, "e" ) ; // e
    add( map, '\u00e6', "LATIN_SMALL_LETTER_AE", "aelig", "ae" ) ; 
    add( map, '\u00d6', "LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS", "Ouml", "O" ) ; 
    add( map, '\u00c4', "LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS", "Auml", "A" ) ; 
    add( map, '\u00ed', "LATIN_SMALL_LETTER_I_WITH_ACUTE", null, "i" ) ; 
    add( map, '\u00c2', "LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX", "Acirc", "A" ) ; 
    add( map, '\u00db', "LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX", "Ucirc", "U" ) ; 
    add( map, '\u00ea', "LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX", "ecirc", "e" ) ; 
    add( map, '\u004c', "LATIN_CAPITAL_LETTER_L", null, "L" ) ; // L
    add( map, '\u0058', "LATIN_CAPITAL_LETTER_X", null, "X" ) ; // X
    add( map, '\u203a', "SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK", "rsaquo", null ) ; 
    add( map, '\u0062', "LATIN_SMALL_LETTER_B", null, "b" ) ; // b
    add( map, '\u006e', "LATIN_SMALL_LETTER_N", null, "n" ) ; // n
    add( map, '\u0038', "DIGIT_8", null, "8" ) ; // 8
    add( map, '\u005f', "LOW_LINE", null, null ) ; // _
    add( map, '\u2019', "LEFT_SINGLE_QUOTATION_MARK", "rsquo", null ) ; 
    add( map, '\u0046', "LATIN_CAPITAL_LETTER_F", null, "F" ) ; // F
    add( map, '\u00ef', "LATIN_SMALL_LETTER_I_WITH_DIAERESIS", "iuml", "i" ) ; 
    add( map, '\u00e4', "LATIN_SMALL_LETTER_A_WITH_DIAERESIS", "auml", "a" ) ; 
    add( map, '\u00cb', "LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS", "Euml", "E" ) ; 
    add( map, '\u002d', "HYPHEN_MINUS", null, null ) ; // -
    add( map, '\u0073', "LATIN_SMALL_LETTER_S", null, "s" ) ; // s
    add( map, '\u002c', "COMMA", null, null ) ; // ,
    add( map, '\u00f6', "LATIN_SMALL_LETTER_O_WITH_DIAERESIS", "ouml", "o" ) ; 
    add( map, '\u004d', "LATIN_CAPITAL_LETTER_M", null, "M" ) ; // M
    add( map, '\u007a', "LATIN_SMALL_LETTER_Z", null, "z" ) ; // z
    add( map, '\u0056', "LATIN_CAPITAL_LETTER_V", null, "V" ) ; // V
    add( map, '\u0078', "LATIN_SMALL_LETTER_X", null, "x" ) ; // x
    add( map, '\u00da', "LATIN_CAPITAL_LETTER_U_WITH_ACUTE", "Ucute", "U" ) ; 
    add( map, '\u006d', "LATIN_SMALL_LETTER_M", null, "m" ) ; // m
    add( map, '\u0061', "LATIN_SMALL_LETTER_A", null, "a" ) ; // a
    add( map, '\u00f3', "LATIN_SMALL_LETTER_O_WITH_ACUTE", "ocute", "o" ) ; 
    add( map, '\u00fb', "LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX", "ucirc", "u" ) ; 
    add( map, '\u0024', "DOLLAR_SIGN", null, null ) ; // $
    add( map, '\u00a7', "SECTION_SIGN", "sect", null ) ; 
    add( map, '\u0053', "LATIN_CAPITAL_LETTER_S", null, "S" ) ; // S
    add( map, '\u0031', "DIGIT_1", null, "1" ) ; // 1
    add( map, '\u0074', "LATIN_SMALL_LETTER_T", null, "t" ) ; // t
    add( map, '\u005d', "RIGHT_SQUARE_BRACKET", null, null ) ; // ]
    add( map, '\u0150', "LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE", null, "O" ) ; 
    add( map, '\u0040', "COMMERCIAL_AT", null, null ) ; // @
    add( map, '\u002f', "SOLIDUS", null, null ) ; // /
    add( map, '\u0049', "LATIN_CAPITAL_LETTER_I", null, "I" ) ; // I
    add( map, '\u0047', "LATIN_CAPITAL_LETTER_G", null, "G" ) ; // G
    add( map, '\u00e0', "LATIN_SMALL_LETTER_A_WITH_GRAVE", "agrave", "a" ) ; 
    add( map, '\'', "APOSTROPHE", null, null ) ; // '
    add( map, '\u00bb', "RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK", "raquo", null ) ; 
    add( map, '\u00e1', "LATIN_SMALL_LETTER_A_WITH_ACUTE", "acute", "a" ) ; 
    add( map, '\u0050', "LATIN_CAPITAL_LETTER_P", null, "P" ) ; // P
    add( map, '\u0036', "DIGIT_6", null, "6" ) ; // 6
    add( map, '\u00ca', "LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX", "Ecirc", "E" ) ; 
    add( map, '\u0152', "LATIN_CAPITAL_LIGATURE_OE", "OElig", "OE" ) ; 
    add( map, '\u00d9', "LATIN_CAPITAL_LETTER_U_WITH_GRAVE", "Ugrave", "U" ) ; 
    add( map, '\u0041', "LATIN_CAPITAL_LETTER_A", null, "A" ) ; // A
    add( map, '\u0025', "PERCENT_SIGN", null, null ) ; // %
    add( map, '\u006b', "LATIN_SMALL_LETTER_K", null, "k" ) ; // k
    add( map, '\u0055', "LATIN_CAPITAL_LETTER_U", null, "U" ) ; // U
    add( map, '\u0033', "DIGIT_3", null, "3" ) ; // 3
    add( map, '\u00ab', "LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK", "laquo", null ) ; 
    add( map, '\u004a', "LATIN_CAPITAL_LETTER_J", null, "J" ) ; // J
    add( map, '\u00c9', "LATIN_CAPITAL_LETTER_E_WITH_ACUTE", "Ecute", "E" ) ; 
    add( map, '\u00ae', "REGISTERED_SIGN", "reg", null ) ; 
    add( map, '\u0066', "LATIN_SMALL_LETTER_F", null, "f" ) ; // f
    add( map, '\u0030', "DIGIT_0", null, "0" ) ; // 0
    add( map, '\u0048', "LATIN_CAPITAL_LETTER_H", null, "H" ) ; // H
    add( map, '\u0051', "LATIN_CAPITAL_LETTER_Q", null, "Q" ) ; // Q
    add( map, '\u00c6', "LATIN_CAPITAL_LETTER_AE", "AElig", "AE" ) ; 
    add( map, '\u007c', "VERTICAL_LINE", null, null ) ; // |
    add( map, '\u0072', "LATIN_SMALL_LETTER_R", null, "r" ) ; // r
    add( map, '\u007b', "LEFT_CURLY_BRACKET", null, null ) ; // {
    add( map, '\u003e', "GREATER_THAN_SIGN", "gt", null ) ; // >
    add( map, '\u00e8', "LATIN_SMALL_LETTER_E_WITH_GRAVE", "egrave", "e" ) ; 
    add( map, '\u0075', "LATIN_SMALL_LETTER_U", null, "u" ) ; // u
    add( map, '\u004b', "LATIN_CAPITAL_LETTER_K", null, "K" ) ; // K
    add( map, '\u0045', "LATIN_CAPITAL_LETTER_E", null, "E" ) ; // E
    add( map, '\u2039', "SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK", "lsaquo", null ) ; 
    add( map, '\u0063', "LATIN_SMALL_LETTER_C", null, "c" ) ; // c
    add( map, '\u006f', "LATIN_SMALL_LETTER_O", null, "o" ) ; // o
    add( map, '\u003f', "QUESTION_MARK", null, null ) ; // ?
    add( map, '\u005b', "LEFT_SQUARE_BRACKET", null, null ) ; // [
    add( map, '\u00f4', "LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX", "ocirc", "o" ) ; 
    add( map, '\u00c1', "LATIN_CAPITAL_LETTER_A_WITH_ACUTE", "Acute", "A" ) ; 
    add( map, '\u0034', "DIGIT_4", null, "4" ) ; // 4
    add( map, '\u0076', "LATIN_SMALL_LETTER_V", null, "v" ) ; // v
    add( map, '\u00c8', "LATIN_CAPITAL_LETTER_E_WITH_GRAVE", "Egrave", "E" ) ; 
    add( map, '\u00cd', "LATIN_CAPITAL_LETTER_I_WITH_ACUTE", null, "I" ) ; 
    add( map, '\u00ee', "LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX", "icirc", "i" ) ; 
    add( map, '\u003c', "LESS_THAN_SIGN", "lt", null ) ; // <
    add( map, '\u00e2', "LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX", "acirc", "a" ) ; 
    add( map, '\\', "REVERSE_SOLIDUS", null, null ) ; 
    add( map, '\u00e7', "LATIN_SMALL_LETTER_C_WITH_CEDILLA", "ccedil", "c" ) ; 
    add( map, '\u0052', "LATIN_CAPITAL_LETTER_R", null, "R" ) ; // R
    add( map, '\u0068', "LATIN_SMALL_LETTER_H", null, "h" ) ; // h
    add( map, '\u0043', "LATIN_CAPITAL_LETTER_C", null, "C" ) ; // C
    add( map, '\u00fc', "LATIN_SMALL_LETTER_U_WITH_DIAERESIS", "uuml", "u" ) ; 
    add( map, '\u00d3', "LATIN_CAPITAL_LETTER_O_WITH_ACUTE", "Ocute", "O" ) ; 
    add( map, '\u002e', "FULL_STOP", null, null ) ; // .
    add( map, '\u0077', "LATIN_SMALL_LETTER_W", null, "w" ) ; // w
    add( map, '\u0057', "LATIN_CAPITAL_LETTER_W", null, "W" ) ; // W
    add( map, '\u0035', "DIGIT_5", null, "5" ) ; // 5
    add( map, '\u0153', "LATIN_SMALL_LIGATURE_OE", "oelig", "oe" ) ; 
    add( map, '\"', "DOUBLE_QUOTE", null, null ) ; // "
    add( map, '\u0069', "LATIN_SMALL_LETTER_I", null, "i" ) ; // i
    add( map, '\u0023', "NUMBER_SIGN", null, null ) ; // #
    add( map, '\u0067', "LATIN_SMALL_LETTER_G", null, "g" ) ; // g
    add( map, '\u007e', "TILDE", null, null ) ; // ~
    add( map, '\u00e9', "LATIN_SMALL_LETTER_E_WITH_ACUTE", "ecute", "e" ) ; 
    add( map, '\u0032', "DIGIT_2", null, "2" ) ; // 2
    add( map, '\u0028', "LEFT_PARENTHESIS", null, null ) ; // (
    add( map, '\u00b0', "DEGREE_SIGN", "deg", null ) ; 
    add( map, '\u00c7', "LATIN_CAPITAL_LETTER_C_WITH_CEDILLA", "Ccedil", "C" ) ; 
    add( map, '\u00eb', "LATIN_SMALL_LETTER_E_WITH_DIAERESIS", "euml", "e" ) ; 
    add( map, '\u0037', "DIGIT_7", null, "7" ) ; // 7
    add( map, '\u0151', "LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE", null, "o" ) ; 
    add( map, '\u006a', "LATIN_SMALL_LETTER_J", null, "j" ) ; // j
    add( map, '\u0060', "GRAVE_ACCENT", null, null ) ; // `
    add( map, '\u0042', "LATIN_CAPITAL_LETTER_B", null, "B" ) ; // B
    add( map, '\u00d4', "LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX", "Ocirc", "O" ) ; 
    add( map, '\u005a', "LATIN_CAPITAL_LETTER_Z", null, "Z" ) ; // Z
    add( map, '\u002b', "PLUS_SIGN", null, null ) ; // +
    add( map, '\u0171', "LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE", null, "u" ) ; 
    add( map, '\u00c0', "LATIN_CAPITAL_LETTER_A_WITH_GRAVE", "Agrave", "A" ) ; 
    add( map, '\u005e', "CIRCUMFLEX_ACCENT", null, null ) ; // ^
    add( map, '\u0044', "LATIN_CAPITAL_LETTER_D", null, "D" ) ; // D
    add( map, '\u0079', "LATIN_SMALL_LETTER_Y", null, "y" ) ; // y
    add( map, '\u00f9', "LATIN_SMALL_LETTER_U_WITH_GRAVE", "ugrave", "u" ) ; 
    add( map, '\u004f', "LATIN_CAPITAL_LETTER_O", null, "O" ) ; // O
    add( map, '\u0071', "LATIN_SMALL_LETTER_Q", null, "q" ) ; // q
    add( map, '\u003a', "COLON", null, null ) ; // :
    add( map, '\u0059', "LATIN_CAPITAL_LETTER_Y", null, "Y" ) ; // Y
    add( map, '\u00fa', "LATIN_SMALL_LETTER_U_WITH_ACUTE", "ucute", "u" ) ; 
    add( map, '\u007d', "RIGHT_CURLY_BRACKET", null, null ) ; // }
    add( map, '\u0054', "LATIN_CAPITAL_LETTER_T", null, "T" ) ; // T
    add( map, '\u0029', "RIGHT_PARENTHESIS", null, null ) ; // )
    add( map, '\u0026', "AMPERSAND", "amp", null ) ; // &
    add( map, '\u0064', "LATIN_SMALL_LETTER_D", null, "d" ) ; // d
    add( map, '\u0070', "LATIN_SMALL_LETTER_P", null, "p" ) ; // p
    add( map, '\u00ce', "LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX", "Icirc", "I" ) ; 
    add( map, '\u2018', "RIGHT_SINGLE_QUOTATION_MARK", "lsquo", null ) ; 
    add( map, '\u20ac', "EURO_SIGN", "euro", null ) ; 
    add( map, '\u00d7', "MULTIPLICATION_SIGN", "times", null ) ; 
    add( map, '\u00dc', "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS", "Uuml", "U" ) ; 
    add( map, '\u003b', "SEMICOLON", null, null ) ; // ;
    add( map, '\u0021', "EXCLAMATION_MARK", null, null ) ; // !
    LEXEMES = ImmutableMap.copyOf( map ) ;
  }

  public static Set< Character > getCharacters() {
    return LEXEMES.keySet() ;
  }

  public static Map< Character, Lexeme > getLexemes() {
    return LEXEMES ;
  }

  private static void add(
      final Map< Character, Lexeme > map,
      final Character character,
      final String unicodeName,
      final String htmlEntityName,
      final String ascii62
  ) {
    final Lexeme lexeme = new Lexeme( unicodeName, character, htmlEntityName, ascii62 ) ;
    map.put( lexeme.getCharacter(), lexeme ) ;
  }



}