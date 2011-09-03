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

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for {@link org.novelang.outfit.xml.SaxPipeline}.
 *
 * @author Laurent Caillette
 */
public class SaxPipelineTest {

  @Test
  public void singleStage() throws SAXException {
    final Counter lastStage = new Counter( "end" ) ;
    final SaxPipeline pipeline = new SaxPipeline( lastStage ) ;
    activatePipeline( pipeline ) ;
    verifyCallCount( pipeline, lastStage, 1 ) ;
  }

  @Test
  public void threeStages() throws SAXException {
    final Counter lastStage = new Counter( "end" ) ;
    final SaxPipeline pipeline = new SaxPipeline( lastStage ) ;
    final Counter counter0 = addNewCounterAsFork( pipeline, 0 );
    final Counter counter1 = addNewCounterAsFork( pipeline, 1 );

    activatePipeline( pipeline ) ;

    verifyCallCount( pipeline, counter0, 1 ) ;
    verifyCallCount( pipeline, counter1, 1 ) ;
    verifyCallCount( pipeline, lastStage, 1 ) ;
  }

  @Test
  public void threeStagesWithReplace() throws SAXException {
    final Counter lastStage = new Counter( "end")  ;
    final SaxPipeline pipeline = new SaxPipeline( lastStage ) ;
    final Counter counter0 = addNewCounterAsFork( pipeline, 0 ) ;
    final Counter counter1 = addNewCounterAsFork( pipeline, 1 ) ;
    final Counter counter1bis = new Counter( "1bis" ) ;

    activatePipeline( pipeline ) ;
    pipeline.replace( 1, new SaxPipeline.ForkingStage( counter1bis ) ) ;
    activatePipeline( pipeline ) ;

    verifyCallCount( pipeline, counter0, 2 ) ;
    verifyCallCount( pipeline, counter1, 1 ) ;
    verifyCallCount( pipeline, counter1bis, 1 ) ;
    verifyCallCount( pipeline, lastStage, 2 ) ;
  }

// =======
// Fixture
// =======

  private static Counter addNewCounterAsFork( final SaxPipeline pipeline, final int position ) {
    return addNewCounterAsFork( pipeline, position, "" + position ) ;
  }

  private static Counter addNewCounterAsFork(
      final SaxPipeline pipeline,
      final int position,
      final String name
  ) {
    final Counter counter = new Counter( name ) ;
    pipeline.add( new SaxPipeline.ForkingStage( counter ), position ) ;
    return counter ;
  }

  private static void activatePipeline( final SaxPipeline pipeline ) throws SAXException {
    pipeline.startDocument() ;
    pipeline.startElement( "", "", "", null ) ;
    pipeline.endElement( "", "", "" ) ;
    pipeline.endDocument() ;
  }

  private static void verifyCallCount(
      final SaxPipeline pipeline, 
      final Counter counter,
      final int callCount
  ) {
    final String pipelineAsString = asString( pipeline );
    assertThat( counter.startDocumentCallCount ).describedAs( pipelineAsString )
        .isEqualTo( callCount ) ;
    assertThat( counter.startElementCallCount ).describedAs( pipelineAsString )
        .isEqualTo( callCount ) ;
    assertThat( counter.endElementCallCount ).describedAs( pipelineAsString )
        .isEqualTo( callCount ) ;
    assertThat( counter.endDocumentCallCount ).describedAs( pipelineAsString )
        .isEqualTo( callCount ) ;
  }

  private static String asString( final SaxPipeline pipeline ) {
    final StringBuilder stringBuilder = new StringBuilder( SaxPipeline.class.getSimpleName() ) ;
    stringBuilder.append( "{" ) ;
    for( int i = 0 ; i < pipeline.getContentHandlerCount() ; i ++ ) {
      final ContentHandler contentHandler = pipeline.getContentHandlerAt( i ) ;
      stringBuilder.append( i == 0 ? "" : ", " ) ;
      stringBuilder.append( i ).append( ":" ) ;
      if( contentHandler instanceof SaxPipeline.ForkingStage ) {
        final ContentHandler fork = ( ( SaxPipeline.ForkingStage ) contentHandler ).getFork() ;
        stringBuilder.append( fork ) ;
      } else {
        stringBuilder.append( contentHandler ) ;
      }
    }
    stringBuilder.append( "}" ) ;
    return stringBuilder.toString() ;
  }

  private static class Counter extends ContentHandlerAdapter {

    private final String name ;

    private Counter( final String name ) {
      this.name = name ;
    }

    private int startDocumentCallCount = 0 ;
    private int endDocumentCallCount = 0 ;
    private int startElementCallCount = 0 ;
    private int endElementCallCount = 0 ;

    @Override
    public String toString() {
      return getClass().getSimpleName() + "{" + name + ", "
          + startDocumentCallCount // Don't need everything.
          + "}"
      ;
    }

    @Override
    public void startDocument() {
      startDocumentCallCount ++ ;
    }

    @Override
    public void startElement(
        final String uri,
        final String localName,
        final String qName,
        final Attributes attributes
    ) {
      startElementCallCount ++ ;
    }

    @Override
    public void endElement( final String uri, final String localName, final String qName ) {
      endElementCallCount ++ ;
    }

    @Override
    public void endDocument() {
      endDocumentCallCount ++ ;
    }


  }




}
