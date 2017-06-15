package edu.ucsf.rbvi.polylayout.internal.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class PolyLayoutTaskFactory extends AbstractNetworkTaskFactory implements TaskFactory
{
	final CyServiceRegistrar reg;
	
	public PolyLayoutTaskFactory(final CyServiceRegistrar reg) {
		super();
		this.reg = reg;
	}
	
	@Override
	public boolean isReady() {
		System.out.println("isReady");
		// (network == null)
		//	return false;
		return true;
	}
	@Override
	public boolean isReady(CyNetwork network) {
		System.out.println("isReady");
		// (network == null)
		//	return false;
		return true;
	}

	@Override
	public TaskIterator createTaskIterator() {
		PolyLayoutTask newTask = new PolyLayoutTask(reg, null);
		return new TaskIterator(newTask);
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		PolyLayoutTask newTask = new PolyLayoutTask(reg, network);
		return new TaskIterator(newTask);
	}
}