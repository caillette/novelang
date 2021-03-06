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

const SPEED = "fast" ;
const ACTIVE_BACKGROUND = "#c0c0c0" ;
const INACTIVE_BACKGROUND = "#e4e4e4" ;

function initializeTabs() {

  $( ".descriptor-disclosure" ).click( function() {

    var descriptor = $( this ).parent() ;
    var container = descriptor.parent() ;
    var collapsibleDescriptor = descriptor.find( ".collapsable-descriptor" ) ;

    var visible = ! collapsibleDescriptor.is( ":visible" ) ;

    if( visible ) {
      container.animate( {
          borderLeftStyle : "solid",
          borderLeftColor : ACTIVE_BACKGROUND
      }, SPEED ) ;
      descriptor.animate( {
          paddingLeft : "0px" ,
          backgroundColor : ACTIVE_BACKGROUND,
          marginTop : "3px"
      }, SPEED ) ;
    } else {
      container.animate( {
          borderLeftStyle : "none",
          borderLeftColor : INACTIVE_BACKGROUND
      }, SPEED ) ;
      descriptor.animate( {
          paddingLeft : "-5px" ,
          backgroundColor : INACTIVE_BACKGROUND,
          marginTop : "0px"
      }, SPEED ) ;

    }
    collapsibleDescriptor.slideToggle( SPEED ) ;

    return false ;
  } ) ;


  var tabs = $( "#navigation" ).tabs( {
      selected : -1,
      collapsible : true,
      fx : { opacity : "toggle", duration : 100 }
  } ) ;
  // Forces some kid of refresh that doesn't occur if 0 is default.
  tabs.tabs( "select", 0 ) ;



  var identifierListWidth = $( "#identifier-list" ).width() ;
  var tagListWidth = $( "#tag-list" ).width() ;
  var navigationWidth = $( "#navigation" ).width() ;

  // Force width of navigation area.
  // This enables smarter resizing where navigation area keeps its full width
  // while rendered document area shrinks.
  if( identifierListWidth > tagListWidth ) {
    $( "#tag-list" ).width( identifierListWidth ) ;
  }

  // Add extra width because of margins and paddings for the sidebar.
  var maxWidth = Math.max( identifierListWidth, tagListWidth, navigationWidth ) ;
  $( "#right-sidebar" ).width( maxWidth + 10 ) ;
  $( "#navigation" ).width( maxWidth ) ;


} ;

var pinnedNavigation = true ;

function togglePinNavigation() {
  pinnedNavigation = ! pinnedNavigation ;
  if( pinnedNavigation ) {
    $( "#pin-navigation > ul > li:last" ).text( "unpin" ) ;
  } else {
    $( "#pin-navigation > ul > li:last" ).text( "pin" ) ;
  }
  $( "#navigation" ).toggleClass( "fixed-position" ) ;
}

function resetNavigation() {

}


function spiceUpSpacesInPre() {
  $( "pre" ).each( function() {
    var text = $( this ).text() ;
    text = text.replace( /\x20/g, "\u00a0\u200b" ) ;
    $( this ).text( text ) ;

  } ) ;
}