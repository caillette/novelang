package novelang.treemangling.designator;

import novelang.common.SyntacticTree;
import novelang.common.tree.Treepath;

/**
 * Enriches a {@link Treepath} with with {@link novelang.parser.NodeKind#IMPLICIT_IDENTIFIER} and
   *  {@link novelang.parser.NodeKind#IMPLICIT_IDENTIFIER} nodes.
 *
 * @author Laurent Caillette
 */
public class EnricherWithDesignator
{
  private EnricherWithDesignator() { }

  public static Treepath< SyntacticTree > enrich(
      final Treepath< SyntacticTree > treepath,
      final FragmentMapper mapper
  ) {
    return treepath ;
  }


}
