 package data.map;

 import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.LevelData;
import data.map.resources.BuildingResource;
import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.MapItemResource;
import data.map.resources.Resource;
import game.items.AbstractBuilding;
import game.items.Building;
import game.items.Warehouse;

public class MapUtils {



	public static boolean addBuildingToMap(int resourceId, Coords position, boolean isEditor){

		BuildingResource buildingR = Resource.getBuildingResourceById(resourceId);

        ImageResource imageResource = buildingR.getImageResource();
		Coords span = imageResource.getSpan();

        if(!canAddBuildingToMap(position, buildingR, span)) return false;

        System.out.println("adding "+buildingR.getName());

        LevelData levelData = LevelData.getInstance();

		MapCell[][] mapGrid = levelData.getMapGrid();
		
		long reference = levelData.getNextIdentifier();
		
		boolean isWarehouse = buildingR.isWarehouse();
		
		AbstractBuilding abBuilding = null;

		if(isWarehouse){
			Warehouse warehouse = new Warehouse(reference, position.x, position.y, resourceId);
			populateGeneralBuildingDetails(warehouse, buildingR);
			levelData.addWarehouse(warehouse);
			abBuilding = warehouse;

		}else{
			Building building = new Building(reference, position.x, position.y, resourceId, buildingR.getBuildingActionList());
			populateGeneralBuildingDetails(building, buildingR);
			levelData.addBuilding(building);
			abBuilding = building;
		}
		
		boolean[][] walkover = imageResource.getWalkover();
		for (int x = position.x; x > position.x-span.x; x--) {
			for (int y = position.y; y > position.y-span.y; y--) {
				
				if(mapGrid[x][y]==null) mapGrid[x][y] = new MapCell();

				mapGrid[x][y].setCanWalkOver(walkover[span.x-(position.x-x)-1][span.y-(position.y-y)-1]);
				
//				mapGrid[x][y].setBuildingRef(reference);
				mapGrid[x][y].setBuilding(abBuilding);
				
			}
		}
		
		createImageStatus(resourceId, imageResource, mapGrid, position, false, -1, isEditor, span);
		
		if(isEditor) abBuilding.setUnderConstruction(false);
		
//		soutWalkover(mapGrid, levelData.getGridX(), levelData.getGridY(), position,  buildingR.getImageResource().getFirstHotspot());
		
		return true;
		
	}

    public static boolean canAddBuildingToMap(Coords position,  BuildingResource buildingR, Coords span){

        LevelData levelData = LevelData.getInstance();

        MapCell[][] mapGrid = levelData.getMapGrid();

        System.out.println("attemping to add "+buildingR.getName());

        return canPlaceObjectHere(buildingR.getAllowedon(), span, position, mapGrid, levelData.getGridX(), levelData.getGridY());
    }
    
    public static boolean canAddMapItemToMap(Coords position,  MapItemResource mapItemR, Coords span){

        LevelData levelData = LevelData.getInstance();

        MapCell[][] mapGrid = levelData.getMapGrid();

        return canPlaceObjectHere(mapItemR.getAllowedon(), span, position, mapGrid, levelData.getGridX(), levelData.getGridY());
    }
	
	private static void populateGeneralBuildingDetails(AbstractBuilding absBuilding, BuildingResource buildingR){
		
		absBuilding.setUnderConstruction(true);
		absBuilding.initWorkers(buildingR.getWorkers());
	}

