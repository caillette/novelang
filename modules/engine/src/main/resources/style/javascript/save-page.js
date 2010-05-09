/**
 * Methods and declarations for making a page save itself after some changes.
 * The part to be saved in between marker comments.
 * This has only been tested on Firefox.
 */


/* Start of code copied from TiddlyWiki

TiddlyWiki created by Jeremy Ruston, (jeremy [at] osmosoft [dot] com)

Copyright (c) UnaMesa Association 2004-2009

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or other
materials provided with the distribution.

Neither the name of the UnaMesa Association nor the names of its contributors may be
used to endorse or promote products derived from this software without specific
prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.
*/

// Returns array with start and end index of chunk between given start and end marker, or undefined.
String.prototype.getChunkRange = function( start, end ) {
  var s = this.indexOf( start ) ;
  if( s != -1 ) {
    s += start.length ;
    var e = this.indexOf( end, s ) ;
    if( e != -1 ) return [ s, e ] ;
  }
} ;

// Replace a chunk of a string given start and end markers
String.prototype.replaceChunk = function( start, end, sub ) {
  var r = this.getChunkRange( start, end ) ;
  return r ? this.substring( 0, r[ 0 ] ) + sub + this.substring( r[ 1 ] ) : this ;
} ;

// Returns a chunk of a string between start and end markers, or undefined
String.prototype.getChunk = function( start, end ) {
  var r = this.getChunkRange( start, end ) ;
  if( r ) return this.substring( r[ 0 ], r[ 1 ] ) ;
} ;


// Split up into two so that indexOf() of this source doesn't find it.
var START_SAVE_AREA = "<" + "!-- BEGIN-MODIFIABLE-ZONE -->" ;
var END_SAVE_AREA = "<" + "!-- END-MODIFIABLE-ZONE -->" ;


function locateStoreArea( original ) {
  // Locate the storeArea div's
  var posOpeningDiv = original.indexOf( START_SAVE_AREA ) ;
  var posClosingDiv = original.indexOf( END_SAVE_AREA ) ;
  if( null == posOpeningDiv || null == posClosingDiv ) {
    alert( "Could not locate storage area" ) ;
    return null ;
  }
  return [ posOpeningDiv,posClosingDiv ] ;
}


// Returns null if it can't do it, false if there's an error, or a string of the content if successful
function mozillaLoadFile( filePath ) {
  if( window.Components ) {
    try {
      netscape.security.PrivilegeManager.enablePrivilege( "UniversalXPConnect" ) ;
      var file = Components.classes[ "@mozilla.org/file/local;1" ].createInstance(
          Components.interfaces.nsILocalFile ) ;
      file.initWithPath( filePath ) ;
      if( ! file.exists() ) return null ;
      var inputStream = Components.classes[ "@mozilla.org/network/file-input-stream;1" ].
          createInstance( Components.interfaces.nsIFileInputStream ) ;
      inputStream.init( file, 0x01, 00004, null ) ;
      var sInputStream = Components.classes[ "@mozilla.org/scriptableinputstream;1" ].
          createInstance( Components.interfaces.nsIScriptableInputStream ) ;
      sInputStream.init( inputStream ) ;
      var contents = sInputStream.read( sInputStream.available() ) ;
      sInputStream.close() ;
      inputStream.close() ;
      return contents ;
    } catch( ex ) {
      return false ;
    }
  }
  return null ;
}

// Returns null if it can't do it, false if there's an error, or a string of the content if successful
function ieLoadFile( filePath ) {
  try {
    var fso = new ActiveXObject( "Scripting.FileSystemObject" ) ;
    var file = fso.OpenTextFile( filePath, 1 ) ;
    var content = file.ReadAll() ;
    file.Close() ;
  } catch( ex ) {
    return null ;
  }
  return content ;
}

// Returns null if it can't do it, false if there's an error, true if it saved OK
function mozillaSaveFile( filePath, content ) {
  if( window.Components ) {
    showMessage( "Attempting to save with Mozilla..." ) ;
    try {
      netscape.security.PrivilegeManager.enablePrivilege( "UniversalXPConnect" ) ;
      var file = Components.classes[ "@mozilla.org/file/local;1" ].
          createInstance( Components.interfaces.nsILocalFile ) ;
      file.initWithPath( filePath ) ;
      if( ! file.exists() ) file.create( 0, 0664 ) ;
      var out = Components.classes[ "@mozilla.org/network/file-output-stream;1" ].
          createInstance( Components.interfaces.nsIFileOutputStream ) ;
      out.init( file, 0x20 | 0x02, 00004, null ) ;
      out.write( content, content.length ) ;
      out.flush() ;
      out.close() ;
      return true ;
    } catch( ex ) {
      return false ;
    }
  }
  return null ;
}

// Returns null if it can't do it, false if there's an error, true if it saved OK
function ieSaveFile( filePath, content )
{
  ieCreatePath( filePath ) ;
  try {
    var fso = new ActiveXObject( "Scripting.FileSystemObject" ) ;
  } catch( ex ) {
    return null ;
  }
  var file = fso.OpenTextFile( filePath, 2, -1, 0 ) ;
  file.Write( content ) ;
  file.Close() ;
  return true ;
}

function convertUriToUTF8( uri, charSet )
{
  if( window.netscape == undefined || charSet == undefined || charSet == "" ) return uri ;
  try {
    netscape.security.PrivilegeManager.enablePrivilege( "UniversalXPConnect" ) ;
    var converter = Components.classes[ "@mozilla.org/intl/utf8converterservice;1" ].
        getService( Components.interfaces.nsIUTF8ConverterService ) ;
  } catch( ex ) {
    return uri ;
  }
  return converter.convertURISpecToUTF8( uri, charSet ) ;
}

