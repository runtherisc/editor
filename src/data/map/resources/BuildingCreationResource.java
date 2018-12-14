package data.map.resources;

import java.util.ArrayList;
import java.util.List;

public class BuildingCreationResource extends BuildingLifecycleResource {

	private int destructionIdleId;

	public int getDestructionIdleId() {
		return destructionIdleId;
	}

	public void setDestructionIdleId(int destructionIdleId) {
		this.destructionIdleId = destructionIdleId;
	}

	
	public BuildingCreationResource copy(){
		
		BuildingCreationResource copy = new BuildingCreationResource();
		
		copy.setDestructionIdleId(this.getDestructionIdleId());
		copy.setEndFrame(this.getEndFrame());
		copy.setIdleId(this.getIdleId());
		
		List<LifecycleItemResource> itemcopys = new ArrayList<LifecycleItemResource>();
		List<LifecycleItemResource> items = getLifecycleItems();
		for (LifecycleItemResource lifecycleItemResource : items) {
			
			if(lifecycleItemResource instanceof CreationItemResource){
				
				itemcopys.add(((CreationItemResource)lifecycleItemResource).copy());
				
			}else if(lifecycleItemResource instanceof DestructionItemResource){
				
				itemcopys.add(((DestructionItemResource)lifecycleItemResource).copy());
			}
		}
		copy.setLifecycleItems(itemcopys);
		
		return copy;
	}
	

}
