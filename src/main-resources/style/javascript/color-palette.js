
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

// Those arrays contain color names, as sorted in colors.xhtml .
var BACKGROUND_COLORS = [] ;
var FOREGROUND_COLORS = [] ;


// Loads the content of colors.xhtml into the two color arrays and create CSS classes.
// Uses a temporary div.
// This function is supposed to be called only once.
function setupColors( tags, colorDefinitions ) {
  if( colorDefinitions == undefined ) {
    colorDefinitions = "colors.xhtml" ;
  }
  $( "#externalColorDefinitionsPlaceholder" ).load(
      colorDefinitions + " #editableColorDefinitions > *",
      {},
      function() {
        $( "#externalColorDefinitionsPlaceholder > dt > strong" ).each( function() {
          BACKGROUND_COLORS.push( $( this ).text() ) ;
        } ) ;
        $( "#externalColorDefinitionsPlaceholder > dt > em" ).each( function() {
          FOREGROUND_COLORS.push( $( this ).text() ) ;
        } ) ;
//        showMessage(
//            "Loaded " + BACKGROUND_COLORS.length + " colors " +
//            "(starting with " + BACKGROUND_COLORS[ 0 ] + ")."
//        ) ;
        $( "#externalColorDefinitionsPlaceholder" ).remove() ;

        for( var tagIndex = 0 ; tagIndex < tags.length ; tagIndex++ ) {
          $.rule(
              "." + "Tag-" + tags[ tagIndex ] + "{ " +
              "border: solid 1px " + " " + FOREGROUND_COLORS[ tagIndex ] + " ; " +
              "color: " + FOREGROUND_COLORS[ tagIndex ] + " ; " +
              "background-color: " + BACKGROUND_COLORS[ tagIndex ] + " ; " +
              "}"
          ).appendTo( "style" ) ;
//          showMessage( "Created CSS rule for tag " + tags[ tagIndex ] ) ;
        }
      }
  ) ;
}

