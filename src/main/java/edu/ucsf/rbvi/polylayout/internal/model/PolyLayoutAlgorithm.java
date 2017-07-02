package edu.ucsf.rbvi.polylayout.internal.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.geom.Point2D;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

public class PolyLayoutAlgorithm {	


	/**
	 * Main entry point for the polyLayout algorithm.
	 */
	public static void doLayout(final Map<Object, List<View<CyNode>>> nodeMap, 
		                          final CyNetworkView networkView, 
															final Double spacing,
															final String sortColumn) {

		Map<Object, Point2D> sizeMap = new HashMap<Object, Point2D>();
		final double maxSideLength = getMax(sizeMap, nodeMap, spacing);
		if(sizeMap.size() == 2) 
			doLengthTwo(networkView, sizeMap, nodeMap, spacing, sortColumn);
		else if(sizeMap.size() > 2)
			doPolygons(networkView, sizeMap, nodeMap, maxSideLength, spacing, sortColumn);
	}

	/**
	 * This method is called when we only have two categories.  This turns out to be a special
	 * case because there isn't a polygon that we're actually implementing -- we actually
	 * use two parallel lines instead.
	 *
	 * @param view the network view for this layout
	 * @param sizeMap the map of categories to total width and height of the nodes
	 * @param nodeMap the map of categories and lists of nodes in those categories
	 * @param spacing how far apart each node should be from it's neighbor
	 * @param sortColumn the column to use for sorting the nodes (if any)
	 */
	private static void doLengthTwo(final CyNetworkView view, final Map<Object, Point2D> sizeMap, 
	                                final Map<Object, List<View<CyNode>>> nodeMap, 
	                                final Double spacing,
																	final String sortColumn) { //for n = 2, where n is number of sides/groups
		double x = 0;
		for(Object categoryKey : nodeMap.keySet()) {
			List<View<CyNode>> nodesInCategory = nodeMap.get(categoryKey);
			sortList(view, sortColumn, nodesInCategory);
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

	/**
	 * This method is called to layout nodes along the sides of a regular polygon,
	 * with the nodes belonging to each category along a separate side.
	 *
	 * @param view the network view for this layout
	 * @param nodeMap the map of categories and lists of nodes in those categories
	 * @param sizeMap the map of categories to total width and height of the nodes
	 * @param maxSideLength the length of the longest side
	 * @param spacing how far apart each node should be from it's neighbor
	 * @param sortColumn the column to use for sorting the nodes (if any)
	 */
	private static void doPolygons(final CyNetworkView view,
	                               final Map<Object, Point2D> sizeMap, 
									               final Map<Object, List<View<CyNode>>> nodeMap, 
	                               final double maxSideLength, final Double spacing,
																 final String sortColumn) {		
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

	/**
	 * This method sorts a list of nodes based on the natural sort order of the specified sortColumn.
	 */
	private static void sortList(final CyNetworkView view, final String sortColumn, final List<View<CyNode>> nodeViews) {
		if (sortColumn == null)
			return;

		// Get the network
		final CyNetwork net = view.getModel();

		// Sanity check -- make sure the column actually exists
		if (net.getDefaultNodeTable().getColumn(sortColumn) == null)
			return;

		Collections.sort(nodeViews, new ColumnSort(net, sortColumn));
	}
}
