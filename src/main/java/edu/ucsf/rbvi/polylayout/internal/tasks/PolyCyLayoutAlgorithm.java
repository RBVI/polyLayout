package edu.ucsf.rbvi.polylayout.internal.tasks;

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class PolyCyLayoutAlgorithm extends AbstractLayoutAlgorithm
{
	final CyServiceRegistrar reg;
	final CyApplicationManager man;
	
	public PolyCyLayoutAlgorithm(final CyServiceRegistrar reg, UndoSupport undoSupport) {
		super("polylayout", "Layout n-partide graphs along regular polygons", undoSupport);
		this.reg = reg;
		this.man = reg.getService(CyApplicationManager.class);
	}

	@Override
	public Object createLayoutContext() {
		PolyLayoutContext context = new PolyLayoutContext(reg);
		return context;
	}
	
	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object layoutContext, 
	                                       Set<View<CyNode>> nodesToLayout, String layoutAttribute) {
		PolyLayoutAlgorithmTask newTask = new PolyLayoutAlgorithmTask(reg, "polylayout", (PolyLayoutContext)layoutContext, networkView,
		                                                              nodesToLayout, layoutAttribute, undoSupport);
		return new TaskIterator(newTask);
	}

	@Override
	public Set<Class<?>> getSupportedNodeAttributeTypes() {
		Set<Class<?>> supportedClasses = new HashSet<Class<?>>();
		supportedClasses.add(Double.class);
		supportedClasses.add(String.class);
		supportedClasses.add(Long.class);
		supportedClasses.add(Integer.class);
		supportedClasses.add(Boolean.class);
		return supportedClasses;
	}

	@Override
	public boolean getSupportsSelectedOnly() { return true; }
	
}