	public static boolean removeBuildingFromMap(AbstractBuilding building){

		boolean success = false;

		BuildingResource buildingR = Resource.getBuildingResourceById(building.getResourceId());

		ImageResource imageResource = buildingR.getImageResource();
		Coords span = imageResource.getSpan();

		LevelData levelData = LevelData.getInstance();

		MapCell[][] mapGrid = levelData.getMapGrid();

		int posX = building.getPosX();
		int posY = building.getPosY();

		Set<MapObjectItem> affectedMapObjects = new HashSet<MapObjectItem>();

		for (int x = posX; x > posX-span.x; x--) {
			for (int y = posY; y > posY-span.y; y--) {

				mapGrid[x][y].setBuilding(null);
				fixAffectedMapObject(mapGrid, x, y);
			}
		}

		soutWalkover(mapGrid, levelData.getGridX(), levelData.getGridY(), null, null);

		if(buildingR.isWarehouse()){
			levelData.removeWarehouse((Warehouse) building);
		}else{
			levelData.removeBuilding((Building) building);
		}

		return success;
	}
	
	public static boolean addMapItemToMap(int resourceIndex, Coords position, boolean isEditor){
		
		LevelData levelData = LevelData.getInstance();
		
		MapCell[][] mapGrid = levelData.getMapGrid();
		
		MapItemResource mapItemR = Resource.getMapItemResourceById(resourceIndex);
		
		ImageResource imageResource = mapItemR.getImageResource();
		
		Coords span = imageResource.getSpan();
		
		System.out.println("attemping to add "+mapItemR.getName());
		
		if(!canPlaceObjectHere(mapItemR.getAllowedon(), span, position, mapGrid, levelData.getGridX(), levelData.getGridY())) return false;
		
		//lets add the item
//		System.out.println("adding item "+mapItemR.getLocalizedName()+" to map");
		
		MapObjectItem mapObject = new MapObjectItem(position, resourceIndex);
		
		mapObject.initItems();
		
		if(!isEditor) mapObject.setLocked(true);
		
		int drawFirstLevel = 0;

		levelData.addMapObject(mapObject);
		
		boolean[][] walkover = imageResource.getWalkover();
		for (int x = position.x; x > position.x-span.x; x--) {
			for (int y = position.y; y > position.y-span.y; y--) {
				
				if(mapGrid[x][y]==null) mapGrid[x][y] = new MapCell();
				
				mapGrid[x][y].setCanWalkOver(walkover[span.x-(position.x-x)-1][span.y-(position.y-y)-1]);
				
				mapGrid[x][y].addMapObjectIdentifier(mapObject.getIdentifier());
				
				if(mapItemR.isDrawFirst()==true){
					int totalMOinCell = mapGrid[x][y].getNumberOfMapObjects();//this includes the one just added			
					if(drawFirstLevel<totalMOinCell) drawFirstLevel=totalMOinCell;
				}
				
			}
		}

		System.out.println("drawlevel"+drawFirstLevel);
		createImageStatus(resourceIndex, imageResource, mapGrid, position, true, drawFirstLevel, isEditor, span);
		
//		imageStatus.setStatus()
		
		
//		mapGrid[position.x][position.y].set

//		soutWalkover(mapGrid, levelData.getGridX(), levelData.getGridY(), position, mapItemR.getImageResource().getFirstHotspot());
		
		return true;
		
	}
	
	private static void createImageStatus(int resourceIndex, ImageResource imageResource, MapCell[][] mapGrid, Coords position, boolean isMapItem, int drawFirstLevel, boolean isEditor, Coords span){
		
		ImageStatus imageStatus = new ImageStatus(imageResource, isMapItem, span, resourceIndex);

		boolean ishidden = (isMapItem && !isEditor);
		
		imageStatus.initCreationStatus(resourceIndex, ishidden, isMapItem);
		
		imageStatus.setDrawLevel((short) drawFirstLevel);
		
		if(isEditor) imageStatus.constructionComplete();
		
//		soutIscovered(mapGrid, levelData.getGridX(), levelData.getGridY());
		
		mapGrid[position.x][position.y].addMapObjectImages(imageStatus);
	}

