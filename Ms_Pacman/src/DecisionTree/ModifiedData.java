package DecisionTree;

import dataRecording.DataTuple;
import pacman.game.Constants.MOVE;

public class ModifiedData {

	public MOVE directionChosen;

	public int nearestGhostDist;
	public boolean nearestGhostEdible;
	public MOVE nearestGhostDir;

	
	public ModifiedData(DataTuple data) {

		int nearestGhostDist = Integer.MAX_VALUE;
		boolean nearestGhostEdible = false;
		MOVE nearestGhostDir = MOVE.NEUTRAL;

		if (data.blinkyDist <= nearestGhostDist) {
			nearestGhostDist = data.blinkyDist;
			nearestGhostEdible = data.isBlinkyEdible;
			nearestGhostDir = data.blinkyDir;
		}
		if (data.inkyDist <= nearestGhostDist) {
			nearestGhostDist = data.inkyDist;
			nearestGhostEdible = data.isInkyEdible;
			nearestGhostDir = data.inkyDir;
		}
		if (data.pinkyDist <= nearestGhostDist) {
			nearestGhostDist = data.pinkyDist;
			nearestGhostEdible = data.isPinkyEdible;
			nearestGhostDir = data.pinkyDir;
		}
		if (data.sueDist <= nearestGhostDist) {
			nearestGhostDist = data.sueDist;
			nearestGhostEdible = data.isSueEdible;
			nearestGhostDir = data.sueDir;
		}
		
		this.directionChosen = data.DirectionChosen;
		
		this.nearestGhostDist = nearestGhostDist;
		this.nearestGhostEdible = nearestGhostEdible;
		this.nearestGhostDir = nearestGhostDir;
	}
}
