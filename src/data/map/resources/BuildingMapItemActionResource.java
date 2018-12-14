package data.map.resources;

public class BuildingMapItemActionResource {

	private int mapItem;
	private int action;
//	private int area;
//	private boolean areaFixed;
	public int getMapItem() {
		return mapItem;
	}
	public void setMapItem(int mapItem) {
		this.mapItem = mapItem;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	
	public BuildingMapItemActionResource copy(){
		
		BuildingMapItemActionResource copy = new BuildingMapItemActionResource();
		
		copy.setAction(this.action);
		copy.setMapItem(this.mapItem);
		
		return copy;
	}
	
}
