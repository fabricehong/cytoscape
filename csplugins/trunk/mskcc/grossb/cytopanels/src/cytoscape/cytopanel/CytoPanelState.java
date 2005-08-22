// our package
package cytoscape.cytopanel;

/**
 *  CytoPanelState Class.  The following States are supported:
 * <UL>
 * <LI>CytoPanelState.HIDE:  Hide the CytoPanel.
 * <LI>CytoPanelState.FLOAT: Float the CytoPanel.
 * <LI>CytoPanelState.DOCK:  Dock the CytoPanel.
 * </UL>
 *
 * @author Ben Gross
 */
public final class CytoPanelState {

	/*
	 * The state name.
	 */
	private final String name;

	/*
	 * Private constructor.
	 */
	private CytoPanelState(String name){
		this.name = name;
	}

    /**
     * Hide state of a CytoPanel.
     */
	public static final CytoPanelState HIDE = new CytoPanelState("hide");

    /**
     * Float state of a CytoPanel.
     */
	public static final CytoPanelState FLOAT = new CytoPanelState("float");

    /**
     * Dock state of a CytoPanel.
     */
	public static final CytoPanelState DOCK = new CytoPanelState("dock");
}
