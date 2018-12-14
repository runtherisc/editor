package data.map.resources;

public class LevelTargetResource {

    private int item;
    private int amount;

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public LevelTargetResource copy(){
    	
    	LevelTargetResource copy = new LevelTargetResource();
    	copy.setItem(this.item);
    	copy.setAmount(this.amount);
    	
    	return copy;
    }
}
