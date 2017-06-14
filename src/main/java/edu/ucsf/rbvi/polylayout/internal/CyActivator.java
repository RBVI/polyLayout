package org.cytoscape.myapp.poly_app.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.work.ServiceProperties;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableSetter;
import org.cytoscape.work.undo.UndoSupport;
import org.osgi.framework.BundleContext;
import edu.ucsf.rbvi.polyLayout.internal.tasks.PolyLayoutTaskFactory;

public class CyActivator extends AbstractCyActivator {
	
	public CyActivator() {
		super();
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		
		CyLayoutAlgorithmManager layoutManager = getService(context, CyLayoutAlgorithmManager.class);
		TunableSetter tunableSetter = getService(context, TunableSetter.class);		
		PolyLayoutTaskFactory polyTF = new PolyLayoutTaskFactory(layoutManager, tunableSetter); //TO DO
		
		UndoSupport undo = getService(context, UndoSupport.class);
		MyLayout layout = new MyLayout(undo);
		
		Properties myLayoutProps = new Properties();
		myLayoutProps.setProperty("preferredMenu","My Layouts");
		registerService(context, layout, CyLayoutAlgorithm.class, myLayoutProps);
		
		Properties applyPolyProps = new Properties();
		applyPolyProps.setProperty(ServiceProperties.PREFERRED_MENU, "Apps");
		applyPolyProps.setProperty(ServiceProperties.TITLE, "polyLayout");
		registerService(context, polyTF, NetworkTaskFactory.class, myLayoutProps);
			
	}

}