    private static boolean canPlaceObjectHere(List<Integer> allowedOn, Coords span, Coords position, MapCell[][] mapGrid, int maxX, int maxY){

        for (int x = position.x; x > position.x-span.x; x--) {
            for (int y = position.y; y > position.y-span.y; y--) {

                if(x<0 || x>=maxX || y<0 || y>=maxY) return false;

                if(mapGrid[x][y]!=null && mapGrid[x][y].getBuilding()!=null)return false;//cannot place anything on a building

                if(mapGrid[x][y]==null || mapGrid[x][y].getLastMapObject()==null){

                    if(allowedOn!= null && !allowedOn.contains(-1)){
//                        if(Constants.DEBUG) System.out.println("not allowed on grass");
                        return false;//not allowed on grass

                    }
                }else{

                    MapObjectItem lastMapObj = mapGrid[x][y].getLastMapObject();

                    if((allowedOn==null && lastMapObj.getItemId()!=-1) ||//if allowed on is null then it can only be on grass
                            !allowedOn.contains(lastMapObj.getItemId())){ //index is not in allowed list

//                        if(Constants.DEBUG) System.out.println("not allowed on "+Resource.getMapItemResourceById(lastMapObj.getItemId()).getName());
//						if(Constants.DEBUG) System.out.println("mapGrid[x][y].getBuildingRef()>-1:"+(mapGrid[x][y].getBuildingRef()>-1));
//						if(Constants.DEBUG) System.out.println("allowedOn==null && lastMapObj.getItemId()!=0:"+(allowedOn==null && lastMapObj.getItemId()!=0));
//						if(Constants.DEBUG) System.out.println("!allowedOn.contains(lastMapObj.getItemId():"+(!allowedOn.contains(lastMapObj.getItemId())));
//						if(Constants.DEBUG) System.out.println(lastMapObj.getItemId());
                        return false;
                    }
                }
            }
        }

        return true;

    }
	
	public static boolean removeObjectFromMap(MapObjectItem mapObject){
		
		boolean success = false;
		
		if(!mapObject.isCovered()){ //should be check before calling this!
		
			Coords coords = mapObject.getCoords();
			
			LevelData levelData = LevelData.getInstance();
	
			MapCell[][] mapGrid = levelData.getMapGrid();
			Coords span = Resource.getMapItemResourceById(mapObject.getItemId()).getImageResource().getSpan();
			
			Set<MapObjectItem> affectedMapObjects = new HashSet<MapObjectItem>();

			String mapItemId = mapObject.getIdentifier();
			
			for (int x = coords.x; x > coords.x-span.x; x--) {
				for (int y = coords.y; y > coords.y-span.y; y--) {
					
					if(mapGrid[x][y].getLastMapObject()==mapObject){
						System.out.println("removing from x:"+x + "y:"+y + " id:"+mapItemId);
						success = mapGrid[x][y].removeMapObjectIdentifier(mapItemId);
						
						if(!success) return false;
					}else{
						System.out.println("mapObject does match last one in last in cell, the sky has fallen");
					}
					fixAffectedMapObject(mapGrid, x, y);
					
				}
				
			}
			levelData.removeMapItem(mapObject);

			soutWalkover(mapGrid, levelData.getGridX(), levelData.getGridY(), null, null);
		}
		
		
		return success;
		

	}
	
	private static void fixAffectedMapObject(MapCell[][] mapGrid, int x, int y){

		MapObjectItem newLastObject = mapGrid[x][y].getLastMapObject();

		if(newLastObject!=null){

			newLastObject.setCovered(mapObjectCoveredCheck(newLastObject, mapGrid));
			MapItemResource mir = Resource.getMapItemResourceById(newLastObject.getItemId());
			Coords newLastcoords = newLastObject.getCoords();
			int xDif = newLastcoords.x() - x;
			int yDif = newLastcoords.y() - y;
			Coords span = mir.getImageResource().getSpan();			
			mapGrid[x][y].setCanWalkOver(mir.getImageResource().getWalkover()[span.x()-1-xDif][span.y()-1-yDif]);
		}else{

			mapGrid[x][y].setCanWalkOver(true);
		}
	}

