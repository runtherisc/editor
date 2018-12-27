package game.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import data.map.resources.BuildingActionProduceResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.Coords;



public class Building extends AbstractBuilding{


	public Building(long buildingNumber, int posX, int posY, int resourceId, List<BuildingActionResource> buildingActionList) {
				
		super(buildingNumber, posX, posY, resourceId);

		if(buildingActionList!=null) {
			activeActions = new boolean[buildingActionList.size()];
			Arrays.fill(activeActions, Boolean.TRUE);
		}

		searchStartingPoint = new Coords(getPosX(), getPosY());
	}

    private int currentActionToPerform = -1;
    
    private int progressDisplay = -1;

    private boolean inProgress = false;
	
	private boolean[] activeActions;
	
	private Coords searchStartingPoint;
	
	private boolean areaSearchPending;
	
	private boolean userCancelled;
	
	private Set<Coords> remainingSearchCoords;

	private List<BuildingActionProduceResource> produceRemaining = new ArrayList<BuildingActionProduceResource>();
	
	private boolean mapItemPlaceOngoing;

}
