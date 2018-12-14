package data.map.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import data.LevelDataIO;

public class LevelResource {

    private int id;
    private String title;
    //in seconds
    private int gold;
    private int silver;
    private int bronze;

    private List<LevelTargetResource> targets = new ArrayList<LevelTargetResource>();
    private InfoResource texts = new InfoResource();
    private List<Integer> buildings;
    private int[] images;
    private int[] workers;

    private String mapPath;
    private String json;
    
    //temp map attributes (not written to the xml, but required for the map before it is created)
    private int gridX = -1;
    private int gridY = -1;

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getInternalTitle() {
        return title;
    }
    
    public void setInternalTitle(String title) {
        this.title = title;
    }

    public InfoResource getInfoResource() {
        return texts;
    }
    
	public void setInfoResource(InfoResource infoResource) {
		this.texts = infoResource;
	}

    public List<Integer> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Integer> buildings) {
        this.buildings = buildings;
    }

    public int[] getImages() {
        return images;
    }

    public void setImages(int[] images) {
        this.images = images;
    }

    public int[] getWorkers() {
        return workers;
    }

    public void setWorkers(int[] workers) {
        this.workers = workers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getSilver() {
        return silver;
    }

    public void setSilver(int silver) {
        this.silver = silver;
    }

    public int getBronze() {
        return bronze;
    }

    public void setBronze(int bronze) {
        this.bronze = bronze;
    }

    public List<LevelTargetResource> getTargets() {
        return targets;
    }

    public void setTargets(List<LevelTargetResource> targets) {
        this.targets = targets;
    }

    public void addTarget(LevelTargetResource levelTarget){

        this.targets.add(levelTarget);
    }
    
    public int getGridX() {
    	if(gridX==-1) LevelDataIO.populateGridDimentionsFromJson(this);
		return gridX;
	}

	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	public int getGridY() {
		if(gridY==-1) LevelDataIO.populateGridDimentionsFromJson(this);
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public List<LevelTargetResource> copyTargets(){
    	
    	List<LevelTargetResource> copy = new ArrayList<>();
    	
    	for (LevelTargetResource levelTargetResource : getTargets()) {
			copy.add(levelTargetResource.copy());
		}
    	
    	return copy;
    }
	
	public LevelResource copy(){
		
		LevelResource copy = new LevelResource();
		
		copy.setId(id);
	    copy.setInternalTitle(title);
	    copy.setGold(gold);
	    copy.setSilver(silver);
	    copy.setBronze(bronze);
		copy.setBuildings(new ArrayList<Integer>(buildings));
		copy.setImages(Arrays.copyOf(images,images.length));
		copy.setWorkers(Arrays.copyOf(workers,workers.length));
		copy.setMapPath(mapPath);
		copy.setJson(json);
		copy.setGridX(gridX);
	    copy.setGridY(gridY);
		InfoResource newInfo = new InfoResource();
		newInfo.setTextMap(new HashMap<String, String>(getInfoResource().getTextMap()));
		copy.setInfoResource(newInfo);
		copy.setTargets(copyTargets());

	    return copy;
	}
}
