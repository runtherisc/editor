package data.map.resources;

public class MapItemAttResource {
	
	private int id;
	private short amount;
	private int onDepletion;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public short getAmount() {
		return amount;
	}
	public void setAmount(short amount) {
		this.amount = amount;
	}
	public int getOnDepletion() {
		return onDepletion;
	}
	public void setOnDepletion(int onDepletion) {
		this.onDepletion = onDepletion;
	}
	
	public MapItemAttResource copy(){
		
		MapItemAttResource copy =  new MapItemAttResource();
		copy.setAmount(this.getAmount());
		copy.setId(this.getId());
		copy.setOnDepletion(this.getOnDepletion());
		return copy;
	}

}
