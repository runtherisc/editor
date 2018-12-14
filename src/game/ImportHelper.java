package game;

import java.util.ArrayList;
import java.util.List;

import data.map.resources.BuildingCreationResource;
import data.map.resources.LifecycleItemResource;
import data.map.resources.MultiImageResourceAction;

public class ImportHelper {

	public static void adjustBuildingCreationRequirementEndframe(List<BuildingCreationResource> creationList, int newLastFrame){
		
		int lastFrame = creationList.get(creationList.size()-2).getEndFrame();	

		while(creationList.size() > 2 && lastFrame > newLastFrame){
			
			creationList.remove(creationList.size()-2);					
			lastFrame = creationList.get(creationList.size()-2).getEndFrame();					
		}					
		creationList.get(creationList.size()-2).setEndFrame(newLastFrame);	
	}
	
	public static void clearBuildingCreationRequirementIdles(List<BuildingCreationResource> creationList, List<MultiImageResourceAction> idleList, int mainIdle){
		
		List<Integer> idleIds = new ArrayList<Integer>();
		for (MultiImageResourceAction multiImageResourceAction : idleList) {
			
			idleIds.add(multiImageResourceAction.getId());
		}
		
		for (BuildingCreationResource buildingCreationResource : creationList) {
			
			if(!idleIds.contains(buildingCreationResource.getDestructionIdleId())) buildingCreationResource.setDestructionIdleId(mainIdle);
			if(!idleIds.contains(buildingCreationResource.getIdleId())) buildingCreationResource.setIdleId(mainIdle);
			List<LifecycleItemResource> itemList = buildingCreationResource.getLifecycleItems();
			for (LifecycleItemResource lifecycleItemResource : itemList) {
				if(!idleIds.contains(lifecycleItemResource.getIdleId())) lifecycleItemResource.setIdleId(-1);
			}
		}
	}
}
