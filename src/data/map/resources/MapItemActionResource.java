package data.map.resources;

public class MapItemActionResource {
	
	private int id;
	private int busy;
	private int state;
	private int mapitem;
	private String internalName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBusy() {
		return busy;
	}
	public void setBusy(int busy) {
		this.busy = busy;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getMapitem() {
		return mapitem;
	}
	public void setMapitem(int mapitem) {
		this.mapitem = mapitem;
	}
	public String getInternalName() {
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	
	public MapItemActionResource copy(){
		
		MapItemActionResource copy = new MapItemActionResource();
		
		copy.setBusy(this.getBusy());
		copy.setId(this.getId());
		copy.setInternalName(this.getInternalName());
		copy.setMapitem(this.getMapitem());
		copy.setState(this.getState());
		
		return copy;
		
	}
	@Override
	public String toString() {

		return "busy: " + getBusy() + 
				" id: " + getId() +
				" name: " + getInternalName() +
				" map item: " + getMapitem() +
				" state: " + getState();
	}
	
	
	
}
