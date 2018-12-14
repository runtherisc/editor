package data.map.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuildingActionResource {
	
	
	//may drop fulfill and leave it down to the player to toggle actions on and off to increase game play and game difficulty
	private boolean fulfill;
	private int busyAction;
	private int issueIdle;
	private String title;
	List<BuildingActionRequireResource> requireList = new ArrayList<BuildingActionRequireResource>();
	List<BuildingActionProduceResource> produceList = new ArrayList<BuildingActionProduceResource>();
	private InfoResource infoResource = new InfoResource();

	public void addProduce(BuildingActionProduceResource produce){
		
		produceList.add(produce);
	}
	
	public List<BuildingActionProduceResource> getProduces(){
		
		return produceList;
	}

	public void addRequirement(BuildingActionRequireResource requirement){
		
		requireList.add(requirement);
	}
	
	public List<BuildingActionRequireResource> getRequirements(){
		
		return requireList;
	}

	

	public void setRequireList(List<BuildingActionRequireResource> requireList) {
		this.requireList = requireList;
	}

	public void setProduceList(List<BuildingActionProduceResource> produceList) {
		this.produceList = produceList;
	}

	public boolean isFulfill() {
		return fulfill;
	}

	public void setFulfill(boolean fulfill) {
		this.fulfill = fulfill;
	}

	public int getBusyAction() {
		return busyAction;
	}

	public void setBusyAction(int busyAction) {
		this.busyAction = busyAction;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public InfoResource getInfoResource() {
		return infoResource;
	}

	public void setInfoResource(InfoResource infoResource) {
		this.infoResource = infoResource;
	}
	
	public int getIssueIdle() {
		return issueIdle;
	}

	public void setIssueIdle(int issueIdle) {
		this.issueIdle = issueIdle;
	}

	public BuildingActionResource copy(){
		
		BuildingActionResource copy = new BuildingActionResource();
		
		copy.setBusyAction(this.busyAction);
		copy.setIssueIdle(this.issueIdle);
		copy.setFulfill(this.fulfill);
		InfoResource newInfo = new InfoResource();
		newInfo.setTextMap(new HashMap<String, String>(getInfoResource().getTextMap()));
		copy.setInfoResource(newInfo);
		copy.setTitle(this.title);
		for (BuildingActionProduceResource buildingActionProduceResource : produceList) {
			copy.addProduce(buildingActionProduceResource.copy());
		}
		for (BuildingActionRequireResource buildingActionRequireResource : requireList) {
			copy.addRequirement(buildingActionRequireResource.copy());
		}
		
		return copy;
	}
	
}
