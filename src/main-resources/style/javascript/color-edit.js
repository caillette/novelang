
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

function initializeColorEdit() {
  $( "#editableColorDefinitions > dt" ).each( function() {
    $( this ).css( "background-color", $( "strong", this ).text() ) ;
    $( this ).css( "color", $( "em", this).text() ) ;
    $( this ).css( "border", "solid 1px " + $( "em", this ).text() ) ;
    $( this ).css( "padding", "4px 2px 1px 2px" ) ;
  } ) ;

  $( "#editableColorDefinitions > dt > em" ).each( function() {
    $( this ).hide() ;
  } ) ;
}



// Extract colors from the DOM.
function extractColors() {
var colors = "" ;
$( "#editableColorDefinitions > dt" ).each( function() {
  var clone = $( this ).clone().removeAttr( "style" ) ;
  $( "em", clone ).removeAttr( "style" ) ;
  colors += "<dt>" + $( clone ).html() + "</dt>\n" ;
} ) ;
return colors ;
}


