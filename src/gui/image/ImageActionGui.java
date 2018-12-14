package gui.image;

import java.util.List;

import javax.swing.JFrame;

import data.map.resources.BuildingActionResource;
import data.map.resources.BuildingResource;
import data.map.resources.MapItemActionResource;
import data.map.resources.MapItemResource;
import data.map.resources.MultiImageResourceAction;
import data.map.resources.Resource;
import gui.ITableUpdateHook;

public class ImageActionGui extends BaseMultiActionGui {

	public ImageActionGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	@Override
	protected boolean addSkipToForm() {

		return true;
	}
	
	
	@Override
	protected String getInnerPathPrefix(){
		
		return "action/sequence";
	}
	
	@Override
	protected List<MultiImageResourceAction> getResources(){
		
		return imageResource.getBusy();
	}
	
	@Override
	protected void setMultiResource(List<MultiImageResourceAction> resources) {
		
		imageResource.setBusy(resources);
		
	}


	@Override
	protected Object[] getTableColumnNames() {
		
		return new Object[]{"Name", "skip", "Directory", "Images"};
	}


	@Override
	protected int[] getTableColumnSizes() {
		
		return new int[]{200, 50, 200, 50}; 
	}
	
	protected void addRowFromResource(MultiImageResourceAction resource, int row, ITableUpdateHook hook, String sequenceStr){
		
		Object[] data = new Object[]{
				resource.getInternalName(),
				resource.getSkip(),
				sequenceStr+resource.getDirectory(),
				resource.getTotalNumberImages()				
			};

			hook.addDataRowToTable(data, row);
	}
	
	@Override
	protected String checkForUsage(MultiImageResourceAction action){
		
		int id = action.getId();
		
		//map item
		List<MapItemResource> mapItems = Resource.getMapItemResourceList();
		
		if(mapItems!=null && !mapItems.isEmpty()){
			
			for (MapItemResource mapItemResource : mapItems) {
				
				if(imageResource.getId() == mapItemResource.getImageResourceId()){
				
					List<MapItemActionResource> mapItemActionList = mapItemResource.getMapItemActionList();
					
					if(mapItemActionList!=null && !mapItemActionList.isEmpty()){
						
						for (MapItemActionResource mapItemActionResource : mapItemActionList) {
							
							if(mapItemActionResource.getBusy() == id)
								return "action sequence used by map item '" + mapItemResource.getName()+ "' on it's Action '"+mapItemActionResource.getInternalName()+"'";
							
						}
					}
				
				}
			}
		}
		
		//building
		List<BuildingResource> buildingList = Resource.getBuildingResourceList();
		
		for (BuildingResource buildingResource : buildingList) {
			if(!buildingResource.isWarehouse() && imageResource.getId() == buildingResource.getImageResourceId()){
				
				List<BuildingActionResource> buildingActions = buildingResource.getBuildingActionList();
				for (BuildingActionResource buildingActionResource : buildingActions) {
					if(buildingActionResource.getBusyAction() == id){
						return "action sequence used by building '" + buildingResource.getName()+ "' on it's Action '"+buildingActionResource.getTitle()+"'";
					}
				}
			}
		}
		
		return null;
	}

	@Override
	protected String getHelpText() {
		
		StringBuilder sb;
		
		if(imageResource.isMapObject()){
			sb = getMapItemActionHelp();
		}else{
			sb = getBuildingActionHelp();
		}
		sb.append("The Name field is used by the editor to identify the action when being referenced by a building.\n");
		sb.append("The skip is the number of game ticks that the image is held for before the game displays the next image.\n\n");
		sb.append("Click the Add button under the imagebox and select the images you want to display, you can select multiple images and they will be added in an alphanumic order, depending on their filename\n");
		sb.append("When images have been added, you can swap them with the image to their right (by clicking Swap) in order to put them im the right posistions.\n");
		sb.append("If an incorrect image has been added, it can be removed if it the last by clicking on Clear.\n");
		sb.append("You can scroll through the added images by dragging the slider horizontally\n\n");
		sb.append("After filling all the fields and selecting the desired images, you can add them to the table using the Add button\n\n");
		sb.append("You can edit previously added images by selecting the row on the table and clicking the Edit button.  Selecting a row and clicking the Remove button will remove the row.\n\n");
		sb.append("You can only save your changes after Adding/Removing a row to/from the table");
		
		return sb.toString();
	}
	
	protected StringBuilder getMapItemActionHelp(){
		
		return new StringBuilder("Map Item Action\n\n")
						 .append("You can add images for interactions with a map item\n")
						 .append("These are typically workers interaction with the map item, for example; a worker planting a tree, a worker havesting apples, a worker cutting down a tree.\n\n");
	}
	
	protected StringBuilder getBuildingActionHelp(){
		
		return new StringBuilder("Building Action\n\n")
				 		 .append("You can add images to show a worker working at the building\n")
				 		 .append("For example; A sawmill sawing wood\n\n");
	}
}
