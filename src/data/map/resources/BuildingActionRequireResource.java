package data.map.resources;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import data.map.MapObjectItem;

//extended by BuildingActionProduceResource
public class BuildingActionRequireResource {
	
	private short amount;

//	private int area;
//	private boolean areaFixed;
	private int item;
//	private List<Integer> mapItems;
	private List<BuildingMapItemActionResource> buildingMapitemActionResource = new ArrayList<BuildingMapItemActionResource>();
	private List<WorkerActionResource> workerActionResource = new ArrayList<WorkerActionResource>();
	
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public short getAmount() {
		return amount;
	}
	public void setAmount(short amount) {
		this.amount = amount;
	}

//	public int getArea() {
//		return area;
//	}
//	public void setArea(int area) {
//		this.area = area;
//	}
//	public boolean isAreaFixed() {
//		return areaFixed;
//	}
//	public void setAreaFixed(boolean areaFixed) {
//		this.areaFixed = areaFixed;
//	}
//	public List<Integer> getMapItem() {
//		return mapItems;
//	}
//	public void setMapItem(List<Integer> mapItem) {
//		this.mapItems = mapItem;
//	}
	public List<WorkerActionResource> getWorkerActions() {
		return workerActionResource;
	}
	public void setWorkerActions(List<WorkerActionResource> workerActions) {
		this.workerActionResource = workerActions;
	}
	public void addWorkerAction(WorkerActionResource workerAction){
		
		this.workerActionResource.add(workerAction);
	}
	
	public List<BuildingMapItemActionResource> getBuildingMapItemActionResource() {
		return buildingMapitemActionResource;
	}
	public void setBuildingMapItemActionResource(List<BuildingMapItemActionResource> buildingMapitemActionResource) {
		this.buildingMapitemActionResource = buildingMapitemActionResource;
	}
	public void addBuildingMapItemAction(BuildingMapItemActionResource buildingMapitemAction){
		
		this.buildingMapitemActionResource.add(buildingMapitemAction);
	}
	
	public BuildingMapItemActionResource getFirstBuildingMapItemActionResource(){
		
		if(buildingMapitemActionResource==null || buildingMapitemActionResource.isEmpty()) return null;
		
		return buildingMapitemActionResource.get(0);
	}
	
	public int getRequiredActionByMapItem(int mapItem){
		
		int action = -1;
		
		for (BuildingMapItemActionResource buildingMapItemAction : buildingMapitemActionResource) {
			
			if(buildingMapItemAction.getMapItem()==mapItem){
				
				action = buildingMapItemAction.getAction();
				break;
			}
		}
		
		return action;
	}
	
	public WorkerActionResource getFirstWorkerAction(){
		
		if(workerActionResource==null || workerActionResource.isEmpty()) return null;

		return workerActionResource.get(0);
	}
	
	
	//dupe methods also used in BuildingItemResource (BuildingItemResource throws if null)
	public WorkerActionResource getRandomWorkerAction(){
		
		if(workerActionResource==null || workerActionResource.isEmpty()) return null;
		
		Random randomGenerator = new Random();
		
		return workerActionResource.get(randomGenerator.nextInt(workerActionResource.size()));
	}
	
	public List<Integer> getActionItemList(){
		
		List<Integer> itemsToReturn = new ArrayList<Integer>();
		
		for (BuildingMapItemActionResource buildingMapItemAction : buildingMapitemActionResource) {

			itemsToReturn.add(buildingMapItemAction.getMapItem());
		}
		
		return itemsToReturn;
	}
	
	public boolean hasMapResources(){
		
		return buildingMapitemActionResource!=null && !buildingMapitemActionResource.isEmpty();
	}
	
	public boolean mapObjectMeetsThisRequirement(MapObjectItem mapObjectItem){
		
		List<Integer> mapItems = getActionItemList();
		
//		System.out.println("mapObjectItem!=null"+mapObjectItem!=null);
//		System.out.println("mapObjectItem.hasAmount(this.item, this.amount)"+mapObjectItem.hasAmount(this.item, this.amount));
//		System.out.println("mapItems.contains(mapObjectItem.getItemId())"+mapItems.contains(mapObjectItem.getItemId()));
//		System.out.println("!mapObjectItem.isLocked()"+!mapObjectItem.isLocked());
//		System.out.println("!mapObjectItem.isCovered()"+!mapObjectItem.isCovered());

		 return mapObjectItem!=null &&
				mapObjectItem.hasAmount(this.item, this.amount) && 
				mapItems.contains(mapObjectItem.getItemId()) &&
				!mapObjectItem.isLocked() &&
				!mapObjectItem.isCovered();
		

	}
	
	public BuildingActionRequireResource copy(){
		
		BuildingActionRequireResource copy = new BuildingActionRequireResource();
		
		populateCopy(copy);
		
		return copy;
	}
	
	protected void populateCopy(BuildingActionRequireResource copy){
		
		copy.setAmount(this.amount);
		for (BuildingMapItemActionResource buildingMapItemAction : buildingMapitemActionResource) {
			copy.addBuildingMapItemAction(buildingMapItemAction.copy());
		}
		copy.setItem(this.item);
		//copy.setWorkerActions(workerActions);
		for (WorkerActionResource workerAction : workerActionResource) {
			copy.addWorkerAction(workerAction.copy());
		}
	}

}
