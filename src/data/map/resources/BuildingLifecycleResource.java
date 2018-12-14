package data.map.resources;

import java.util.ArrayList;
import java.util.List;

//needs a better name, used in creation, destruction and maybe upgrade one day
public class BuildingLifecycleResource {
	
	private List<LifecycleItemResource> lifecycleItems = new ArrayList<LifecycleItemResource>();
	
	public List<LifecycleItemResource> getLifecycleItems() {
		return lifecycleItems;
	}
	public void setLifecycleItems(List<LifecycleItemResource> lifecycleItems) {
		this.lifecycleItems = lifecycleItems;
	}
	public void setLifecycleItems(List<LifecycleItemResource> creationItems, List<LifecycleItemResource> destructionItems) {
		
		lifecycleItems = new ArrayList<LifecycleItemResource>(creationItems);
		lifecycleItems.addAll(destructionItems);
	}
	public void addLifecycleItem(LifecycleItemResource lifecycleItem){
		this.lifecycleItems.add(lifecycleItem);
	}

//	private int sequence;
	private int endFrame;//ignored for destruction
	private int idleId;
//	private int idle;

//	public int getSequence() {
//		return sequence;
//	}
//	protected void setSequence(int sequence) {
//		this.sequence = sequence;
//	}
	public int getEndFrame() {
		return endFrame;
	}
	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}
	
//	public int getIdle() {
//		return idle;
//	}
//	protected void setIdle(int idle) {
//		this.idle = idle;
//	}

	public int getIdleId() {
		return idleId;
	}
	public void setIdleId(int idleId) {
		this.idleId = idleId;
	}
	
	//to simplify creation table
	public List<LifecycleItemResource> getOnlyCreationItems(){
		
		List<LifecycleItemResource> creationItems = new ArrayList<LifecycleItemResource>();
		
		for (LifecycleItemResource lifecycleItemResource : getLifecycleItems()) {
			if(lifecycleItemResource instanceof CreationItemResource) creationItems.add(lifecycleItemResource);
		}
		
		return creationItems;
	}
	
	//to simplify destruction table
	public List<LifecycleItemResource> getOnlyDestructionItems(){
		
		List<LifecycleItemResource> destructionItems = new ArrayList<LifecycleItemResource>();
		
		for (LifecycleItemResource lifecycleItemResource : getLifecycleItems()) {
			if(lifecycleItemResource instanceof DestructionItemResource) destructionItems.add(lifecycleItemResource);
		}
		
		return destructionItems;
	}
	
	public int getNumberOfCreationItems(){

		return getOnlyCreationItems().size();
	}
	
	public int getNumberOfDestructionItems(){
		
		return getOnlyDestructionItems().size();
		
	}
	
}
