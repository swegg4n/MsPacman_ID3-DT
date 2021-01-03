package pacman.entries.pacman;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import DecisionTree.Node;
import dataRecording.DataSaverLoader;
import dataRecording.DataTuple;
import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {

	public List<DataTuple> trainingData = new ArrayList<DataTuple>();
	List<DataTuple> testData = new ArrayList<DataTuple>();

	public HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
	public Node rootNode;

	public MyPacMan() {
		CreateTrainingTestData();

		List<String> yesNo_attributes = Arrays.asList(new String[] { "YES", "NO" });
		List<String> dist_attributes = Arrays
				.asList(new String[] { "VERY_LOW", "LOW", "MEDIUM", "HIGH", "VERY_HIGH", "NONE" });
		List<String> dir_attributes = Arrays.asList(new String[] { "UP", "DOWN", "LEFT", "RIGHT" });

		attributes.put("isBlinkyEdible", yesNo_attributes);
		attributes.put("isInkyEdible", yesNo_attributes);
		attributes.put("isPinkyEdible", yesNo_attributes);
		attributes.put("isSueEdible", yesNo_attributes);
		attributes.put("blinkyDist", dist_attributes);
		attributes.put("inkyDist", dist_attributes);
		attributes.put("pinkyDist", dist_attributes);
		attributes.put("sueDist", dist_attributes);
		attributes.put("blinkyDir", dir_attributes);
		attributes.put("inkyDir", dir_attributes);
		attributes.put("pinkyDir", dir_attributes);
		attributes.put("sueDir", dir_attributes);
	}

	public void Print() {
		try {
			System.setOut(new PrintStream(new FileOutputStream("myData/DT_model.txt")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		rootNode.Print("");
	}

	private void CreateTrainingTestData() {

		DataTuple[] data = DataSaverLoader.LoadPacManData();
		List<DataTuple> dataList = Arrays.asList(data);
		Collections.shuffle(dataList);

		List<DataTuple> attributeData = new ArrayList<DataTuple>();
		for (int i = 0; i < dataList.size(); i++) {
			attributeData.add(dataList.get((i)));
		}

		int entriesLength = attributeData.size();
		int trainingLength = (int) (0.8f * entriesLength);

		for (int i = 0; i < trainingLength; i++) {
			this.trainingData.add(attributeData.get(i));
		}
		for (int i = trainingLength; i < entriesLength; i++) {
			this.testData.add(attributeData.get(i));
		}
	}

	public void GenerateDT() {
		ArrayList<String> attributeKeys = new ArrayList<String>(attributes.keySet());
		rootNode = CreateDT(trainingData, attributeKeys);

		// rootNode.Print();
		// validate training
	}

	private Node CreateDT(List<DataTuple> data, ArrayList<String> attributeKeys) {
		Node N = new Node();

		if (SameMove(data)) {
			N.label = data.get(0).DirectionChosen.toString();
			return N;
		}

		if (attributeKeys.isEmpty()) {
			N.label = MajorityMove(data).toString();
			return N;
		}

		String A = AttributeSelection(data, attributeKeys);
		N.label = A;
		attributeKeys.remove(A);

		for (String a_j : attributes.get(A)) {

			ArrayList<String> attributes_clone = (ArrayList<String>) attributeKeys.clone();

			ArrayList<DataTuple> d_j = new ArrayList<DataTuple>();
			for (DataTuple d : data)
				if (d.GetAttributeValue(A) == a_j)
					d_j.add(d);

			if (d_j.isEmpty()) {
				N.childNodes.put(a_j, new Node(MajorityMove(data).toString()));
			} else {
				N.childNodes.put(a_j, CreateDT(d_j, attributes_clone));
			}
		}

		return N;
	}

	private boolean SameMove(List<DataTuple> data) {
		MOVE first = data.get(0).DirectionChosen;
		for (int i = 1; i < data.size(); i++) {
			if (data.get(i).DirectionChosen != first)
				return false;
		}
		return true;
	}

	private MOVE MajorityMove(List<DataTuple> data) {
		HashMap<MOVE, Integer> moves = new HashMap<MOVE, Integer>();
		moves.put(MOVE.UP, 0);
		moves.put(MOVE.DOWN, 0);
		moves.put(MOVE.LEFT, 0);
		moves.put(MOVE.RIGHT, 0);
		moves.put(MOVE.NEUTRAL, 0);

		for (int i = 0; i < data.size(); i++) {
			MOVE key = data.get(i).DirectionChosen;
			moves.put(key, moves.get(key) + 1);
		}

		int maxMoveCount = Integer.MIN_VALUE;
		MOVE maxMove = MOVE.NEUTRAL;

		for (MOVE key : moves.keySet()) {
			if (moves.get(key) > maxMoveCount) {
				maxMoveCount = moves.get(key);
				maxMove = key;
			}
		}
		return maxMove;
	}

	private String AttributeSelection(List<DataTuple> data, List<String> attributeKeys) {

		String selectedAttribute = "";
		double bestIG = Double.MAX_VALUE;

		for (int i = 0; i < attributeKeys.size(); i++) {

			double IG = 0;
			List<String> currentAttributeValues = attributes.get(attributeKeys.get(i));
			int[] nbrOfEachValue = new int[currentAttributeValues.size()];

			for (int j = 0; j < currentAttributeValues.size(); j++) {
				List<DataTuple> subSet = new ArrayList<DataTuple>();

				for (DataTuple D : data) {
					if (D.GetAttributeValue(attributeKeys.get(i)) == currentAttributeValues.get(j)) {
						nbrOfEachValue[j]++;
						subSet.add(D);
					}
				}
				double up = 0, down = 0, left = 0, right = 0, neutral = 0;
				for (DataTuple D : subSet) {
					switch (D.DirectionChosen) {
					case UP:
						up++;
						break;
					case DOWN:
						down++;
						break;
					case LEFT:
						left++;
						break;
					case RIGHT:
						right++;
						break;
					default:
						neutral++;
						break;
					}
				}
				double S_v = nbrOfEachValue[j];
				if (S_v != 0) {
					IG += (S_v / data.size()) * (-((up / S_v) * log2(up / S_v)) - ((down / S_v) * log2(down / S_v))
							- ((left / S_v) * log2(left / S_v)) - ((right / S_v) * log2(right / S_v))
							- ((neutral / S_v) * log2(neutral / S_v)));
				}
				IG *= -1;
			}

			if (IG < bestIG) {
				bestIG = IG;
				selectedAttribute = attributeKeys.get(i);
			}
		}
		
		return selectedAttribute;
	}

	public static double log2(double N) {
		if (N == 0)
			return 0;
		return (Math.log(N) / Math.log(2));
	}

	public void ValidateTraining() {
		if (testData.size() <= 0)
			throw new RuntimeException("Unable to validate training - no test data found");

		MOVE testMove;
		MOVE trainedMove;
		int correctMoves = 0;

		for (int i = 0; i < testData.size(); i++) {
			testMove = testData.get(i).DirectionChosen;
			trainedMove = getMove(this.rootNode, testData.get(i));

			if (testMove == trainedMove)
				correctMoves++;
		}
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		System.out.println(correctMoves + " correct moves (out of " + testData.size() + " moves)");
		System.out.println("Accuracy: " + (double) correctMoves / testData.size());
	}

	public MOVE getMove(Game game, long timeDue) {

		DataTuple data = new DataTuple(game, null);
		return getMove(this.rootNode, data);
	}

	
	boolean printMoves = false;
	
	public MOVE getMove(Node node, DataTuple data) {
		MOVE move = MOVE.NEUTRAL;

		if (node.IsLeaf()) {
			move = MOVE.valueOf(node.label);
			
			if (printMoves)
				System.out.println("Selected move: " + move + ",  Data: " + data.ToString(new ArrayList<String>(attributes.keySet())));
			
		} else {
			Node nextNode = (Node) node.childNodes.get(data.GetAttributeValue(node.label));
			move = getMove(nextNode, data);
		}
		return move;
	}
}