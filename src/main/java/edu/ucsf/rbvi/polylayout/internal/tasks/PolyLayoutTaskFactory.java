package edu.ucsf.rbvi.polylayout.internal.tasks;

import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class PolyLayoutTaskFactory extends AbstractNetworkViewTaskFactory
{
	final CyServiceRegistrar reg;
	
	public PolyLayoutTaskFactory(final CyServiceRegistrar reg) {
		super();
		this.reg = reg;
	}
	
	@Override
	public boolean isReady(CyNetworkView networkView) {
		if (networkView == null)
			return false;
		return true;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView) {
		PolyLayoutTunableAndCountingTask newTask = new PolyLayoutTunableAndCountingTask(reg, networkView);
		return new TaskIterator(newTask);
	}
}