function javaUrlToFilename( url ) {
  var f = "//localhost" ;
  if( url.indexOf(f) == 0 ) return url.substring( f.length ) ;
  var i = url.indexOf( ":" ) ;
  return i > 0 ? url.substring( i - 1 ) : url ;
}


function javaLoadFile( filePath ) {
  try {
    if( document.applets[ "TiddlySaver" ] ) {
      return String( document.applets[ "TiddlySaver" ].loadFile(
          javaUrlToFilename( filePath ), "UTF-8" ) ) ;
    }
  } catch( ex ) { }
  var content = [] ;
  try {
    var r = new java.io.BufferedReader( new java.io.FileReader( javaUrlToFilename( filePath ) ) ) ;
    var line ;
    while( ( line = r.readLine() ) != null ) content.push( new String( line ) ) ;
    r.close() ;
  } catch( ex ) {
    return null;
  }
  return content.join( "\n" ) ;
}

function javaSaveFile( filePath, content ) {
  try {
    if( document.applets[ "TiddlySaver" ] ) {
      return document.applets[ "TiddlySaver" ].
          saveFile( javaUrlToFilename( filePath ), "UTF-8", content ) ;
    }
  } catch( ex ) { }
  try {
    var s = new java.io.PrintStream(
        new java.io.FileOutputStream( javaUrlToFilename( filePath ) ) ) ;
    s.print( content ) ;
    s.close() ;
  } catch( ex ) {
    return null ;
  }
  return true ;
}


function loadFile( fileUrl ) {
  var r = mozillaLoadFile( fileUrl ) ;
  if( ( r == null ) || ! r ) r = ieLoadFile( fileUrl ) ;
  if( ( r == null ) || ( ! r ) ) r = javaLoadFile( fileUrl ) ;
  return r ;
}



function getLocalPath( origPath ) {
  var originalPath = convertUriToUTF8( origPath, "UTF-8" ) ;
  // Remove any location or query part of the URL
  var argPos = originalPath.indexOf( "?" ) ;
  if( argPos != -1 ) originalPath = originalPath.substr( 0, argPos ) ;
  var hashPos = originalPath.indexOf( "#" ) ;
  if( hashPos != -1 ) originalPath = originalPath.substr( 0, hashPos ) ;
  // Convert file://localhost/ to file:///
  if( originalPath.indexOf( "file://localhost/" ) == 0 )
      originalPath = "file://" + originalPath.substr( 16 ) ;
  // Convert to a native file format
  var localPath ;
  if( originalPath.charAt( 9 ) == ":" ) // pc local file
      localPath = unescape( originalPath.substr( 8 ) ).replace( new RegExp( "/", "g" ), "\\" ) ;
  else if( originalPath.indexOf("file://///") == 0 ) // FireFox pc network file
    localPath = "\\\\" + unescape( originalPath.substr( 10 ) ).
        replace( new RegExp( "/", "g" ), "\\" ) ;
  else if( originalPath.indexOf( "file:///" ) == 0 ) // mac/unix local file
      localPath = unescape( originalPath.substr( 7 ) ) ;
  else if( originalPath.indexOf( "file:/" ) == 0 ) // mac/unix local file
      localPath = unescape( originalPath.substr( 5 ) ) ;
  else // pc network file
    localPath = "\\\\" + unescape( originalPath.substr( 7 ) ).
        replace( new RegExp( "/", "g" ), "\\" ) ;
  return localPath ;
}


function saveFile( fileUrl, content ) {
  var r = mozillaSaveFile( fileUrl, content ) ;
  if( !r )
    r = ieSaveFile( fileUrl, content ) ;
  if( !r )
    r = javaSaveFile( fileUrl, content ) ;
  return r ;
}

function loadOriginal( localPath ) {
  return loadFile( localPath ) ;
}


function updateOriginal( original, localPath ) {
  var posDiv = locateStoreArea( original ) ;
  if( !posDiv ) {
    alert( "Could not locate store area for " + localPath ) ;
    return null ;
  }
  var revised = original.substr( 0, posDiv[ 0 ] ) + "\n" ;
  revised += START_SAVE_AREA + "\n" + extractColors() + "\n" + END_SAVE_AREA + "\n" ;
  revised +=  original.substr( posDiv[ 1 ] + END_SAVE_AREA.length ) ;
  return revised ;
}


function saveMain( localPath, original ) {
  var save ;
  try {
    var revised = updateOriginal( original, localPath ) ;
    save = saveFile( localPath, revised ) ;
  } catch ( ex ) {
    alert( ex ) ;
  }
  if( save ) {
    showMessage(  "Saved to file://" + localPath ) ;
  } else {
    alert( "Save failed." ) ;
  }
}


function saveChanges() {
  var originalPath = document.location.toString() ;
  if( originalPath.substr( 0, 5 ) != "file:" ) {
    alert( "Cannot save page if it is not a file" ) ;
    return ;
  }
  var localPath = getLocalPath( originalPath ) ;
  var original = loadOriginal( localPath ) ;
  if( original == null ) {
    alert(
        "It's not possible to save changes. Possible reasons include:" +
        "\n- Your browser doesn't support saving " +
        "(Firefox, Internet Explorer, Safari and Opera all work if properly configured)." +
        "\n- The pathname to the document contains illegal characters." +
        "\n- The document file has been moved or renamed."
    ) ;
    return ;
  }
  saveMain( localPath, original ) ;

}

/* End of code copied from TiddlyWiki */
