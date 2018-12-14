package data.map.resources;

import java.util.ArrayList;
import java.util.List;

public class ItemMakeResource{
	
	private List<MakeRequireResource> makeRequirements = new ArrayList<MakeRequireResource>();
	
	private int frequency;
	private short amount;
	
	public short getAmount() {
		return amount;
	}
	public void setAmount(short amount) {
		this.amount = amount;
	}
	public List<MakeRequireResource> getMakeRequirements() {
		return makeRequirements;
	}
	public void setMakeRequirements(List<MakeRequireResource> makeRequirements) {
		this.makeRequirements = makeRequirements;
	}
	public void addMakeRequirement(MakeRequireResource makeRequireResource){
		this.makeRequirements.add(makeRequireResource);
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public ItemMakeResource copy(){
		
		ItemMakeResource copy = new ItemMakeResource();
		
		copy.setAmount(this.amount);
		copy.setFrequency(this.frequency);
		for (MakeRequireResource makeRequireResource : getMakeRequirements()) {
			
			copy.addMakeRequirement(makeRequireResource.copy());
		}
		
		return copy;
	}

}