	private static boolean mapObjectCoveredCheck(MapObjectItem affectedmapObject, MapCell[][] mapGrid){
		
		Coords coords = affectedmapObject.getCoords();
		Coords span = Resource.getMapItemResourceById(affectedmapObject.getItemId()).getImageResource().getSpan();
		
		for (int x = coords.x; x > coords.x-span.x; x--) {
			for (int y = coords.y; y > coords.y-span.y; y--) {
				
				if(mapGrid[x][y].getLastMapObject()==null){
					System.out.println("mapGrid is not configure correctly, "+affectedmapObject.getItemId()+" is missing, the sky has fallen");
				}else{
					if(mapGrid[x][y].getLastMapObject()!=affectedmapObject){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static MapObjectItem getLastmapObjectFromCoords(Coords pos){
		
		MapObjectItem lastMapObject = null;

        LevelData levelData = LevelData.getInstance();
		
		MapCell[][] grid = levelData.getMapGrid();
		
		if(pos.x>=0 && pos.y>=0 && pos.x<levelData.getGridX() && pos.y<levelData.getGridY() && grid[pos.x][pos.y]!=null){
			
			lastMapObject = grid[pos.x][pos.y].getLastMapObject();
		}

		return lastMapObject;
	}

    public static AbstractBuilding getBuildingFromCoords(Coords pos){

        LevelData levelData = LevelData.getInstance();

        MapCell[][] grid = levelData.getMapGrid();

        AbstractBuilding building = null;

        if(pos.x>=0 && pos.y>=0 && pos.x<=levelData.getGridX() && pos.y<=levelData.getGridY() && grid[pos.x][pos.y]!=null){

            building = grid[pos.x][pos.y].getBuilding();

        }

        return building;
    }


	//temp!
	public static void soutWalkover(MapCell[][] mapGrid, int gx, int gy, Coords posistion, Coords hotspot){
		
		for (int y = 0; y < gy; y++) {
			for (int x = 0; x < gx; x++) {
				
				if(posistion!=null && x==posistion.x-hotspot.x && y==posistion.y-hotspot.y){
					
					System.out.print("X");
				}else if(mapGrid[x][y] == null){//|| mapGrid[x][y].getMapObjects().isEmpty()){	
					System.out.print(".");
				}else if(mapGrid[x][y].isCanWalkOver()){
					System.out.print("Y");
				}else if(!mapGrid[x][y].isCanWalkOver()){
					System.out.print("N");
				}else{//probably empty
					 System.out.print(".");
				}
				

				
			}
			System.out.println("");
		}
		
		System.out.println("");
	}

    public static Building getBuildingById(long id){

        return getBuildingById(id, true);
    }

    public static Warehouse getWarehouseById(long id){

        return getWarehouseById(id, true);
    }

    public static AbstractBuilding getAnyBuildingById(long id){

        AbstractBuilding building = getBuildingById(id, false);

        if(building==null) return getWarehouseById(id, true);

        return building;
    }


    private static Building getBuildingById(long id, boolean throwError){

        List<Building> buildingsList = LevelData.getInstance().getBuildings();

        if(buildingsList!=null && !buildingsList.isEmpty()){

            for (Building building : buildingsList) {

                if(building.getBuildingNumber() == id){
                    return building;
                }
            }
        }

        if(throwError)
            throw new RuntimeException("trying to get" + id + " but the building no longer exist");

        return null;
    }

    private static Warehouse getWarehouseById(long id, boolean throwError){

        List<Warehouse> warehouseList = LevelData.getInstance().getWarehouses();

        if(warehouseList!=null && !warehouseList.isEmpty()){

            for (Warehouse warehouse : warehouseList) {

                if(warehouse.getBuildingNumber() ==  id){

                    return warehouse;
                }
            }
        }

        if(throwError)
            throw new RuntimeException("trying to get" + id + " but the building no longer exist");

        return null;

    }


}
