
/*
 * Copyright (C) 2009 Laurent Caillette
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

// Those arrays contain color names, as sorted in colors.html .
var BACKGROUND_COLORS = [] ;
var FOREGROUND_COLORS = [] ;


// Loads the content of colors.html into the two color arrays. Uses a temporary div.
// This function is supposed to be called only once.
function loadColors() {
  $( "#externalColorDefinitionsPlaceholder" ).load(
      "colors.html #editableColorDefinitions > *",
      {},
      function() {
        $( "#externalColorDefinitionsPlaceholder > dt > strong" ).each( function() {
          BACKGROUND_COLORS.push( $( this ).text() ) ;
        } ) ;
        $( "#externalColorDefinitionsPlaceholder > dt > em" ).each( function() {
          FOREGROUND_COLORS.push( $( this ).text() ) ;
        } ) ;
        showMessage(
            "Loaded " + BACKGROUND_COLORS.length + " colors " +
            "(starting with " + BACKGROUND_COLORS[ 0 ] + ")."
        ) ;
        $( "#externalColorDefinitionsPlaceholder" ).remove() ;
      }
  ) ;
}

// Create one CSS class per tag in the array with colors picked in the arrays.
// This function is supposed to be called only once.
function createTagClasses( tags ) {
  if( BACKGROUND_COLORS != null && FOREGROUND_COLORS != null ) {
    for( var tagIndex = 0 ; tagIndex < tags.length ; tagIndex++ ) {
    }
  }
}
