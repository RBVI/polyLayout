package edu.ucsf.rbvi.polylayout.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.polylayout.internal.model.PolyLayoutAlgorithm;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;

public class PolyLayoutContext implements SetCurrentNetworkListener
{
	// @Tunable (description= "Category column: ")
	// public ListSingleSelection<String> category = null; 

	@Tunable (description= "Spacing: ")
	public Double spacing = 50.0; 

	@Tunable (description= "Sort column: ")
	public ListSingleSelection<String> sortColumn = null; 

	private final CyServiceRegistrar reg;

	public PolyLayoutContext(final CyServiceRegistrar reg)
	{
		this.reg = reg;
		reg.registerService(this, SetCurrentNetworkListener.class, new Properties());
		CyNetwork network = reg.getService(CyApplicationManager.class).getCurrentNetwork();
		if (network != null)
			setColumnTunables(network);
	}

	private void setColumnTunables(CyNetwork network) {
		CyTable nodeTable = network.getDefaultNodeTable();
		Collection<CyColumn> columnsCollection = nodeTable.getColumns();
		List<String> columnNames = new ArrayList<String>();
		for(CyColumn column : columnsCollection)
		{
			String name = column.getName();
			if (name.equals(CyNetwork.SUID) ||
					name.equals(CyNetwork.NAME) ||
					name.equals(CyRootNetwork.SHARED_NAME) || 
					name.equals(CyNetwork.SELECTED))
				continue;
			columnNames.add(column.getName());
		}

		// Now sort the list
		Collections.sort(columnNames);
		columnNames.add(0, "--None--");
		sortColumn = new ListSingleSelection<String>(columnNames);

	}

	public void handleEvent(SetCurrentNetworkEvent ev) {
		setColumnTunables(ev.getNetwork());
	}
}