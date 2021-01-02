package DecisionTree;

import java.util.HashMap;

public class Node {

	public boolean leaf = true;
	public String label = "";
	public HashMap <String, Node> childNodes = new HashMap<String, Node>();
}
