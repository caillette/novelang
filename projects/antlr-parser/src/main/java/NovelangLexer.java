// $ANTLR 3.1.1 /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g 2010-05-02 00:44:51

package novelang.parser.antlr ;
import novelang.parser.antlr.ProblemDelegate ; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class NovelangLexer extends Lexer {
    public static final int COPYRIGHT_SIGN=113;
    public static final int SIGN_QUESTIONMARK=45;
    public static final int LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX=181;
    public static final int SIGN_COLON=48;
    public static final int LATIN_CAPITAL_LETTER_U_WITH_ACUTE=206;
    public static final int LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS=194;
    public static final int LATIN_SMALL_LETTER_E_WITH_ACUTE=179;
    public static final int BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENT_PAIRS=17;
    public static final int LATIN_SMALL_LETTER_AE=173;
    public static final int SIGN_EXCLAMATIONMARK=46;
    public static final int COMMERCIAL_AT=58;
    public static final int EOF=-1;
    public static final int MULTIPLICATION_SIGN=118;
    public static final int LEFT_CURLY_BRACKET=116;
    public static final int HYPHEN_MINUS=66;
    public static final int LATIN_CAPITAL_LETTER_A_WITH_GRAVE=166;
    public static final int DIGIT_0=124;
    public static final int DIGIT_1=125;
    public static final int PERCENT_SIGN=96;
    public static final int QUESTION_MARK=76;
    public static final int LATIN_CAPITAL_LETTER_C_WITH_CEDILLA=176;
    public static final int GRAVE_ACCENT=73;
    public static final int WORD_=33;
    public static final int COMMAND_INSERT_CREATELEVEL_=50;
    public static final int APOSTROPHE=80;
    public static final int RELATIVE_IDENTIFIER=38;
    public static final int LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS=170;
    public static final int SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK=211;
    public static final int URL_LITERAL=25;
    public static final int LOW_LINE=67;
    public static final int COMMAND_INSERT_NOHEAD_=51;
    public static final int CIRCUMFLEX_ACCENT=101;
    public static final int RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK=103;
    public static final int LATIN_CAPITAL_LETTER_A_WITH_ACUTE=172;
    public static final int DEGREE_SIGN=114;
    public static final int FULL_STOP=74;
    public static final int DOLLAR_SIGN=93;
    public static final int SIGN_COMMA=42;
    public static final int WHITESPACE=68;
    public static final int SEMICOLON=78;
    public static final int LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS=188;
    public static final int RIGHT_CURLY_BRACKET=121;
    public static final int PARAGRAPH_AS_LIST_ITEM_WITH_TRIPLE_HYPHEN_=23;
    public static final int LEVEL_INTRODUCER_INDENT_=7;
    public static final int LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX=202;
    public static final int LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK=102;
    public static final int REVERSE_SOLIDUS=59;
    public static final int LEFT_SINGLE_QUOTATION_MARK=117;
    public static final int SIGN_FULLSTOP=43;
    public static final int LEVEL_INTRODUCER_=6;
    public static final int SECTION_SIGN=123;
    public static final int LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE=207;
    public static final int LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX=168;
    public static final int BLOCK_INSIDE_DOUBLE_QUOTES=12;
    public static final int PUNCTUATION_SIGN=40;
    public static final int LATIN_SMALL_LETTER_O_WITH_DIAERESIS=193;
    public static final int SUBBLOCK=19;
    public static final int LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE=198;
    public static final int LATIN_SMALL_LETTER_A_WITH_GRAVE=165;
    public static final int ASTERISK=95;
    public static final int GREATER_THAN_SIGN=72;
    public static final int REGISTERED_SIGN=120;
    public static final int LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX=191;
    public static final int LATIN_CAPITAL_LIGATURE_OE=210;
    public static final int APOSTROPHE_WORDMATE=41;
    public static final int LATIN_SMALL_LETTER_E_WITH_DIAERESIS=183;
    public static final int RIGHT_SINGLE_QUOTATION_MARK=122;
    public static final int COMPOSIUM=5;
    public static final int LATIN_CAPITAL_LETTER_E_WITH_GRAVE=178;
    public static final int LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX=192;
    public static final int AMPERSAND=90;
    public static final int DIGIT_2=126;
    public static final int DIGIT_3=127;
    public static final int SOLIDUS=65;
    public static final int DIGIT_4=128;
    public static final int POUND_SIGN=119;
    public static final int DIGIT_5=129;
    public static final int LESS_THAN_SIGN=71;
    public static final int DIGIT_6=130;
    public static final int DIGIT_7=131;
    public static final int LEFT_SQUARE_BRACKET=62;
    public static final int DIGIT_8=132;
    public static final int DIGIT_9=133;
    public static final int COLON=79;
    public static final int EQUALS_SIGN=91;
    public static final int NOVELLA=21;
    public static final int LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS=184;
    public static final int SOFTBREAK=69;
    public static final int LATIN_SMALL_LETTER_D=109;
    public static final int LATIN_SMALL_LETTER_E=84;
    public static final int LATIN_SMALL_LETTER_B=110;
    public static final int LATIN_SMALL_LETTER_E_WITH_GRAVE=177;
    public static final int LATIN_SMALL_LETTER_C=105;
    public static final int WORD_AFTER_CIRCUMFLEX_ACCENT=24;
    public static final int LATIN_SMALL_LETTER_A=108;
    public static final int CELL_ROWS_WITH_VERTICAL_LINE=32;
    public static final int DOUBLE_QUOTE=64;
    public static final int ABSOLUTE_IDENTIFIER=37;
    public static final int LATIN_SMALL_LETTER_I_WITH_ACUTE=189;
    public static final int SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK=212;
    public static final int WHITESPACE_=34;
    public static final int EXTENDED_WORD_=9;
    public static final int LEFT_PARENTHESIS=60;
    public static final int BLOCK_COMMENT=213;
    public static final int LATIN_CAPITAL_LETTER_U_WITH_GRAVE=200;
    public static final int VERTICAL_LINE=70;
    public static final int SIGN_ELLIPSIS=44;
    public static final int SIGN_SEMICOLON=47;
    public static final int PARAGRAPH_REGULAR=22;
    public static final int LINE_COMMENT=214;
    public static final int LATIN_SMALL_LETTER_U_WITH_GRAVE=199;
    public static final int LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX=186;
    public static final int BLOCK_OF_LITERAL_INSIDE_GRAVE_ACCENTS=16;
    public static final int COMMAND_INSERT_=49;
    public static final int COMMAND_MAPSTYLESHEET_ASSIGNMENT_=57;
    public static final int COMMAND_INSERT_STYLE_=55;
    public static final int LATIN_SMALL_LETTER_A_WITH_ACUTE=171;
    public static final int EURO_SIGN=115;
    public static final int COMMAND_INSERT_RECURSE_=53;
    public static final int BLOCK_INSIDE_PARENTHESIS=10;
    public static final int LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX=185;
    public static final int LATIN_CAPITAL_LETTER_AE=174;
    public static final int LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX=167;
    public static final int LATIN_CAPITAL_LETTER_I_WITH_ACUTE=190;
    public static final int LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE=208;
    public static final int LATIN_CAPITAL_LETTER_A=134;
    public static final int COMPOSITE_IDENTIFIER=39;
    public static final int LATIN_CAPITAL_LETTER_B=135;
    public static final int LATIN_CAPITAL_LETTER_C=136;
    public static final int LATIN_CAPITAL_LETTER_D=137;
    public static final int LATIN_SMALL_LIGATURE_OE=209;
    public static final int LATIN_CAPITAL_LETTER_E=138;
    public static final int LATIN_CAPITAL_LETTER_F=139;
    public static final int COMMAND_MAPSTYLESHEET_=56;
    public static final int LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE=197;
    public static final int EXCLAMATION_MARK=77;
    public static final int LATIN_CAPITAL_LETTER_M=151;
    public static final int LATIN_CAPITAL_LETTER_N=152;
    public static final int LATIN_CAPITAL_LETTER_K=149;
    public static final int LATIN_SMALL_LETTER_U_WITH_ACUTE=205;
    public static final int LATIN_CAPITAL_LETTER_L=150;
    public static final int LATIN_CAPITAL_LETTER_I=147;
    public static final int BLOCK_AFTER_TILDE=18;
    public static final int LATIN_CAPITAL_LETTER_J=148;
    public static final int LATIN_CAPITAL_LETTER_G=145;
    public static final int LATIN_CAPITAL_LETTER_H=146;
    public static final int RASTER_IMAGE=26;
    public static final int LATIN_CAPITAL_LETTER_U=159;
    public static final int LATIN_CAPITAL_LETTER_V=160;
    public static final int LATIN_CAPITAL_LETTER_S=157;
    public static final int LATIN_CAPITAL_LETTER_T=158;
    public static final int LATIN_CAPITAL_LETTER_Q=155;
    public static final int LATIN_CAPITAL_LETTER_R=156;
    public static final int LATIN_CAPITAL_LETTER_O=153;
    public static final int LATIN_CAPITAL_LETTER_P=154;
    public static final int LATIN_CAPITAL_LETTER_E_WITH_ACUTE=180;
    public static final int CELL=30;
    public static final int LATIN_CAPITAL_LETTER_Z=164;
    public static final int LATIN_SMALL_LETTER_A_WITH_DIAERESIS=169;
    public static final int LATIN_CAPITAL_LETTER_Y=163;
    public static final int LATIN_CAPITAL_LETTER_X=162;
    public static final int LATIN_CAPITAL_LETTER_W=161;
    public static final int COMMAND_INSERT_SORT_=54;
    public static final int LATIN_SMALL_LETTER_I_WITH_DIAERESIS=187;
    public static final int RIGHT_PARENTHESIS=61;
    public static final int LATIN_CAPITAL_LETTER_O_WITH_ACUTE=196;
    public static final int COMMA=75;
    public static final int LATIN_SMALL_LETTER_U_WITH_DIAERESIS=203;
    public static final int TILDE=92;
    public static final int PARAGRAPHS_INSIDE_ANGLED_BRACKET_PAIRS=4;
    public static final int CELL_ROW=31;
    public static final int LINES_OF_LITERAL=20;
    public static final int BLOCK_INSIDE_SQUARE_BRACKETS=11;
    public static final int PLUS_SIGN=94;
    public static final int BLOCK_INSIDE_TWO_HYPHENS_THEN_HYPHEN_LOW_LINE=15;
    public static final int COMMAND_INSERT_LEVELABOVE_=52;
    public static final int LEVEL_TITLE=8;
    public static final int LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS=204;
    public static final int RESOURCE_LOCATION=28;
    public static final int LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX=201;
    public static final int TAG=36;
    public static final int RIGHT_SQUARE_BRACKET=63;
    public static final int LATIN_SMALL_LETTER_W=142;
    public static final int LATIN_SMALL_LETTER_V=100;
    public static final int LATIN_SMALL_LETTER_Y=111;
    public static final int LATIN_SMALL_LETTER_X=143;
    public static final int LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX=182;
    public static final int LATIN_SMALL_LETTER_Z=144;
    public static final int BLOCK_INSIDE_SOLIDUS_PAIRS=13;
    public static final int LATIN_SMALL_LETTER_O_WITH_ACUTE=195;
    public static final int VECTOR_IMAGE=27;
    public static final int LATIN_SMALL_LETTER_F=81;
    public static final int LATIN_SMALL_LETTER_G=98;
    public static final int LATIN_SMALL_LETTER_H=85;
    public static final int LATIN_SMALL_LETTER_I=82;
    public static final int NUMBER_SIGN=89;
    public static final int LATIN_SMALL_LETTER_J=99;
    public static final int LATIN_SMALL_LETTER_K=140;
    public static final int LATIN_SMALL_LETTER_L=83;
    public static final int LATIN_SMALL_LETTER_M=112;
    public static final int LATIN_SMALL_LETTER_N=97;
    public static final int LATIN_SMALL_LETTER_C_WITH_CEDILLA=175;
    public static final int LATIN_SMALL_LETTER_O=107;
    public static final int BLOCK_INSIDE_HYPHEN_PAIRS=14;
    public static final int LATIN_SMALL_LETTER_P=87;
    public static final int LATIN_SMALL_LETTER_Q=141;
    public static final int LINE_BREAK_=35;
    public static final int EMBEDDED_LIST_ITEM_WITH_HYPHEN_=29;
    public static final int LATIN_SMALL_LETTER_R=104;
    public static final int LATIN_SMALL_LETTER_S=88;
    public static final int LATIN_SMALL_LETTER_T=86;
    public static final int LATIN_SMALL_LETTER_U=106;

     
      private ProblemDelegate delegate = new ProblemDelegate() ;
     
      public void setProblemDelegate( ProblemDelegate delegate ) {
        this.delegate = delegate ;
      }
     
      @Override
      public void reportError(org.antlr.runtime.RecognitionException e ) {
        if( null == delegate ) {
          super.reportError( e ) ;
        } else {
          delegate.report( e ) ;
        }
      }
     
      private static final Logger LOGGER = LoggerFactory.getLogger( NovelangLexer.class ) ;

      @Override
      public void traceIn( String s, int i ) {
        // Do nothing, parser's logging is enough.
      }

      @Override
      public void traceOut( String s, int i ) {
        // Do nothing, parser's logging is enough.
      }


    // delegates
    // delegators

    public NovelangLexer() {;} 
    public NovelangLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public NovelangLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g"; }

    // $ANTLR start "LATIN_SMALL_LETTER_A"
    public final void mLATIN_SMALL_LETTER_A() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_A;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2191:22: ( 'a' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2191:24: 'a'
            {
            match('a'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_A"

    // $ANTLR start "LATIN_SMALL_LETTER_B"
    public final void mLATIN_SMALL_LETTER_B() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_B;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2192:22: ( 'b' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2192:24: 'b'
            {
            match('b'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_B"

    // $ANTLR start "LATIN_SMALL_LETTER_C"
    public final void mLATIN_SMALL_LETTER_C() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_C;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2193:22: ( 'c' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2193:24: 'c'
            {
            match('c'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_C"

    // $ANTLR start "LATIN_SMALL_LETTER_D"
    public final void mLATIN_SMALL_LETTER_D() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_D;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2194:22: ( 'd' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2194:24: 'd'
            {
            match('d'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_D"

    // $ANTLR start "LATIN_SMALL_LETTER_E"
    public final void mLATIN_SMALL_LETTER_E() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_E;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2195:22: ( 'e' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2195:24: 'e'
            {
            match('e'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_E"

    // $ANTLR start "LATIN_SMALL_LETTER_F"
    public final void mLATIN_SMALL_LETTER_F() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_F;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2196:22: ( 'f' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2196:24: 'f'
            {
            match('f'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_F"

    // $ANTLR start "LATIN_SMALL_LETTER_G"
    public final void mLATIN_SMALL_LETTER_G() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_G;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2197:22: ( 'g' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2197:24: 'g'
            {
            match('g'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_G"

    // $ANTLR start "LATIN_SMALL_LETTER_H"
    public final void mLATIN_SMALL_LETTER_H() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_H;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2198:22: ( 'h' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2198:24: 'h'
            {
            match('h'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_H"

    // $ANTLR start "LATIN_SMALL_LETTER_I"
    public final void mLATIN_SMALL_LETTER_I() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_I;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2199:22: ( 'i' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2199:24: 'i'
            {
            match('i'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_I"

    // $ANTLR start "LATIN_SMALL_LETTER_J"
    public final void mLATIN_SMALL_LETTER_J() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_J;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2200:22: ( 'j' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2200:24: 'j'
            {
            match('j'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_J"

    // $ANTLR start "LATIN_SMALL_LETTER_K"
    public final void mLATIN_SMALL_LETTER_K() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_K;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2201:22: ( 'k' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2201:24: 'k'
            {
            match('k'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_K"

    // $ANTLR start "LATIN_SMALL_LETTER_L"
    public final void mLATIN_SMALL_LETTER_L() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_L;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2202:22: ( 'l' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2202:24: 'l'
            {
            match('l'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_L"

    // $ANTLR start "LATIN_SMALL_LETTER_M"
    public final void mLATIN_SMALL_LETTER_M() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_M;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2203:22: ( 'm' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2203:24: 'm'
            {
            match('m'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_M"

    // $ANTLR start "LATIN_SMALL_LETTER_N"
    public final void mLATIN_SMALL_LETTER_N() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_N;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2204:22: ( 'n' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2204:24: 'n'
            {
            match('n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_N"

    // $ANTLR start "LATIN_SMALL_LETTER_O"
    public final void mLATIN_SMALL_LETTER_O() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_O;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2205:22: ( 'o' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2205:24: 'o'
            {
            match('o'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_O"

    // $ANTLR start "LATIN_SMALL_LETTER_P"
    public final void mLATIN_SMALL_LETTER_P() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_P;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2206:22: ( 'p' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2206:24: 'p'
            {
            match('p'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_P"

    // $ANTLR start "LATIN_SMALL_LETTER_Q"
    public final void mLATIN_SMALL_LETTER_Q() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_Q;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2207:22: ( 'q' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2207:24: 'q'
            {
            match('q'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_Q"

    // $ANTLR start "LATIN_SMALL_LETTER_R"
    public final void mLATIN_SMALL_LETTER_R() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_R;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2208:22: ( 'r' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2208:24: 'r'
            {
            match('r'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_R"

    // $ANTLR start "LATIN_SMALL_LETTER_S"
    public final void mLATIN_SMALL_LETTER_S() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_S;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2209:22: ( 's' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2209:24: 's'
            {
            match('s'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_S"

    // $ANTLR start "LATIN_SMALL_LETTER_T"
    public final void mLATIN_SMALL_LETTER_T() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_T;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2210:22: ( 't' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2210:24: 't'
            {
            match('t'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_T"

    // $ANTLR start "LATIN_SMALL_LETTER_U"
    public final void mLATIN_SMALL_LETTER_U() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_U;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2211:22: ( 'u' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2211:24: 'u'
            {
            match('u'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_U"

    // $ANTLR start "LATIN_SMALL_LETTER_V"
    public final void mLATIN_SMALL_LETTER_V() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_V;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2212:22: ( 'v' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2212:24: 'v'
            {
            match('v'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_V"

    // $ANTLR start "LATIN_SMALL_LETTER_W"
    public final void mLATIN_SMALL_LETTER_W() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_W;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2213:22: ( 'w' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2213:24: 'w'
            {
            match('w'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_W"

    // $ANTLR start "LATIN_SMALL_LETTER_X"
    public final void mLATIN_SMALL_LETTER_X() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_X;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2214:22: ( 'x' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2214:24: 'x'
            {
            match('x'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_X"

    // $ANTLR start "LATIN_SMALL_LETTER_Y"
    public final void mLATIN_SMALL_LETTER_Y() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_Y;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2215:22: ( 'y' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2215:24: 'y'
            {
            match('y'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_Y"

    // $ANTLR start "LATIN_SMALL_LETTER_Z"
    public final void mLATIN_SMALL_LETTER_Z() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_Z;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2216:22: ( 'z' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2216:24: 'z'
            {
            match('z'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_Z"

    // $ANTLR start "LATIN_CAPITAL_LETTER_A"
    public final void mLATIN_CAPITAL_LETTER_A() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_A;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2218:24: ( 'A' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2218:26: 'A'
            {
            match('A'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_A"

    // $ANTLR start "LATIN_CAPITAL_LETTER_B"
    public final void mLATIN_CAPITAL_LETTER_B() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_B;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2219:24: ( 'B' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2219:26: 'B'
            {
            match('B'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_B"

    // $ANTLR start "LATIN_CAPITAL_LETTER_C"
    public final void mLATIN_CAPITAL_LETTER_C() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_C;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2220:24: ( 'C' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2220:26: 'C'
            {
            match('C'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_C"

    // $ANTLR start "LATIN_CAPITAL_LETTER_D"
    public final void mLATIN_CAPITAL_LETTER_D() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_D;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2221:24: ( 'D' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2221:26: 'D'
            {
            match('D'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_D"

    // $ANTLR start "LATIN_CAPITAL_LETTER_E"
    public final void mLATIN_CAPITAL_LETTER_E() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_E;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2222:24: ( 'E' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2222:26: 'E'
            {
            match('E'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_E"

    // $ANTLR start "LATIN_CAPITAL_LETTER_F"
    public final void mLATIN_CAPITAL_LETTER_F() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_F;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2223:24: ( 'F' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2223:26: 'F'
            {
            match('F'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_F"

    // $ANTLR start "LATIN_CAPITAL_LETTER_G"
    public final void mLATIN_CAPITAL_LETTER_G() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_G;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2224:24: ( 'G' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2224:26: 'G'
            {
            match('G'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_G"

    // $ANTLR start "LATIN_CAPITAL_LETTER_H"
    public final void mLATIN_CAPITAL_LETTER_H() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_H;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2225:24: ( 'H' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2225:26: 'H'
            {
            match('H'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_H"

    // $ANTLR start "LATIN_CAPITAL_LETTER_I"
    public final void mLATIN_CAPITAL_LETTER_I() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_I;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2226:24: ( 'I' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2226:26: 'I'
            {
            match('I'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_I"

    // $ANTLR start "LATIN_CAPITAL_LETTER_J"
    public final void mLATIN_CAPITAL_LETTER_J() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_J;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2227:24: ( 'J' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2227:26: 'J'
            {
            match('J'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_J"

    // $ANTLR start "LATIN_CAPITAL_LETTER_K"
    public final void mLATIN_CAPITAL_LETTER_K() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_K;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2228:24: ( 'K' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2228:26: 'K'
            {
            match('K'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_K"

    // $ANTLR start "LATIN_CAPITAL_LETTER_L"
    public final void mLATIN_CAPITAL_LETTER_L() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_L;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2229:24: ( 'L' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2229:26: 'L'
            {
            match('L'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_L"

    // $ANTLR start "LATIN_CAPITAL_LETTER_M"
    public final void mLATIN_CAPITAL_LETTER_M() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_M;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2230:24: ( 'M' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2230:26: 'M'
            {
            match('M'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_M"

    // $ANTLR start "LATIN_CAPITAL_LETTER_N"
    public final void mLATIN_CAPITAL_LETTER_N() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_N;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2231:24: ( 'N' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2231:26: 'N'
            {
            match('N'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_N"

    // $ANTLR start "LATIN_CAPITAL_LETTER_O"
    public final void mLATIN_CAPITAL_LETTER_O() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_O;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2232:24: ( 'O' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2232:26: 'O'
            {
            match('O'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_O"

    // $ANTLR start "LATIN_CAPITAL_LETTER_P"
    public final void mLATIN_CAPITAL_LETTER_P() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_P;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2233:24: ( 'P' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2233:26: 'P'
            {
            match('P'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_P"

    // $ANTLR start "LATIN_CAPITAL_LETTER_Q"
    public final void mLATIN_CAPITAL_LETTER_Q() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_Q;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2234:24: ( 'Q' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2234:26: 'Q'
            {
            match('Q'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_Q"

    // $ANTLR start "LATIN_CAPITAL_LETTER_R"
    public final void mLATIN_CAPITAL_LETTER_R() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_R;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2235:24: ( 'R' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2235:26: 'R'
            {
            match('R'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_R"

    // $ANTLR start "LATIN_CAPITAL_LETTER_S"
    public final void mLATIN_CAPITAL_LETTER_S() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_S;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2236:24: ( 'S' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2236:26: 'S'
            {
            match('S'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_S"

    // $ANTLR start "LATIN_CAPITAL_LETTER_T"
    public final void mLATIN_CAPITAL_LETTER_T() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_T;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2237:24: ( 'T' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2237:26: 'T'
            {
            match('T'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_T"

    // $ANTLR start "LATIN_CAPITAL_LETTER_U"
    public final void mLATIN_CAPITAL_LETTER_U() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_U;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2238:24: ( 'U' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2238:26: 'U'
            {
            match('U'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_U"

    // $ANTLR start "LATIN_CAPITAL_LETTER_V"
    public final void mLATIN_CAPITAL_LETTER_V() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_V;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2239:24: ( 'V' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2239:26: 'V'
            {
            match('V'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_V"

    // $ANTLR start "LATIN_CAPITAL_LETTER_W"
    public final void mLATIN_CAPITAL_LETTER_W() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_W;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2240:24: ( 'W' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2240:26: 'W'
            {
            match('W'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_W"

    // $ANTLR start "LATIN_CAPITAL_LETTER_X"
    public final void mLATIN_CAPITAL_LETTER_X() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_X;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2241:24: ( 'X' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2241:26: 'X'
            {
            match('X'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_X"

    // $ANTLR start "LATIN_CAPITAL_LETTER_Y"
    public final void mLATIN_CAPITAL_LETTER_Y() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_Y;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2242:24: ( 'Y' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2242:26: 'Y'
            {
            match('Y'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_Y"

    // $ANTLR start "LATIN_CAPITAL_LETTER_Z"
    public final void mLATIN_CAPITAL_LETTER_Z() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_Z;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2243:24: ( 'Z' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2243:26: 'Z'
            {
            match('Z'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_Z"

    // $ANTLR start "DIGIT_0"
    public final void mDIGIT_0() throws RecognitionException {
        try {
            int _type = DIGIT_0;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2245:9: ( '0' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2245:11: '0'
            {
            match('0'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_0"

    // $ANTLR start "DIGIT_1"
    public final void mDIGIT_1() throws RecognitionException {
        try {
            int _type = DIGIT_1;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2246:9: ( '1' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2246:11: '1'
            {
            match('1'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_1"

    // $ANTLR start "DIGIT_2"
    public final void mDIGIT_2() throws RecognitionException {
        try {
            int _type = DIGIT_2;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2247:9: ( '2' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2247:11: '2'
            {
            match('2'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_2"

    // $ANTLR start "DIGIT_3"
    public final void mDIGIT_3() throws RecognitionException {
        try {
            int _type = DIGIT_3;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2248:9: ( '3' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2248:11: '3'
            {
            match('3'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_3"

    // $ANTLR start "DIGIT_4"
    public final void mDIGIT_4() throws RecognitionException {
        try {
            int _type = DIGIT_4;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2249:9: ( '4' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2249:11: '4'
            {
            match('4'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_4"

    // $ANTLR start "DIGIT_5"
    public final void mDIGIT_5() throws RecognitionException {
        try {
            int _type = DIGIT_5;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2250:9: ( '5' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2250:11: '5'
            {
            match('5'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_5"

    // $ANTLR start "DIGIT_6"
    public final void mDIGIT_6() throws RecognitionException {
        try {
            int _type = DIGIT_6;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2251:9: ( '6' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2251:11: '6'
            {
            match('6'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_6"

    // $ANTLR start "DIGIT_7"
    public final void mDIGIT_7() throws RecognitionException {
        try {
            int _type = DIGIT_7;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2252:9: ( '7' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2252:11: '7'
            {
            match('7'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_7"

    // $ANTLR start "DIGIT_8"
    public final void mDIGIT_8() throws RecognitionException {
        try {
            int _type = DIGIT_8;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2253:9: ( '8' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2253:11: '8'
            {
            match('8'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_8"

    // $ANTLR start "DIGIT_9"
    public final void mDIGIT_9() throws RecognitionException {
        try {
            int _type = DIGIT_9;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2254:9: ( '9' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2254:11: '9'
            {
            match('9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIGIT_9"

    // $ANTLR start "LATIN_SMALL_LETTER_A_WITH_GRAVE"
    public final void mLATIN_SMALL_LETTER_A_WITH_GRAVE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_A_WITH_GRAVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2256:33: ( '\\u00e0' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2256:35: '\\u00e0'
            {
            match('\u00E0'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_A_WITH_GRAVE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_A_WITH_GRAVE"
    public final void mLATIN_CAPITAL_LETTER_A_WITH_GRAVE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_A_WITH_GRAVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2257:35: ( '\\u00c0' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2257:37: '\\u00c0'
            {
            match('\u00C0'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_A_WITH_GRAVE"

    // $ANTLR start "LATIN_SMALL_LETTER_A_WITH_ACUTE"
    public final void mLATIN_SMALL_LETTER_A_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_A_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2259:33: ( '\\u00e1' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2259:35: '\\u00e1'
            {
            match('\u00E1'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_A_WITH_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_A_WITH_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_A_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_A_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2260:35: ( '\\u00c1' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2260:37: '\\u00c1'
            {
            match('\u00C1'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_A_WITH_ACUTE"

    // $ANTLR start "LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX"
    public final void mLATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2262:38: ( '\\u00e2' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2262:40: '\\u00e2'
            {
            match('\u00E2'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX"
    public final void mLATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2263:40: ( '\\u00c2' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2263:42: '\\u00c2'
            {
            match('\u00C2'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_SMALL_LETTER_A_WITH_DIAERESIS"
    public final void mLATIN_SMALL_LETTER_A_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_A_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2265:37: ( '\\u00e4' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2265:39: '\\u00e4'
            {
            match('\u00E4'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_A_WITH_DIAERESIS"

    // $ANTLR start "LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS"
    public final void mLATIN_CAPITAL_LETTER_A_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2266:39: ( '\\u00c4' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2266:41: '\\u00c4'
            {
            match('\u00C4'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS"

    // $ANTLR start "LATIN_SMALL_LETTER_AE"
    public final void mLATIN_SMALL_LETTER_AE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_AE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2268:23: ( '\\u00e6' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2268:25: '\\u00e6'
            {
            match('\u00E6'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_AE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_AE"
    public final void mLATIN_CAPITAL_LETTER_AE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_AE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2269:25: ( '\\u00c6' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2269:27: '\\u00c6'
            {
            match('\u00C6'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_AE"

    // $ANTLR start "LATIN_SMALL_LETTER_C_WITH_CEDILLA"
    public final void mLATIN_SMALL_LETTER_C_WITH_CEDILLA() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_C_WITH_CEDILLA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2271:35: ( '\\u00e7' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2271:37: '\\u00e7'
            {
            match('\u00E7'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_C_WITH_CEDILLA"

    // $ANTLR start "LATIN_CAPITAL_LETTER_C_WITH_CEDILLA"
    public final void mLATIN_CAPITAL_LETTER_C_WITH_CEDILLA() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_C_WITH_CEDILLA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2272:37: ( '\\u00c7' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2272:39: '\\u00c7'
            {
            match('\u00C7'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_C_WITH_CEDILLA"

    // $ANTLR start "LATIN_SMALL_LETTER_E_WITH_GRAVE"
    public final void mLATIN_SMALL_LETTER_E_WITH_GRAVE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_E_WITH_GRAVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2274:33: ( '\\u00e8' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2274:35: '\\u00e8'
            {
            match('\u00E8'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_E_WITH_GRAVE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_E_WITH_GRAVE"
    public final void mLATIN_CAPITAL_LETTER_E_WITH_GRAVE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_E_WITH_GRAVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2275:35: ( '\\u00c8' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2275:37: '\\u00c8'
            {
            match('\u00C8'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_E_WITH_GRAVE"

    // $ANTLR start "LATIN_SMALL_LETTER_E_WITH_ACUTE"
    public final void mLATIN_SMALL_LETTER_E_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_E_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2277:33: ( '\\u00e9' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2277:35: '\\u00e9'
            {
            match('\u00E9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_E_WITH_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_E_WITH_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_E_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_E_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2278:35: ( '\\u00c9' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2278:37: '\\u00c9'
            {
            match('\u00C9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_E_WITH_ACUTE"

    // $ANTLR start "LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX"
    public final void mLATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2280:38: ( '\\u00ea' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2280:40: '\\u00ea'
            {
            match('\u00EA'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX"
    public final void mLATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2281:40: ( '\\u00ca' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2281:42: '\\u00ca'
            {
            match('\u00CA'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_SMALL_LETTER_E_WITH_DIAERESIS"
    public final void mLATIN_SMALL_LETTER_E_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_E_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2283:37: ( '\\u00eb' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2283:39: '\\u00eb'
            {
            match('\u00EB'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_E_WITH_DIAERESIS"

    // $ANTLR start "LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS"
    public final void mLATIN_CAPITAL_LETTER_E_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2284:39: ( '\\u00cb' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2284:41: '\\u00cb'
            {
            match('\u00CB'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS"

    // $ANTLR start "LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX"
    public final void mLATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2286:38: ( '\\u00ee' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2286:40: '\\u00ee'
            {
            match('\u00EE'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX"
    public final void mLATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2287:40: ( '\\u00ce' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2287:42: '\\u00ce'
            {
            match('\u00CE'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_SMALL_LETTER_I_WITH_DIAERESIS"
    public final void mLATIN_SMALL_LETTER_I_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_I_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2289:37: ( '\\u00ef' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2289:39: '\\u00ef'
            {
            match('\u00EF'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_I_WITH_DIAERESIS"

    // $ANTLR start "LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS"
    public final void mLATIN_CAPITAL_LETTER_I_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2290:39: ( '\\u00cf' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2290:41: '\\u00cf'
            {
            match('\u00CF'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS"

    // $ANTLR start "LATIN_SMALL_LETTER_I_WITH_ACUTE"
    public final void mLATIN_SMALL_LETTER_I_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_I_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2292:33: ( '\\u00ed' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2292:35: '\\u00ed'
            {
            match('\u00ED'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_I_WITH_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_I_WITH_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_I_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_I_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2293:35: ( '\\u00cd' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2293:37: '\\u00cd'
            {
            match('\u00CD'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_I_WITH_ACUTE"

    // $ANTLR start "LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX"
    public final void mLATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2295:38: ( '\\u00f4' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2295:40: '\\u00f4'
            {
            match('\u00F4'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX"
    public final void mLATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2296:40: ( '\\u00d4' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2296:42: '\\u00d4'
            {
            match('\u00D4'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_SMALL_LETTER_O_WITH_DIAERESIS"
    public final void mLATIN_SMALL_LETTER_O_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_O_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2298:37: ( '\\u00f6' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2298:39: '\\u00f6'
            {
            match('\u00F6'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_O_WITH_DIAERESIS"

    // $ANTLR start "LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS"
    public final void mLATIN_CAPITAL_LETTER_O_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2299:39: ( '\\u00d6' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2299:41: '\\u00d6'
            {
            match('\u00D6'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS"

    // $ANTLR start "LATIN_SMALL_LETTER_O_WITH_ACUTE"
    public final void mLATIN_SMALL_LETTER_O_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_O_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2301:33: ( '\\u00f3' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2301:35: '\\u00f3'
            {
            match('\u00F3'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_O_WITH_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_O_WITH_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_O_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_O_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2302:35: ( '\\u00d3' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2302:37: '\\u00d3'
            {
            match('\u00D3'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_O_WITH_ACUTE"

    // $ANTLR start "LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE"
    public final void mLATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2304:40: ( '\\u0151' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2304:42: '\\u0151'
            {
            match('\u0151'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2305:42: ( '\\u0150' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2305:44: '\\u0150'
            {
            match('\u0150'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE"

    // $ANTLR start "LATIN_SMALL_LETTER_U_WITH_GRAVE"
    public final void mLATIN_SMALL_LETTER_U_WITH_GRAVE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_U_WITH_GRAVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2307:33: ( '\\u00f9' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2307:35: '\\u00f9'
            {
            match('\u00F9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_U_WITH_GRAVE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_U_WITH_GRAVE"
    public final void mLATIN_CAPITAL_LETTER_U_WITH_GRAVE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_U_WITH_GRAVE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2308:35: ( '\\u00d9' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2308:37: '\\u00d9'
            {
            match('\u00D9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_U_WITH_GRAVE"

    // $ANTLR start "LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX"
    public final void mLATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2310:38: ( '\\u00fb' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2310:40: '\\u00fb'
            {
            match('\u00FB'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX"
    public final void mLATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2311:40: ( '\\u00db' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2311:42: '\\u00db'
            {
            match('\u00DB'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX"

    // $ANTLR start "LATIN_SMALL_LETTER_U_WITH_DIAERESIS"
    public final void mLATIN_SMALL_LETTER_U_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_U_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2313:37: ( '\\u00fc' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2313:39: '\\u00fc'
            {
            match('\u00FC'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_U_WITH_DIAERESIS"

    // $ANTLR start "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS"
    public final void mLATIN_CAPITAL_LETTER_U_WITH_DIAERESIS() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2314:39: ( '\\u00dc' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2314:41: '\\u00dc'
            {
            match('\u00DC'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS"

    // $ANTLR start "LATIN_SMALL_LETTER_U_WITH_ACUTE"
    public final void mLATIN_SMALL_LETTER_U_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_U_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2316:33: ( '\\u00fa' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2316:35: '\\u00fa'
            {
            match('\u00FA'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_U_WITH_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_U_WITH_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_U_WITH_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_U_WITH_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2317:35: ( '\\u00da' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2317:37: '\\u00da'
            {
            match('\u00DA'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_U_WITH_ACUTE"

    // $ANTLR start "LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE"
    public final void mLATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2319:40: ( '\\u0171' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2319:42: '\\u0171'
            {
            match('\u0171'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE"

    // $ANTLR start "LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE"
    public final void mLATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2320:42: ( '\\u0170' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2320:44: '\\u0170'
            {
            match('\u0170'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE"

    // $ANTLR start "LATIN_SMALL_LIGATURE_OE"
    public final void mLATIN_SMALL_LIGATURE_OE() throws RecognitionException {
        try {
            int _type = LATIN_SMALL_LIGATURE_OE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2322:25: ( '\\u0153' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2322:27: '\\u0153'
            {
            match('\u0153'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_SMALL_LIGATURE_OE"

    // $ANTLR start "LATIN_CAPITAL_LIGATURE_OE"
    public final void mLATIN_CAPITAL_LIGATURE_OE() throws RecognitionException {
        try {
            int _type = LATIN_CAPITAL_LIGATURE_OE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2323:27: ( '\\u0152' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2323:29: '\\u0152'
            {
            match('\u0152'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LATIN_CAPITAL_LIGATURE_OE"

    // $ANTLR start "SOFTBREAK"
    public final void mSOFTBREAK() throws RecognitionException {
        try {
            int _type = SOFTBREAK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:11: ( ( '\\r' ( '\\n' )? ) | '\\n' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                alt2=1;
            }
            else if ( (LA2_0=='\n') ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:13: ( '\\r' ( '\\n' )? )
                    {
                    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:13: ( '\\r' ( '\\n' )? )
                    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:15: '\\r' ( '\\n' )?
                    {
                    match('\r'); 
                    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:20: ( '\\n' )?
                    int alt1=2;
                    int LA1_0 = input.LA(1);

                    if ( (LA1_0=='\n') ) {
                        alt1=1;
                    }
                    switch (alt1) {
                        case 1 :
                            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:20: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2327:31: '\\n'
                    {
                    match('\n'); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SOFTBREAK"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2328:12: ( ( ' ' )+ )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2328:14: ( ' ' )+
            {
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2328:14: ( ' ' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==' ') ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2328:16: ' '
            	    {
            	    match(' '); 

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHITESPACE"

    // $ANTLR start "AMPERSAND"
    public final void mAMPERSAND() throws RecognitionException {
        try {
            int _type = AMPERSAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2331:11: ( '&' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2331:13: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AMPERSAND"

    // $ANTLR start "APOSTROPHE"
    public final void mAPOSTROPHE() throws RecognitionException {
        try {
            int _type = APOSTROPHE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2332:12: ( '\\'' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2332:14: '\\''
            {
            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "APOSTROPHE"

    // $ANTLR start "ASTERISK"
    public final void mASTERISK() throws RecognitionException {
        try {
            int _type = ASTERISK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2333:10: ( '*' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2333:12: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASTERISK"

    // $ANTLR start "CIRCUMFLEX_ACCENT"
    public final void mCIRCUMFLEX_ACCENT() throws RecognitionException {
        try {
            int _type = CIRCUMFLEX_ACCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2334:19: ( '^' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2334:21: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CIRCUMFLEX_ACCENT"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2335:7: ( ':' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2335:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "COPYRIGHT_SIGN"
    public final void mCOPYRIGHT_SIGN() throws RecognitionException {
        try {
            int _type = COPYRIGHT_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2336:16: ( '\\u00a9' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2336:18: '\\u00a9'
            {
            match('\u00A9'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COPYRIGHT_SIGN"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2337:7: ( ',' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2337:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "COMMERCIAL_AT"
    public final void mCOMMERCIAL_AT() throws RecognitionException {
        try {
            int _type = COMMERCIAL_AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2338:15: ( '@' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2338:17: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMERCIAL_AT"

    // $ANTLR start "DEGREE_SIGN"
    public final void mDEGREE_SIGN() throws RecognitionException {
        try {
            int _type = DEGREE_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2339:13: ( '\\u00b0' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2339:15: '\\u00b0'
            {
            match('\u00B0'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DEGREE_SIGN"

    // $ANTLR start "DOLLAR_SIGN"
    public final void mDOLLAR_SIGN() throws RecognitionException {
        try {
            int _type = DOLLAR_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2340:13: ( '$' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2340:15: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOLLAR_SIGN"

    // $ANTLR start "DOUBLE_QUOTE"
    public final void mDOUBLE_QUOTE() throws RecognitionException {
        try {
            int _type = DOUBLE_QUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2341:14: ( '\\\"' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2341:16: '\\\"'
            {
            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_QUOTE"

    // $ANTLR start "EURO_SIGN"
    public final void mEURO_SIGN() throws RecognitionException {
        try {
            int _type = EURO_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2343:11: ( '\\u20ac' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2343:13: '\\u20ac'
            {
            match('\u20AC'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EURO_SIGN"

    // $ANTLR start "EQUALS_SIGN"
    public final void mEQUALS_SIGN() throws RecognitionException {
        try {
            int _type = EQUALS_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2344:13: ( '=' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2344:15: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS_SIGN"

    // $ANTLR start "EXCLAMATION_MARK"
    public final void mEXCLAMATION_MARK() throws RecognitionException {
        try {
            int _type = EXCLAMATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2345:18: ( '!' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2345:20: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXCLAMATION_MARK"

    // $ANTLR start "FULL_STOP"
    public final void mFULL_STOP() throws RecognitionException {
        try {
            int _type = FULL_STOP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2346:11: ( '.' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2346:13: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FULL_STOP"

    // $ANTLR start "GRAVE_ACCENT"
    public final void mGRAVE_ACCENT() throws RecognitionException {
        try {
            int _type = GRAVE_ACCENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2347:14: ( '`' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2347:16: '`'
            {
            match('`'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GRAVE_ACCENT"

    // $ANTLR start "GREATER_THAN_SIGN"
    public final void mGREATER_THAN_SIGN() throws RecognitionException {
        try {
            int _type = GREATER_THAN_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2348:19: ( '>' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2348:21: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_THAN_SIGN"

    // $ANTLR start "HYPHEN_MINUS"
    public final void mHYPHEN_MINUS() throws RecognitionException {
        try {
            int _type = HYPHEN_MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2349:14: ( '-' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2349:16: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "HYPHEN_MINUS"

    // $ANTLR start "LEFT_CURLY_BRACKET"
    public final void mLEFT_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = LEFT_CURLY_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2350:20: ( '{' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2350:22: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_CURLY_BRACKET"

    // $ANTLR start "LEFT_PARENTHESIS"
    public final void mLEFT_PARENTHESIS() throws RecognitionException {
        try {
            int _type = LEFT_PARENTHESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2351:18: ( '(' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2351:20: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_PARENTHESIS"

    // $ANTLR start "LEFT_SQUARE_BRACKET"
    public final void mLEFT_SQUARE_BRACKET() throws RecognitionException {
        try {
            int _type = LEFT_SQUARE_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2352:21: ( '[' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2352:23: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_SQUARE_BRACKET"

    // $ANTLR start "LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK"
    public final void mLEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2353:43: ( '\\u00ab' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2353:45: '\\u00ab'
            {
            match('\u00AB'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK"

    // $ANTLR start "LESS_THAN_SIGN"
    public final void mLESS_THAN_SIGN() throws RecognitionException {
        try {
            int _type = LESS_THAN_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2354:16: ( '<' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2354:18: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_THAN_SIGN"

    // $ANTLR start "LOW_LINE"
    public final void mLOW_LINE() throws RecognitionException {
        try {
            int _type = LOW_LINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2355:10: ( '_' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2355:12: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LOW_LINE"

    // $ANTLR start "MULTIPLICATION_SIGN"
    public final void mMULTIPLICATION_SIGN() throws RecognitionException {
        try {
            int _type = MULTIPLICATION_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2356:21: ( '\\u00d7' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2356:23: '\\u00d7'
            {
            match('\u00D7'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULTIPLICATION_SIGN"

    // $ANTLR start "NUMBER_SIGN"
    public final void mNUMBER_SIGN() throws RecognitionException {
        try {
            int _type = NUMBER_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2357:13: ( '#' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2357:15: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMBER_SIGN"

    // $ANTLR start "PLUS_SIGN"
    public final void mPLUS_SIGN() throws RecognitionException {
        try {
            int _type = PLUS_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2358:11: ( '+' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2358:13: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS_SIGN"

    // $ANTLR start "PERCENT_SIGN"
    public final void mPERCENT_SIGN() throws RecognitionException {
        try {
            int _type = PERCENT_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2359:14: ( '%' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2359:16: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PERCENT_SIGN"

    // $ANTLR start "POUND_SIGN"
    public final void mPOUND_SIGN() throws RecognitionException {
        try {
            int _type = POUND_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2360:12: ( '£' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2360:14: '£'
            {
            match("£"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POUND_SIGN"

    // $ANTLR start "QUESTION_MARK"
    public final void mQUESTION_MARK() throws RecognitionException {
        try {
            int _type = QUESTION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2361:15: ( '?' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2361:17: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUESTION_MARK"

    // $ANTLR start "REGISTERED_SIGN"
    public final void mREGISTERED_SIGN() throws RecognitionException {
        try {
            int _type = REGISTERED_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2362:17: ( '\\u00ae' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2362:19: '\\u00ae'
            {
            match('\u00AE'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REGISTERED_SIGN"

    // $ANTLR start "REVERSE_SOLIDUS"
    public final void mREVERSE_SOLIDUS() throws RecognitionException {
        try {
            int _type = REVERSE_SOLIDUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2363:17: ( '\\\\' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2363:19: '\\\\'
            {
            match('\\'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REVERSE_SOLIDUS"

    // $ANTLR start "RIGHT_CURLY_BRACKET"
    public final void mRIGHT_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_CURLY_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2364:21: ( '}' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2364:23: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_CURLY_BRACKET"

    // $ANTLR start "RIGHT_PARENTHESIS"
    public final void mRIGHT_PARENTHESIS() throws RecognitionException {
        try {
            int _type = RIGHT_PARENTHESIS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2365:19: ( ')' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2365:21: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_PARENTHESIS"

    // $ANTLR start "RIGHT_SQUARE_BRACKET"
    public final void mRIGHT_SQUARE_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_SQUARE_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2366:22: ( ']' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2366:24: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_SQUARE_BRACKET"

    // $ANTLR start "RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK"
    public final void mRIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2367:44: ( '\\u00bb' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2367:46: '\\u00bb'
            {
            match('\u00BB'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK"

    // $ANTLR start "SECTION_SIGN"
    public final void mSECTION_SIGN() throws RecognitionException {
        try {
            int _type = SECTION_SIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2368:14: ( '\\u00a7' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2368:16: '\\u00a7'
            {
            match('\u00A7'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SECTION_SIGN"

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2369:11: ( ';' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2369:13: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK"
    public final void mSINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2370:43: ( '\\u2039' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2370:45: '\\u2039'
            {
            match('\u2039'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK"

    // $ANTLR start "LEFT_SINGLE_QUOTATION_MARK"
    public final void mLEFT_SINGLE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = LEFT_SINGLE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2371:28: ( '\\u2019' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2371:30: '\\u2019'
            {
            match('\u2019'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_SINGLE_QUOTATION_MARK"

    // $ANTLR start "RIGHT_SINGLE_QUOTATION_MARK"
    public final void mRIGHT_SINGLE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = RIGHT_SINGLE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2372:29: ( '\\u2018' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2372:31: '\\u2018'
            {
            match('\u2018'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_SINGLE_QUOTATION_MARK"

    // $ANTLR start "SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK"
    public final void mSINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK() throws RecognitionException {
        try {
            int _type = SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2373:44: ( '\\u203a' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2373:46: '\\u203a'
            {
            match('\u203A'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK"

    // $ANTLR start "SOLIDUS"
    public final void mSOLIDUS() throws RecognitionException {
        try {
            int _type = SOLIDUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2374:9: ( '/' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2374:11: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SOLIDUS"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2375:7: ( '~' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2375:9: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "VERTICAL_LINE"
    public final void mVERTICAL_LINE() throws RecognitionException {
        try {
            int _type = VERTICAL_LINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2376:15: ( '|' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2376:17: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VERTICAL_LINE"

    // $ANTLR start "BLOCK_COMMENT"
    public final void mBLOCK_COMMENT() throws RecognitionException {
        try {
            int _type = BLOCK_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2386:3: ( '{{' ( options {greedy=false; } : . )* '}}' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2386:5: '{{' ( options {greedy=false; } : . )* '}}'
            {
            match("{{"); 

            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2386:10: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='}') ) {
                    int LA4_1 = input.LA(2);

                    if ( (LA4_1=='}') ) {
                        alt4=2;
                    }
                    else if ( ((LA4_1>='\u0000' && LA4_1<='|')||(LA4_1>='~' && LA4_1<='\uFFFF')) ) {
                        alt4=1;
                    }


                }
                else if ( ((LA4_0>='\u0000' && LA4_0<='|')||(LA4_0>='~' && LA4_0<='\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2386:43: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match("}}"); 

             _channel = HIDDEN ; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BLOCK_COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2394:3: ( '%%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2394:5: '%%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("%%"); 

            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2394:10: (~ ( '\\n' | '\\r' ) )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>='\u0000' && LA5_0<='\t')||(LA5_0>='\u000B' && LA5_0<='\f')||(LA5_0>='\u000E' && LA5_0<='\uFFFF')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2394:10: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2394:24: ( '\\r' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\r') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:2394:24: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 
             _channel=HIDDEN ; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    public void mTokens() throws RecognitionException {
        // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:8: ( LATIN_SMALL_LETTER_A | LATIN_SMALL_LETTER_B | LATIN_SMALL_LETTER_C | LATIN_SMALL_LETTER_D | LATIN_SMALL_LETTER_E | LATIN_SMALL_LETTER_F | LATIN_SMALL_LETTER_G | LATIN_SMALL_LETTER_H | LATIN_SMALL_LETTER_I | LATIN_SMALL_LETTER_J | LATIN_SMALL_LETTER_K | LATIN_SMALL_LETTER_L | LATIN_SMALL_LETTER_M | LATIN_SMALL_LETTER_N | LATIN_SMALL_LETTER_O | LATIN_SMALL_LETTER_P | LATIN_SMALL_LETTER_Q | LATIN_SMALL_LETTER_R | LATIN_SMALL_LETTER_S | LATIN_SMALL_LETTER_T | LATIN_SMALL_LETTER_U | LATIN_SMALL_LETTER_V | LATIN_SMALL_LETTER_W | LATIN_SMALL_LETTER_X | LATIN_SMALL_LETTER_Y | LATIN_SMALL_LETTER_Z | LATIN_CAPITAL_LETTER_A | LATIN_CAPITAL_LETTER_B | LATIN_CAPITAL_LETTER_C | LATIN_CAPITAL_LETTER_D | LATIN_CAPITAL_LETTER_E | LATIN_CAPITAL_LETTER_F | LATIN_CAPITAL_LETTER_G | LATIN_CAPITAL_LETTER_H | LATIN_CAPITAL_LETTER_I | LATIN_CAPITAL_LETTER_J | LATIN_CAPITAL_LETTER_K | LATIN_CAPITAL_LETTER_L | LATIN_CAPITAL_LETTER_M | LATIN_CAPITAL_LETTER_N | LATIN_CAPITAL_LETTER_O | LATIN_CAPITAL_LETTER_P | LATIN_CAPITAL_LETTER_Q | LATIN_CAPITAL_LETTER_R | LATIN_CAPITAL_LETTER_S | LATIN_CAPITAL_LETTER_T | LATIN_CAPITAL_LETTER_U | LATIN_CAPITAL_LETTER_V | LATIN_CAPITAL_LETTER_W | LATIN_CAPITAL_LETTER_X | LATIN_CAPITAL_LETTER_Y | LATIN_CAPITAL_LETTER_Z | DIGIT_0 | DIGIT_1 | DIGIT_2 | DIGIT_3 | DIGIT_4 | DIGIT_5 | DIGIT_6 | DIGIT_7 | DIGIT_8 | DIGIT_9 | LATIN_SMALL_LETTER_A_WITH_GRAVE | LATIN_CAPITAL_LETTER_A_WITH_GRAVE | LATIN_SMALL_LETTER_A_WITH_ACUTE | LATIN_CAPITAL_LETTER_A_WITH_ACUTE | LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_A_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS | LATIN_SMALL_LETTER_AE | LATIN_CAPITAL_LETTER_AE | LATIN_SMALL_LETTER_C_WITH_CEDILLA | LATIN_CAPITAL_LETTER_C_WITH_CEDILLA | LATIN_SMALL_LETTER_E_WITH_GRAVE | LATIN_CAPITAL_LETTER_E_WITH_GRAVE | LATIN_SMALL_LETTER_E_WITH_ACUTE | LATIN_CAPITAL_LETTER_E_WITH_ACUTE | LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_E_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS | LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_I_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS | LATIN_SMALL_LETTER_I_WITH_ACUTE | LATIN_CAPITAL_LETTER_I_WITH_ACUTE | LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_O_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS | LATIN_SMALL_LETTER_O_WITH_ACUTE | LATIN_CAPITAL_LETTER_O_WITH_ACUTE | LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE | LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE | LATIN_SMALL_LETTER_U_WITH_GRAVE | LATIN_CAPITAL_LETTER_U_WITH_GRAVE | LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_U_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS | LATIN_SMALL_LETTER_U_WITH_ACUTE | LATIN_CAPITAL_LETTER_U_WITH_ACUTE | LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE | LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE | LATIN_SMALL_LIGATURE_OE | LATIN_CAPITAL_LIGATURE_OE | SOFTBREAK | WHITESPACE | AMPERSAND | APOSTROPHE | ASTERISK | CIRCUMFLEX_ACCENT | COLON | COPYRIGHT_SIGN | COMMA | COMMERCIAL_AT | DEGREE_SIGN | DOLLAR_SIGN | DOUBLE_QUOTE | EURO_SIGN | EQUALS_SIGN | EXCLAMATION_MARK | FULL_STOP | GRAVE_ACCENT | GREATER_THAN_SIGN | HYPHEN_MINUS | LEFT_CURLY_BRACKET | LEFT_PARENTHESIS | LEFT_SQUARE_BRACKET | LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK | LESS_THAN_SIGN | LOW_LINE | MULTIPLICATION_SIGN | NUMBER_SIGN | PLUS_SIGN | PERCENT_SIGN | POUND_SIGN | QUESTION_MARK | REGISTERED_SIGN | REVERSE_SOLIDUS | RIGHT_CURLY_BRACKET | RIGHT_PARENTHESIS | RIGHT_SQUARE_BRACKET | RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK | SECTION_SIGN | SEMICOLON | SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK | LEFT_SINGLE_QUOTATION_MARK | RIGHT_SINGLE_QUOTATION_MARK | SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK | SOLIDUS | TILDE | VERTICAL_LINE | BLOCK_COMMENT | LINE_COMMENT )
        int alt7=157;
        alt7 = dfa7.predict(input);
        switch (alt7) {
            case 1 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:10: LATIN_SMALL_LETTER_A
                {
                mLATIN_SMALL_LETTER_A(); 

                }
                break;
            case 2 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:31: LATIN_SMALL_LETTER_B
                {
                mLATIN_SMALL_LETTER_B(); 

                }
                break;
            case 3 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:52: LATIN_SMALL_LETTER_C
                {
                mLATIN_SMALL_LETTER_C(); 

                }
                break;
            case 4 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:73: LATIN_SMALL_LETTER_D
                {
                mLATIN_SMALL_LETTER_D(); 

                }
                break;
            case 5 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:94: LATIN_SMALL_LETTER_E
                {
                mLATIN_SMALL_LETTER_E(); 

                }
                break;
            case 6 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:115: LATIN_SMALL_LETTER_F
                {
                mLATIN_SMALL_LETTER_F(); 

                }
                break;
            case 7 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:136: LATIN_SMALL_LETTER_G
                {
                mLATIN_SMALL_LETTER_G(); 

                }
                break;
            case 8 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:157: LATIN_SMALL_LETTER_H
                {
                mLATIN_SMALL_LETTER_H(); 

                }
                break;
            case 9 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:178: LATIN_SMALL_LETTER_I
                {
                mLATIN_SMALL_LETTER_I(); 

                }
                break;
            case 10 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:199: LATIN_SMALL_LETTER_J
                {
                mLATIN_SMALL_LETTER_J(); 

                }
                break;
            case 11 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:220: LATIN_SMALL_LETTER_K
                {
                mLATIN_SMALL_LETTER_K(); 

                }
                break;
            case 12 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:241: LATIN_SMALL_LETTER_L
                {
                mLATIN_SMALL_LETTER_L(); 

                }
                break;
            case 13 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:262: LATIN_SMALL_LETTER_M
                {
                mLATIN_SMALL_LETTER_M(); 

                }
                break;
            case 14 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:283: LATIN_SMALL_LETTER_N
                {
                mLATIN_SMALL_LETTER_N(); 

                }
                break;
            case 15 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:304: LATIN_SMALL_LETTER_O
                {
                mLATIN_SMALL_LETTER_O(); 

                }
                break;
            case 16 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:325: LATIN_SMALL_LETTER_P
                {
                mLATIN_SMALL_LETTER_P(); 

                }
                break;
            case 17 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:346: LATIN_SMALL_LETTER_Q
                {
                mLATIN_SMALL_LETTER_Q(); 

                }
                break;
            case 18 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:367: LATIN_SMALL_LETTER_R
                {
                mLATIN_SMALL_LETTER_R(); 

                }
                break;
            case 19 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:388: LATIN_SMALL_LETTER_S
                {
                mLATIN_SMALL_LETTER_S(); 

                }
                break;
            case 20 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:409: LATIN_SMALL_LETTER_T
                {
                mLATIN_SMALL_LETTER_T(); 

                }
                break;
            case 21 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:430: LATIN_SMALL_LETTER_U
                {
                mLATIN_SMALL_LETTER_U(); 

                }
                break;
            case 22 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:451: LATIN_SMALL_LETTER_V
                {
                mLATIN_SMALL_LETTER_V(); 

                }
                break;
            case 23 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:472: LATIN_SMALL_LETTER_W
                {
                mLATIN_SMALL_LETTER_W(); 

                }
                break;
            case 24 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:493: LATIN_SMALL_LETTER_X
                {
                mLATIN_SMALL_LETTER_X(); 

                }
                break;
            case 25 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:514: LATIN_SMALL_LETTER_Y
                {
                mLATIN_SMALL_LETTER_Y(); 

                }
                break;
            case 26 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:535: LATIN_SMALL_LETTER_Z
                {
                mLATIN_SMALL_LETTER_Z(); 

                }
                break;
            case 27 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:556: LATIN_CAPITAL_LETTER_A
                {
                mLATIN_CAPITAL_LETTER_A(); 

                }
                break;
            case 28 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:579: LATIN_CAPITAL_LETTER_B
                {
                mLATIN_CAPITAL_LETTER_B(); 

                }
                break;
            case 29 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:602: LATIN_CAPITAL_LETTER_C
                {
                mLATIN_CAPITAL_LETTER_C(); 

                }
                break;
            case 30 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:625: LATIN_CAPITAL_LETTER_D
                {
                mLATIN_CAPITAL_LETTER_D(); 

                }
                break;
            case 31 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:648: LATIN_CAPITAL_LETTER_E
                {
                mLATIN_CAPITAL_LETTER_E(); 

                }
                break;
            case 32 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:671: LATIN_CAPITAL_LETTER_F
                {
                mLATIN_CAPITAL_LETTER_F(); 

                }
                break;
            case 33 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:694: LATIN_CAPITAL_LETTER_G
                {
                mLATIN_CAPITAL_LETTER_G(); 

                }
                break;
            case 34 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:717: LATIN_CAPITAL_LETTER_H
                {
                mLATIN_CAPITAL_LETTER_H(); 

                }
                break;
            case 35 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:740: LATIN_CAPITAL_LETTER_I
                {
                mLATIN_CAPITAL_LETTER_I(); 

                }
                break;
            case 36 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:763: LATIN_CAPITAL_LETTER_J
                {
                mLATIN_CAPITAL_LETTER_J(); 

                }
                break;
            case 37 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:786: LATIN_CAPITAL_LETTER_K
                {
                mLATIN_CAPITAL_LETTER_K(); 

                }
                break;
            case 38 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:809: LATIN_CAPITAL_LETTER_L
                {
                mLATIN_CAPITAL_LETTER_L(); 

                }
                break;
            case 39 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:832: LATIN_CAPITAL_LETTER_M
                {
                mLATIN_CAPITAL_LETTER_M(); 

                }
                break;
            case 40 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:855: LATIN_CAPITAL_LETTER_N
                {
                mLATIN_CAPITAL_LETTER_N(); 

                }
                break;
            case 41 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:878: LATIN_CAPITAL_LETTER_O
                {
                mLATIN_CAPITAL_LETTER_O(); 

                }
                break;
            case 42 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:901: LATIN_CAPITAL_LETTER_P
                {
                mLATIN_CAPITAL_LETTER_P(); 

                }
                break;
            case 43 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:924: LATIN_CAPITAL_LETTER_Q
                {
                mLATIN_CAPITAL_LETTER_Q(); 

                }
                break;
            case 44 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:947: LATIN_CAPITAL_LETTER_R
                {
                mLATIN_CAPITAL_LETTER_R(); 

                }
                break;
            case 45 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:970: LATIN_CAPITAL_LETTER_S
                {
                mLATIN_CAPITAL_LETTER_S(); 

                }
                break;
            case 46 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:993: LATIN_CAPITAL_LETTER_T
                {
                mLATIN_CAPITAL_LETTER_T(); 

                }
                break;
            case 47 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1016: LATIN_CAPITAL_LETTER_U
                {
                mLATIN_CAPITAL_LETTER_U(); 

                }
                break;
            case 48 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1039: LATIN_CAPITAL_LETTER_V
                {
                mLATIN_CAPITAL_LETTER_V(); 

                }
                break;
            case 49 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1062: LATIN_CAPITAL_LETTER_W
                {
                mLATIN_CAPITAL_LETTER_W(); 

                }
                break;
            case 50 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1085: LATIN_CAPITAL_LETTER_X
                {
                mLATIN_CAPITAL_LETTER_X(); 

                }
                break;
            case 51 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1108: LATIN_CAPITAL_LETTER_Y
                {
                mLATIN_CAPITAL_LETTER_Y(); 

                }
                break;
            case 52 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1131: LATIN_CAPITAL_LETTER_Z
                {
                mLATIN_CAPITAL_LETTER_Z(); 

                }
                break;
            case 53 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1154: DIGIT_0
                {
                mDIGIT_0(); 

                }
                break;
            case 54 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1162: DIGIT_1
                {
                mDIGIT_1(); 

                }
                break;
            case 55 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1170: DIGIT_2
                {
                mDIGIT_2(); 

                }
                break;
            case 56 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1178: DIGIT_3
                {
                mDIGIT_3(); 

                }
                break;
            case 57 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1186: DIGIT_4
                {
                mDIGIT_4(); 

                }
                break;
            case 58 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1194: DIGIT_5
                {
                mDIGIT_5(); 

                }
                break;
            case 59 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1202: DIGIT_6
                {
                mDIGIT_6(); 

                }
                break;
            case 60 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1210: DIGIT_7
                {
                mDIGIT_7(); 

                }
                break;
            case 61 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1218: DIGIT_8
                {
                mDIGIT_8(); 

                }
                break;
            case 62 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1226: DIGIT_9
                {
                mDIGIT_9(); 

                }
                break;
            case 63 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1234: LATIN_SMALL_LETTER_A_WITH_GRAVE
                {
                mLATIN_SMALL_LETTER_A_WITH_GRAVE(); 

                }
                break;
            case 64 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1266: LATIN_CAPITAL_LETTER_A_WITH_GRAVE
                {
                mLATIN_CAPITAL_LETTER_A_WITH_GRAVE(); 

                }
                break;
            case 65 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1300: LATIN_SMALL_LETTER_A_WITH_ACUTE
                {
                mLATIN_SMALL_LETTER_A_WITH_ACUTE(); 

                }
                break;
            case 66 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1332: LATIN_CAPITAL_LETTER_A_WITH_ACUTE
                {
                mLATIN_CAPITAL_LETTER_A_WITH_ACUTE(); 

                }
                break;
            case 67 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1366: LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX
                {
                mLATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX(); 

                }
                break;
            case 68 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1403: LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX
                {
                mLATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX(); 

                }
                break;
            case 69 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1442: LATIN_SMALL_LETTER_A_WITH_DIAERESIS
                {
                mLATIN_SMALL_LETTER_A_WITH_DIAERESIS(); 

                }
                break;
            case 70 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1478: LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS
                {
                mLATIN_CAPITAL_LETTER_A_WITH_DIAERESIS(); 

                }
                break;
            case 71 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1516: LATIN_SMALL_LETTER_AE
                {
                mLATIN_SMALL_LETTER_AE(); 

                }
                break;
            case 72 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1538: LATIN_CAPITAL_LETTER_AE
                {
                mLATIN_CAPITAL_LETTER_AE(); 

                }
                break;
            case 73 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1562: LATIN_SMALL_LETTER_C_WITH_CEDILLA
                {
                mLATIN_SMALL_LETTER_C_WITH_CEDILLA(); 

                }
                break;
            case 74 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1596: LATIN_CAPITAL_LETTER_C_WITH_CEDILLA
                {
                mLATIN_CAPITAL_LETTER_C_WITH_CEDILLA(); 

                }
                break;
            case 75 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1632: LATIN_SMALL_LETTER_E_WITH_GRAVE
                {
                mLATIN_SMALL_LETTER_E_WITH_GRAVE(); 

                }
                break;
            case 76 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1664: LATIN_CAPITAL_LETTER_E_WITH_GRAVE
                {
                mLATIN_CAPITAL_LETTER_E_WITH_GRAVE(); 

                }
                break;
            case 77 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1698: LATIN_SMALL_LETTER_E_WITH_ACUTE
                {
                mLATIN_SMALL_LETTER_E_WITH_ACUTE(); 

                }
                break;
            case 78 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1730: LATIN_CAPITAL_LETTER_E_WITH_ACUTE
                {
                mLATIN_CAPITAL_LETTER_E_WITH_ACUTE(); 

                }
                break;
            case 79 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1764: LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX
                {
                mLATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX(); 

                }
                break;
            case 80 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1801: LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX
                {
                mLATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX(); 

                }
                break;
            case 81 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1840: LATIN_SMALL_LETTER_E_WITH_DIAERESIS
                {
                mLATIN_SMALL_LETTER_E_WITH_DIAERESIS(); 

                }
                break;
            case 82 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1876: LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS
                {
                mLATIN_CAPITAL_LETTER_E_WITH_DIAERESIS(); 

                }
                break;
            case 83 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1914: LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX
                {
                mLATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX(); 

                }
                break;
            case 84 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1951: LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX
                {
                mLATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX(); 

                }
                break;
            case 85 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:1990: LATIN_SMALL_LETTER_I_WITH_DIAERESIS
                {
                mLATIN_SMALL_LETTER_I_WITH_DIAERESIS(); 

                }
                break;
            case 86 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2026: LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS
                {
                mLATIN_CAPITAL_LETTER_I_WITH_DIAERESIS(); 

                }
                break;
            case 87 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2064: LATIN_SMALL_LETTER_I_WITH_ACUTE
                {
                mLATIN_SMALL_LETTER_I_WITH_ACUTE(); 

                }
                break;
            case 88 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2096: LATIN_CAPITAL_LETTER_I_WITH_ACUTE
                {
                mLATIN_CAPITAL_LETTER_I_WITH_ACUTE(); 

                }
                break;
            case 89 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2130: LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX
                {
                mLATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX(); 

                }
                break;
            case 90 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2167: LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX
                {
                mLATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX(); 

                }
                break;
            case 91 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2206: LATIN_SMALL_LETTER_O_WITH_DIAERESIS
                {
                mLATIN_SMALL_LETTER_O_WITH_DIAERESIS(); 

                }
                break;
            case 92 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2242: LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS
                {
                mLATIN_CAPITAL_LETTER_O_WITH_DIAERESIS(); 

                }
                break;
            case 93 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2280: LATIN_SMALL_LETTER_O_WITH_ACUTE
                {
                mLATIN_SMALL_LETTER_O_WITH_ACUTE(); 

                }
                break;
            case 94 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2312: LATIN_CAPITAL_LETTER_O_WITH_ACUTE
                {
                mLATIN_CAPITAL_LETTER_O_WITH_ACUTE(); 

                }
                break;
            case 95 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2346: LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE
                {
                mLATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE(); 

                }
                break;
            case 96 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2385: LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE
                {
                mLATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE(); 

                }
                break;
            case 97 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2426: LATIN_SMALL_LETTER_U_WITH_GRAVE
                {
                mLATIN_SMALL_LETTER_U_WITH_GRAVE(); 

                }
                break;
            case 98 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2458: LATIN_CAPITAL_LETTER_U_WITH_GRAVE
                {
                mLATIN_CAPITAL_LETTER_U_WITH_GRAVE(); 

                }
                break;
            case 99 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2492: LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX
                {
                mLATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX(); 

                }
                break;
            case 100 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2529: LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX
                {
                mLATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX(); 

                }
                break;
            case 101 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2568: LATIN_SMALL_LETTER_U_WITH_DIAERESIS
                {
                mLATIN_SMALL_LETTER_U_WITH_DIAERESIS(); 

                }
                break;
            case 102 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2604: LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS
                {
                mLATIN_CAPITAL_LETTER_U_WITH_DIAERESIS(); 

                }
                break;
            case 103 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2642: LATIN_SMALL_LETTER_U_WITH_ACUTE
                {
                mLATIN_SMALL_LETTER_U_WITH_ACUTE(); 

                }
                break;
            case 104 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2674: LATIN_CAPITAL_LETTER_U_WITH_ACUTE
                {
                mLATIN_CAPITAL_LETTER_U_WITH_ACUTE(); 

                }
                break;
            case 105 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2708: LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE
                {
                mLATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE(); 

                }
                break;
            case 106 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2747: LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE
                {
                mLATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE(); 

                }
                break;
            case 107 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2788: LATIN_SMALL_LIGATURE_OE
                {
                mLATIN_SMALL_LIGATURE_OE(); 

                }
                break;
            case 108 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2812: LATIN_CAPITAL_LIGATURE_OE
                {
                mLATIN_CAPITAL_LIGATURE_OE(); 

                }
                break;
            case 109 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2838: SOFTBREAK
                {
                mSOFTBREAK(); 

                }
                break;
            case 110 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2848: WHITESPACE
                {
                mWHITESPACE(); 

                }
                break;
            case 111 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2859: AMPERSAND
                {
                mAMPERSAND(); 

                }
                break;
            case 112 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2869: APOSTROPHE
                {
                mAPOSTROPHE(); 

                }
                break;
            case 113 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2880: ASTERISK
                {
                mASTERISK(); 

                }
                break;
            case 114 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2889: CIRCUMFLEX_ACCENT
                {
                mCIRCUMFLEX_ACCENT(); 

                }
                break;
            case 115 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2907: COLON
                {
                mCOLON(); 

                }
                break;
            case 116 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2913: COPYRIGHT_SIGN
                {
                mCOPYRIGHT_SIGN(); 

                }
                break;
            case 117 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2928: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 118 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2934: COMMERCIAL_AT
                {
                mCOMMERCIAL_AT(); 

                }
                break;
            case 119 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2948: DEGREE_SIGN
                {
                mDEGREE_SIGN(); 

                }
                break;
            case 120 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2960: DOLLAR_SIGN
                {
                mDOLLAR_SIGN(); 

                }
                break;
            case 121 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2972: DOUBLE_QUOTE
                {
                mDOUBLE_QUOTE(); 

                }
                break;
            case 122 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2985: EURO_SIGN
                {
                mEURO_SIGN(); 

                }
                break;
            case 123 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:2995: EQUALS_SIGN
                {
                mEQUALS_SIGN(); 

                }
                break;
            case 124 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3007: EXCLAMATION_MARK
                {
                mEXCLAMATION_MARK(); 

                }
                break;
            case 125 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3024: FULL_STOP
                {
                mFULL_STOP(); 

                }
                break;
            case 126 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3034: GRAVE_ACCENT
                {
                mGRAVE_ACCENT(); 

                }
                break;
            case 127 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3047: GREATER_THAN_SIGN
                {
                mGREATER_THAN_SIGN(); 

                }
                break;
            case 128 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3065: HYPHEN_MINUS
                {
                mHYPHEN_MINUS(); 

                }
                break;
            case 129 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3078: LEFT_CURLY_BRACKET
                {
                mLEFT_CURLY_BRACKET(); 

                }
                break;
            case 130 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3097: LEFT_PARENTHESIS
                {
                mLEFT_PARENTHESIS(); 

                }
                break;
            case 131 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3114: LEFT_SQUARE_BRACKET
                {
                mLEFT_SQUARE_BRACKET(); 

                }
                break;
            case 132 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3134: LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
                {
                mLEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK(); 

                }
                break;
            case 133 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3176: LESS_THAN_SIGN
                {
                mLESS_THAN_SIGN(); 

                }
                break;
            case 134 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3191: LOW_LINE
                {
                mLOW_LINE(); 

                }
                break;
            case 135 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3200: MULTIPLICATION_SIGN
                {
                mMULTIPLICATION_SIGN(); 

                }
                break;
            case 136 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3220: NUMBER_SIGN
                {
                mNUMBER_SIGN(); 

                }
                break;
            case 137 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3232: PLUS_SIGN
                {
                mPLUS_SIGN(); 

                }
                break;
            case 138 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3242: PERCENT_SIGN
                {
                mPERCENT_SIGN(); 

                }
                break;
            case 139 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3255: POUND_SIGN
                {
                mPOUND_SIGN(); 

                }
                break;
            case 140 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3266: QUESTION_MARK
                {
                mQUESTION_MARK(); 

                }
                break;
            case 141 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3280: REGISTERED_SIGN
                {
                mREGISTERED_SIGN(); 

                }
                break;
            case 142 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3296: REVERSE_SOLIDUS
                {
                mREVERSE_SOLIDUS(); 

                }
                break;
            case 143 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3312: RIGHT_CURLY_BRACKET
                {
                mRIGHT_CURLY_BRACKET(); 

                }
                break;
            case 144 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3332: RIGHT_PARENTHESIS
                {
                mRIGHT_PARENTHESIS(); 

                }
                break;
            case 145 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3350: RIGHT_SQUARE_BRACKET
                {
                mRIGHT_SQUARE_BRACKET(); 

                }
                break;
            case 146 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3371: RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK
                {
                mRIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK(); 

                }
                break;
            case 147 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3414: SECTION_SIGN
                {
                mSECTION_SIGN(); 

                }
                break;
            case 148 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3427: SEMICOLON
                {
                mSEMICOLON(); 

                }
                break;
            case 149 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3437: SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK
                {
                mSINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK(); 

                }
                break;
            case 150 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3479: LEFT_SINGLE_QUOTATION_MARK
                {
                mLEFT_SINGLE_QUOTATION_MARK(); 

                }
                break;
            case 151 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3506: RIGHT_SINGLE_QUOTATION_MARK
                {
                mRIGHT_SINGLE_QUOTATION_MARK(); 

                }
                break;
            case 152 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3534: SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK
                {
                mSINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK(); 

                }
                break;
            case 153 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3577: SOLIDUS
                {
                mSOLIDUS(); 

                }
                break;
            case 154 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3585: TILDE
                {
                mTILDE(); 

                }
                break;
            case 155 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3591: VERTICAL_LINE
                {
                mVERTICAL_LINE(); 

                }
                break;
            case 156 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3605: BLOCK_COMMENT
                {
                mBLOCK_COMMENT(); 

                }
                break;
            case 157 :
                // /Users/Shared/Novelang/projects/antlr-parser/src/main/antlr3/novelang/parser/antlr/Novelang.g:1:3619: LINE_COMMENT
                {
                mLINE_COMMENT(); 

                }
                break;

        }

    }


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\u0081\uffff\1\u009d\10\uffff\1\u009f\25\uffff";
    static final String DFA7_eofS =
        "\u00a0\uffff";
    static final String DFA7_minS =
        "\1\12\u0080\uffff\1\173\10\uffff\1\45\25\uffff";
    static final String DFA7_maxS =
        "\1\u20ac\u0080\uffff\1\173\10\uffff\1\45\25\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1"+
        "\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31"+
        "\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46"+
        "\1\47\1\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63"+
        "\1\64\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75\1\76\1\77\1\100"+
        "\1\101\1\102\1\103\1\104\1\105\1\106\1\107\1\110\1\111\1\112\1\113"+
        "\1\114\1\115\1\116\1\117\1\120\1\121\1\122\1\123\1\124\1\125\1\126"+
        "\1\127\1\130\1\131\1\132\1\133\1\134\1\135\1\136\1\137\1\140\1\141"+
        "\1\142\1\143\1\144\1\145\1\146\1\147\1\150\1\151\1\152\1\153\1\154"+
        "\1\155\1\156\1\157\1\160\1\161\1\162\1\163\1\164\1\165\1\166\1\167"+
        "\1\170\1\171\1\172\1\173\1\174\1\175\1\176\1\177\1\u0080\1\uffff"+
        "\1\u0082\1\u0083\1\u0084\1\u0085\1\u0086\1\u0087\1\u0088\1\u0089"+
        "\1\uffff\1\u008b\1\u008c\1\u008d\1\u008e\1\u008f\1\u0090\1\u0091"+
        "\1\u0092\1\u0093\1\u0094\1\u0095\1\u0096\1\u0097\1\u0098\1\u0099"+
        "\1\u009a\1\u009b\1\u009c\1\u0081\1\u009d\1\u008a";
    static final String DFA7_specialS =
        "\u00a0\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\155\2\uffff\1\155\22\uffff\1\156\1\174\1\171\1\u0088\1\170"+
            "\1\u008a\1\157\1\160\1\u0082\1\u0090\1\161\1\u0089\1\165\1\u0080"+
            "\1\175\1\u0099\1\65\1\66\1\67\1\70\1\71\1\72\1\73\1\74\1\75"+
            "\1\76\1\163\1\u0094\1\u0085\1\173\1\177\1\u008c\1\166\1\33\1"+
            "\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45\1\46\1\47\1"+
            "\50\1\51\1\52\1\53\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63\1"+
            "\64\1\u0083\1\u008e\1\u0091\1\162\1\u0086\1\176\1\1\1\2\1\3"+
            "\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20"+
            "\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\u0081\1"+
            "\u009b\1\u008f\1\u009a\50\uffff\1\u0093\1\uffff\1\164\1\uffff"+
            "\1\u0084\1\u008b\1\uffff\1\u008d\1\uffff\1\167\12\uffff\1\u0092"+
            "\4\uffff\1\100\1\102\1\104\1\uffff\1\106\1\uffff\1\110\1\112"+
            "\1\114\1\116\1\120\1\122\1\uffff\1\130\1\124\1\126\3\uffff\1"+
            "\136\1\132\1\uffff\1\134\1\u0087\1\uffff\1\142\1\150\1\144\1"+
            "\146\3\uffff\1\77\1\101\1\103\1\uffff\1\105\1\uffff\1\107\1"+
            "\111\1\113\1\115\1\117\1\121\1\uffff\1\127\1\123\1\125\3\uffff"+
            "\1\135\1\131\1\uffff\1\133\2\uffff\1\141\1\147\1\143\1\145\123"+
            "\uffff\1\140\1\137\1\154\1\153\34\uffff\1\152\1\151\u1ea6\uffff"+
            "\1\u0097\1\u0096\37\uffff\1\u0095\1\u0098\161\uffff\1\172",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u009c",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u009e",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( LATIN_SMALL_LETTER_A | LATIN_SMALL_LETTER_B | LATIN_SMALL_LETTER_C | LATIN_SMALL_LETTER_D | LATIN_SMALL_LETTER_E | LATIN_SMALL_LETTER_F | LATIN_SMALL_LETTER_G | LATIN_SMALL_LETTER_H | LATIN_SMALL_LETTER_I | LATIN_SMALL_LETTER_J | LATIN_SMALL_LETTER_K | LATIN_SMALL_LETTER_L | LATIN_SMALL_LETTER_M | LATIN_SMALL_LETTER_N | LATIN_SMALL_LETTER_O | LATIN_SMALL_LETTER_P | LATIN_SMALL_LETTER_Q | LATIN_SMALL_LETTER_R | LATIN_SMALL_LETTER_S | LATIN_SMALL_LETTER_T | LATIN_SMALL_LETTER_U | LATIN_SMALL_LETTER_V | LATIN_SMALL_LETTER_W | LATIN_SMALL_LETTER_X | LATIN_SMALL_LETTER_Y | LATIN_SMALL_LETTER_Z | LATIN_CAPITAL_LETTER_A | LATIN_CAPITAL_LETTER_B | LATIN_CAPITAL_LETTER_C | LATIN_CAPITAL_LETTER_D | LATIN_CAPITAL_LETTER_E | LATIN_CAPITAL_LETTER_F | LATIN_CAPITAL_LETTER_G | LATIN_CAPITAL_LETTER_H | LATIN_CAPITAL_LETTER_I | LATIN_CAPITAL_LETTER_J | LATIN_CAPITAL_LETTER_K | LATIN_CAPITAL_LETTER_L | LATIN_CAPITAL_LETTER_M | LATIN_CAPITAL_LETTER_N | LATIN_CAPITAL_LETTER_O | LATIN_CAPITAL_LETTER_P | LATIN_CAPITAL_LETTER_Q | LATIN_CAPITAL_LETTER_R | LATIN_CAPITAL_LETTER_S | LATIN_CAPITAL_LETTER_T | LATIN_CAPITAL_LETTER_U | LATIN_CAPITAL_LETTER_V | LATIN_CAPITAL_LETTER_W | LATIN_CAPITAL_LETTER_X | LATIN_CAPITAL_LETTER_Y | LATIN_CAPITAL_LETTER_Z | DIGIT_0 | DIGIT_1 | DIGIT_2 | DIGIT_3 | DIGIT_4 | DIGIT_5 | DIGIT_6 | DIGIT_7 | DIGIT_8 | DIGIT_9 | LATIN_SMALL_LETTER_A_WITH_GRAVE | LATIN_CAPITAL_LETTER_A_WITH_GRAVE | LATIN_SMALL_LETTER_A_WITH_ACUTE | LATIN_CAPITAL_LETTER_A_WITH_ACUTE | LATIN_SMALL_LETTER_A_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_A_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_A_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_A_WITH_DIAERESIS | LATIN_SMALL_LETTER_AE | LATIN_CAPITAL_LETTER_AE | LATIN_SMALL_LETTER_C_WITH_CEDILLA | LATIN_CAPITAL_LETTER_C_WITH_CEDILLA | LATIN_SMALL_LETTER_E_WITH_GRAVE | LATIN_CAPITAL_LETTER_E_WITH_GRAVE | LATIN_SMALL_LETTER_E_WITH_ACUTE | LATIN_CAPITAL_LETTER_E_WITH_ACUTE | LATIN_SMALL_LETTER_E_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_E_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_E_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_E_WITH_DIAERESIS | LATIN_SMALL_LETTER_I_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_I_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_I_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_I_WITH_DIAERESIS | LATIN_SMALL_LETTER_I_WITH_ACUTE | LATIN_CAPITAL_LETTER_I_WITH_ACUTE | LATIN_SMALL_LETTER_O_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_O_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_O_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_O_WITH_DIAERESIS | LATIN_SMALL_LETTER_O_WITH_ACUTE | LATIN_CAPITAL_LETTER_O_WITH_ACUTE | LATIN_SMALL_LETTER_O_WITH_DOUBLE_ACUTE | LATIN_CAPITAL_LETTER_O_WITH_DOUBLE_ACUTE | LATIN_SMALL_LETTER_U_WITH_GRAVE | LATIN_CAPITAL_LETTER_U_WITH_GRAVE | LATIN_SMALL_LETTER_U_WITH_CIRCUMFLEX | LATIN_CAPITAL_LETTER_U_WITH_CIRCUMFLEX | LATIN_SMALL_LETTER_U_WITH_DIAERESIS | LATIN_CAPITAL_LETTER_U_WITH_DIAERESIS | LATIN_SMALL_LETTER_U_WITH_ACUTE | LATIN_CAPITAL_LETTER_U_WITH_ACUTE | LATIN_SMALL_LETTER_U_WITH_DOUBLE_ACUTE | LATIN_CAPITAL_LETTER_U_WITH_DOUBLE_ACUTE | LATIN_SMALL_LIGATURE_OE | LATIN_CAPITAL_LIGATURE_OE | SOFTBREAK | WHITESPACE | AMPERSAND | APOSTROPHE | ASTERISK | CIRCUMFLEX_ACCENT | COLON | COPYRIGHT_SIGN | COMMA | COMMERCIAL_AT | DEGREE_SIGN | DOLLAR_SIGN | DOUBLE_QUOTE | EURO_SIGN | EQUALS_SIGN | EXCLAMATION_MARK | FULL_STOP | GRAVE_ACCENT | GREATER_THAN_SIGN | HYPHEN_MINUS | LEFT_CURLY_BRACKET | LEFT_PARENTHESIS | LEFT_SQUARE_BRACKET | LEFT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK | LESS_THAN_SIGN | LOW_LINE | MULTIPLICATION_SIGN | NUMBER_SIGN | PLUS_SIGN | PERCENT_SIGN | POUND_SIGN | QUESTION_MARK | REGISTERED_SIGN | REVERSE_SOLIDUS | RIGHT_CURLY_BRACKET | RIGHT_PARENTHESIS | RIGHT_SQUARE_BRACKET | RIGHT_POINTING_DOUBLE_ANGLE_QUOTATION_MARK | SECTION_SIGN | SEMICOLON | SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK | LEFT_SINGLE_QUOTATION_MARK | RIGHT_SINGLE_QUOTATION_MARK | SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK | SOLIDUS | TILDE | VERTICAL_LINE | BLOCK_COMMENT | LINE_COMMENT );";
        }
    }
 

}