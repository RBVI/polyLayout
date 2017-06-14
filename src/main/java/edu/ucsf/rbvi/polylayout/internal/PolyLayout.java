package edu.ucsf.rbvi.polylayout.internal;


import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

/**
 * Creates a new menu item under Apps menu section.
 *
 */
public class PolyLayout extends AbstractLayoutAlgorithm {

	public PolyLayout(UndoSupport undo) {
		super("customLayout","Custom Layout", undo);
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, "Hello Cytoscape World!");
		
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView arg0, Object arg1, Set<View<CyNode>> arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

}
