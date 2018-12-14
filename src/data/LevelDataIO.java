package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import data.map.ImageStatus;
import data.map.MapCell;
import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingItemResource;
import data.map.resources.BuildingMapItemActionResource;
import data.map.resources.BuildingResource;
import data.map.resources.Coords;
import data.map.resources.LevelResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import data.map.resources.WorkerActionResource;
import data.map.resources.WorkerImageResource;
import game.ImageHelper;
import game.items.AbstractBuilding;
import game.items.Building;
import game.items.Warehouse;

public class LevelDataIO{

//    public static String SAVED_GAME_FILENAME = "level4.json";
    
    public static String LEVEL_JSON_PREFIX = "level";
    public static String JSON_EXT = ".json";
    public static String DATA_FOLDER = "Data/";
    public static String TEMP_DATA_FOLDER = "/temp/";

    
    public static String getJsonFilePath(int level){
    	
    	return new StringBuilder(DATA_FOLDER).append(LEVEL_JSON_PREFIX).append(level).toString();
    }
    

    public static boolean doesTempLevelExist(int level){

        File file = new File(ImageHelper.getTempFolderPath().append(LEVEL_JSON_PREFIX).append(level).append(JSON_EXT).toString());
        return file.exists();
    }
    
    public static void duplicateJsonInTemp(int sourceLevel, int destLevel){
    	
    	File source = new File(ImageHelper.getTempFolderPath().append(LEVEL_JSON_PREFIX).append(sourceLevel).append(JSON_EXT).toString());
    	File dest = new File(ImageHelper.getTempFolderPath().append(LEVEL_JSON_PREFIX).append(destLevel).append(JSON_EXT).toString());
    	
    	if(!source.exists()){
    		System.err.println("LevelDataIO.duplicateJson: Returning because source json does not exist: "+sourceLevel);
    		return;
    	}
    	if(dest.exists()){
    		System.err.println("LevelDataIO.duplicateJson: Returning because dest json does exist (will not overwrite): "+destLevel);
    		return;
    	}
    	

		ImageHelper.attemptCopyAndReplace(source, dest);
    }
    
    public static void copyDataToTemp(){
    	
    	copyDataFiles(ImageHelper.getResourceFolder(null, -1, DATA_FOLDER).toString(), ImageHelper.getResourceFolder(null, -1, TEMP_DATA_FOLDER).toString());
    }
    
    public static void copyTempToData(List<LevelResource> levelsToCopy){
    	
    	String resourceStr = ImageHelper.getResourceFolder(null, -1, DATA_FOLDER).toString();
    	File resourceDir = new File(resourceStr);
    	
		if(resourceDir.exists()) ImageHelper.attemptDelete(resourceDir, true);
		
		ImageHelper.attemptToCreate(resourceDir);
		
		int level = 1;
		
		for (LevelResource levelResource : levelsToCopy) {
			
	    	File source = new File(ImageHelper.getTempFolderPath().append(LEVEL_JSON_PREFIX).append(levelResource.getId()).append(JSON_EXT).toString());
	    	File dest = new File(new StringBuilder(resourceStr).append(LEVEL_JSON_PREFIX).append(level).append(JSON_EXT).toString());
			
	    	levelResource.setId(level);
	    	
	    	if(!ImageHelper.attemptCopyAndReplace(source, dest)) break;
	    	
			level++;
		}

    }
    
	private static void copyDataFiles(String sourceDir, String destDir){
		
		File dest = new File(destDir);
		ImageHelper.attemptToCreate(dest);//move method to a generic place
		
		File source = new File(sourceDir);
		
		if(source.exists()){
		
			String[] files = source.list();
			
			for (String file : files) {
				if(file.endsWith(".json")){
					if(!ImageHelper.attemptCopyAndReplace(new File(sourceDir, file), new File(destDir, file))) break;
				}
			}
		}
	}
	


