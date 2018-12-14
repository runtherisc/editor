package data.map.resources;

public class BuildingActionProduceResource extends BuildingActionRequireResource{

	public BuildingActionProduceResource copy(){
		
		BuildingActionProduceResource copy = new BuildingActionProduceResource();
		
		populateCopy(copy);
		
		return copy;
	}
}
