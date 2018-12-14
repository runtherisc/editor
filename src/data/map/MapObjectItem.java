package data.map;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Constants;
import data.map.resources.Coords;
import data.map.resources.MapItemAttResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;

public class MapObjectItem{
	
	public MapObjectItem(Coords coords, int itemIndex){
		
		this.coords = coords;
		this.itemIndex = itemIndex;
		this.identifier = Integer.toString(System.identityHashCode(this))+System.currentTimeMillis();
		System.out.println("identifier is "+identifier);
	}
	
	private Coords coords;
	private int itemIndex;
//	private int frameIndex;
	private int time;
	
	private boolean locked = false;
	private int actionpos = -2;
	private int nextaction;

	private boolean actionNearlyFinished = false;
	private boolean workerWorking = false;
	
	private String identifier;

	public Coords getCoords() {
		return coords;
	}
	public void setCoords(Coords coords) {
		this.coords = coords;
	}
	
	public int getItemId() {
		return itemIndex;
	}
	public void setItemId(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	
	
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {

		this.locked = locked;
	}

	private Map<Integer, Short> amounts  = new HashMap<Integer, Short>();


	private boolean covered;

	

	public boolean isCovered() {
		return covered;
	}
	public void setCovered(boolean covered) {
		this.covered = covered;
	}

	public Map<Integer, Short> getAmounts() {
		return amounts;
	}
	public void putAmount(int key, short value){
		amounts.put(key, value);
	}
	public void setAmounts(Map<Integer, Short> amounts) {
		this.amounts = amounts;
	}
	
	public boolean hasAmount(int key, short value){
		
		boolean hasAmount = false;
		if(amounts!=null && amounts.get(key)!=null){
			hasAmount = amounts.get(key) >= value;
		}
		
		return hasAmount;
	}
	
	public short getAmount(int itemId){
		
		if(amounts!=null && amounts.get(itemId)!=null){
			return amounts.get(itemId);
		}
		return 0;
	}
	
	public void initItems(){
		
		MapItemResource mapItemR = Resource.getMapItemResourceById(itemIndex);
		
		List<MapItemAttResource> mial = mapItemR.getMapItemAttList();

		amounts  = new HashMap<Integer, Short>();
		
		for (MapItemAttResource mapItemAttResource : mial) {
			
			putAmount(mapItemAttResource.getId(), mapItemAttResource.getAmount());
		}
		setTime(mapItemR.getTime() * Constants.TIMER_SPEED_ADJ);

	}
	
	public String getNameWithCoords(){
		
		StringBuilder sb =  new StringBuilder(Resource.getMapItemResourceById(itemIndex).getName());

		sb.append("[")
				.append(coords.toString())
				.append("]");

		return sb.toString();
	}

	public String getIdentifier(){
		
		return identifier;
	}
	
	

}
