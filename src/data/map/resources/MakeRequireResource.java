package data.map.resources;

public class MakeRequireResource {
	
	private short amount;
	private int id;
	
	public short getAmount() {
		return amount;
	}
	public void setAmount(short amount) {
		this.amount = amount;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public MakeRequireResource copy(){
		
		MakeRequireResource copy = new MakeRequireResource();
		
		copy.setAmount(this.amount);
		copy.setId(this.id);
		
		return copy;
	}

}
