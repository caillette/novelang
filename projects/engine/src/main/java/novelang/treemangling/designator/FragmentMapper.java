package novelang.treemangling.designator;

import java.util.Map;

import novelang.designator.FragmentIdentifier;

/**
 * Needed by tests.
 * 
 * @author Laurent Caillette
 */
public interface FragmentMapper< T > {

  Map< FragmentIdentifier, T > getPureIdentifierMap() ;

  Map< FragmentIdentifier, T > getDerivedIdentifierMap() ;
}
