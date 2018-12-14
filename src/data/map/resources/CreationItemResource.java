package data.map.resources;

public class CreationItemResource extends LifecycleItemResource {
	
	public CreationItemResource(){}

    public CreationItemResource(boolean fromWarehouse) {
		super();
		this.fromWarehouse = fromWarehouse;
	}

	private boolean fromWarehouse;

    public boolean isFromWarehouse() {
        return fromWarehouse;
    }
    public void setFromWarehouse(boolean fromWarehouse) {
        this.fromWarehouse = fromWarehouse;
    }
    
    public LifecycleItemResource copy(){
    	
    	CreationItemResource copy = new CreationItemResource();
    	
    	populateCopy(copy);
    	copy.setFromWarehouse(this.isFromWarehouse());
    	
    	return copy;
    }

}
