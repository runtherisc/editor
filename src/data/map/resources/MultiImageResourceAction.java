package data.map.resources;

public class MultiImageResourceAction extends ImageResourceActions{

	private int id;
	private int sequence;
	private String internalName;
	
	public String getInternalName() {
		if(internalName == null || internalName.trim().length()==0) return String.valueOf(getId());
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDirectory() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public MultiImageResourceAction copy(){
		
		MultiImageResourceAction copy = new MultiImageResourceAction();
		copy.setId(getId());
		copy.setInternalName(getInternalName());
		copy.setSequence(getDirectory());
		copy.setSkip(getSkip());
		copy.setTotalNumberImages(getTotalNumberImages());
		
		return copy;
	}
	
}
