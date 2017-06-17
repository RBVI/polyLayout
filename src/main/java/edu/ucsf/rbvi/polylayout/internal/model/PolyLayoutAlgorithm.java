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
				Collection<View<CyNode>> collecOfNodes = networkView.getNodeViews();
				double x = 0;
				double size = 0;
				for(Object categoryKey : nodeMap.keySet()) {
					List<View<CyNode>> nodesInCategory = nodeMap.get(categoryKey);
					double y = size - sizeMap.get(categoryKey) / 2;
					for(View<CyNode> nodeView : nodesInCategory) {
						y += nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) / 2.0;
						nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
						nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
						y += spacing + nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) / 2.0;
					}
					x += 1000;
				}
			}
		}
	}
}