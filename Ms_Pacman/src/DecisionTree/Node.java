package DecisionTree;

import java.util.HashMap;
import java.util.Map;

public class Node {

	public String label = "";
	public HashMap<String, Node> childNodes = new HashMap<String, Node>();

	public Node() {
	}

	public Node(String label) {
		this.label = label;
	}
	
	public boolean IsLeaf()
	{
		return childNodes.size() == 0;
	}

	public void Print(String indent) {
		if (this.IsLeaf()) {
			System.out.print(indent);
			System.out.println("  └─ Return " + label);
		}
		Map.Entry<String, Node>[] nodes = childNodes.entrySet().toArray(new Map.Entry[0]);
		for (int i = 0; i < nodes.length; i++) {
			System.out.print(indent);
			if (i == nodes.length - 1) {
				System.out.println("└─ \"" + label + "\" = " + nodes[i].getKey() + ":");
				nodes[i].getValue().Print(indent + "    ");
			} else {
				System.out.println("├─ \"" + label + "\" = " + nodes[i].getKey() + ":");
				nodes[i].getValue().Print(indent + (char) 0x007C + "   ");
			}
		}

	}
}
