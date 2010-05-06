
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

var lastColorClicked ;

function initializeColorEdit() {
  $( "#editableColorDefinitions > dt" ).each( function() {
    var backgroundColorName = getBackground( this ) ;
    var foregroundColorName = getForeground( this ) ;
    var color = this ;
    setForeground( this, foregroundColorName ) ;
    $( this ).css( "background-color", backgroundColorName ) ;
    $( this ).css( "border", "solid 1px " + $( "em", this ).text() ) ;
    $( this ).css( "padding", "4px 2px 1px 2px" ) ;

    $( this ).click( function( event ) {
      if( event.altKey ) {
        if( lastColorClicked ) {
          setForeground( lastColorClicked, getBackground( color ) ) ;
        }
      } else {
        lastColorClicked = color ;
      }
    } ) ;

  } ) ;

  $( "#editableColorDefinitions > dt > em" ).each( function() {
    $( this ).hide() ;
  } ) ;
}

function getBackground( colorElement ) {
  return $( "strong", colorElement ).text() ;
}

function getForeground( colorElement ) {
  return $( "em", colorElement ).text() ;
}

function setForeground( colorElement, colorName ) {
  $( colorElement ).css( "color", colorName ) ;
  $( "em", colorElement ).text( colorName ) ;
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


