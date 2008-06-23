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
package novelang.reader;

import java.io.File;
import java.io.FileInputStream;
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
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import novelang.model.renderable.Renderable;
import novelang.parser.Encoding;
import novelang.parser.GenericParser;
import novelang.parser.GenericParserFactory;

/**
 * @author Laurent Caillette
 */
public abstract class AbstractSourceReader implements LocationFactory, Renderable {

  protected static final Logger LOGGER = LoggerFactory.getLogger( AbstractSourceReader.class ) ;
  private final String thisToString ;
  private final List< Problem > problems = Lists.newArrayList() ;
  protected final String locationName ;
  protected final Charset encoding ;

  public AbstractSourceReader() {
    this.thisToString = ClassUtils.getShortClassName( getClass() ) +
        "@" + System.identityHashCode( this ) ;
    this.locationName = "<String>" ;
    this.encoding = Objects.nonNull( Encoding.DEFAULT ) ;
  }

  protected AbstractSourceReader(
      URL partUrl,
      Charset encoding,
      String thisToString
  ) {
    this.thisToString = thisToString + "@" + System.identityHashCode( this ) ;
    this.locationName = partUrl.toExternalForm() ;
    this.encoding = Objects.nonNull( encoding ) ;
  }

  protected String readContent( URL partUrl, Charset encoding ) {

    LOGGER.info( "Attempting to load file '{}' from {}", partUrl.toExternalForm(), this ) ;

    try {
      final InputStream inputStream = partUrl.openStream() ;
      return IOUtils.toString( inputStream, encoding.name() ) ;

    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
      return null ;
    }
  }

  protected Tree parse( GenericParserFactory parserFactory, String content ) {

    if( null == content ) {
      return null ;
    }

    final GenericParser parser = parserFactory.createParser( this, content ) ;
    Tree tree = null ;
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

    return tree ;
  }

  public Iterable< Problem > getProblems() {
    return ImmutableList.copyOf( problems ) ;
  }

  public boolean hasProblem() {
    return ! problems.isEmpty() ;
  }

  protected final void collect( Problem problem ) {
    LOGGER.debug( "Collecting Problem: " + problem ) ;
    problems.add( Objects.nonNull( problem ) ) ;
  }

  protected final void collect( Iterable< Problem > problems ) {
    for( Problem problem : problems ) {
      collect( problem ) ;
    }
  }

  public Location createLocation( int line, int column ) {
    return new Location( locationName, line, column ) ;
  }

  public Charset getEncoding() {
    return encoding ;
  }


  @Override
  public String toString() {
    return thisToString;
  }

  
}
