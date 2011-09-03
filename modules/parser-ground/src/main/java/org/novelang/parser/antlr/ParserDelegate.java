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

import java.util.List;

import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;

import org.novelang.common.Location;
import org.novelang.common.Problem;
import org.novelang.parser.antlr.delimited.BlockDelimiter;

/**
 * @author Laurent Caillette
 */
public interface ParserDelegate {
  String unescapeCharacter( String escaped, int line, int column );

  void traceIn( String s, int ruleIndex );

  void traceOut( String s, int ruleIndex );

  void startDelimitedText(
      BlockDelimiter blockDelimiter,
      Token startToken
  );

  void reachEndDelimiter( BlockDelimiter blockDelimiter );

  void endDelimitedText( BlockDelimiter blockDelimiter );

  void reportMissingDelimiter(
      BlockDelimiter blockDelimiter,
      MismatchedTokenException mismatchedTokenException
  )
      throws MismatchedTokenException;

  void enterBlockDelimiterBoundary( Token location );

  Iterable<Problem> leaveBlockDelimiterBoundary();

  void setTokenNames( String[] tokenNames );

  void setAdaptor( TreeAdaptor adaptor );

  String getTokenName( int imaginaryTokenIndex );

  void report( RecognitionException exception );

  Location createLocation( Token token );

  Tree createTree( int imaginaryTokenIdentifier, Location location, String tokenPayload );
  Tree createTree( int imaginaryTokenIdentifier, String tokenPayload );

  Tree createTree( String tokenPayload );

  Object createTree(
          int imaginaryTokenIdentifier,
          Location location,
          List list_p
      );

  Object createTree(
          int imaginaryTokenIdentifier,
          Location location,
          Object... trees
      );

  Object createTree(
          int imaginaryTokenIdentifier,
          Location location,
          String payload,
          Object... trees
      );

  Object createRoot( int imaginaryTokenIdentifier, Location location );
}
