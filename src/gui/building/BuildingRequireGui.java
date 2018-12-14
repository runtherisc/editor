package gui.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import data.map.resources.BuildingActionRequireResource;
import data.map.resources.BuildingActionResource;
import data.map.resources.ItemResource;
import data.map.resources.Resource;
import gui.PropertyKeys;

public class BuildingRequireGui extends BaseRequirementsGui {
	
	BuildingActionResource buildingActionResource;
	private List<BuildingActionRequireResource> tableItems, revertTableItems;

	public BuildingRequireGui(String title, JFrame parent) {
		super(title, parent);
	}
	
	@Override
	protected void passedBundle(Map<String, Object> properties) {
		
		super.passedBundle(properties);
	
		buildingActionResource = (BuildingActionResource) properties.get(PropertyKeys.BUILDING_ACTION_RESOURCE);

	
	}
	
	@Override
	protected BuildingActionRequireResource createNewResource(){
		
		return new BuildingActionRequireResource();
	}
	
	
	@Override
	protected String getComponetNamePrefix() {

		return "Required";
	}
	
	@Override
	protected String[] initItemNames(List<Integer> items){
		
		List<ItemResource> resourceItems = Resource.getWarehouseItemInternalNames();
		
		String[] itemNames = new String[resourceItems.size()];
		
		for (int i = 0; i < itemNames.length; i++) {
			itemNames[i] = resourceItems.get(i).getName();
			items.add(resourceItems.get(i).getId());
		}
		
		return itemNames;
	}


	@Override
	protected void clearTable() {
		tableItems = new ArrayList<BuildingActionRequireResource>();
		
	}
	
	protected List<BuildingActionRequireResource> copyTableItems(List<BuildingActionRequireResource> tableItems){
		
		ArrayList<BuildingActionRequireResource> tableItemsCopy = new ArrayList<BuildingActionRequireResource>();
		
		for (BuildingActionRequireResource resource : tableItems) {
			
			tableItemsCopy.add(resource.copy());
		}
		
		return tableItemsCopy;
		
	}

	@Override
	protected List<? extends BuildingActionRequireResource> getTableItems() {
		return tableItems;
	}

	@Override
	protected void configureTableList() {
		
		List<BuildingActionRequireResource> resourceList = buildingActionResource.getRequirements();
		
		if(resourceList!=null && !resourceList.isEmpty()){
			
			for(BuildingActionRequireResource resource : resourceList){
				
				tableItems.add(resource.copy());

			}
		}
		
	}

	@Override
	protected void addRowToTableItems(int row, BuildingActionRequireResource resource) {
		if(row == -1) tableItems.add(resource);
		else tableItems.add(row, resource);
		
	}

	@Override
	protected void saveData() {
		
		revertTableItems = copyTableItems(tableItems);
		buildingActionResource.setRequireList(copyTableItems(tableItems));
		
	}
	
	@Override
	protected void revertUnsavedChanges(int pos) {
		
		if(revertTableItems==null){			
			
			tableItems = new ArrayList<BuildingActionRequireResource>();
			configureTableList();

		}else{
			
			tableItems = copyTableItems(revertTableItems);
		}
		
		displayTable(tableItems);
		
	}

	@Override
	protected String getHelpText() {
		return new StringBuilder("Building Requirements\n\n")
						 .append("This is where you setup any requirements for the task you selected on the previous form, if items are needed to fulfill the task.\n")
						 .append("Items can come from either a warehouse or a map item.\n\n")
						 .append("Select the item needed for the task from the Required Item dropdown combo box.\n\n")
						 .append("Enter how many of this item are needed in the Required Amount box.\n\n")
						 .append("If the item is coming from a warehouse or a map item that a worker need to walk to (eg. this building is not a mine), then the following components need to be set (Required Carry, Required Worker Out and Required Worker In)\n\n")
						 .append("The Required Carry is how many items a worker can carry.\n\n")
						 .append("Required Worker Out dropdown combo box contains all the workers that have been created.  This worker image set would typically be a empty handed worker as they are going to get the item.\n\n")
						 .append("Required Worker In dropdown combo box contains all the workers that have been created.  This worker image set would typically be the image set you created of the worker carrying the item as they are returning.\n\n")
						 .append("When you are happy with your selection, click the Add button to add them to the table.\n\n")
						 .append("Once added, if the item is coming from a map item and not a warehouse, you can configure all the map items it could come from, by selecting the newly added row and clicking the map items button.\n")
						 .append("Remember that an area needs to be set on the preivous form before you can add a Map Item.\n\n")
						 .append("You can Edit or Remove a previously added row, by selecting the row and using the Edit and Remove buttons.").toString();
	}

}
