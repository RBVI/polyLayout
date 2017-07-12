package edu.ucsf.rbvi.polylayout.internal.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

class ColumnSort implements Comparator<View<CyNode>> {
	final CyNetwork network;
	final String sortColumn;

	public ColumnSort(final CyNetwork net, final String sortColumn) {
		this.network = net;
		this.sortColumn = sortColumn;
	}

	public int compare(View<CyNode> v1, View<CyNode> v2) {
		Object o1 = network.getRow(v1.getModel()).getRaw(sortColumn);
		Object o2 = network.getRow(v2.getModel()).getRaw(sortColumn);

		// Deal with null data
		if (o1 == null && o2 == null) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;

		if (o1 instanceof Double)
			return ((Double)o1).compareTo((Double)o2);
		else if (o1 instanceof Integer)
			return ((Integer)o1).compareTo((Integer)o2);
		else if (o1 instanceof Long)
			return ((Long)o1).compareTo((Long)o2);
		else if (o1 instanceof String)
			return ((String)o1).compareTo((String)o2);
		else if (o1 instanceof Boolean)
			return ((Boolean)o1).compareTo((Boolean)o2);
		else
			return o1.toString().compareTo(o2.toString());
	}
}
