package data.map.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Resource {

    private static List<LevelResource> levelResources;

//    private static String workerPath;
	private static List<ItemResource> itemResources;
	private static List<MapItemResource> mapItemResources;
	private static List<BuildingResource> buildingResources;
	private static List<ImageResource> mapImageResources;
	private static List<ImageResource> buildingImageResources;
	private static int minorVersion;
	private static int majorVersion;
	private static String codename;
	private static String defaultLocale;
	private static String xmlstatus;
	
	//editor helpers
	private static Set<Integer> workerResourceFolderNumber;
	private static int lastWorkerResourceFolderNumber = 1;

    public static void init(){

        levelResources = new ArrayList<LevelResource>();
        itemResources = new ArrayList<ItemResource>();
        mapItemResources = new ArrayList<MapItemResource>();
        buildingResources = new ArrayList<BuildingResource>();
        mapImageResources = new ArrayList<ImageResource>();
        buildingImageResources = new ArrayList<ImageResource>();
		minorVersion = 0;
		majorVersion = 0;
//		workerPath = null;
		codename = null;
		defaultLocale = null;
		xmlstatus = null;
    }
    
    public static int getNumberOfLevelRes(){
    	if(levelResources==null) return 0;
    	return levelResources.size();
    }
    
    public static int getNumberOfItemRes(){
    	if(itemResources==null) return 0;
    	return itemResources.size();
    }
    
    public static int getNumberOfMapItemRes(){
    	if(mapItemResources==null) return 0;
    	return mapItemResources.size();
    }
    
    //not the best
    public static int getNumberOfBuildingRes(boolean isWarehouse){
    	if(buildingResources==null) return 0;
    	int i =0;
    	for (BuildingResource buildingResource : buildingResources) {
			if(isWarehouse == buildingResource.isWarehouse()) i++;
		}
    	return i;
    } 

    
    public static int getNumberOfMapImageRes(){
    	if(mapImageResources==null) return 0;
    	return mapImageResources.size();
    }
    
    public static int getNumberOfBuildingImageRes(){
    	if(buildingImageResources==null) return 0;
    	return buildingImageResources.size();
    }
    
	public static String getWorkerPath() {
		return "Images/workers/";
	}
	
	public static String getMapPath(){
		return "Images/Maps/";
	}

//	public static void setWorkerPath(String workerPath) {
//		Resource.workerPath = workerPath;
//	}
	
	public static String getCodename() {
		return codename;
	}

	public static void setCodename(String codename) {
		Resource.codename = codename;
	}

	public static String getXmlstatus() {
		return xmlstatus;
	}

	public static void setXmlstatus(String xmlstatus) {
		Resource.xmlstatus = xmlstatus;
	}
	
	public static void increaseMinorVersion(){
		minorVersion++;
	}
	
	public static void increaseMajorVersion(){
		majorVersion++;
	}

	public static int getMinorVersion() {
		return minorVersion;
	}

	public static void setMinorVersion(int minorVersion) {
		Resource.minorVersion = minorVersion;
	}

	public static int getMajorVersion() {
		return majorVersion;
	}

	public static void setMajorVersion(int majorVersion) {
		Resource.majorVersion = majorVersion;
	}
	
	public static String getVersionStr(){
		
		//passing in major version doesn't work
		return new StringBuilder()
						 .append(Resource.getMajorVersion())
						 .append(".")
						 .append(Resource.getMinorVersion()).toString();

	}
	
	public static float getVersion(){
		
		return Float.parseFloat(getVersionStr());
	}
	
	public static String getDefaultLocale() {
		return defaultLocale;
	}

	public static void setDefaultLocale(String defaultLocale) {
		Resource.defaultLocale = defaultLocale;
	}

	public static void addMapImageResource(ImageResource imageResource){

		mapImageResources.add(imageResource);
	}

	public static void setMapImageResource(int pos, ImageResource imageResource){

		mapImageResources.set(pos, imageResource);
	}


	public static ImageResource getMapImageResource(int pos){

		return mapImageResources.get(pos);
	}


	public static List<ImageResource> getMapImageResourceList(){

		return mapImageResources;
	}

	public static ImageResource getMapImageResourceById(int id){

		for (ImageResource image : mapImageResources) {

			if(image.getId() == id){

				return image;
			}
		}

//		if(Constants.DEBUG) System.out.println("could not find image id "+id);

		return null;
	}
	
	public static List<Integer> getAllMapImageResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (ImageResource image : mapImageResources) {

			ids.add(image.getId());
		}
		
		return ids;
	}
	
	
	public static void addBuildingImageResource(ImageResource imageResource){

		buildingImageResources.add(imageResource);
	}

	public static void setBuildingImageResource(int pos, ImageResource imageResource){

		buildingImageResources.set(pos, imageResource);
	}


	public static ImageResource getBuildingImageResource(int pos){

		return buildingImageResources.get(pos);
	}


	public static List<ImageResource> getBuildingImageResourceList(){

		return buildingImageResources;
	}

	public static ImageResource getBuildingImageResourceById(int id){

		for (ImageResource image : buildingImageResources) {

			if(image.getId() == id){

				return image;
			}
		}

//		if(Constants.DEBUG) System.out.println("could not find image id "+id);

		return null;
	}
	
	public static List<Integer> getAllBuildingImageResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (ImageResource image : buildingImageResources) {

			ids.add(image.getId());
		}
		
		return ids;
	}

	public static void addItemResource(ItemResource itemResource){
		
		itemResources.add(itemResource);
	}
	
	public static void removeItemResource(ItemResource itemResource){
		
		int index = itemResources.lastIndexOf(itemResource);
		
		if(index > -1) itemResources.remove(index);

	}
	
	public static void removeMapImageResource(ImageResource imageResource){
		
		int index = mapImageResources.lastIndexOf(imageResource);
		
		if(index > -1) mapImageResources.remove(index);

	}
	
	public static void removeBuildingImageResource(ImageResource imageResource){
		
		int index = buildingImageResources.lastIndexOf(imageResource);
		
		if(index > -1) buildingImageResources.remove(index);

	}
	
	public static void removeMapItemResource(MapItemResource mapItemResource){
		
		int index = mapItemResources.lastIndexOf(mapItemResource);
		
		if(index > -1) mapItemResources.remove(index);

	}
	
	public static void removeBuildingResource(BuildingResource buildingResource){
		
		int index = buildingResources.lastIndexOf(buildingResource);
		
		if(index > -1) buildingResources.remove(index);

	}

	public static void setItemResource(int pos, ItemResource itemResource){
		
		itemResources.set(pos, itemResource);
	}

	
	public static ItemResource getItemResource(int pos){
		
		return itemResources.get(pos);
	}
	
	public static void nullItemResources(){
		
		itemResources=null;
	}
	
	public static ItemResource getLastItemResource(){
		
		return itemResources.get(itemResources.size()-1);
	}
	
	public static List<ItemResource> getItemResourceList(){
		
		return itemResources;
	}

	public static ItemResource getItemResourceById(int id){

		for (ItemResource item : itemResources) {

			if(item.getId() == id){

				return item;
			}
		}

		return null;
	}
	
	public static List<Integer> getAllImageResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (ImageResource resource : mapImageResources) {
			
			ids.add(resource.getId());
		}
		
		for (ImageResource resource : buildingImageResources) {
			
			ids.add(resource.getId());
		}
		
		return ids;
	}
	
	
	public static int getNextImageResourceId(){
		
		return findUnusedId(getAllImageResourceIds());
		

	}
	
	public static List<Integer> getAllItemResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (ItemResource item : itemResources) {
			
			ids.add(item.getId());
		}
		
		return ids;
	}

	public static int getNextItemResourceId(){
		
		return findUnusedId(getAllItemResourceIds());
	}
	
