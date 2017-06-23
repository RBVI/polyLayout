package edu.ucsf.rbvi.polylayout.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class PolyLayoutTunableAndCountingTask extends AbstractNetworkTask 
{
	@Tunable (description= "Choose Column: ")
	public ListSingleSelection<String> columnChoices = null; 
	@Tunable (description= "Choose spacing: ")
	public Double spacing = 50.0; 

	private final CyServiceRegistrar reg;
	private final CyNetwork network;
	private CyNetworkViewManager nVM;

	public PolyLayoutTunableAndCountingTask(final CyServiceRegistrar reg, final CyNetwork network)
	{
		super(network);
		this.reg = reg;
		this.network = network;
		this.nVM = reg.getService(CyNetworkViewManager.class);

		CyTable nodeTable = network.getDefaultNodeTable();
		Collection<CyColumn> columnsCollection = nodeTable.getColumns();
		ArrayList<String> columnsNames = new ArrayList<String>();
		for(CyColumn column : columnsCollection)
		{
			String name = column.getName();
			if (name.equals(CyNetwork.SUID) ||
					name.equals(CyNetwork.NAME) ||
					name.equals(CyRootNetwork.SHARED_NAME) || 
					name.equals(CyNetwork.SELECTED))
				continue;
			columnsNames.add(column.getName());
		}
		columnChoices = new ListSingleSelection<String>(columnsNames);//tunable
	}

	@Override
	public void run(TaskMonitor arg0) {
		String columnName = columnChoices.getSelectedValue();
		
		// Get the networkView from CyNetworkViewManager
		
		CyNetworkView networkV = null;
		for (CyNetworkView v :  nVM.getNetworkViews(network) ) {
			networkV = v;
			break;
		}
			
		Map<Object, List<View<CyNode>>> nodeMap = new HashMap<Object,List<View<CyNode>>>();
		Map<Object, Double> sizeMap = new HashMap<Object, Double>();
		
		for (View<CyNode> nv: networkV.getNodeViews()) {
			CyNode node = nv.getModel();
			double size = nv.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
			
			Object cat = network.getRow(node).getRaw(columnName);
			if (sizeMap.containsKey(cat)) {
				sizeMap.put(cat, size + sizeMap.get(cat) + spacing); 
			}
			else {
				sizeMap.put(cat, size);
				nodeMap.put(cat, new ArrayList<View<CyNode>>());
			}
			nodeMap.get(cat).add(nv);
		}
		
		PolyLayoutAlgorithm.doLayout(sizeMap, nodeMap, reg, networkV, spacing);
	}	
}
