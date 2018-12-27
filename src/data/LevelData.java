package data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import data.map.MapCell;
import data.map.MapObjectItem;
import game.items.Building;
import game.items.Warehouse;
import game.path.CompletePath;


public class LevelData implements Serializable{

	//singleton
	private static LevelData s_instance = null; 
	
	private int instanceTest;
	
	public int getInstanceTest() {
		return instanceTest;
	}
	public void setInstanceTest(int instanceTest) {
		this.instanceTest = instanceTest;
	}
	public static synchronized LevelData getInstance() {         
		if (s_instance == null) {             
			s_instance = new LevelData();         
		}        
		return s_instance;     
	}

    public static synchronized LevelData getInstance(boolean reset) {
        if (reset) {
            s_instance = new LevelData();
        }
        return s_instance;
    }

	public static synchronized LevelData getInstance(String json) {

		Gson gson = new Gson();
		s_instance = gson.fromJson(json, LevelData.class);
		return s_instance;
	}

    public static synchronized void destroyInstance() {

        s_instance = null;

    }

	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		synchronized (LevelData.class) {             
			if (s_instance == null) {                 
				// re-initialize if needed                  
				s_instance = this; // only if everything succeeds             
			}         
		}     
	}      
	// this function must not be called other than by the deserialization runtime     
	private Object readResolve() throws ObjectStreamException {         
		assert(s_instance != null);         
		return s_instance;     
	} 
	
	public LevelData(){
		
	}


    public void init(int gridx, int gridy, int overScroll, int level) {

		this.isInit = true;
		setGridX(gridx);
		setGridY(gridy);
		setOverScroll(overScroll);
		mapGrid = new MapCell[gridx][gridy];
//        setContext(context);
//        setGlGraphics(glGraphics);
		setCurrentLevel(level);
		
	}
	
	private boolean isInit = false;
	
//	use Collections.swap to re-order list
	private List<Warehouse> warehouses;
	private List<Building> buildings;
	
	private long identifier;
	
	private MapCell[][] mapGrid;
	
	private int gridX;
	private int gridY;
	private int overScroll;
	
	private int currentBuilding = -1;
	private int currentWarehouse = -1;

    private int currentLevel = 0;

	//used when moving workers, so we can tell which ones have already been moved and which have not
	//this boolean toggles with every pass
	private boolean alternativeWorkersPhase = false;


	
	private Map<String, CompletePath> pathMap = new HashMap<String, CompletePath>();


	private long counter;



	private List<MapObjectItem> mapObjects;

//	public boolean checkVersion(float level_version, String codeName){
//
//		System.out.println("xml" + level_version + " level "+resource_version);
//		System.out.println("xml" + codeName + " level "+resource_codeName);
//
//		return (level_version == resource_version && codeName.equals(resource_codeName));
//	}
	
	public long getNextIdentifier(){
		
		identifier++;
		
		if(identifier == Long.MAX_VALUE){
			throw new RuntimeException("need to re-order building identifiers stub");
		}
		
		return identifier;
	}
	
	public void resetBuildingIdentifier(){
		
		identifier=0;
	}
	
//    public MyActivity getContext(){
//
//        return context;
//    };
//
//    public void setContext(MyActivity context){
//
//          this.context = context;
//    }

	public MapObjectItem getMapItemByIndex(String identifier){

		MapObjectItem mapObject = null;

		if(mapObjects!=null && !mapObjects.isEmpty()){

			//not efficient?  switch to hashmap?
			for(MapObjectItem itemToCompare: mapObjects){

				if(itemToCompare.getIdentifier().equals(identifier)){

					mapObject = itemToCompare;
					break;
				}
			}
		}

//		if(mapObject==null) if(Constants.DEBUG) Log.w(Constants.GENERAL_TAG, "unable to get mapObject with identifier "+identifier);


		return mapObject;
	}


	public void removeMapItem(MapObjectItem mapItem){

		mapObjects.remove(mapItem);
//		if(Constants.DEBUG) Log.d(Constants.GENERAL_TAG, "removed mapObject " + mapItem.getIdentifier());

	}

	public void addMapObject(MapObjectItem mapItem){

		if(mapObjects==null) mapObjects = new ArrayList<MapObjectItem>();

		mapObjects.add(mapItem);
	}
	
	public List<MapObjectItem> getMapObjectList(){
		
		return mapObjects;
	}

	public long getCounter() {
		return counter;
	}

	public void incCounter() {

		counter++;
	}

	public void resetCounter() {

		counter = 0;
	}

	public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getGridX() {
		return gridX;
	}
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}
	public int getGridY() {
		return gridY;
	}
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}
	public int getOverScroll() {
		return overScroll;
	}
	public void setOverScroll(int overScroll) {
		this.overScroll = overScroll;
	}
	
	public boolean isAlternativeWorkersPhase() {
		return alternativeWorkersPhase;
	}
	public void setAlternativeWorkersPhase(boolean alternativeWorkersPhase) {
		this.alternativeWorkersPhase = alternativeWorkersPhase;
	}
	public MapCell[][] getMapGrid() {
		if(!isInit){
			try {

				throw new Exception("level data has not been initionalized!");

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return mapGrid;
	}
	public void setMapGrid(MapCell[][] mapGrid) {
		this.mapGrid = mapGrid;
	}
	

	public List<Warehouse> getWarehouses() {
		return warehouses;
	}

	public void setWarehouses(List<Warehouse> warehouses) {
		this.warehouses = warehouses;
	}
	
	public void addWarehouse(Warehouse warehouse){
		
		if(warehouses==null) warehouses=new ArrayList<Warehouse>();
		this.warehouses.add(warehouse);
	}

	public boolean removeWarehouse(Warehouse warehouse){

		if(warehouses!=null){

			return warehouses.remove(warehouse);
		}

		return false;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}
	
	public void addBuilding(Building building){
		
		if(buildings==null) buildings = new ArrayList<Building>();
		this.buildings.add(building);
	}

	public boolean removeBuilding(Building building){

		if(buildings!=null){

			return buildings.remove(building);
		}

		return false;
	}

	public Building getNextBuilding(){
		
		if(buildings==null || buildings.isEmpty()) return null;
		
		currentBuilding++;
		
		if(currentBuilding>=buildings.size()){
			currentBuilding=-1;//will be 0 next call
			return null;//to stop infinite loop
		}
		
		return buildings.get(currentBuilding);
	}

	public Warehouse getNextWarehouse(){
		
		if(warehouses==null || warehouses.isEmpty()) return null;
		
		currentWarehouse++;
		
		if(currentWarehouse>=warehouses.size()){
			currentWarehouse=-1;
			return null;
		}
		
		return warehouses.get(currentWarehouse);
	}

    public void addPathToMap(String key, CompletePath path){
		
		pathMap.put(key, path);
	}
	
	public CompletePath getAndRemovePathFromMap(String key){
		
		return pathMap.remove(key);

	}
	
	public boolean doesPathMapContainsEntry(String key){
		
		return pathMap.containsKey(key);
	}

}
