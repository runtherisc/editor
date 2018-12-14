package data.map;

import java.util.ArrayList;
import java.util.List;

import data.LevelData;
import data.map.resources.MapItemResource;
import game.items.AbstractBuilding;

public class MapCell {
	
//	private List<MapObjectItem> mapObjects;

	//stop multiple object being created on restore
	private List<String> mapObjectIdentifier;
	
	private boolean canWalkOver=true;
	
	private List<ImageStatus> mapObjectImages;

	private List<WorkerImageStatus> workerImages;
	
	transient private AbstractBuilding building;
	
	public AbstractBuilding getBuilding() {
		return building;
	}

	public void setBuilding(AbstractBuilding building) {
		this.building = building;
	}

	public List<ImageStatus> getMapObjectImages() {
		return mapObjectImages;
	}
	
	public List<MapItemResource> getMapItemResources(){
		
		List<MapItemResource> mapItemResourceList = new ArrayList<MapItemResource>();
		
		if(mapObjectImages!=null && !mapObjectImages.isEmpty()){
		
			for (ImageStatus imageStatus : mapObjectImages) {
				
				MapItemResource mapItemResource = imageStatus.getMapItemResource();	
				if(mapItemResource!=null) mapItemResourceList.add(mapItemResource);
			}
		}
		
		return mapItemResourceList;
	}

	public void setMapObjectImages(List<ImageStatus> mapObjectImages) {
		this.mapObjectImages = mapObjectImages;
	}
	
	public void addMapObjectImages(ImageStatus imageStatus){
		
		if(mapObjectImages==null) mapObjectImages = new ArrayList<ImageStatus>();
		mapObjectImages.add(imageStatus);
	}
	
	public boolean removeMapObjectImage(ImageStatus imageStatus){
	
		return mapObjectImages.remove(imageStatus);

	}
	
	public boolean removeBuildingImageStatus(){
		
		for (ImageStatus status : mapObjectImages) {
			
			if(!status.isMapObject()) return removeMapObjectImage(status);
		}
		
		return false;
	}
	
	public boolean removeLastImageStatus(){
		
		if(!mapObjectImages.isEmpty()){
			
			mapObjectImages.remove(mapObjectImages.size()-1);
			return true;
		}
		
		return false;
	}

	public boolean hasMapObjects() {
		return mapObjectIdentifier !=null && !mapObjectIdentifier.isEmpty();
	}
	
	public void addMapObjectIdentifier(String mapObjectString){
		
		if(mapObjectIdentifier ==null){
			mapObjectIdentifier = new ArrayList<String>();


		}else if(!mapObjectIdentifier.isEmpty()){
			getLastMapObject().setCovered(true);
		}
		mapObjectIdentifier.add(mapObjectString);
	}
	
	public int getNumberOfMapObjects(){
		
		if(mapObjectIdentifier !=null){

			return mapObjectIdentifier.size();
		}
		
		return 0;
	}
	
	public boolean removeMapObjectIdentifier(String mapObjectIdentifier){

		return this.mapObjectIdentifier.remove(mapObjectIdentifier);

	}

	public void updateIdentifier(String oldId, String newId){

		int index = mapObjectIdentifier.indexOf(oldId);
		mapObjectIdentifier.set(index, newId);

	}
	
	public MapObjectItem getLastMapObject(){

		if(mapObjectIdentifier ==null || mapObjectIdentifier.isEmpty()) return null;

		String mapItemIdentifier = mapObjectIdentifier.get(mapObjectIdentifier.size()-1);

		return LevelData.getInstance().getMapItemByIndex(mapItemIdentifier);
	}


	public boolean isCanWalkOver() {
		return canWalkOver;
	}

	public void setCanWalkOver(boolean canWalkOver) {
		this.canWalkOver = canWalkOver;
	}

	
}
