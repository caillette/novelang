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
package org.novelang.produce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.novelang.common.Renderable;
import org.novelang.common.SyntacticTree;
import org.novelang.common.metadata.Page;
import org.novelang.common.metadata.PageIdentifier;
import org.novelang.logger.Logger;
import org.novelang.logger.LoggerFactory;
import org.novelang.rendering.multipage.PagesExtractor;

/**
 * Dispatches one or more {@link Page}s into {@code OutputStream}s, and feeds them with rendered
 * content.
 * This class is an abstraction over two different behaviors: single stream for
 * {@link org.novelang.daemon.DocumentHandler}, and multiple files corresponding to multiple pages
 * in case of a multipage document for {@link org.novelang.batch.DocumentGenerator}.
 * The purpose is to insert this specific logic inside the
 * {@link org.novelang.rendering.GenericRenderer} which creates the
 * {@link org.novelang.rendering.multipage.PagesExtractor} (as a
 * {@link org.novelang.rendering.FragmentWriter}) just before the rendering occurs.
 *
 * @author Laurent Caillette
 */
public abstract class StreamDirector {

  private static final Logger LOGGER = LoggerFactory.getLogger( StreamDirector.class ) ;

  public void feedStreams(
      final Renderable renderable,
      PagesExtractor pageIdentifierExtractor,
      final PageIdentifier pageIdentifier,
      final StreamFeeder streamFeeder
  ) throws Exception {

    // Hack. TODO: remove this.
    pageIdentifierExtractor = new PagesExtractor() {
      @Override
      public ImmutableMap<PageIdentifier, String> extractPages( final SyntacticTree documentTree )
          throws Exception
      {
        return ImmutableMap.of(
            new PageIdentifier( "Level-0" ), "/opus/level[1]",
            new PageIdentifier( "Level-1" ), "/opus/level[2]"
        ) ;
      }
    } ;
    

    if( pageIdentifierExtractor == null ) {
      feedDefaultPage( renderable, streamFeeder ) ;
    } else {
      final ImmutableMap< PageIdentifier,String > pageMap =
          pageIdentifierExtractor.extractPages( renderable.getDocumentTree() ) ;
      if( pageMap.isEmpty() ) {
        feedDefaultPage( renderable, streamFeeder ) ;
      } else {
        if( pageIdentifier == null ) {
          feedDefaultPage( renderable, streamFeeder ) ;
          if( supportsMultipage() ) {
            LOGGER.debug( "Feeding additional pages: ", pageMap ) ;
            for( final PageIdentifier someIdentifier : pageMap.keySet() ) {
              feedPage( renderable, streamFeeder, Page.get( pageMap, someIdentifier ) );
            }
          }
        } else {
          final Page page = Page.get( pageMap, pageIdentifier ) ;
          feedPage( renderable, streamFeeder, page ) ;
        }
      }
    }
  }

  private void feedDefaultPage(
      final Renderable renderable,
      final StreamFeeder streamFeeder
  ) throws Exception {
    feedPage( renderable, streamFeeder, null ) ;
  }

  private void feedPage(
      final Renderable renderable,
      final StreamFeeder streamFeeder,
      final Page page
 ) throws Exception {
    final OutputStream outputStream =
        getOutputStream( page == null ? null : page.getPageIdentifier() ) ;
    try {
      streamFeeder.feed( renderable, outputStream, page ) ;
    } finally {
      finishWith( outputStream ) ;
    }
  }

  protected abstract OutputStream getOutputStream( final PageIdentifier pageIdentifier )
      throws IOException ;

  protected abstract void finishWith( final OutputStream outputStream ) throws IOException ;

  protected abstract boolean supportsMultipage() ;

  /**
   * Wires code from {@link org.novelang.produce.DocumentProducer} at the point it only deals
   * with {@code OutputStream}.
   */
  public interface StreamFeeder {
    void feed( Renderable rendered, OutputStream outputStream, Page page ) throws Exception ;
  }


  public static StreamDirector forExistingStream( final OutputStream outputStream ) {

    return new StreamDirector() {
      @Override
      protected OutputStream getOutputStream( final PageIdentifier pageIdentifier ) {
        return outputStream ;
      }

      @Override
      protected void finishWith( final OutputStream leftUntouched ) { }

      @Override
      protected boolean supportsMultipage() {
        return false ;
      }
    } ;
  }

  public static StreamDirector forDirectory(
      final DocumentRequest documentRequest, 
      final File directory
  ) {
    Preconditions.checkArgument( directory.isDirectory() ) ;

    return new StreamDirector() {
      @Override
      protected OutputStream getOutputStream( final PageIdentifier pageIdentifier )
          throws IOException
      {

        final String relativeFileName = documentRequest.getDocumentSourceName()
            + ( pageIdentifier == null ? ""
                : DocumentRequest.PAGEIDENTIFIER_PREFIX + pageIdentifier.getName() )
            + "." + documentRequest.getRenditionMimeType().getFileExtension()
        ;
        final File outputFile =  new File( directory, relativeFileName ) ;
        FileUtils.forceMkdir( outputFile.getParentFile() );

        LOGGER.info( "Generating document file '", outputFile.getAbsolutePath(), "'..." ) ;
        return new FileOutputStream( outputFile ) ;
      }

      @Override
      protected void finishWith( final OutputStream outputStream ) throws IOException {
        outputStream.close() ;
      }

      @Override
      protected boolean supportsMultipage() {
        return true ;
      }
    } ;
  }

}