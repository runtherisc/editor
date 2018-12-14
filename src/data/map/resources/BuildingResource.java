package data.map.resources;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BuildingResource {
	

	private String name;
	private int id;
	private boolean isWarehouse;
	private int workers;
	private int imageResource;
	private List<Integer> allowedon;
	private BuildingAreaResource buildingAreaResource;
//	private ImageResource imageResource;
	private InfoResource infoResource = new InfoResource();
	//building stuff
	private List<BuildingActionResource> buildingActionList = new ArrayList<BuildingActionResource>();
	//warehouse stuff
//	private List<BuildingItemResource> buildingItemList = new ArrayList<BuildingItemResource>();
	private Map<Integer, BuildingItemResource> buildingItemMap = new LinkedHashMap<Integer, BuildingItemResource>();

	public int getImageResourceId() {
		return imageResource;
	}

	public ImageResource getImageResource() {
		return Resource.getBuildingImageResourceById(imageResource);
	}

	public void setImageResourceId(int imageResource) {
		this.imageResource = imageResource;
	}
	
	public List<Integer> getAllowedon() {
		return allowedon;
	}

	public void setAllowedon(List<Integer> allowedon) {
		this.allowedon = allowedon;
	}

	public BuildingAreaResource getBuildingAreaResource() {
		return buildingAreaResource;
	}

	public void setBuildingAreaResource(BuildingAreaResource buildingAreaResource) {
		this.buildingAreaResource = buildingAreaResource;
	}

//	public ImageResource getImageResource() {
//		return imageResource;
//	}
//
//	public void setImageResource(ImageResource imageResource) {
//		this.imageResource = imageResource;
//	}

	public InfoResource getInfoResource() {
		return infoResource;
	}

	public void addBuildingAction(BuildingActionResource buildingAction){
		
		buildingActionList.add(buildingAction);
	}
	
	public List<BuildingActionResource> getBuildingActionList(){
		
		return buildingActionList;
	}
	
	public void setBuildingAction(List<BuildingActionResource> buildingActionList){
		
		this.buildingActionList = buildingActionList;
	}

	public void putBuildingItem(BuildingItemResource buildingItem, int itemId){
		
		if(buildingItemMap.get(itemId)!=null) System.err.println("ERROR: duplicate warehouse item");
		
		buildingItemMap.put(itemId, buildingItem);
	}
	
	public void clearBuildingItemMap(){
		
		buildingItemMap = new LinkedHashMap<Integer, BuildingItemResource>();
	}

//	public String getItemTextFromMap(int itemId, String type){
//
//		String text = "unknown";
//
//		BuildingItemResource buildingItemResource = buildingItemMap.get(itemId);
//
//		if(buildingItemResource!=null){
//
//			text = buildingItemResource.getInfoResource().getText(type);
//		}
//
//		return text;
//	}
	
	public Map<Integer, BuildingItemResource> getBuildingItemMap(){
		
		return buildingItemMap;
	}

	public String getName() {
		return name;
	}

	public String getTitle(){

		return infoResource.getText(ResourceConstants.INFO_TYPE_TITLE);
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isWarehouse() {
		return isWarehouse;
	}

	public void setWarehouse(boolean isWarehouse) {
		this.isWarehouse = isWarehouse;
	}

	public int getWorkers() {
		return workers;
	}

	public void setWorkers(int workers) {
		this.workers = workers;
	}
}
