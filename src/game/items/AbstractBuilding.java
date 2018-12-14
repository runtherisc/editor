package game.items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.map.resources.Resource;


public abstract class AbstractBuilding {
	
	public AbstractBuilding(long buildingNumber, int x, int y, int resourceId) {
		
		this.buildingNumber = buildingNumber;
		this.posX = x;
		this.posY = y;
		this.resourceId = resourceId;
	}

	protected int posX;
	protected int posY;

	protected Worker[] workers;

	protected int resourceId;
	
	long buildingNumber;

	protected boolean underConstruction;

	protected byte destructionState;

	protected int constructionState;

	protected Map<Integer,Short> incomingItems = new HashMap<Integer, Short>();

	protected Map<Integer,Short> storedItems = new HashMap<Integer, Short>();
	
	protected Map<Integer,Short> outgoingItems = new HashMap<Integer, Short>();

	protected long preferredWarehouse = -1;

	protected int constructionActionProgress = -2;

	protected String[] preferedWarehousePending;

	protected Set<String> issues = new HashSet<String>();

	protected boolean alertIssues = true;

	Set<Worker> itemWorkers = new HashSet<Worker>();
	
	private boolean performingBuildingAction; 

	public void fillStoreItems(int item, short amount){
		
		storedItems.put(item, amount);
	}
	
	public short getStoredItemAmount(int id){
		
		if(storedItems.get(id)!=null) return storedItems.get(id);
		
		return 0;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public void initWorkers(int amount){
		
		Worker[] workers = new Worker[amount];
		
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker();
		}
		
		this.workers = workers;
	}

	public long getBuildingNumber() {
		return buildingNumber;
	}

	public void setBuildingNumber(int buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public boolean isUnderConstruction() {
		return underConstruction;
	}

	public void setUnderConstruction(boolean underConstruction) {
		this.underConstruction = underConstruction;
	}
	
	public String getBuildingNameAndNum(){
		
		return Resource.getBuildingResourceById(resourceId).getName() + "["+buildingNumber +"]";
	}

	public String getBuildingTitleAndNum(){

		return Resource.getBuildingResourceById(resourceId).getTitle() + "["+buildingNumber +"]";
	}
}