//	public static int findUnusedId(int[] ids){
//		
//		//must be a better way :|
//		List<Integer> intList = new ArrayList<Integer>();
//		
//		for (int i = 0; i < ids.length; i++) {
//			intList.add(ids[i]);
//		}
//		
//		return findUnusedId(intList);
//	}
	
	//global these up to save to looping?
	public static int findUnusedId(List<Integer> ids){
		
		int lastId;
		
		if(ids == null || ids.isEmpty()){
			lastId = 1;
		}else{
			lastId = ids.get(ids.size()-1);
			while(ids.contains(++lastId));
		}

		return lastId;
	}
	
	
	public static int getNextWorkerResourceFolderNumber(){
	
		//get all used
		if(workerResourceFolderNumber == null){
			
			workerResourceFolderNumber = new HashSet<Integer>();
			
			for (ItemResource itemResource : itemResources) {
				
				if(itemResource.getResource()!=null){
					
					workerResourceFolderNumber.add(itemResource.getResource().getResource());
				}
			}
			
		}
		
		while(workerResourceFolderNumber.contains(lastWorkerResourceFolderNumber)){
			lastWorkerResourceFolderNumber++;
		}
		
		//assume we are going to use it
		workerResourceFolderNumber.add(lastWorkerResourceFolderNumber);
		
		return lastWorkerResourceFolderNumber;
	}
	
	//global up a set of names?
	public static boolean isImageNameUsed(String name, int id){
		
		name = name.toLowerCase();
		
		for (ImageResource resource : mapImageResources) {
			
			if(resource.getId()!=id && resource.getNameFromDir().toLowerCase().equals(name)) return true;
		}
		
		for (ImageResource resource : buildingImageResources) {
			
			if(resource.getId()!=id && resource.getNameFromDir().toLowerCase().equals(name)) return true;
		}
		
		
		return false;
	}
			
	
	public static String getItemLocalizedNameById(int id){
		
		for (ItemResource item : itemResources) {
			
			if(item.getId() == id){
				
				return item.getLocalizedName();
			}
		}
		
		return "unable to find item by id "+id;
	}
	
	public static String getItemInternalNameById(int id){
		
		for (ItemResource item : itemResources) {
			
			if(item.getId() == id){
				
				return item.getName();
			}
		}
		
		return "unable to find item by id "+id;
	}
	
	//return all with descriptions
	public static List<ItemResource> getWarehouseItemInternalNames(){
		
		List<ItemResource> names = new ArrayList<ItemResource>();
		
		for (ItemResource item : itemResources) {
			
			if(item.getInfoResource().hasTexts()) names.add(item);

		}
		
		return names;
	}
	
	//return all with images
	public static List<ItemResource> getWorkerItemInternalNames(){
		
		List<ItemResource> names = new ArrayList<ItemResource>();
		
		for (ItemResource item : itemResources) {
			
			if(item.getResource()!=null) names.add(item);

		}
		
		return names;
	}

	public static String getItemLocalizedDescriptionById(int id){

		for (ItemResource item : itemResources) {

			if(item.getId() == id){

				return item.getDescription();
			}
		}

		return "unable to find item by id "+id;
	}
	
	//TODO improve this (maybe first time compile set and then remember where the loop got too?)
	public static int getFirstAvalibaleItemId(){
		
		Set<Integer> allIds = new HashSet<Integer>();
		
		for (ItemResource item : itemResources) {
			
			allIds.add(item.getId());
			
		}
		
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			
			if(!allIds.contains(i)){
				return i;
			}
		}
		
		//handle this better
		throw new RuntimeException("No more room at the inn(teger)");
	}
	
	public static void addMapItemResource(MapItemResource mapItemResource){
		
		mapItemResources.add(mapItemResource);
	}
	
	public static void setMapItemResource(int pos, MapItemResource mapItemResource){
		
		mapItemResources.set(pos, mapItemResource);
	}
	
	public static MapItemResource getMapItemResourceById(int id){

		for(MapItemResource mapItemResource : mapItemResources){

			if(mapItemResource.getId()==id) return mapItemResource;
		}

		System.err.println("ERROR: unable to find map resource for id "+id);
		return null;
	}

	public static MapItemResource getMapItemResource(int pos){

		return mapItemResources.get(pos);
	}
	
	public static void nullMapItemResources(){
		
		mapItemResources=null;
	}
	
	public static MapItemResource getLastMapItemResource(){
		
		return mapItemResources.get(mapItemResources.size()-1);
	}
	
	public static List<MapItemResource> getMapItemResourceList(){
		
		return mapItemResources;
	}
	
	public static List<Integer> getAllMapItemResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (MapItemResource mapItem : mapItemResources) {
			
			ids.add(mapItem.getId());
		}
		
		return ids;
	}
	
	public static List<Integer> getFilteredBuildingResourceIds(boolean isWarehouse){
		
		List<Integer> ids = new ArrayList<>();
		
		for (BuildingResource building : buildingResources) {
			
			if(isWarehouse == building.isWarehouse())ids.add(building.getId());
		}
		
		return ids;
	}
	
	public static List<Integer> getAllBuildingResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (BuildingResource building : buildingResources) {
			
			ids.add(building.getId());
		}
		
		return ids;
	}
	
	public static int getNextMapItemResourceId(){
		
		return findUnusedId(getAllMapItemResourceIds());
	}
	
	public static void addBuildingResource(BuildingResource buildingResource){
		
		buildingResources.add(buildingResource);

	}
	
	public static void setBuildingResource(int pos, BuildingResource buildingResource){
		
		buildingResources.set(pos, buildingResource);
	}
	
	public static int getBuildingResourceIdFromIndex(int pos){
		
		return buildingResources.get(pos).getId();
	}
	
	public static BuildingResource getBuildingResource(int pos){
		
		return buildingResources.get(pos);
	}
	
	public static BuildingResource getBuildingResourceById(int id){

		for (BuildingResource buildingResource : buildingResources) {

			if(buildingResource.getId() == id){

				return buildingResource;
			}
		}
		
		System.err.println("unable to find building by ID " + id + ", returning null");

		return null;
	}
	
	public static void nullBuildingResources(){
		
		buildingResources=null;
	}
	
	public static BuildingResource getLastBuildingResource(){
		
		return buildingResources.get(buildingResources.size()-1);
	}
	
	public static List<BuildingResource> getBuildingResourceList(){
		
		return buildingResources;
	}

	public static int getNextBuildingResourceId(){
		
		return findUnusedId(getAllBuildingResourceIds());
	}


    public static void addLevelResource(LevelResource levelResource){

        levelResources.add(levelResource);
    }

    public static void setLevelResource(int pos, LevelResource levelResource){

        levelResources.set(pos, levelResource);
    }

    public static LevelResource getLevelResource(int pos){

        return levelResources.get(pos);
    }

	public static LevelResource getLevelResourceById(int id){

		for(LevelResource levelResource : levelResources){

			if(levelResource.getId() == id){

				return levelResource;
			}
		}

		System.err.println("ERROR: level "+id+" was not found in xml");
		return null;
	}
	
	public static List<Integer> getAllLevelResourceIds(){
		
		List<Integer> ids = new ArrayList<>();
		
		for (LevelResource level : levelResources) {

			ids.add(level.getId());
		}
		
		return ids;
	}

    public static void nullLevelResources(){

        levelResources=null;
    }

    public static LevelResource getLastLevelResource(){

        return levelResources.get(levelResources.size()-1);
    }

    public static List<LevelResource> getLevelResourceList(){

        return levelResources;
    }
    
    public static void replaceLevelResources(List<LevelResource> newLevelResources){
    	
    	levelResources = new ArrayList<LevelResource>();
    	
    	for (LevelResource levelResource : newLevelResources) {
			
    		levelResources.add(levelResource.copy());
		}
    }

}
