package edu.ucsf.rbvi.polylayout.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PolyLayoutTaskFactory extends AbstractNetworkTaskFactory 
{
	final CyServiceRegistrar reg;
	
	public PolyLayoutTaskFactory(final CyServiceRegistrar reg) {
		this.reg = reg;
	}
	
	public boolean isReady(CyNetwork network) {
		if(network == null)
			return false;
		return true;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		PolyLayoutTask newTask = new PolyLayoutTask(reg, network);
		return new TaskIterator(newTask);
	}
}