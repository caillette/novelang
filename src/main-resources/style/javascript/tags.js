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


var TAGS = [] ; // All declared tags.
var TAGS_REGEX = /(?:\?|&)tags=([0-9a-zA-Z\-_;]+)/ ;

function initializeTagSystem( colorDefinitions ) {
  // Gather all declared tags.
  $( "#tag-definitions > dt" ).each( function() {
    TAGS.push( $( this ).text() ) ;
  } ) ;

  $( "ul.tags > li " ).each( function() {
    $( this ).addClass( "Tag-" + $( this ).text() ) ;
//    showMessage( "Added class " + $( this ).text() ) ;
  } ) ;

  var uri = document.baseURI ;
  var activeTagsMatcher = uri.match( TAGS_REGEX ) ;
  var activeTags =
      activeTagsMatcher && activeTagsMatcher.length == 2 ?
      activeTagsMatcher[ 1 ].split( ";" ) :
      []
  ;

  // Create the combo boxes.
  for( var i in TAGS ) {
    var tag = TAGS[ i ] ;
    $( "#tag-list" ).append(
        "<input " +
            "type='checkbox' " +
            "name='" + tag + "' " +
            "onclick='checkTag() ; ' " +
        ">" +
        "<span class='Tag-" + tag + "' >" + tag + "</span>" +
        "<br/>"
    ) ;
  }
  $( "#tag-list" ).wrapInner( "<div id='tag-list-content' ></div>" ) ;
  $( "#tag-list" ).prepend( "<p id='tag-list-disclosure' >+</p>" ) ;
  $( "#tag-list-disclosure" ).click( function() {
    $( "#tag-list-content" ).slideToggle( 100 ) ;
  } ) ;

  if( activeTags.length == 0 ) {
    $( "#tag-list-content" ).hide() ;
  } else {
    // Check the combo boxes.
    for( var j in activeTags ) {
      $( "input[name='" + activeTags[ j ] + "']" ).attr( "checked", true ) ;
    }

  }



  setupColors( TAGS, colorDefinitions ) ;
}

function showMessage( message ) {
  $( "p#messages" ).append( "<pre>" + message.toString() + "</pre>" ) ;
}

function getClassName( obj ) {
  if( obj === null ) return undefined ;
  var s = obj.toString() ;
  var arr = s.match( /\[object\ (\w+)\]/ ) ;
  return arr && arr.length == 2 ? arr[ 1 ] : undefined ;


}


function checkTag() {
//  alert( "Updating tag visibility..." ) ;
  window.status = "Updating tag visibility..." ;
  var checked = [] ;
  var newUri = location.href.replace( TAGS_REGEX, "" ) ;
  ( $( "#tag-list :checked" ).each( function() {
    checked.push( $( this ).attr( "name" ) ) ;
  } ) ) ;
  if( checked.length > 0 ) {
    if( newUri.indexOf( "?" ) > -1 ) {
      newUri += "&" ;
    } else {
      newUri += "?" ;
    }
    newUri += "tags=" ;
    var first = true ;
    for( var i = 0 ; i < checked.length ; i++ ) {
      if( first ) {
        first = false ;
      } else {
        newUri += ";" ;
      }
      newUri += checked[ i ] ;
    }
  }
  location.href = newUri ;


}
