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

package novelang.model.implementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ListMultimap;
import novelang.model.common.IdentifierHelper;
import novelang.model.common.Location;
import novelang.model.common.LocationFactory;
import novelang.model.common.NodeKind;
import novelang.model.common.Problem;
import novelang.model.common.Tree;
import novelang.model.common.Treepath;
import static novelang.model.common.NodeKind.SECTION;
import novelang.model.renderable.Renderable;
import novelang.parser.Encoding;
import novelang.parser.PartParser;
import novelang.parser.PartParserFactory;
import novelang.parser.antlr.DefaultPartParserFactory;

/**
 * A Part loads a Tree, building a table of identifiers for subnodes
 * and a list of encountered Problems.
 *
 * @author Laurent Caillette
 */
public class Part implements LocationFactory, Renderable
{

  private static final Logger LOGGER = LoggerFactory.getLogger( Part.class ) ;

  private final List< Problem > problems = Lists.newArrayList() ;
  private final Tree tree ;
  private final String thisToString ;
  private final String locationName ;
  private final Charset encoding ;
  private final Multimap< String, Tree > identifiers ;


  public Part( String content ) {
    this.thisToString = ClassUtils.getShortClassName( Part.class ) +
        "@" + System.identityHashCode( this ) ;
    this.locationName = "<String>" ;
    this.encoding = Objects.nonNull( Encoding.DEFAULT ) ;
    tree = parse( new DefaultPartParserFactory(), content ) ;
    identifiers = findIdentifiers() ;
  }

  public Part( final File partFile ) {
    this(
        partFile,
        Encoding.DEFAULT,
        "part[" + partFile.getName() + "]"
    ) ;
  }

  protected Part(
      File partFile,
      Charset encoding,
      String thisToString
  ) {
    this.thisToString = thisToString + "@" + System.identityHashCode( this ) ;
    this.locationName = partFile.getName() ;
    this.encoding = Objects.nonNull( encoding ) ;
    tree = parse( new DefaultPartParserFactory(), readContent( partFile, encoding ) ) ;
    identifiers = findIdentifiers() ;
  }


// ==============
// Content access
// ==============

  private String readContent( File partFile, Charset encoding ) {

    LOGGER.info( "Attempting to load file '{}' from {}", partFile.getAbsolutePath(), this ) ;

    try {
      final FileInputStream inputStream = new FileInputStream( partFile ) ;
      return IOUtils.toString( inputStream, encoding.name() ) ;

    } catch( IOException e ) {
      LOGGER.warn( "Could not load file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
      return null ;
    }
  }

  private Tree parse( PartParserFactory partParserFactory, String content ) {

    if( null == content ) {
      return null ;
    }

    final PartParser parser = partParserFactory.createParser( this, content ) ;
    Tree tree = null ;
    try {

      // Yeah we do it here!
      tree = parser.parse() ;
      if( null != tree ) {
        tree = Hierarchizer.rehierarchize( Treepath.create( tree ) ).getBottom() ;
      }

      for( final Problem problem : parser.getProblems() ) {
        collect( problem ) ;
      }

    } catch( RecognitionException e ) {
      LOGGER.warn( "Could not parse file", e ) ;
      collect( Problem.createProblem( this, e ) ) ;
    }

    return tree ;
  }


// ===========
// Identifiers
// ===========

  /**
   * Finds Section identifiers from inside the {@link #getTree() tree}.
   * At the first glance it seems better to do it from the grammar but
   * I'm not sure on how to concatenate tokens.
   * If I find how to do I should just add a Multimap member to the grammar file and
   * get it after parsing.
   */
  private Multimap< String, Tree > findIdentifiers() {

    final Multimap< String, Tree > identifiedSectionTrees = Multimaps.newHashMultimap() ;

    if( null == tree ) {
      return Multimaps.immutableMultimap() ;  
    }

    for( final Tree sectionCandidate : tree.getChildren() ) {
      if( SECTION.name().equals( sectionCandidate.getText() ) ) {
        for( final Tree identifierCandidate : sectionCandidate.getChildren() ) {
          if( NodeKind.IDENTIFIER.name().equals( identifierCandidate.getText() ) ) {
            final String identifier = IdentifierHelper.createIdentifier( identifierCandidate ) ;
            identifiedSectionTrees.put( identifier, sectionCandidate ) ;
            LOGGER.debug( "Recognized Section identifier '{}' inside {}", identifier, this ) ;
          }
        }
      }
    }

    final ListMultimap< String, Tree > identifiersFound =
        Multimaps.newArrayListMultimap( identifiedSectionTrees ) ;
    return Multimaps.unmodifiableListMultimap( identifiersFound ) ;
  }

  public Multimap< String, Tree > getIdentifiers() {
    return identifiers ;
  }


// ========
// Problems
// ========

  public Iterable< Problem > getProblems() {
    return Lists.immutableList( problems ) ;
  }

  public boolean hasProblem() {
    return ! problems.isEmpty() ;
  }

  protected final void collect( Problem problem ) {
    LOGGER.debug( "Collecting Problem: " + problem ) ;
    problems.add( Objects.nonNull( problem ) ) ;
  }


// ====
// Misc
// ====

  @Override
  public String toString() {
    return thisToString;
  }

  /**
   * Returns result of parsing, may be null if it failed.
   * @return a possibly null object.
   */
  public Tree getTree() {
    return tree ;
  }

  public Location createLocation( int line, int column ) {
    return new Location( locationName, line, column ) ;
  }

  public Charset getEncoding() {
    return encoding ;
  }


}
