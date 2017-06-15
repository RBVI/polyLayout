package edu.ucsf.rbvi.polylayout.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.undo.UndoSupport;

import edu.ucsf.rbvi.polylayout.internal.tasks.Coordinates;

public class PolyLayoutAlgorithmTask extends AbstractLayoutTask {
	private CyNetwork network;
	private Set<View<CyNode>> nodeViews;
	private Map<Object, Double> sizeMap;
	private Map<Object, List<View<CyNode>>> nodeMap;
	private int polySidesQuantity;
	private ArrayList<Object> categories;
	
	public PolyLayoutAlgorithmTask(String displayName, CyNetworkView networkView, Set<View<CyNode>> nodeViews,
			String layoutAttribute, UndoSupport undo) {
		super(displayName, networkView, nodeViews, layoutAttribute, undo);	
		this.nodeViews = nodeViews;
		this.network = networkView.getModel();
	}

	@Override
	protected void doLayout(TaskMonitor taskMonitor) {
		if(sizeMap == null || nodeMap == null || polySidesQuantity == 0)
			return;
		else {
			Map<Integer, Coordinates> nodeLocations = new HashMap<Integer, Coordinates>();
			if(polySidesQuantity != 2)
				return;
			else {
				/*for()
				{
					
				}*/
			}
		}
	}
}