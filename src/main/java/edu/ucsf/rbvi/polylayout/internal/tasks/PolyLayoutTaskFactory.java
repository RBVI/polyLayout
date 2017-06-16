package edu.ucsf.rbvi.polylayout.internal.tasks;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class PolyLayoutTaskFactory extends AbstractNetworkTaskFactory implements TaskFactory
{
	final CyServiceRegistrar reg;
	final CyApplicationManager man;
	
	public PolyLayoutTaskFactory(final CyServiceRegistrar reg) {
		super();
		this.reg = reg;
		this.man = reg.getService(CyApplicationManager.class);
	}
	
	@Override
	public boolean isReady() {
		if(man.getCurrentNetwork() == null)
			return false;
		return true;
	}
	
	@Override
	public boolean isReady(CyNetwork network) {
		if (network == null)
			return false;
		return true;
	}

	@Override
	public TaskIterator createTaskIterator() {
		PolyLayoutTunableAndCountingTask newTask = new PolyLayoutTunableAndCountingTask(reg, man.getCurrentNetwork());
		return new TaskIterator(newTask);
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		PolyLayoutTunableAndCountingTask newTask = new PolyLayoutTunableAndCountingTask(reg, network);
		return new TaskIterator(newTask);
	}
}