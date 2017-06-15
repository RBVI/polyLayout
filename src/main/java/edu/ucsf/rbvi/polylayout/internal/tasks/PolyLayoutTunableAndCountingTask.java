package edu.ucsf.rbvi.polylayout.internal.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.layout.AbstractLayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.work.Tunable;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetwork;

public class PolyLayoutTunableAndCountingTask extends AbstractNetworkTask 
{
	@Tunable (description= "Choose Column: ")
	public ListSingleSelection<String> columnChoices = null; 
	@Tunable (description= "Choose spacing: ")
	public int spacing = 10; 

	private final CyServiceRegistrar reg;
	private final CyNetwork network;

	public PolyLayoutTunableAndCountingTask(final CyServiceRegistrar reg, final CyNetwork network)
	{
		super(network);
		this.reg = reg;
		this.network = network;

		CyTable nodeTable = network.getDefaultNodeTable();
		Collection<CyColumn> columnsCollection = nodeTable.getColumns();
		ArrayList<String> columnsNames = new ArrayList<String>();
		for(CyColumn column : columnsCollection)
		{
			String name = column.getName();
			if (name.equals(CyNetwork.SUID) ||
					name.equals(CyNetwork.NAME) ||
					name.equals(CyRootNetwork.SHARED_NAME))
				continue;
			columnsNames.add(column.getName());
		}
		columnChoices = new ListSingleSelection<String>(columnsNames);//tunable
	}

	@Override
	public void run(TaskMonitor arg0) {
		String columnName = columnChoices.getSelectedValue();
		CyTable polyNodeTable = network.getDefaultNodeTable();
		CyColumn polyChosenColumn = polyNodeTable.getColumn(columnName);
		List<String> polyColumnAttributes = polyChosenColumn.getValues(String.class);
		ArrayList<String> polyDifferentAttributes = new ArrayList<String>();
		
		for(final String polyAttribute : polyColumnAttributes)//this for loop should make polyDifferentAttributes a list of different attributes from column polyChosenColumn
		{
			boolean unique = true;
			for(final String diffAttribute : polyDifferentAttributes)
				if(diffAttribute.equals(polyAttribute))
					unique = false;
			if(unique == true)
				polyDifferentAttributes.add(polyAttribute);
		}
		/*Another method: */
		
		int polyPartitionsNum = polyDifferentAttributes.size();
	}	
}