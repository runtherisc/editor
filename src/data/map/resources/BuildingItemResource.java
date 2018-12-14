package data.map.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BuildingItemResource {
	
	private int id;
	private int amount;
	
	private List<WorkerActionResource> workerActions = new ArrayList<WorkerActionResource>();
	private List<ItemMakeResource> warehouseMakeList = new ArrayList<ItemMakeResource>();
//	private InfoResource infoResource = new InfoResource();
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public List<WorkerActionResource> getWorkerActions() {
		return workerActions;
	}
	public void setWorkerActions(List<WorkerActionResource> workerActions) {
		this.workerActions = workerActions;
	}
	public void addWorkerAction(WorkerActionResource workerAction){
//		if(id==1) if(Constants.DEBUG) System.out.println("adding a action for the warehouse");
		this.workerActions.add(workerAction);
	}

	//not implemented fully yet, probably better to have multiple images at an item level?
	public WorkerActionResource getRandomWorkerAction(){
		
		if(workerActions==null || workerActions.isEmpty()) throw new RuntimeException("workerActions must be set unless it is under construction or upgrade");
		
		Random randomGenerator = new Random();
		
		return workerActions.get(randomGenerator.nextInt(workerActions.size()));
	}
	
	public WorkerActionResource getFirstWorkerActionResource(){
		
		if(workerActions==null || workerActions.isEmpty()) return null;
		return workerActions.get(0);
	}
	
	public WorkerActionResource getRandomWorkerAction(boolean onlyOut){
		
		WorkerActionResource action = null;
		
		//if it has a action, then it must be in and out
		if(workerActions!=null && !workerActions.isEmpty()){
			
			action = getRandomWorkerAction();
			
		}else{
			//worker is the item
			//if its not only out, then its only in
			if(onlyOut){
				
				action = new WorkerActionResource(-1, id);
			}else{
				
				action = new WorkerActionResource(id, -1);
			}
		}
		
		return action;
		
	}

	public void addWarehouseMake(ItemMakeResource make){
		
		warehouseMakeList.add(make);
	}
	
	public List<ItemMakeResource> getWarehouseMake(){
		
		return warehouseMakeList;
	}
	
	public void setWarehouseMake(List<ItemMakeResource> warehouseMakeList) {
		this.warehouseMakeList = warehouseMakeList;
	}
	
	public BuildingItemResource copy(){
		
		BuildingItemResource copy = new BuildingItemResource();
		
		copy.setId(this.id);
		copy.setAmount(this.amount);
		for (ItemMakeResource itemMakeResource : getWarehouseMake()) {
			copy.addWarehouseMake(itemMakeResource.copy());
		}	
		for (WorkerActionResource workerAction : getWorkerActions()) {
			copy.addWorkerAction(workerAction.copy());
		}
		
		return copy;
	}
	
}
