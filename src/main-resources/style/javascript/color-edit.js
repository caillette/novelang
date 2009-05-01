/**
 *
 */

function initializeColorEdit() {
  $( "#sortable > dt" ).each( function() {
    $( this ).css( "background-color", $( "strong", this ).text() ) ;
    $( this ).css( "color", $( "em", this).text() ) ;
    $( this ).css( "border", "solid 1px " + $( "em", this ).text() ) ;
    $( this ).css( "padding", "4px 2px 1px 2px" ) ;
  } ) ;

  $( "#sortable > dt > em" ).each( function() {
    $( this ).hide() ;
  } ) ;
}



// Extract colors from the DOM.
function extractColors() {
var colors = "" ;
$( "#sortable > dt" ).each( function() {
  var clone = $( this ).clone().removeAttr( "style" ) ;
  $( "em", clone ).removeAttr( "style" ) ;
  colors += "<dt>" + $( clone ).html() + "</dt>\n" ;
} ) ;
return colors ;
}


