package edu.ucsf.rbvi.polylayout.internal;

import java.util.Set;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

public class PolyLayoutAlgorithm extends AbstractLayoutAlgorithm
{
	public PolyLayoutAlgorithm(String computerName, String humanName, UndoSupport undoSupport) {
		super(computerName, humanName, undoSupport);
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkView networkView, Object layoutAttribute, Set<View<CyNode>> setOfNodeViews, String displayName) {
		PolyLayoutAlgorithmTask polyLayoutAlgorithmTask = new PolyLayoutAlgorithmTask(displayName, networkView, setOfNodeViews, (String) layoutAttribute, undoSupport);
		return new TaskIterator(polyLayoutAlgorithmTask);
	}
}