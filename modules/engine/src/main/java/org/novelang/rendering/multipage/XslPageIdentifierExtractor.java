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
package org.novelang.rendering.multipage;

import com.google.common.collect.ImmutableMap;
import org.dom4j.Document;
import org.novelang.common.SyntacticTree;

/**
 * Computes the pages of a document tree, using
 * previously-{@link XslMultipageStylesheetCapture captured}
 * stylesheet.
 *
 * @author Laurent Caillette
 */
public class XslPageIdentifierExtractor implements PageIdentifierExtractor {

  public XslPageIdentifierExtractor( final Document stylesheetDocument ) {
  }

  @Override
  public ImmutableMap< PageIdentifier, String > extractPageIdentifiers( final SyntacticTree documentTree ) {
    throw new UnsupportedOperationException( "TODO" ) ;
  }
}
