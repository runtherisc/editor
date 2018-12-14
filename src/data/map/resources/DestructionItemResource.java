package data.map.resources;

public class DestructionItemResource extends LifecycleItemResource{
	
	 public DestructionItemResource() {}

    public DestructionItemResource(boolean onlyWhenStocked) {
		super();
		this.onlyWhenStocked = onlyWhenStocked;
	}

	boolean onlyWhenStocked;

    public boolean isOnlyWhenStocked() {
        return onlyWhenStocked;
    }

    public void setOnlyWhenStocked(boolean onlyWhenStocked) {
        this.onlyWhenStocked = onlyWhenStocked;
    }
    
    public LifecycleItemResource copy(){
    	
    	DestructionItemResource copy = new DestructionItemResource();
    	
    	populateCopy(copy);
    	copy.setOnlyWhenStocked(this.isOnlyWhenStocked());
    	
    	return copy;
    }
}
