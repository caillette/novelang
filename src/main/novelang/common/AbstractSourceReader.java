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
package novelang.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.net.URL;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.common.Location;
import novelang.common.LocationFactory;
import novelang.common.Problem;
import novelang.common.SyntacticTree;
import novelang.common.Renderable;
import novelang.common.tree.TreeTools;
import novelang.common.metadata.MetadataHelper;
import novelang.system.DefaultCharset;
import novelang.parser.GenericParser;
import novelang.parser.GenericParserFactory;

/**
 * Base class holding parsing and some error-handling.
 *
 * @author Laurent Caillette
 */
public abstract class AbstractSourceReader implements LocationFactory, Renderable {

  protected static final Logger LOGGER = LoggerFactory.getLogger( AbstractSourceReader.class ) ;
  private final String thisToString ;
  private final List< Problem > problems = Lists.newArrayList() ;
  protected final String locationName ;
  protected final Charset charset;

  public AbstractSourceReader() {
    this.thisToString = ClassUtils.getShortClassName( getClass() ) +
        "@" + System.identityHashCode( this ) ;
    this.locationName = "<String>" ;
    this.charset = Preconditions.checkNotNull( DefaultCharset.SOURCE ) ;
  }

  protected AbstractSourceReader(
      URL partUrl,
      Charset charset,
      String thisToString
  ) {
    this.thisToString = thisToString + "@" + System.identityHashCode( this ) ;
    this.locationName = partUrl.toExternalForm() ;
    this.charset = Preconditions.checkNotNull( charset ) ;
  }

  protected String readContent( URL partUrl, Charset charset ) {

    LOGGER.info(
        "Attempting to load file '{}' from {} with charset " + charset.name(),
        partUrl.toExternalForm(), 
        this
    ) ;

    try {
      final InputStream inputStream = partUrl.openStream() ;
      return IOUtils.toString( inputStream, charset.name() ) ;

    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
      return null ;
    }
  }

  protected SyntacticTree parse( GenericParserFactory parserFactory, String content ) {

    if( null == content ) {
      return null ;
    }

    final GenericParser parser = parserFactory.createParser( this, content ) ;
    SyntacticTree tree = null ;
    try {

      // Yeah we do it here!
      tree = parser.parse() ;

      for( final Problem problem : parser.getProblems() ) {
        collect( problem ) ;
      }

    } catch( RecognitionException e ) {
      LOGGER.warn( "Could not parse file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
    }

    return ( SyntacticTree ) tree ;
  }

  protected static SyntacticTree addMetadata( SyntacticTree tree ) {
    final SyntacticTree metadata = MetadataHelper.createMetadataDecoration( tree ) ;
    return TreeTools.addFirst( tree, metadata ) ;
  }

  public Iterable< Problem > getProblems() {
    return ImmutableList.copyOf( problems ) ;
  }

  public boolean hasProblem() {
    return ! problems.isEmpty() ;
  }

  protected final void collect( Problem problem ) {
    LOGGER.debug( "Collecting Problem: " + problem ) ;
    problems.add( Preconditions.checkNotNull( problem ) ) ;
  }

  protected final void collect( Iterable< Problem > problems ) {
    for( Problem problem : problems ) {
      collect( problem ) ;
    }
  }

  public Location createLocation( int line, int column ) {
    return new Location( locationName, line, column ) ;
  }

  public Charset getCharset() {
    return charset;
  }


  @Override
  public String toString() {
    return thisToString;
  }


}
