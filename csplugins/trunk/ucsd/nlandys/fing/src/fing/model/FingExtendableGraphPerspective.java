package fing.model;

import cytoscape.util.intr.IntIterator;

/**
 * Please try to restrain from using this class.  This class was created so
 * that certain legacy applications would have an easier time using this
 * giny.model implementation.
 * @deprecated Use FingRootGraphFactory instead.
 * @see FingRootGraphFactory
 **/
public class FingExtendableGraphPerspective extends FGraphPerspective
{

  /**
   * rootGraphNodeInx need not contain all endpoint nodes corresponding to
   * edges in rootGraphEdgeInx - this is calculated automatically by this
   * constructor.  If any index does not correspond to an existing node or
   * edge, an IllegalArgumentException is thrown.  The indices lists need not
   * be non-repeating - the logic in this constructor handles duplicate
   * filtering.
   **/
  public FingExtendableGraphPerspective(FingExtendableRootGraph root,
                                        IntIterator rootGraphNodeInx,
                                        IntIterator rootGraphEdgeInx)
  {
    super(root, rootGraphNodeInx, rootGraphEdgeInx);
  }

}
