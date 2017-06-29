package edu.ucsf.rbvi.polylayout.internal.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.geom.Point2D;

import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class PolyLayoutAlgorithm {	
	public static void doLayout(Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, CyNetworkView networkView, final Double spacing) {
		Map<Object, Point2D> sizeMap = new HashMap<Object, Point2D>();
		final double maxSideLength = getMax(sizeMap, nodeMap, spacing);
		if(sizeMap.size() == 2) 
			doLengthTwo(sizeMap, nodeMap, reg, spacing);
		else if(sizeMap.size() > 2)
			doPolygons(sizeMap, nodeMap, reg, maxSideLength, spacing);
	}

	private static void doLengthTwo(Map<Object, Point2D> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, final Double spacing) { //for n = 2, where n is number of sides/groups
		double x = 0;
		for(Object categoryKey : nodeMap.keySet()) {
			List<View<CyNode>> nodesInCategory = nodeMap.get(categoryKey);
			double y = 0;
			for(View<CyNode> nodeView : nodesInCategory) {
				y += nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) / 2.0;
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
				y += spacing + nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) / 2.0;
			}
			x += 1000;
		}
	}

	private static void doPolygons(Map<Object, Point2D> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, final double maxSideLength, final Double spacing) {		
		List<Point2D> criticalPoints = new ArrayList<Point2D>();
		double totalDegree = findSideAngle(sizeMap.size()) * sizeMap.size();
		double angle = 0; 
		
		for(Object categoryKey : nodeMap.keySet()) {
			final int quadraint = (int) (angle / (totalDegree / 4.0)) + 1;
			doShape(nodeMap, sizeMap, categoryKey, maxSideLength, spacing, angle, criticalPoints, quadraint);
			angle += findSideAngle(nodeMap.size());
		}
	}

	private static Double findSideAngle(int size) {
		return (180.0 * (size - 2)) / size * 3.1415 / 180;//each corner angle in radians
	}

	private static Double getDifferenceInSpacing(Map<Object, Point2D> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, double maxSideLength,  
			Object categoryKey, double angle, final double spacing) {
		double thisSideLength = getSideLength(sizeMap, categoryKey, angle, null);
		
		double differenceSpacing = 0.0;//difference in spacing between this category and the max length category
		if(nodeMap.get(categoryKey).size() - 1 != 0) 
			differenceSpacing = (maxSideLength - (thisSideLength + spacing * (nodeMap.get(categoryKey).size() - 1))) / (nodeMap.get(categoryKey).size() + 1);
		else
			differenceSpacing = (maxSideLength - thisSideLength) / 2;
		return differenceSpacing;
	}

	private static Double getMax(Map<Object, Point2D> sizeMap, Map<Object, 
			List<View<CyNode>>> nodeMap, final double spacing) {
		double maxSize = 0.0;
		Object categKeyOfMax = null;
		double angle = 0;
		for(Object categoryKey : nodeMap.keySet()) {
			List<View<CyNode>> nodeViews = nodeMap.get(categoryKey);
			for(View<CyNode> nodeView : nodeViews) {
				double x = nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH);
				double y = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
				if (sizeMap.containsKey(categoryKey)) {
					sizeMap.put(categoryKey, new Point2D.Double(sizeMap.get(categoryKey).getX() + x, 
							sizeMap.get(categoryKey).getY() + y)); 
				}
				else {
					sizeMap.put(categoryKey, new Point2D.Double(x, y));
				}
			}			
			double size = getSideLength(sizeMap, categoryKey, angle, null) + (spacing * (nodeMap.get(categoryKey).size() - 1));
			if(maxSize < size) {	
				categKeyOfMax = categoryKey;
				maxSize = size;
			}
			angle += findSideAngle(nodeMap.size());
		}
		if(categKeyOfMax != null)
			return maxSize;
		return -1.0;
	}
	
	private static double getSideLength(Map<Object, Point2D> sizeMap, 
			Object categoryKey, double angle, View<CyNode> nodeView) {
		double sideLength = 0.0;
		if(nodeView == null) {
			if(Math.abs(Math.tan(angle)) <= 0.0005 || (Math.abs(Math.cos(angle)) >= 0.0005 && (Math.abs(Math.tan(angle)) <= 
					sizeMap.get(categoryKey).getY() / sizeMap.get(categoryKey).getX())))
				sideLength = (sizeMap.get(categoryKey).getX()) / Math.abs(Math.cos(angle));
			else
				sideLength = (sizeMap.get(categoryKey).getY()) / Math.abs(Math.sin(angle));
		}
		else {
			if(Math.abs(Math.tan(angle)) <= 0.0005 || (Math.abs(Math.cos(angle)) >= 0.0005 && (Math.abs(Math.tan(angle)) <= nodeView.getVisualProperty(
					BasicVisualLexicon.NODE_HEIGHT) / nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH)))) 
				sideLength = (nodeView.getVisualProperty(BasicVisualLexicon.NODE_WIDTH) / 2) / Math.abs(Math.cos(angle));
			else
				sideLength = (nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) / 2) / Math.abs(Math.sin(angle));
		}
		return sideLength;
	}

	private static void doShape(Map<Object, List<View<CyNode>>> nodeMap, Map<Object, Point2D> sizeMap, 
			Object categoryKey, Double maxSideLength, final Double spacing, Double angle, 
			List<Point2D> criticalPoints, final int quadraint) {		
		double x = 0.0;
		double y = 0.0;
		if(criticalPoints.size() > 0) {
			x = criticalPoints.get(criticalPoints.size() - 1).getX();
			y = criticalPoints.get(criticalPoints.size() - 1).getY();
		}
		
		double xCompAngle = -1 * Math.abs(Math.cos(angle));
		double yCompAngle = -1 * Math.abs(Math.sin(angle));
		if(quadraint == 2 || quadraint == 3)
			xCompAngle *= -1;
		if(quadraint == 3 || quadraint == 4)
			yCompAngle *= -1;
		
		double differenceSpacing = getDifferenceInSpacing(sizeMap, nodeMap, maxSideLength, categoryKey, angle, spacing);
		double changeVector = spacing + differenceSpacing;

		x = x + (3 * spacing) * xCompAngle;
		y = y + (3 * spacing) * yCompAngle;

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			double nodeDiagonalDistance = getSideLength(sizeMap, categoryKey, angle, nodeView);
			
			x += (changeVector + nodeDiagonalDistance) * xCompAngle;
			y += (changeVector + nodeDiagonalDistance) * yCompAngle;
			
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
			
			x += nodeDiagonalDistance * xCompAngle;
			y += nodeDiagonalDistance * yCompAngle;
		}
		
		double startX = x + (3 * spacing + differenceSpacing) * xCompAngle;
		double startY = y + (3 * spacing + differenceSpacing) * yCompAngle;

		criticalPoints.add(new Point2D.Double(startX, startY));
	}
}