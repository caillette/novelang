/*
 * Copyright (C) 2008 Laurent Caillette
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package novelang.parser.antlr;

import java.util.List;
import java.util.Iterator;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;

import novelang.system.LogFactory;
import novelang.system.Log;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;
import novelang.parser.antlr.delimited.BlockDelimiter;
import novelang.parser.NoUnescapedCharacterException;
import novelang.parser.SourceUnescape;
import novelang.parser.antlr.delimited.BlockDelimiterSupervisor;
import novelang.parser.antlr.delimited.DefaultBlockDelimiterSupervisor;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public class GrammarDelegate extends ProblemDelegate implements BlockDelimiterSupervisor {

  private static final Log LOGGER = LogFactory.getLog( GrammarDelegate.class ) ;

  /**
   * With this constructor the {@code LocationFactory} gives only partial information.
   * Its use is reserved to ANTLR parser, which needs a default {@code GrammarDelegate}
   * for running in the debugger.
   */
  public GrammarDelegate() { }

  public GrammarDelegate( final LocationFactory locationFactory ) {
    super( locationFactory ) ;
  }


// ================
// Character escape
// ================

  public String unescapeCharacter( final String escaped, final int line, final int column ) {
    try {
      return "" + SourceUnescape.unescapeCharacter( escaped ) ;
    } catch( NoUnescapedCharacterException e ) {
      final Location location = locationFactory.createLocation( line, column ) ;
      problems.add( Problem.createProblem(
          "Cannot unescape: '" + escaped + "'", location ) ) ;
      return "<unescaped:" + escaped + ">" ;
    }
  }


// ====================
// Parser rules logging
// ====================

  private static final Log parserLogger = LogFactory.getLog( GrammarDelegate.class ) ;
  private int loggingRuleDepth = 0 ;

  public void traceIn( final String s, final int ruleIndex ) {
    if( parserLogger.isDebugEnabled() ) {
      String indent = "" ;
      for( int i = 0 ; i < loggingRuleDepth ; i++ ) {
        indent += ". " ;
      }
      parserLogger.debug( indent + s ) ;
    }
    loggingRuleDepth ++ ;
  }

  public void traceOut( final String s, final int ruleIndex ) {
    loggingRuleDepth -- ;
  }


// ==========
// Delimiters
// ==========


  private final BlockDelimiterSupervisor blockDelimiterSupervisor =
      new DefaultBlockDelimiterSupervisor( locationFactory ) ;

  public void startDelimitedText( 
      final BlockDelimiter blockDelimiter, 
      final Token startToken 
  ) {
    blockDelimiterSupervisor.startDelimitedText( blockDelimiter, startToken ) ;
  }

  public void reachEndDelimiter( final BlockDelimiter blockDelimiter ) {
    blockDelimiterSupervisor.reachEndDelimiter( blockDelimiter ) ;
  }

  public void endDelimitedText( final BlockDelimiter blockDelimiter ) {
    blockDelimiterSupervisor.endDelimitedText( blockDelimiter ) ;
  }

  public void reportMissingDelimiter(
      final BlockDelimiter blockDelimiter,
      final MismatchedTokenException mismatchedTokenException
  )
      throws MismatchedTokenException
  {
    blockDelimiterSupervisor.reportMissingDelimiter( blockDelimiter, mismatchedTokenException ) ;
  }

  public void enterBlockDelimiterBoundary( final Token location ) {
    blockDelimiterSupervisor.enterBlockDelimiterBoundary( location ) ;
  }

  public Iterable< Problem > leaveBlockDelimiterBoundary() {
    final Iterable< Problem > boundaryProblems =
        blockDelimiterSupervisor.leaveBlockDelimiterBoundary() ;
    report( boundaryProblems ) ;
    return boundaryProblems;
  }


// ================================
// Configuration done by the parser
// ================================

  private String[] tokenNames = null ;

  public void setTokenNames( final String[] tokenNames ) {
    this.tokenNames = tokenNames ;
  }

  private TreeAdaptor adaptor = null ;

  public void setAdaptor( final TreeAdaptor adaptor ) {
    this.adaptor = adaptor ;
  }

// =============
// Tree creation
// =============

  public Location createLocation( final Token token ) {
    return locationFactory.createLocation(
        token.getLine(),
        token.getCharPositionInLine()
    ) ;
  }

  @Deprecated
  public Tree createTree( final int tokenIdentifier, final String tokenPayload ) {
    return new CustomTree(
      new CommonToken( tokenIdentifier, tokenPayload ),
      getLocationFactory().createLocation()
      ) ;
  }

  public Object createTree(
      final int imaginaryTokenIdentifier,
      final Location location,
      final String tokenPayload
  ) {
    final Object root_1 = createRoot( imaginaryTokenIdentifier, location ) ;
    final Object payloadEmbedder = adaptor.create( imaginaryTokenIdentifier, tokenPayload ) ;
    adaptor.addChild( root_1, payloadEmbedder ) ;

    return root_1 ;
  }

  public Object createTree(
      final int imaginaryTokenIdentifier,
      final Location location,
      final List list_p
  ) {
    final Object root_1 = createRoot( imaginaryTokenIdentifier, location ) ;
    final Iterator stream_p = list_p.iterator() ;

    if( !( stream_p.hasNext() ) ) {
      throw new RewriteEarlyExitException();
    }
    while( stream_p.hasNext() ) {
      final Object tree = stream_p.next();
      if( tree != null ) {
        adaptor.addChild( root_1, tree ) ;
      }

    }
    return root_1 ;

  }

  public Object createTree(
      final int imaginaryTokenIdentifier,
      final Location location,
      final Object... trees
  ) {
    final Object root_1 = createRoot( imaginaryTokenIdentifier, location ) ;
    for( final Object tree : trees ) {
      if( tree != null ) {
        adaptor.addChild( root_1, tree ) ;        
      }
    }

    return root_1 ;
  }

  private Object createRoot( final int imaginaryTokenIdentifier, final Location location ) {
    final CustomTree customTree = ( CustomTree ) adaptor.create(
        imaginaryTokenIdentifier,
        tokenNames[ imaginaryTokenIdentifier ]
    ) ;
    customTree.setLocation( location ) ;
    return customTree ;
  }


}

