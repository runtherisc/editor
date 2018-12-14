package data.map.resources;

import java.util.ArrayList;
import java.util.List;

public class MapItemResource {
	
	private String name;
	private int id;
	private int time = -1;
	private int onElapse;
	private List<Integer> allowedon;
//	private ImageResource imageResource;
	private InfoResource infoResource = new InfoResource();
	private List<MapItemAttResource> mapItemAttList = new ArrayList<MapItemAttResource>();
	private List<MapItemActionResource> mapItemActionList = new ArrayList<MapItemActionResource>();
	private boolean drawFirst;
	private boolean showAttributes;
	private int imageResource;

	public int getImageResourceId() {
		return imageResource;
	}

	public ImageResource getImageResource() {
		return Resource.getMapImageResourceById(imageResource);
	}

	public void setImageResourceId(int imageResource) {
		this.imageResource = imageResource;
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
	
	public void setMapItemAttributes(List<MapItemAttResource> mapItemAttList){
		
		this.mapItemAttList = mapItemAttList;
	}

	public void addMapItemAtt(MapItemAttResource mapItemAtt){
		
		mapItemAttList.add(mapItemAtt);
	}
	
	public List<MapItemAttResource> getMapItemAttList(){
		
		return mapItemAttList;
	}
	
	public void addMapItemAction(MapItemActionResource mapItemAction){
		
		mapItemActionList.add(mapItemAction);
	}
	
	public void setMapItemAction(List<MapItemActionResource> mapItemActionList){
		
		this.mapItemActionList = mapItemActionList;
	}
	
	public List<MapItemActionResource> getMapItemActionList(){
		
		return mapItemActionList;
	}
	
	public MapItemActionResource getMapItemActionByIndex(int index){

		return mapItemActionList.get(index);
	}

	public MapItemActionResource getMapItemActionById(int id){

		for(MapItemActionResource mapItemActionResource : mapItemActionList) {

			if(mapItemActionResource.getId() == id) {
				return mapItemActionResource;
			}
		}


		System.err.println("unable to find map item action id "+id);
		
		return null;
	}
	
	public List<Integer> getItemIdsFromAttrubutes(){
		
		List<Integer> ids =  new ArrayList<Integer>();
		
		for (MapItemAttResource attribute : getMapItemAttList()) {
			ids.add(attribute.getId());
		}
		
		return ids;
	}
	
	public int getAttributeMaxFromItemId(int id){
		
		for (MapItemAttResource attribute : getMapItemAttList()) {
			if(id == attribute.getId()){
				return attribute.getAmount();
			}
		}
		
		return 999;
	}
	
	public int getOnDepletionById(int id){
		
		for (MapItemAttResource mapItemAttResource : this.mapItemAttList) {
			
			if(mapItemAttResource.getId()==id){
				return mapItemAttResource.getOnDepletion();
			}
			
		}
		return -1;
		
	}

	public String getName() {
		return name;
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

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getOnElapse() {
		return onElapse;
	}

	public void setOnElapse(int onElapse) {
		this.onElapse = onElapse;
	}
	public List<Integer> getAllowedon() {
		return allowedon;
	}

	public void setAllowedon(List<Integer> allowedon) {
		this.allowedon = allowedon;
	}

	public boolean isDrawFirst() {
		return drawFirst;
	}

	public void setDrawFirst(boolean drawFirst) {
		this.drawFirst = drawFirst;
	}

	public boolean isShowAttributes() {
		return showAttributes;
	}

	public void setShowAttributes(boolean showAttributes) {
		this.showAttributes = showAttributes;
	}
	


}
