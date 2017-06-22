package edu.ucsf.rbvi.polylayout.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyNode;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import edu.ucsf.rbvi.polylayout.internal.tasks.Coordinates;

public class PolyLayoutAlgorithm {	
	private static ArrayList<Coordinates> criticalPoints;

	public static void doLayout(Map<Object, Double> sizeMap, 
			Map<Object, List<View<CyNode>>> nodeMap, 
			CyServiceRegistrar reg, CyNetworkView networkView, final Double spacing) {
		criticalPoints = new ArrayList<Coordinates>();
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
			CyServiceRegistrar reg, Collection<View<CyNode>> collecOfNodes, final Double spacing) {
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
		Double angle = findSideAngle(sizeMap.size()) * 3.1415 / 180;

		int groupCounter = 1;
		for(Object categoryKey : nodeMap.keySet()) {
			if(groupCounter < 100) { 
				Double differenceSpacing = 0.0;
				if(nodeMap.get(categoryKey).size() - 1 != 0)
					differenceSpacing = (maxSideLength - sizeMap.get(categoryKey)) / (nodeMap.get(categoryKey).size() - 1);
				else
					differenceSpacing = (maxSideLength - sizeMap.get(categoryKey)) / 2;
				printPolygon(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, differenceSpacing);
				groupCounter++;
			}
			else 
				printNothing(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, 1, 0.0);
		}
	}

	private static Double findSideAngle(int size) {
		return (180.0 * (size - 2)) / size;
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
		maxSize += spacing * nodeMap.get(categKey).size();
		if(categKey != null)
			return maxSize;
		else {
			System.out.println("ooooh");
			return -1.0;
		}
	}

