/*
 * Copyright (C) 2008 Laurent Caillette
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
package org.novelang.outfit.xml;

import java.io.IOException;
import java.io.StringWriter;

import com.google.common.collect.ImmutableList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Records and replays SAX events.
 * We don't use {@link org.dom4j.io.SAXEventRecorder} which discards location.
 * Location is useful for correct error reports when reparsing nested stylesheets.
 *
 * {@link org.dom4j.io.SAXEventRecorder} probably does a better job as it implements:
 * <ul>
 *   <li>{@link org.xml.sax.ext.DeclHandler},
 *   <li>{@link org.xml.sax.DTDHandler},
 *   <li>{@link org.xml.sax.EntityResolver},
 *   <li>{@link org.xml.sax.ErrorHandler},
 *   <li>{@link org.xml.sax.ext.LexicalHandler}.
 * </ul>
 * <p>
 * This class is not thread-safe.
 *
 * @author Laurent Caillette
 */
public final class SaxRecorder extends ContentHandlerAdapter {

  public interface Player {
    void playOn( final ContentHandler target ) throws SAXException ;
  }

  /**
   * Returns an immutable object able to replay captured events.
   * 
   * @return a non-null object.
   */
  public Player getPlayer() {
    final ImmutableList< Event > eventsToPlay = this.events.build() ;
    return new Player() {
      @Override
      public void playOn( final ContentHandler target ) throws SAXException {
        play( eventsToPlay, target ) ;
      }
    } ;
  }

  /**
   * Replays events.
   * <p>
   * Warning: this methods installs its own {@code Locator} in the target {@code ContentHandler}
   * and doesn't restore the previous one once done. This is because {@code ContentHandler}
   * doesn't expose something like a {@code getDocumentLocator} method.
   */
  private static void play(
      final ImmutableList< Event > events,
      final ContentHandler target
  ) throws SAXException {
    final InstrumentedLocator locator = new InstrumentedLocator() ;
    target.setDocumentLocator( locator ) ;
    for( final Event replayedEvent : events ) {
      locator.setLocationRecord( replayedEvent.locationRecord ) ;
      replayedEvent.replay( target ) ;
    }
  }

  public static String asXml( final Player player ) throws SAXException, IOException {
    final SAXContentHandler saxContentHandler = new SAXContentHandler() ;
    player.playOn( saxContentHandler ) ;
    final StringWriter stringWriter = new StringWriter() ;
    new XMLWriter( stringWriter , OutputFormat.createPrettyPrint() )
        .write( saxContentHandler.getDocument() ) ;
    return stringWriter.toString() ;
  }



// ==============
// ContentHandler
// ==============


  @Override
  public void startPrefixMapping( final String prefix, final String uri ) {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.startPrefixMapping( prefix, uri ) ;
      }
    } ) ;
  }

  @Override
  public void startDocument() {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.startDocument() ;
      }
    } ) ;
  }

  @Override
  public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
  ) {
    final Attributes attributesCopy = new ImmutableAttributes( attributes ) ;
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.startElement( uri, localName, qName, attributesCopy ) ;
      }
    } ) ;
  }

  @Override
  public void characters( final char[] chars, final int start, final int length ) {
    final char[] charactersCopy = chars.clone() ;
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.characters( charactersCopy, start, length ) ;
      }
    } ) ;
  }

  @Override
  public void ignorableWhitespace( final char[] chars, final int start, final int length ) {
    final char[] charactersCopy = chars.clone() ;
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.ignorableWhitespace( charactersCopy, start, length ) ;
      }
    } ) ;
  }

  @Override
  public void processingInstruction( final String piTarget, final String data ) {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.processingInstruction( piTarget, data ) ;
      }
    } ) ;
  }

  @Override
  public void skippedEntity( final String name ) {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.skippedEntity( name ) ;
      }
    } ) ;
  }

  @Override
  public void endElement(
      final String uri,
      final String localName,
      final String qName
  ) {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.endElement( uri, localName, qName ) ;
      }
    } ) ;
  }


  @Override
  public void endDocument() {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.endDocument() ;
      }
    } ) ;
  }

  @Override
  public void endPrefixMapping( final String prefix ) {
    add( new Event( getLocationRecord() ) {
      @Override
      public void replay( final ContentHandler target ) throws SAXException {
        target.endPrefixMapping( prefix ) ;
      }
    } ) ;
  }

// ================
// Recorder objects
// ================

  private LocationRecord getLocationRecord() {
    return LocationRecord.create( getDocumentLocator() ) ;
  }

  private final ImmutableList.Builder< Event > events = ImmutableList.builder() ;

  private void add( final Event event ) {
    events.add( event ) ;
  }

  private static abstract class Event {
    private final LocationRecord locationRecord ;

    protected Event( final LocationRecord locationRecord ) {
      this.locationRecord = checkNotNull( locationRecord ) ;
    }

    public abstract void replay( final ContentHandler target ) throws SAXException;
  }

  protected static final class LocationRecord {
    private final String publicId ;
    private final String systemId ;
    private final int lineNumber ;
    private final int columnNumber ;

    public LocationRecord(
        final int lineNumber,
        final int columnNumber
    ) {
      this( null, null, lineNumber, columnNumber ) ;
    }
    
    public LocationRecord(
        final String publicId,
        final String systemId,
        final int lineNumber,
        final int columnNumber
    ) {
      this.publicId = publicId ;
      this.systemId = systemId ;
      this.lineNumber = lineNumber ;
      this.columnNumber = columnNumber ;
    }

    public static final LocationRecord NULL = new LocationRecord( null, null, -1, -1 ) ;

    public static LocationRecord create( final Locator locator ) {
      return new LocationRecord(
          locator.getPublicId(),
          locator.getSystemId(),
          locator.getLineNumber(),
          locator.getColumnNumber()
      ) ;
    }

    @Override
    public boolean equals( final Object other ) {
      if( this == other ) {
        return true ;
      }
      if( other == null || getClass() != other.getClass() ) {
        return false ;
      }

      final LocationRecord that = ( LocationRecord ) other ;

      if( columnNumber != that.columnNumber ) {
        return false ;
      }
      if( lineNumber != that.lineNumber ) {
        return false ;
      }
      if( publicId != null ? !publicId.equals( that.publicId ) : that.publicId != null ) {
        return false ;
      }
      if( systemId != null ? !systemId.equals( that.systemId ) : that.systemId != null ) {
        return false ;
      }

      return true ;
    }

    @Override
    public int hashCode() {
      int result = publicId != null ? publicId.hashCode() : 0 ;
      result = 31 * result + ( systemId != null ? systemId.hashCode() : 0 ) ;
      result = 31 * result + lineNumber ;
      result = 31 * result + columnNumber ;
      return result ;
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "{"
          + ( publicId == null ? "" : "publicId=" + publicId + "; " )
          + ( systemId == null ? "" : "systemId=" + systemId + "; " )
          + "line=" + lineNumber + "; "
          + "column=" + columnNumber
          + "}"
      ;
    }
  }

  private static class InstrumentedLocator implements Locator {

    private LocationRecord locationRecord = LocationRecord.NULL ;

    public void setLocationRecord( final LocationRecord locationRecord ) {
      this.locationRecord = checkNotNull( locationRecord ) ;
    }

    @Override
    public String getPublicId() {
      return locationRecord.publicId ;
    }

    @Override
    public String getSystemId() {
      return locationRecord.systemId ;
    }

    @Override
    public int getLineNumber() {
      return locationRecord.lineNumber ;
    }

    @Override
    public int getColumnNumber() {
      return locationRecord.columnNumber ;
    }
  }

}
