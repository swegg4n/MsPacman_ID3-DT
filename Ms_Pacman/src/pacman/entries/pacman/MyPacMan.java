package pacman.entries.pacman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import DecisionTree.ModifiedData;
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

	List<ModifiedData> trainingData = new ArrayList<ModifiedData>();
	List<ModifiedData> testData = new ArrayList<ModifiedData>();

	Node rootNode;

	public MyPacMan() {
		CreateTrainingTestData();
		
		CreateDT(trainingData,);
	}

	public void CreateTrainingTestData() {

		DataTuple[] data = DataSaverLoader.LoadPacManData();
		List<DataTuple> dataList = Arrays.asList(data);
		Collections.shuffle(dataList);

		List<ModifiedData> attributeData = new ArrayList<ModifiedData>();
		for (int i = 0; i < dataList.size(); i++) {
			attributeData.add(new ModifiedData(dataList.get((i))));
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

	public Node CreateDT(List<ModifiedData> data, List<String> attributes) {
		Node N = new Node();

		if (SameMove(data)) {
			N.label = data.get(0).directionChosen.toString();
			return N;
		}

		if (attributes.isEmpty()) {
			N.label = MajorityMove(data).toString();
			return N;
		}
		
		
		
		return null;
	}

	public boolean SameMove(List<ModifiedData> data) {
		MOVE first = data.get(0).directionChosen;
		for (int i = 1; i < data.size(); i++) {
			if (data.get(i).directionChosen != first)
				return false;
		}
		return true;
	}

	public MOVE MajorityMove(List<ModifiedData> data) {
		HashMap<MOVE, Integer> moves = new HashMap<MOVE, Integer>();
		moves.put(MOVE.UP, 0);
		moves.put(MOVE.DOWN, 0);
		moves.put(MOVE.LEFT, 0);
		moves.put(MOVE.RIGHT, 0);
		moves.put(MOVE.NEUTRAL, 0);

		for (int i = 0; i < data.size(); i++) {
			MOVE key = data.get(i).directionChosen;
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

	
	
	public MOVE getMove(Game game, long timeDue) {

		return MOVE.NEUTRAL;
	}
}