/*
 * Copyright (C) 2010 Laurent Caillette
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
package org.novelang.outfit.loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.apache.commons.lang.ClassUtils;

/**
 * Holds a resource name, guaranteeing the respect of a {@link #PATTERN}.
 * <ul>
 *   <li>No leading solidus.
 *   <li>No full stops, except for the extension.
 *   <li>Segments can be made of any letter, digit, hyphen minus, low line.
 * </ul>
 * 
 * @author Laurent Caillette
 */
public class ResourceName {

  private static final Logger LOGGER = LoggerFactory.getLogger( ResourceName.class );

  private final String name ;

  /**
   * Sequence of allowed letters.
   */
  private static final String LETTER = "(?:\\w|-|_)" ;

  /**
   * The regex pattern that a resource name must comply to.
   */
  public static final Pattern PATTERN = Pattern.compile(
      "(" +                               // Start of capturing group
          LETTER + "+" +                  // First segment.
          "(?:(?:\\/" + LETTER + "+)*)" + // Multiple segments with their heading solidus.
          "(?:\\." + LETTER + "+)" +      // Extension, full stop included.
      ")"                                 // End of capturing group.
  ) ;

  static {
    LOGGER.debug( "Crafter regex ", PATTERN.pattern() ) ;
  }
  /**
   * Constructor.
   *
   * @param name a non-null, non-empty String satisfying the {@link #PATTERN} pattern.
   * @throws IllegalArgumentException
   */
  public ResourceName( final String name ) throws IllegalArgumentException {
    final Matcher matcher = PATTERN.matcher( name ) ;
    if( matcher.matches() && 1 == matcher.groupCount() ) {
      this.name = matcher.group( 0 ) ;
    } else {
      throw new IllegalArgumentException(
          "Resource name '" + name + "' does not match pattern " + PATTERN.pattern() ) ;
    }
  }

  /**
   * Returns the resource name, less optional leading solidus if there was any.
   *
   * @return a non-null String satisfying the {@link #PATTERN}.
   */
  public String getName() {
    return name;
  }

  public String toString() {
    return ClassUtils.getShortClassName( this.getClass() ) + "[" + getName() + "]" ;
  }

  public int hashCode() {
    return name.hashCode() ;
  }

  public boolean equals( final Object o ) {
    return o instanceof ResourceName && name.equals( ( ( ResourceName ) o ).getName() ) ;
  }
}
