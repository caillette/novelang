package novelang.treemangling.designator;

import novelang.common.SyntacticTree;
import novelang.common.SimpleTree;
import novelang.common.tree.Treepath;
import novelang.common.tree.TreepathTools;
import novelang.marker.FragmentIdentifier;
import novelang.parser.NodeKind;
import novelang.parser.NodeKindTools;
import static novelang.parser.NodeKind.*;

import java.util.Map;
import java.util.Arrays;

/**
 * Enriches a {@link Treepath} with {@link novelang.parser.NodeKind#_IMPLICIT_IDENTIFIER} and
 * {@link novelang.parser.NodeKind#_IMPLICIT_IDENTIFIER} nodes.
 * 
 * @see novelang.treemangling.DesignatorInterpreter which does most of the job. 
 *
 * @author Laurent Caillette
 */
public class DesignatorMangler {
  
  private DesignatorMangler() { }


}