    public static void saveGame(int level){

        try {
        	
        	System.out.println(ImageHelper.getResourceFolder(null, -1, TEMP_DATA_FOLDER).append(LEVEL_JSON_PREFIX).append(level).append(JSON_EXT).toString());

            FileOutputStream outputStream = new FileOutputStream(ImageHelper.getResourceFolder(null, -1, TEMP_DATA_FOLDER).append(LEVEL_JSON_PREFIX).append(level).append(JSON_EXT).toString(), false);

            Gson gson = new GsonBuilder().create();

            String json = gson.toJson(LevelData.getInstance());

            outputStream.write(json.getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean deleteLevelFromTemp(int level){
    	
    	String filePath = ImageHelper.getResourceFolder(null, -1, TEMP_DATA_FOLDER).append(LEVEL_JSON_PREFIX).append(level).append(JSON_EXT).toString();
    
    	return ImageHelper.deleteSingleFileOrEmptyFolder(new File(filePath));
    }

    private static void soutJson(String json){

        int outputchars = 4000;

        System.out.println(json.length());

        for(int i=0; i<json.length(); i=i+outputchars){

            System.out.println(json.substring(i, (i + outputchars < json.length() ? i + outputchars : json.length())));
        }
    }
    
    public static boolean populateIdsFromMap(int mapId, Set<Integer> imageIds, Set<Integer> workerIds){
    	
    	if(LevelDataIO.doesTempLevelExist(mapId)){
    	
	    	String json = getJsonString(mapId);
	
	        if(json != null){
	    	
	        	LevelData leveldata = LevelData.getInstance(json);
	        	
	        	Set<Integer> buildingResourceids = new HashSet<Integer>();
	        	
	        	iterateBuildingResourceIdsUsed(leveldata.getBuildings(), buildingResourceids);
	        	iterateBuildingResourceIdsUsed(leveldata.getWarehouses(), buildingResourceids);
	        	
	        	populateIdsFromBuildingResourceIds(buildingResourceids, imageIds, workerIds);
	
		    	
	        	populateMapItemImageResourceIdsUsed(leveldata, imageIds);
	        	
	        	return true;
	        }
    	}
    	
    	return false;
    	
    }

    public static void populateIdsFromBuildingResourceIds(Set<Integer> buildingResourceids, Set<Integer> imageIds, Set<Integer> workerIds){
    	
    	for (int id : buildingResourceids) {
			
    		BuildingResource buildingResource = Resource.getBuildingResourceById(id);
    		
    		if(buildingResource.isWarehouse()){
    			
    			//populate worker from warehouse
    			for(BuildingItemResource itemResource : buildingResource.getBuildingItemMap().values()){
    				
    				List<WorkerActionResource> workerActions = itemResource.getWorkerActions();
    				
    				if(workerActions == null || workerActions.isEmpty()){
    					
    					WorkerImageResource workerResource = Resource.getItemResourceById(itemResource.getId()).getResource();			
    					if(workerResource!=null) workerIds.add(workerResource.getResource());
    					
    				}else{
    					
    					for (WorkerActionResource workerActionResource : workerActions) {
   
        					WorkerImageResource workerResource = Resource.getItemResourceById(workerActionResource.getWorkerin()).getResource();			
        					if(workerResource!=null) workerIds.add(workerResource.getResource());
        					
        					workerResource = Resource.getItemResourceById(workerActionResource.getWorkerout()).getResource();			
        					if(workerResource!=null)workerIds.add(workerResource.getResource());

						}
    				}
    			}
    			
    		}else{
    			
    			List<BuildingActionResource> actionList = buildingResource.getBuildingActionList();
    			
    			if(actionList!=null && !actionList.isEmpty()){
    				
    				for (BuildingActionResource buildingActionResource : actionList) {
    					
    					populateIdsForBuildingAction(buildingActionResource.getRequirements(), imageIds, workerIds);
    					populateIdsForBuildingAction(buildingActionResource.getProduces(), imageIds, workerIds);
					
					}
    			}
    		}
    		
    		if(imageIds!=null) imageIds.add(buildingResource.getImageResourceId());
    	}
    }
    
    private static void populateIdsForBuildingAction(List<? extends BuildingActionRequireResource> requirements, Set<Integer> imageIds, Set<Integer> workerIds){
    	
		if(requirements!=null && !requirements.isEmpty()){
			
			for (BuildingActionRequireResource buildingActionProduceResource : requirements) {
				
				List<WorkerActionResource> workerActions = buildingActionProduceResource.getWorkerActions();
				
				if(workerActions!=null && !workerActions.isEmpty()){
					
 					for (WorkerActionResource workerActionResource : workerActions) {

    					WorkerImageResource workerResource = Resource.getItemResourceById(workerActionResource.getWorkerin()).getResource();			
    					if(workerResource!=null) workerIds.add(workerResource.getResource());
    					
    					workerResource = Resource.getItemResourceById(workerActionResource.getWorkerout()).getResource();			
    					if(workerResource!=null) workerIds.add(workerResource.getResource());

					}
				}
				
				List<BuildingMapItemActionResource> mapItems = buildingActionProduceResource.getBuildingMapItemActionResource();
				
				if(mapItems!=null && !mapItems.isEmpty()){
					
					for (BuildingMapItemActionResource buildingMapItemActionResource : mapItems) {
						
						MapItemResource mapItem = Resource.getMapItemResourceById(buildingMapItemActionResource.getMapItem());
						processMapItemActions(mapItem, imageIds);
					}
				}
			}
		}
    }
    
    public static void populateGridDimentionsFromJson(LevelResource levelResource){
    	
		String filename = levelResource.getJson().substring(levelResource.getJson().lastIndexOf("/")+1);
		
		String path = new StringBuilder(ImageHelper.getResourceFolder(null, -1, DATA_FOLDER)).append(filename).append(JSON_EXT).toString();
		
		if(new File(path).exists()){
			
			System.out.println("processing path "+path);
			
    		LevelData leveldata = LevelData.getInstance(getJsonString(path));
    		
    		levelResource.setGridX(leveldata.getGridX());
    		levelResource.setGridY(leveldata.getGridY());
		}
    }
    
    public static String checkMapItemImageResourceIdsUsed(int id){
    	
    	return checkMapResourceIdsUsed(id, true);
    }
    
    public static String checkMapItemResourceIdsUsed(int id){
    	
    	return checkMapResourceIdsUsed(id, false);
    }
    
    private static String checkMapResourceIdsUsed(int id, boolean isImageResource){
    	
    	List<LevelResource> levelResources = Resource.getLevelResourceList();
    	
    	int level = 1;
    	
    	for (LevelResource levelResource : levelResources) {
    		
    		String filename = levelResource.getJson().substring(levelResource.getJson().lastIndexOf("/")+1);
    		
    		String path = new StringBuilder(ImageHelper.getResourceFolder(null, -1, DATA_FOLDER)).append(filename).append(JSON_EXT).toString();
    		
    		if(new File(path).exists()){
    			
    			System.out.println("processing path "+path);
    			
	    		LevelData leveldata = LevelData.getInstance(getJsonString(path));
	    		
	        	MapCell[][] mapGrid = leveldata.getMapGrid();
	        	
	            for (int x = 0; x < mapGrid.length; x++) {
	    
	                for (int y = 0; y < mapGrid[x].length; y++) {
	    
	                    MapCell cell = mapGrid[x][y];
	                    if (cell != null) {
	    
	                        List<MapItemResource> mapItemResources = cell.getMapItemResources();
	    
	                        if(mapItemResources!=null && !mapItemResources.isEmpty()) {
	                            for (MapItemResource mapItemResource : mapItemResources) {
	    
	                            	if((!isImageResource && mapItemResource.getId() == id)
	                            			|| (isImageResource && mapItemResource.getImageResourceId()==id)){
	                            		
	                            		return "Map item used on map for level "+level;
	                            	}
	                            }
	                        }
	                    }
	                }
	            }
	    		
    		}
    		
    		level++;    		
    	}
    	
    	return null;
    }
    
    public static String checkBuildingResourceUsedInMap(int id){
    	
    	return checkBuildingIdUsedInMap(id, true);
    }
    
    public static String checkBuildingImageResourceUsedInMap(int id){
    	
    	return checkBuildingIdUsedInMap(id, false);
    }
    
    private static String checkBuildingIdUsedInMap(int id, boolean isImageResource){
    	
    	List<LevelResource> levelResources = Resource.getLevelResourceList();
    	
    	int level = 1;
    	
    	for (LevelResource levelResource : levelResources) {
    		
    		String filename = levelResource.getJson().substring(levelResource.getJson().lastIndexOf("/")+1);
    		
    		String path = new StringBuilder(ImageHelper.getResourceFolder(null, -1, DATA_FOLDER)).append(filename).append(JSON_EXT).toString();
			
    		if(new File(path).exists()){
    			
    			System.out.println("processing path "+path);
    			
	    		LevelData leveldata = LevelData.getInstance(getJsonString(path));
	    		
	    		System.out.println("checking building id "+ id +" on level "+level);
	    		
	    		if(isImageResource){

		    		if(getWarehouseResourceIdsUsed(leveldata).contains(id)) 
		    			return "warehouse used on map for level "+level;
		    		
		    		if(getBuildingResourceIdsUsed(leveldata).contains(id)) 
		    			return "Building used on map for level "+level;
	    		}else{
	    			
	    			String warning = iterateBuildingImageResourceIdsUsed(leveldata.getBuildings(), id, level);
	    			if(warning!=null) return warning;
	    			warning = iterateBuildingImageResourceIdsUsed(leveldata.getWarehouses(), id, level);
	    			if(warning!=null) return warning;
	    		}
	    		
    		}else{
    			System.err.println(path + " does not exist for level "+level);
    		}
    		
    		level++;
		}
    	
    	return null;
    }
    
    private static Set<Integer> getBuildingResourceIdsUsed(LevelData leveldata){
    	
    	Set<Integer> ids = new HashSet<Integer>();

        iterateBuildingResourceIdsUsed(leveldata.getBuildings(), ids);
    	
    	return ids;
    }
    
    private static Set<Integer> getWarehouseResourceIdsUsed(LevelData leveldata){
    	
    	Set<Integer> ids = new HashSet<Integer>();

        iterateBuildingResourceIdsUsed(leveldata.getWarehouses(), ids);
    	
    	return ids;
    }
    
    private static void iterateBuildingResourceIdsUsed(List<? extends AbstractBuilding> buildings, Set<Integer> ids){
    	
        if(buildings!=null){

            for(AbstractBuilding building :  buildings){
            	
            	ids.add(building.getResourceId());
            	
            	System.out.println("adding buildings id from map "+building.getResourceId());
            }
        }
    }
    
    private static String iterateBuildingImageResourceIdsUsed(List<? extends AbstractBuilding> buildings, int id, int level){
    	
        if(buildings!=null){

            for(AbstractBuilding building :  buildings){
            	
            	BuildingResource resource = Resource.getBuildingResourceById(building.getResourceId());

            	if(id == Resource.getBuildingResourceById(building.getResourceId()).getImageResourceId()){
            		
            		return "image resource used on building; "+resource.getName()+" on map level "+level;
            	}
            }
        }
        return null;
    }

    public static boolean loadData(int level){

    	String json = getJsonString(level);

        if(json != null){

            LevelData leveldata = LevelData.getInstance(json);

            MapCell[][] mapGrid = leveldata.getMapGrid();

            List<Building> buildings = leveldata.getBuildings();

            if(buildings!=null){

                for(AbstractBuilding building :  buildings){

                    configureMapAndWorkersFromBuilding(building, mapGrid);
                }
            }

            List<Warehouse> warehouses = leveldata.getWarehouses();

            for (AbstractBuilding building : warehouses){

                configureMapAndWorkersFromBuilding(building, mapGrid);
            }


            for (int x = 0; x < mapGrid.length; x++) {

                for (int y = 0; y < mapGrid[x].length; y++) {

                    MapCell cell = mapGrid[x][y];
                    if (cell != null) {

                        List<ImageStatus> imageStatuses = cell.getMapObjectImages();

                        if(imageStatuses!=null && !imageStatuses.isEmpty()) {
                            for (ImageStatus imageStatus : imageStatuses) {

                                imageStatus.restoreStateAfterLoad();
                            }
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }
    
    private static void populateMapItemImageResourceIdsUsed(LevelData leveldata, Set<Integer> imageIds){
    	
    	MapCell[][] mapGrid = leveldata.getMapGrid();
    	
        for (int x = 0; x < mapGrid.length; x++) {

            for (int y = 0; y < mapGrid[x].length; y++) {

                MapCell cell = mapGrid[x][y];
                if (cell != null) {

                    List<MapItemResource> mapItemResources = cell.getMapItemResources();

                    if(mapItemResources!=null && !mapItemResources.isEmpty()) {
                        for (MapItemResource mapItemResource : mapItemResources) {

                        	processMapItemActions(mapItemResource, imageIds);
                        }
                    }
                }
            }
        }
    }
    
    private static void processMapItemActions(MapItemResource mapItemResource, Set<Integer> imageIds){
    	
    	if(mapItemResource==null) return;
    	
//    	System.out.println("process map item "+mapItemResource.getName());
    	
    	imageIds.add(mapItemResource.getImageResourceId());
    	
    	List<MapItemActionResource> mapItemActions = mapItemResource.getMapItemActionList();
    	
    	for (MapItemActionResource mapItemActionResource : mapItemActions) {
			
    		int mapItemId = mapItemActionResource.getMapitem();
    		if(mapItemId > -1 && mapItemId != mapItemResource.getId()){
    			
    			MapItemResource actionsMapItem = Resource.getMapItemResourceById(mapItemId);
    			
    			//recursive call
    			if(!imageIds.contains(actionsMapItem.getImageResourceId())) processMapItemActions(actionsMapItem, imageIds);
    		}
		}
    }
    
    private static String getJsonString(int level){
    	
    	return getJsonString(ImageHelper.getResourceFolder(null, -1, TEMP_DATA_FOLDER).append(LEVEL_JSON_PREFIX).append(level).append(JSON_EXT).toString());
    }

    
    private static String getJsonString(String path){
    	
        String json = null;
        
        BufferedReader bufferedReader = null;
        
        try{

	        try {
	            FileInputStream fis = new FileInputStream(path);
	            InputStreamReader isr = new InputStreamReader(fis);
	            bufferedReader = new BufferedReader(isr);
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = bufferedReader.readLine()) != null) {
	                sb.append(line);
	            }
	
	            json = sb.toString();
	
//	            soutJson(json);
	
	        } finally {
	        	if(bufferedReader!=null) bufferedReader.close();
	        }
		} catch (Exception e) {
            e.printStackTrace();

        }
        
        return json;
    }

    private static void configureMapAndWorkersFromBuilding(AbstractBuilding building, MapCell[][] mapGrid){

        int posX = building.getPosX();
        int posY = building.getPosY();

        BuildingResource buildingR = Resource.getBuildingResourceById(building.getResourceId());

        Coords span = buildingR.getImageResource().getSpan();

        for (int x = posX; x > posX-span.x; x--) {
            for (int y = posY; y > posY-span.y; y--) {

                mapGrid[x][y].setBuilding(building);
               

            }
        }

    }
}
