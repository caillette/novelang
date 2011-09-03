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

package org.novelang.treemangling.designator;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.novelang.common.Problem;
import org.novelang.common.SyntacticTree;
import org.novelang.common.tree.RobustPath;
import org.novelang.common.tree.Treepath;
import org.novelang.designator.FragmentIdentifier;

/**
 * This class creates maps from {@link FragmentIdentifier} to {@link Treepath} objects
 * through {@link RobustPath}.
 *
 * @see org.novelang.treemangling.DesignatorInterpreter
 *
 * @author Laurent Caillette
 */
public class BabyInterpreter implements FragmentMapper< RobustPath< SyntacticTree > > {

  /**
   * Contain only pure identifiers (defined explicitely).
   */
  private final Map< FragmentIdentifier, RobustPath< SyntacticTree > > pureIdentifiers ;

  /**
   * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
   */
  private final Map< FragmentIdentifier, RobustPath< SyntacticTree > > derivedIdentifiers ;


  private final List< Problem > problems ;

  public BabyInterpreter( final Treepath< SyntacticTree > treepath ) {
    final Collector collector = new Collector() ;
    process( collector, treepath ) ;
    pureIdentifiers = Collections.unmodifiableMap( collector.pureIdentifiers ) ;
    for( final FragmentIdentifier duplicate : collector.duplicateDerivedIdentifiers ) {
      collector.derivedIdentifiers.remove( duplicate ) ;
    }

    derivedIdentifiers = Collections.unmodifiableMap( collector.derivedIdentifiers ) ;
    problems = Collections.unmodifiableList( collector.problems ) ;

  }

  /**
   * Returns the map of pure identifiers (those made out only from
   * {@link org.novelang.parser.NodeKind#ABSOLUTE_IDENTIFIER} nodes).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  @Override
  public Map< FragmentIdentifier, RobustPath< SyntacticTree > > getPureIdentifierMap() {
    return pureIdentifiers ;
  }


  /**
   * Returns the map of derived identifiers (those which are not pure).
   *
   * @return a non-null, immutable map containing no nulls, with {@code Treepath} objects
   *     referencing the same tree as passed to the constructor.
   */
  @Override
  public Map< FragmentIdentifier, RobustPath< SyntacticTree > > getDerivedIdentifierMap() {
    return derivedIdentifiers ;
  }


  /**
   * Returns problems like duplicate identifier.
   */
  public Iterable< Problem > getProblems() {
    return problems ;
  }

  public boolean hasProblem() {
    return ! problems.isEmpty() ;
  }


  private void process(
      final Collector collector,
      final Treepath< SyntacticTree > treepath
  ) {
    final SyntacticTree tree = treepath.getTreeAtEnd() ;
    if( tree.isOneOf( DesignatorTools.IDENTIFIER_BEARING_NODEKINDS ) ) {
      final FragmentIdentifier explicitIdentifier ;
      final SegmentExtractor segmentExtractor = 
          new SegmentExtractor( treepath, collector.problems ) ;
      final IdentifierDefinition definition = segmentExtractor.getIdentifierDefinition() ;
      final String segment = segmentExtractor.getSegment() ;

      switch( definition ) {

        case NONE :
          explicitIdentifier = null ;
          break ;

        case ABSOLUTE :
          explicitIdentifier = new FragmentIdentifier( segment ) ;
          if( verifyFreshness( collector, tree, explicitIdentifier, collector.pureIdentifiers ) ) {
            collector.pureIdentifiers.put(
                explicitIdentifier,
                RobustPath.create( treepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
            ) ;
          }
          break ;

        case IMPLICIT :
          if( ! StringUtils.isBlank( segment ) ) {
            explicitIdentifier = null ;
            final FragmentIdentifier implicitAbsoluteIdentifier = new FragmentIdentifier( segment ) ;
            if( collector.derivedIdentifiers.containsKey( implicitAbsoluteIdentifier ) ) {
              collector.duplicateDerivedIdentifiers.add( implicitAbsoluteIdentifier ) ;
            } else {
              collector.derivedIdentifiers.put(
                  implicitAbsoluteIdentifier,
                  RobustPath.create( treepath, DesignatorTools.IDENTIFIER_TREE_FILTER )
              ) ;
            }
          }

          break ;

        default :
          throw new IllegalArgumentException( "Unsupported: " + definition ) ;
      }

      for( int i = 0 ; i < tree.getChildCount() ; i ++ ) {
        process(
            collector,
            Treepath.create( treepath, i )
        ) ;
      }

    }
  }

  private boolean verifyFreshness(
      final Collector collector,
      final SyntacticTree tree,
      final FragmentIdentifier fragmentIdentifier,
      final Map< FragmentIdentifier, RobustPath< SyntacticTree > > map
  ) {
    if( map.containsKey( fragmentIdentifier ) ) {
      final String message = "Already defined: '" + fragmentIdentifier + "'" ;
      addProblem( collector, tree, message ) ;
      return false ;
    }
    return true ;
  }

  private void addProblem( 
      final Collector collector, 
      final SyntacticTree tree, 
      final String message 
  ) {
    if( tree.getLocation()  == null ) {
      // Only for tests.
      collector.problems.add( Problem.createProblem( message ) ) ;
    } else {
      collector.problems.add( Problem.createProblem(
          message,
          tree.getLocation() )
      ) ;
    }
  }

  
  private static class Collector {
    
    /**
     * Contain only pure identifiers (defined explicitely).
     */
    public final Map< FragmentIdentifier, RobustPath< SyntacticTree > >
    pureIdentifiers = Maps.newHashMap() ;

    /**
     * Contain implicit identifiers, or implicit identifiers mixed with explicit identifiers.
     */
    public final Map< FragmentIdentifier, RobustPath< SyntacticTree > >
    derivedIdentifiers = Maps.newHashMap() ;


    public final Set< FragmentIdentifier > duplicateDerivedIdentifiers = Sets.newHashSet() ;

    public final List< Problem > problems = Lists.newArrayList() ;
  }
}