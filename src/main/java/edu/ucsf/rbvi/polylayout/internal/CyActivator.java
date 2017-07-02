package edu.ucsf.rbvi.polylayout.internal;

import java.util.Properties;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;

import edu.ucsf.rbvi.polylayout.internal.tasks.PolyCyLayoutAlgorithm;

public class CyActivator extends AbstractCyActivator {
	
	public CyActivator() {
		super();
	}
	
//	@Overrides
	public void start(BundleContext context) throws Exception {
		CyServiceRegistrar sr = getService(context, CyServiceRegistrar.class);	
		UndoSupport undoSupport = getService(context, UndoSupport.class);	

		PolyCyLayoutAlgorithm polyAlgorithm = new PolyCyLayoutAlgorithm(sr, undoSupport); 
		Properties algLayoutProps = new Properties();
		registerService(context, polyAlgorithm, CyLayoutAlgorithm.class, algLayoutProps);
	}
}
