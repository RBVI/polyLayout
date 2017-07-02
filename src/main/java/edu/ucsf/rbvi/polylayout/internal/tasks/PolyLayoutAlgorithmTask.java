package edu.ucsf.rbvi.polylayout.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

import edu.ucsf.rbvi.polylayout.internal.model.PolyLayoutAlgorithm;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;

public class PolyLayoutAlgorithmTask extends AbstractLayoutTask 
{
	private final CyServiceRegistrar reg;
	private final CyNetwork network;
	private CyNetworkViewManager nVM;
	private final PolyLayoutContext context;
	private final String categoryColumn;
	private final Set<View<CyNode>> nodesToLayout;

	public PolyLayoutAlgorithmTask(final CyServiceRegistrar reg, final String displayName,
	                               final PolyLayoutContext context,
	                               final CyNetworkView view, Set<View<CyNode>> nodesToLayOut,
																 final String categoryColumn, UndoSupport undo)
	{
		super(displayName, view, nodesToLayOut, categoryColumn, undo);
		this.reg = reg;
		this.network = view.getModel();
		this.context = context;
		this.categoryColumn = categoryColumn;
		this.nodesToLayout = nodesToLayOut;
	}

	@Override
	public void doLayout(TaskMonitor taskMonitor) {
		if (categoryColumn == null || categoryColumn.equals("(none)")) {
			taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Must select a column for categories");
			return;
		}
		String columnName = categoryColumn;
		double spacing = context.spacing;
		String sortColumn = null;
		if (context.sortColumn != null && !context.sortColumn.getSelectedValue().equals("--None--"))
			sortColumn = context.sortColumn.getSelectedValue();
		
		Map<Object, List<View<CyNode>>> nodeMap = new HashMap<Object,List<View<CyNode>>>();
		
		for (View<CyNode> nv: nodesToLayout) {
			CyNode node = nv.getModel();
			
			Object cat = network.getRow(node).getRaw(columnName);
			if(!nodeMap.containsKey(cat)) 
				nodeMap.put(cat, new ArrayList<View<CyNode>>());
			nodeMap.get(cat).add(nv);
		}
		
		PolyLayoutAlgorithm.doLayout(nodeMap, networkView, spacing, sortColumn);
	}	
}
