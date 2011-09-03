/*
 * Copyright (C) 2011 Laurent Caillette
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

package org.novelang.parser.antlr;

import java.util.Iterator;
import java.util.List;

import org.novelang.common.Location;
import org.novelang.common.LocationFactory;
import org.novelang.common.Problem;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.parser.NoUnescapedCharacterException;
import org.novelang.parser.SourceUnescape;
import org.novelang.parser.antlr.delimited.BlockDelimiter;
import org.novelang.parser.antlr.delimited.BlockDelimiterSupervisor;
import org.novelang.parser.antlr.delimited.DefaultBlockDelimiterSupervisor;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

/**
 * Holds stuff which is not convenient to code inside ANTLR grammar because of code generation.
 *
 * @author Laurent Caillette
 */
public class GrammarDelegate 
    extends ProblemDelegate 
    implements BlockDelimiterSupervisor, TokenNameProvider, ParserDelegate
{

  private static final Logger LOGGER = LoggerFactory.getLogger( GrammarDelegate.class );

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

  @Override
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

  private static final Logger parserLogger = LoggerFactory.getLogger( GrammarDelegate.class ) ;
  private int loggingRuleDepth = 0 ;

  @Override
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

  @Override
  public void traceOut( final String s, final int ruleIndex ) {
    loggingRuleDepth -- ;
  }


// ==========
// Delimiters
// ==========


  private final BlockDelimiterSupervisor blockDelimiterSupervisor =
      new DefaultBlockDelimiterSupervisor( locationFactory ) ;

  @Override
  public void startDelimitedText(
      final BlockDelimiter blockDelimiter, 
      final Token startToken 
  ) {
    blockDelimiterSupervisor.startDelimitedText( blockDelimiter, startToken ) ;
  }

  @Override
  public void reachEndDelimiter( final BlockDelimiter blockDelimiter ) {
    blockDelimiterSupervisor.reachEndDelimiter( blockDelimiter ) ;
  }

  @Override
  public void endDelimitedText( final BlockDelimiter blockDelimiter ) {
    blockDelimiterSupervisor.endDelimitedText( blockDelimiter ) ;
  }

  @Override
  public void reportMissingDelimiter(
      final BlockDelimiter blockDelimiter,
      final MismatchedTokenException mismatchedTokenException
  )
      throws MismatchedTokenException
  {
    blockDelimiterSupervisor.reportMissingDelimiter( blockDelimiter, mismatchedTokenException ) ;
  }

  @Override
  public void enterBlockDelimiterBoundary( final Token location ) {
    blockDelimiterSupervisor.enterBlockDelimiterBoundary( location ) ;
  }

  @Override
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

  @Override
  public void setTokenNames( final String[] tokenNames ) {
    this.tokenNames = tokenNames ;
  }

  private TreeAdaptor adaptor = null ;

  @Override
  public void setAdaptor( final TreeAdaptor adaptor ) {
    this.adaptor = adaptor ;
  }


// =================  
// TokenNameProvider
// =================  
  
  @Override
  public String getTokenName( final int imaginaryTokenIndex ) {
    return tokenNames[ imaginaryTokenIndex ] ;
  }


// ========================  
// Enhanced error reporting
// ========================  

  @Override
  public void report( final RecognitionException exception ) {
    problems.add( ParsingProblems.createProblem( locationFactory, exception, tokenNames ) ) ;
  }

  
  
// =============
// Tree creation
// =============

  @Override
  public Location createLocation( final Token token ) {
    return locationFactory.createLocation(
        token.getLine(),
        token.getCharPositionInLine()
    ) ;
  }

  @Override
  public Tree createTree( final int imaginaryTokenIdentifier, final String tokenPayload ) {
    return new CustomTree(
        new CommonToken( imaginaryTokenIdentifier ),
        getLocationFactory().createLocation(),
        tokenPayload
    ) ;
  }

  @Override
  public Tree createTree( final String tokenPayload ) {
    return new CustomTree(
        new CommonToken( -1, tokenPayload ),
        getLocationFactory().createLocation()
    ) ;
  }

  @Override
  public Tree createTree(
      final int imaginaryTokenIdentifier,
      final Location location,
      final String tokenPayload
  ) {
    return new CustomTree(
        new CommonToken( imaginaryTokenIdentifier ),
        location,
        tokenPayload
    ) ;
  }

  @Override
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

  @Override
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

  @Override
  public Object createTree(
      final int imaginaryTokenIdentifier,
      final Location location,
      final String payload,
      final Object... trees
  ) {
//    final CustomTree customTree = new CustomTree( );

    final Object root_1 = createTree( imaginaryTokenIdentifier, location, payload ) ;
    for( final Object tree : trees ) {
      if( tree != null ) {
        adaptor.addChild( root_1, tree ) ;
      }
    }

    return root_1 ;
  }

  @Override
  public Object createRoot( final int imaginaryTokenIdentifier, final Location location ) {
    final CustomTree customTree = ( CustomTree ) adaptor.create(
        imaginaryTokenIdentifier,
        tokenNames[ imaginaryTokenIdentifier ]
    ) ;
    customTree.setLocation( location ) ;
    return customTree ;
  }


}

