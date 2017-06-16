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
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.util.ListSingleSelection;

import edu.ucsf.rbvi.polylayout.internal.model.PolyLayoutAlgorithm;

import org.cytoscape.work.Tunable;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;

public class PolyLayoutTunableAndCountingTask extends AbstractTask 
{
	@Tunable (description= "Choose Column: ")
	public ListSingleSelection<String> columnChoices = null; 
	@Tunable (description= "Choose spacing: ")
	public int spacing = 10; 

	private final CyServiceRegistrar reg;
	private final CyNetworkView networkView;
	private final CyNetwork network;

	public PolyLayoutTunableAndCountingTask(final CyServiceRegistrar reg, final CyNetworkView networkView)
	{
		this.reg = reg;
		this.networkView = networkView;
		if (networkView == null) {
			columnChoices = new ListSingleSelection<String>();
			this.network = null;
			return;
		}
		this.network = networkView.getModel();

		CyTable nodeTable = network.getDefaultNodeTable();
		Collection<CyColumn> columnsCollection = nodeTable.getColumns();
		ArrayList<String> columnsNames = new ArrayList<String>();
		for(CyColumn column : columnsCollection)
		{
			String name = column.getName();
			if (name.equals(CyNetwork.SUID) ||
					name.equals(CyNetwork.NAME) ||
					name.equals(CyNetwork.SELECTED) ||
					name.equals(CyRootNetwork.SHARED_NAME))
				continue;
			columnsNames.add(column.getName());
		}
		columnChoices = new ListSingleSelection<String>(columnsNames);//tunable
	}

	@Override
	public void run(TaskMonitor arg0) {
		String columnName = columnChoices.getSelectedValue();
		
		ArrayList<Object> categories = new ArrayList<Object>();
	
		Map<Object, List<View<CyNode>>> nodeMap = new HashMap<Object,List<View<CyNode>>>();
		Map<Object, Double> sizeMap = new HashMap<Object, Double>();
		
		for (View<CyNode> nv: networkView.getNodeViews()) {
			CyNode node = nv.getModel();
			double size = nv.getVisualProperty(BasicVisualLexicon.NODE_SIZE);
			
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
		
		PolyLayoutAlgorithm.doLayout(sizeMap, nodeMap, reg, networkView, spacing);
	}	
}