	private static void printPolygon(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, int groupCounter, Double angle, Double differenceSpacing) { 
		int numOfSides = nodeMap.size();
		if(groupCounter == 1) {
			printBottomHorizontal(nodeMap, categoryKey, maxSideLength, spacing, differenceSpacing);
			return;
		}
		switch(numOfSides % 4) {
		case 0:
			if(groupCounter > 1 && groupCounter < (numOfSides / 4) + 1) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);
			else if(groupCounter == (numOfSides / 4) + 1) 
				printVertical(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, 1, differenceSpacing);
			else if(groupCounter > (numOfSides / 4) + 1 && groupCounter < (numOfSides / 2) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);
			else if(groupCounter == (numOfSides / 2) + 1) 
				printTopHorizontal(nodeMap, categoryKey, maxSideLength, spacing, differenceSpacing);
			else if(groupCounter > (numOfSides / 2) + 1 && groupCounter < (3 * numOfSides / 4.0) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			else if(groupCounter == (3 * numOfSides / 4) + 1) 
				printVertical(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, -1, differenceSpacing);
			else if(groupCounter > (3 * numOfSides / 4) + 1 && groupCounter <= numOfSides) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			break;
		case 1:
			if(groupCounter > 1 && groupCounter <= ((numOfSides / 4)) + 1) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);		
			else if(groupCounter > (numOfSides / 4) + 1 && groupCounter <= (numOfSides / 2) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);
			else if(groupCounter > (numOfSides / 2) + 1 && groupCounter <= (3 * numOfSides / 4) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			else if(groupCounter > (3 * numOfSides / 4) + 1 && groupCounter <= numOfSides) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			break;
		case 2:
			if(groupCounter > 1 && groupCounter <= ((numOfSides / 4)) + 1) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);		
			else if(groupCounter > (numOfSides / 4) + 1 && groupCounter < (numOfSides / 2) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);
			else if(groupCounter == (numOfSides / 2) + 1) 
				printTopHorizontal(nodeMap, categoryKey, maxSideLength, spacing, differenceSpacing);
			else if(groupCounter > (numOfSides / 2) + 1 && groupCounter <= (3 * numOfSides / 4) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			else if(groupCounter > (3 * numOfSides / 4) + 1 && groupCounter <= numOfSides) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			break;
		case 3:
			if(groupCounter > 1 && groupCounter <= ((numOfSides / 4)) + 1) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);		
			else if(groupCounter > (numOfSides / 4) + 1 && groupCounter <= (numOfSides / 2) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, 1, differenceSpacing);
			else if(groupCounter > (numOfSides / 2) + 1 && groupCounter <= (3 * numOfSides / 4) + 1) 
				printTopDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			else if(groupCounter > (3 * numOfSides / 4) + 1 && groupCounter <= numOfSides) 
				printBottomDiagonal(nodeMap, categoryKey, maxSideLength, spacing, groupCounter, angle, -1, differenceSpacing);
			break;
		}
	}

	private static void printBottomHorizontal(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, Double differenceSpacing) {
		Double x = 0.0;
		Double lastNodeViewHeight = 0.0;
		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, 0.0);
			x = x - nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) - (spacing) - differenceSpacing - lastNodeViewHeight;
			lastNodeViewHeight = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
		}
		x += differenceSpacing;
		criticalPoints.add(new Coordinates(x - 5 * spacing, 0.0));
	}

	private static void printTopHorizontal(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, Double differenceSpacing) {
		Double x = criticalPoints.get(criticalPoints.size() - 1).getX();
		Double y = criticalPoints.get(criticalPoints.size() - 1).getY();
		x += spacing * 5;
		Double lastNodeViewHeight = 0.0;
		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
			x = x + nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) + (spacing) + differenceSpacing + lastNodeViewHeight;
			lastNodeViewHeight = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
		}
		x -= differenceSpacing;
		criticalPoints.add(new Coordinates(x + 5 * spacing, y));
	}

	private static void printBottomDiagonal(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, int groupCounter, Double angle, int isOnLeftSideOfShape, Double differenceSpacing) {
		if(isOnLeftSideOfShape != -1 && isOnLeftSideOfShape != 1)
			return;

		Double x = criticalPoints.get(criticalPoints.size() - 1).getX();
		Double y = criticalPoints.get(criticalPoints.size() - 1).getY();
		System.out.println("Before: " + " (" + x + "," + y + ")");
		Double changeX = 0.0;
		Double changeY = 0.0;
		Double changeVector = 0.0;

		System.out.println("angle: " + angle);

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			changeVector = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) + spacing + differenceSpacing;
			changeX = Math.abs(changeVector * Math.cos(angle));
			changeY = isOnLeftSideOfShape * Math.abs(changeVector * Math.sin(angle));
			System.out.println("changeVector: " + changeVector + " changeX: " + changeX + " changeY: " + changeY);
			break;
		}
		
		x = x - 5 * spacing * Math.abs(Math.cos(angle));
		y = y - isOnLeftSideOfShape * (5 * spacing * Math.abs(Math.sin(angle)));

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			System.out.println("After: " + nodeView.getModel().toString() + " (" + x + "," + y + ")");
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
			x -= changeX;
			y -= changeY;
		}

		Double startX = x - 5 * spacing * Math.abs(Math.cos(angle));
		Double startY = y - isOnLeftSideOfShape * (5 * spacing * Math.abs(Math.sin(angle)));
		
		criticalPoints.add(new Coordinates(startX, startY));
	}

	private static void printTopDiagonal(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, int groupCounter, Double angle, int isOnLeftSideOfShape, Double differenceSpacing) {//isOnLeftSideOfShape is 1 if on left, -1 if on right
		if(isOnLeftSideOfShape != -1 && isOnLeftSideOfShape != 1)
			return;

		Double x = criticalPoints.get(criticalPoints.size() - 1).getX();
		Double y = criticalPoints.get(criticalPoints.size() - 1).getY();
		System.out.println("Before: " + " (" + x + "," + y + ")");
		Double changeX = 0.0;
		Double changeY = 0.0;
		Double changeVector = 0.0;

		System.out.println("angle: " + angle);

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) { 
			changeVector = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) + spacing + differenceSpacing;
			changeX = Math.abs(changeVector * Math.cos(angle));
			changeY = isOnLeftSideOfShape * Math.abs(changeVector * Math.sin(angle));
			System.out.println("changeVector: " + changeVector + " changeX: " + changeX + " changeY: " + changeY);
			break;
		}
		
		x = x + 5 * spacing * Math.abs(Math.cos(angle));
		y = y - isOnLeftSideOfShape * (5 * spacing * Math.abs(Math.sin(angle)));

		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			System.out.println("After: " + nodeView.getModel().toString() + " (" + x + "," + y + ")");
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
			x += changeX;
			y -= changeY;
		}

		Double startX = x + 5 * spacing * Math.abs(Math.cos(angle));
		Double startY = y - isOnLeftSideOfShape * (5 * spacing * Math.abs(Math.sin(angle)));
		
		criticalPoints.add(new Coordinates(startX, startY));
	}

	private static void printVertical(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, int groupCounter, int isOnLeftSideOfShape, Double differenceSpacing) {
		if(isOnLeftSideOfShape != -1 && isOnLeftSideOfShape != 1)
			return;

		Double x = criticalPoints.get(criticalPoints.size() - 1).getX();
		Double y = criticalPoints.get(criticalPoints.size() - 1).getY();
		y -= (isOnLeftSideOfShape * 5 * spacing);
		Double lastNodeViewHeight = 0.0;
		
		if(isOnLeftSideOfShape == 1)
			for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
				y = y - nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) - (spacing) - differenceSpacing - lastNodeViewHeight;
				lastNodeViewHeight = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
			}
		else 
			for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, x);
				nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, y);
				y = y + nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT) + (spacing) + differenceSpacing + lastNodeViewHeight;
				lastNodeViewHeight = nodeView.getVisualProperty(BasicVisualLexicon.NODE_HEIGHT);
			}

		y += isOnLeftSideOfShape * (differenceSpacing);
		Double startY = y - (isOnLeftSideOfShape * 5 * spacing);
		criticalPoints.add(new Coordinates(x, startY));
	}

	private static void printNothing(Map<Object, List<View<CyNode>>> nodeMap, Object categoryKey, 
			Double maxSideLength, final Double spacing, int groupCounter, int isOnLeftSideOfShape, Double differenceSpacing) {
		for(View<CyNode> nodeView : nodeMap.get(categoryKey)) {
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, 200);
			nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, 200);
		}
		System.out.println("oooooooooooooooooooooooooooooooooooooooo");
	}
}