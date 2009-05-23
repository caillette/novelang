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
  var activeTagsMatcher = uri.match( /(?:\?|&)tags=([0-9a-zA-Z\-_;]+)/ ) ;
  var activeTags = activeTagsMatcher && activeTagsMatcher.length == 2 ?
      activeTagsMatcher[ 1 ].split( ";" ) :
      []
  ;
  showMessage( "Got " + activeTags.length + " tags: " + activeTags ) ;

  // Create the combo boxes.
  for( var tagIndex in TAGS ) {
    var tag = TAGS[ tagIndex ] ;
    $( "#tag-list" ).append(
        "<input " +
            "type='checkbox' " +
            "name='" + tag + "' " +
            "onclick=\"checkTag() ; \"" +
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
    for( var tagIndex in activeTags ) {
      $( "input[name='" + activeTags[ tagIndex ] + "']" ).attr( "checked", true ) ;
    }

  }



  setupColors( TAGS, colorDefinitions ) ;
}

function showMessage( message ) {
  $( "p#messages" ).append( "<pre>" + message.toString() + "</pre>" ) ;
}

// Traverses the DOM (except elements which may not contain a tag scope) and hides tag scopes
// divs which have no tag in the tagset, or which contain no tag scope satisfying the same
// condition.
// Returns true if at least one children-contained tag scope has some wanted tag(s).
// Returns false if some children-contained tag scope was hidden, or when there were no tag
//   scopes in children.
function updateVisibility( domElement, tagset, indent ) {
  var resultingAction = false ;
  var containsTagsOfInterest = false ;

//  showMessage( indent + "updateVisibility: entering " + tagscopeAsString( domElement ) ) ;

  if( isDirectTagscopeContainer( domElement/*, indent*/ ) ) {
    containsTagsOfInterest = isElementTagged( domElement, tagset/*, indent*/ ) ;
    if( containsTagsOfInterest ) {
//      showMessage( indent + "updateVisibility: found tags of interest for " +
//           tagscopeAsString( domElement ) ) ;
      return true ;
    }
  }

  $( domElement ).children().each( function() {
    if ( isPossibleTagscopeContainer( this ) ) {
      resultingAction |= updateVisibility( this, tagset, indent + "  " ) ;
    }
  } ) ;

  if( isDirectTagscopeContainer( domElement/*, indent*/ ) ) {
    if ( ! resultingAction ) {
      $( domElement ).hide() ;
    }
  }

//  showMessage( indent + "updateVisibility: returning " + resultingAction ) ;
  return resultingAction ;

}

function getClassName( obj ) {
  if( obj === null ) return undefined ;
  var s = obj.toString() ;
  var arr = s.match( /\[object\ (\w+)\]/ ) ;
  return arr && arr.length == 2 ? arr[ 1 ] : undefined ;


}

function tagscopeAsString( domElement ) {
  var s = "" ;

  s += getClassName( domElement ) ;

  var headerName = $( ":header:first", domElement ).text() ;
  s += headerName == "" ? "" : " \"" + headerName + "\"" ;

  if( isDirectTagscopeContainer( domElement ) ) {
    s += " directTagscopeContainer" ;
  }

  return s ;
}

// Returns if the element has at least one tag in the tagset (which is an array).
function isElementTagged( domElement, tagset/*, indent*/ ) {
  function isTag( tag, tagset ) {
    for( var tagIndex in tagset ) {
      if( tagset[ tagIndex ] == tag ) return true ;
    }
    return false ;
  }
//  function showElement( element, returnValue ) {
//    showMessage(
//        indent + "Evaluating " + tagscopeAsString( element ) +
//        "against tagset " + tagset.toString() + " " +
//        "-> " + returnValue
//    ) ;
//  }
  // Take all li from first ul with .tags class.
  var list = $( "ul.tags", domElement ) ;
  if( list === null ||  $( list ).size() == 0 ) {
    return false ;
  }
  var tagElements = $( "li", $( list ).eq( 0 ) ).get() ;
  for( var i = 0 ; i < tagElements.length ; i++ ) {
    if( isTag( $( tagElements[ i ] ).text(), tagset ) ) {
      return true ;
    }
  }
  return false ;
}

// Useful for limiting traversal.
function isPossibleTagscopeContainer( domElement ) {
  return ! (
      $( domElement ).is( "ul" )
   || $( domElement ).is( "ol" )
   || $( domElement ).is( "p" )
   || $( domElement ).is( "pre" )
   || $( domElement ).is( "b" )
   || $( domElement ).is( "i" )
   || $( domElement ).is( "em" )
   || $( domElement ).is( "strong" )
   || $( domElement ).is( "code" )
   || $( domElement ).is( "tt" )
   || $( domElement ).is( "span" )
   || $( domElement ).is( "#messages" )
   || $( domElement ).is( "#demo" )
  ) ;
}

// Returns if the element is a div with the tag-scope class.
function isDirectTagscopeContainer( domElement/*, indent*/ ) {
  direct = $( domElement ).is( ".tag-scope" ) ;
//      showMessage(
//          indent + "isDirectTagscopeContainer( " + tagscopeAsString( domElement ) + " ) " +
//          "-> " + direct
//      ) ;
  return direct ;
}

function checkTag() {
//  alert( "Updating tag visibility..." ) ;
  window.status = "Updating tag visibility..." ;
  var checked = [] ;
  ( $( "#tag-list :checked" ).each( function() {
    checked.push( $( this ).attr( "name" ) ) ;
  } ) ) ;
  $( ".tag-scope" ).show() ;
  if( checked.length == 0 ) {
//        showMessage( "No tag selected; displaying everything." ) ;
  } else {
//        showMessage( "Some tags were selected: " + checked.toString() ) ;
    updateVisibility( $( "body" ).get(), checked, "" ) ;
  }
  window.status = "" ;
//  alert( "Done updating tag visibility" ) ;


}
