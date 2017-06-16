package edu.ucsf.rbvi.polylayout.internal.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class PolyLayoutAlgorithm {	
	public static void doLayout(Map<Object, Double> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, CyNetworkView networkView, final int spacing) {
		if(sizeMap == null || nodeMap == null || networkView == null)
			return;
		else {
			if(sizeMap.size() != 2)
				return;
			else {
				//Collection<View<CyNode>> collecOfNodes = networkView.getNodeViews();
				Double x = 0.0;
				for(Object categoryKey : nodeMap.keySet()) {
					List<View<CyNode>> nodesInCategory = nodeMap.get(categoryKey);
					Double y = 0.0;
					Double yOffSet = 0.0;
					for(View<CyNode> nodeView : nodesInCategory) {
						y += yOffSet;
						nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
						nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y + nodeView.getVisualProperty(BasicVisualLexicon.NODE_SIZE));
						yOffSet = spacing + nodeView.getVisualProperty(BasicVisualLexicon.NODE_SIZE);
					}
					x += 200.0;
				}
			}
			networkView.fitContent();
			networkView.updateView();
		}
	}
}