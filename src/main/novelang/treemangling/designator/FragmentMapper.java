package novelang.treemangling.designator;

import java.util.Map;

import novelang.marker.FragmentIdentifier;

/**
 * @author Laurent Caillette
 */
public interface FragmentMapper< T > {

  Map< FragmentIdentifier, T > getPureIdentifierMap() ;

  Map< FragmentIdentifier, T > getDerivedIdentifierMap() ;
}
