package edu.ucsf.rbvi.polylayout.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.awt.geom.Point2D;

import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class PolyLayoutAlgorithm {	
	public static void doLayout(Map<Object, Double> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, CyNetworkView networkView, final Double spacing) {
		if(sizeMap == null || nodeMap == null || networkView == null)
			return;
		else {
			Collection<View<CyNode>> collecOfNodes = networkView.getNodeViews();
			if(sizeMap.size() == 2)
				doLengthTwo(sizeMap, nodeMap, reg, collecOfNodes, spacing);
			else if(sizeMap.size() > 2)
				doPolygons(sizeMap, nodeMap, reg, collecOfNodes, spacing);
		}
	}

	private static void doLengthTwo(Map<Object, Double> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, Collection<View<CyNode>> collecOfNodes, final Double spacing) { //for n = 2, where n is number of sides/groups
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

	private static void doPolygons(Map<Object, Double> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, Collection<View<CyNode>> collecOfNodes, final Double spacing) {
		Double maxSideLength = getMax(sizeMap, nodeMap, spacing);

		List<Point2D> criticalPoints = new ArrayList<Point2D>();
		double angle = 0;
		double totalDegree = findSideAngle(sizeMap.size()) * sizeMap.size();
		
		for(Object categoryKey : nodeMap.keySet()) {
			double differenceSpacing = 0.0;
			final int quadraint = (int) (angle / (totalDegree / 4.0)) + 1;
			if(nodeMap.get(categoryKey).size() - 1 != 0) {
				differenceSpacing = (maxSideLength - sizeMap.get(categoryKey)) / (nodeMap.get(categoryKey).size() + 1);
			}
			else
				differenceSpacing = (maxSideLength - sizeMap.get(categoryKey)) / 2;
			doShape(nodeMap, categoryKey, maxSideLength, spacing, angle, differenceSpacing, criticalPoints, quadraint);
			angle += findSideAngle(sizeMap.size());
		}
	}

	private static Double findSideAngle(int size) {
		return (180.0 * (size - 2)) / size * 3.1415 / 180;//each corner angle in radians
	}

	private static Double getMax(Map<Object, Double> sizeMap, Map<Object, List<View<CyNode>>> nodeMap, final Double spacing) {
		Double maxSize = 0.0;
		Object categKey = null;
		for(Object categoryKey : sizeMap.keySet()) {
			Double size = sizeMap.get(categoryKey);
			if(maxSize < size) {
				categKey = categoryKey;
				maxSize = size;
			}
		}
		if(categKey != null)
			return maxSize;
		return -1.0;
	}

	private static void doShape(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, Double angle, 
			Double differenceSpacing, List<Point2D> criticalPoints, final int quadraint) {//isOnLeftSideOfShape is 1 if on left, -1 if on right
		double x = 0.0;
		double y = 0.0;
		if(criticalPoints.size() > 0) {
			x = criticalPoints.get(criticalPoints.size() - 1).getX();
			y = criticalPoints.get(criticalPoints.size() - 1).getY();
		}

		double changeX = 0.0;
		double changeY = 0.0;
		double changeVector = 0.0;
		double xCompAngle = -1 * Math.abs(Math.cos(angle));
		double yCompAngle = -1 * Math.abs(Math.sin(angle));
		
		if(quadraint == 2 || quadraint == 3)
			xCompAngle *= -1;
		if(quadraint == 3 || quadraint == 4)
			yCompAngle *= -1;

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) { 
			changeVector = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) + spacing + differenceSpacing;
			changeX = changeVector * xCompAngle;
			changeY = changeVector * yCompAngle;
			break;
		}

		x = x + (5 * spacing + differenceSpacing) * xCompAngle;
		y = y - (5 * spacing + differenceSpacing) * yCompAngle;

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
			x += changeX;
			y -= changeY;
		}

		double startX = x + 5 * spacing * xCompAngle;
		double startY = y - (5 * spacing * yCompAngle);

		criticalPoints.add(new Point2D.Double(startX, startY));
	}
}